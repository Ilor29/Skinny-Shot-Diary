package app.pendia.ui.screens

import android.Manifest
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.pendia.R
import app.pendia.data.Catalog
import app.pendia.data.Medication
import app.pendia.data.Reminder
import app.pendia.data.WeightEntry
import app.pendia.reminder.Reminders
import app.pendia.repo
import app.pendia.ui.Fmt
import app.pendia.ui.NumberField
import app.pendia.ui.PickerField
import app.pendia.ui.locale
import app.pendia.ui.pickDate
import app.pendia.ui.pickTime
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.TextStyle

@Composable
fun SetupScreen() {
    val ctx = LocalContext.current
    val loc = locale()
    var step by rememberSaveable { mutableIntStateOf(0) }
    var accepted by rememberSaveable { mutableStateOf(false) }
    var productKey by rememberSaveable { mutableStateOf(Catalog.products.first().key) }
    var customName by rememberSaveable { mutableStateOf("") }
    var interval by rememberSaveable { mutableIntStateOf(7) }
    var startDate by rememberSaveable { mutableStateOf(LocalDate.now().toString()) }
    var diabetes by rememberSaveable { mutableStateOf(false) }
    var reminderOn by rememberSaveable { mutableStateOf(true) }
    var reminderDay by rememberSaveable { mutableIntStateOf(LocalDate.now().dayOfWeek.value) }
    var reminderTime by rememberSaveable { mutableStateOf("09:00") }
    var weight by rememberSaveable { mutableStateOf("") }
    var nameError by remember { mutableStateOf(false) }
    var weightError by remember { mutableStateOf(false) }

    val permission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (!granted) Toast.makeText(ctx, R.string.notif_denied, Toast.LENGTH_LONG).show()
        Reminders.schedule(ctx)
    }

    fun finish() {
        val kg = if (weight.isBlank()) null else Fmt.parse(weight)
        if (weight.isNotBlank() && (kg == null || kg <= 0)) {
            weightError = true
            return
        }
        val time = LocalTime.parse(reminderTime)
        ctx.repo.update { d ->
            d.copy(
                setupDone = true,
                disclaimerAccepted = true,
                diabetesMode = diabetes,
                medications = listOf(
                    Medication(productKey = productKey, customName = customName.trim(), startDate = startDate, intervalDays = interval)
                ),
                reminder = Reminder(reminderOn, reminderDay, time.hour, time.minute),
                weights = if (kg != null) listOf(WeightEntry(date = LocalDate.now().toString(), kg = kg)) else d.weights,
            )
        }
        Reminders.schedule(ctx)
        if (reminderOn && Build.VERSION.SDK_INT >= 33 && !Reminders.canNotify(ctx)) {
            permission.launch(Manifest.permission.POST_NOTIFICATIONS)
        }
    }

    Surface(Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(Modifier.fillMaxSize().safeDrawingPadding()) {
            Column(
                Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(horizontal = 20.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                StepDots(step)
                when (step) {
                    0 -> {
                        Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                            Icon(
                                painterResource(R.drawable.ic_launcher_foreground), contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(120.dp)
                            )
                        }
                        Text(stringResource(R.string.setup_welcome_title), style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.SemiBold)
                        Text(stringResource(R.string.setup_welcome_text), style = MaterialTheme.typography.bodyLarge)
                        Text(stringResource(R.string.setup_points), style = MaterialTheme.typography.bodyLarge)
                        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)) {
                            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text(stringResource(R.string.disclaimer_title), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold)
                                Text(stringResource(R.string.disclaimer_text), style = MaterialTheme.typography.bodyMedium)
                            }
                        }
                        Row(
                            Modifier.fillMaxWidth().toggleable(accepted, role = Role.Checkbox) { accepted = it },
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Checkbox(checked = accepted, onCheckedChange = null)
                            Spacer(Modifier.size(12.dp))
                            Text(stringResource(R.string.disclaimer_accept), style = MaterialTheme.typography.bodyLarge)
                        }
                    }
                    1 -> {
                        Text(stringResource(R.string.setup_med_title), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
                        MedPicker(
                            productKey = productKey,
                            onProduct = {
                                productKey = it
                                interval = Catalog.product(it).intervalDays
                            },
                            customName = customName,
                            onCustomName = { customName = it; nameError = false },
                            interval = interval,
                            onInterval = { interval = it },
                            nameError = nameError,
                        )
                        PickerField(stringResource(R.string.setup_start_date), Fmt.date(LocalDate.parse(startDate), loc)) {
                            pickDate(ctx, LocalDate.parse(startDate)) {
                                startDate = it.toString()
                                reminderDay = it.dayOfWeek.value
                            }
                        }
                        Text(stringResource(R.string.setup_start_hint), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        SwitchRow(stringResource(R.string.setup_diabetes), stringResource(R.string.setup_diabetes_hint), diabetes) { diabetes = it }
                    }
                    else -> {
                        Text(stringResource(R.string.setup_reminder_title), style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.SemiBold)
                        ReminderEditor(
                            reminder = Reminder(reminderOn, reminderDay, LocalTime.parse(reminderTime).hour, LocalTime.parse(reminderTime).minute),
                            daily = interval == 1,
                        ) { r ->
                            reminderOn = r.enabled
                            reminderDay = r.dayOfWeek
                            reminderTime = LocalTime.of(r.hour, r.minute).toString()
                        }
                        NumberField(
                            stringResource(R.string.setup_weight), weight, { weight = it; weightError = false },
                            if (weightError) stringResource(R.string.error_number) else null, suffix = "kg"
                        )
                    }
                }
            }
            Row(Modifier.fillMaxWidth().padding(20.dp), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                if (step > 0) {
                    OutlinedButton(onClick = { step-- }, modifier = Modifier.weight(1f).height(52.dp)) { Text(stringResource(R.string.back)) }
                }
                Button(
                    onClick = {
                        when (step) {
                            0 -> step = 1
                            1 -> if (productKey == Catalog.OTHER && customName.isBlank()) nameError = true else step = 2
                            else -> finish()
                        }
                    },
                    enabled = step != 0 || accepted,
                    modifier = Modifier.weight(1f).height(52.dp),
                ) { Text(stringResource(if (step == 2) R.string.setup_finish else R.string.next)) }
            }
        }
    }
}

@Composable
private fun StepDots(step: Int) {
    Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        repeat(3) { i ->
            Surface(
                color = if (i <= step) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                shape = MaterialTheme.shapes.small,
                modifier = Modifier.size(width = 28.dp, height = 6.dp),
            ) {}
        }
    }
}

@Composable
fun MedPicker(
    productKey: String,
    onProduct: (String) -> Unit,
    customName: String,
    onCustomName: (String) -> Unit,
    interval: Int,
    onInterval: (Int) -> Unit,
    nameError: Boolean,
) {
    Column {
        Catalog.products.forEach { p ->
            Row(
                Modifier.fillMaxWidth().selectable(productKey == p.key, role = Role.RadioButton) { onProduct(p.key) }.padding(vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                RadioButton(selected = productKey == p.key, onClick = null)
                Spacer(Modifier.size(12.dp))
                if (p.key == Catalog.OTHER) {
                    Text(stringResource(R.string.med_other), style = MaterialTheme.typography.bodyLarge)
                } else {
                    Column {
                        Text(p.brand, style = MaterialTheme.typography.bodyLarge)
                        Text(
                            p.substance + " · " + stringResource(if (p.intervalDays == 1) R.string.interval_daily else R.string.interval_weekly),
                            style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
        if (productKey == Catalog.OTHER) {
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(
                value = customName, onValueChange = onCustomName,
                label = { Text(stringResource(R.string.med_other_name)) },
                isError = nameError,
                supportingText = if (nameError) ({ Text(stringResource(R.string.error_name)) }) else null,
                singleLine = true, modifier = Modifier.fillMaxWidth(),
            )
            Spacer(Modifier.height(8.dp))
            Text(stringResource(R.string.interval_label), style = MaterialTheme.typography.labelLarge)
            listOf(7 to R.string.interval_weekly, 1 to R.string.interval_daily).forEach { (days, label) ->
                Row(
                    Modifier.fillMaxWidth().selectable(interval == days, role = Role.RadioButton) { onInterval(days) }.padding(vertical = 4.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    RadioButton(selected = interval == days, onClick = null)
                    Spacer(Modifier.size(12.dp))
                    Text(stringResource(label))
                }
            }
        }
        Spacer(Modifier.height(4.dp))
        Text(
            stringResource(R.string.med_trademarks),
            style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}

@Composable
fun SwitchRow(title: String, subtitle: String? = null, checked: Boolean, onChange: (Boolean) -> Unit) {
    Row(
        Modifier.fillMaxWidth().toggleable(checked, role = Role.Switch, onValueChange = onChange).padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyLarge)
            if (subtitle != null) Text(subtitle, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Spacer(Modifier.size(12.dp))
        Switch(checked = checked, onCheckedChange = null)
    }
}

@Composable
fun ReminderEditor(reminder: Reminder, daily: Boolean, onChange: (Reminder) -> Unit) {
    val ctx = LocalContext.current
    val loc = locale()
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        SwitchRow(stringResource(R.string.reminder_enable), checked = reminder.enabled) { onChange(reminder.copy(enabled = it)) }
        if (reminder.enabled) {
            if (!daily) {
                var open by remember { mutableStateOf(false) }
                Box {
                    PickerField(stringResource(R.string.reminder_day), DayOfWeek.of(reminder.dayOfWeek).getDisplayName(TextStyle.FULL, loc)) { open = true }
                    DropdownMenu(expanded = open, onDismissRequest = { open = false }) {
                        DayOfWeek.entries.forEach { d ->
                            DropdownMenuItem(
                                text = { Text(d.getDisplayName(TextStyle.FULL, loc)) },
                                onClick = { onChange(reminder.copy(dayOfWeek = d.value)); open = false },
                            )
                        }
                    }
                }
            }
            val t = LocalTime.of(reminder.hour, reminder.minute)
            PickerField(stringResource(R.string.reminder_time), Fmt.time(t, loc)) {
                pickTime(ctx, t) { onChange(reminder.copy(hour = it.hour, minute = it.minute)) }
            }
        }
    }
}
