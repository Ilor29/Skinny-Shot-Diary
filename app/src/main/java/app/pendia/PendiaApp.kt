package app.pendia

import android.app.Application
import android.content.Context
import app.pendia.data.Repository
import app.pendia.reminder.Reminders

class PendiaApp : Application() {
    lateinit var repo: Repository
        private set

    override fun onCreate() {
        super.onCreate()
        repo = Repository(this)
        Reminders.createChannel(this)
        Reminders.schedule(this)
    }
}

val Context.repo: Repository
    get() = (applicationContext as PendiaApp).repo
