package app.pendia.reminder

import android.Manifest
import android.app.AlarmManager
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import app.pendia.MainActivity
import app.pendia.R
import app.pendia.data.AppData
import app.pendia.repo
import java.time.DayOfWeek
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.temporal.TemporalAdjusters

object Reminders {
    const val CHANNEL = "reminders"
    private const val REQUEST = 1001
    private const val NOTIFICATION_ID = 1

    fun createChannel(ctx: Context) {
        val nm = ctx.getSystemService(NotificationManager::class.java)
        nm.createNotificationChannel(
            NotificationChannel(CHANNEL, ctx.getString(R.string.notif_channel), NotificationManager.IMPORTANCE_DEFAULT)
        )
    }

    /** Nächster Erinnerungszeitpunkt. Täglich bei Liraglutid, sonst am gewählten Wochentag. */
    fun nextTrigger(data: AppData, now: LocalDateTime = LocalDateTime.now()): LocalDateTime? {
        val r = data.reminder
        if (!r.enabled || data.activeMedication == null) return null
        val time = LocalTime.of(r.hour, r.minute)
        if (isDaily(data)) {
            val t = now.toLocalDate().atTime(time)
            return if (t.isAfter(now)) t else t.plusDays(1)
        }
        val day = DayOfWeek.of(r.dayOfWeek.coerceIn(1, 7))
        val t = now.toLocalDate().with(TemporalAdjusters.nextOrSame(day)).atTime(time)
        return if (t.isAfter(now)) t else t.plusWeeks(1)
    }

    fun isDaily(data: AppData) = (data.activeMedication?.intervalDays ?: 7) == 1

    fun schedule(ctx: Context) {
        val am = ctx.getSystemService(AlarmManager::class.java) ?: return
        val pi = pendingIntent(ctx)
        am.cancel(pi)
        val next = nextTrigger(ctx.repo.data.value) ?: return
        val millis = next.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
        // Kein exakter Alarm nötig: wenige Minuten Verzögerung sind bei einer Spritzen-Erinnerung in Ordnung.
        am.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, millis, pi)
    }

    private fun pendingIntent(ctx: Context): PendingIntent =
        PendingIntent.getBroadcast(
            ctx, REQUEST, Intent(ctx, ReminderReceiver::class.java),
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

    fun canNotify(ctx: Context): Boolean =
        Build.VERSION.SDK_INT < 33 ||
            ContextCompat.checkSelfPermission(ctx, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED

    fun show(ctx: Context) {
        if (!canNotify(ctx)) return
        val data = ctx.repo.data.value
        if (!data.reminder.enabled) return
        val open = PendingIntent.getActivity(
            ctx, 0, Intent(ctx, MainActivity::class.java).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP),
            PendingIntent.FLAG_IMMUTABLE
        )
        val text = ctx.getString(if (isDaily(data)) R.string.notif_text_daily else R.string.notif_text)
        val n = NotificationCompat.Builder(ctx, CHANNEL)
            .setSmallIcon(R.drawable.ic_notification)
            .setContentTitle(ctx.getString(R.string.notif_title))
            .setContentText(text)
            .setContentIntent(open)
            .setAutoCancel(true)
            .build()
        try {
            NotificationManagerCompat.from(ctx).notify(NOTIFICATION_ID, n)
        } catch (_: SecurityException) {
        }
    }
}

class ReminderReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Reminders.show(context)
        Reminders.schedule(context)
    }
}

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        Reminders.schedule(context)
    }
}
