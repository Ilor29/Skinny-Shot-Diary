package app.pendia.ui.screens

import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material3.Checkbox
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import app.pendia.R
import app.pendia.data.AppData
import app.pendia.data.Catalog
import app.pendia.data.GlucoseEntry
import app.pendia.data.GlucoseKind
import app.pendia.data.GlucoseUnit
import app.pendia.data.Injection
import app.pendia.data.Medication
import app.pendia.data.SideEffect
import app.pendia.data.SideEffectEntry
import app.pendia.data.Site
import app.pendia.data.WeightEntry
import app.pendia.reminder.Reminders
import app.pendia.repo
import app.pendia.ui.BodyMap
import app.pendia.ui.FormScreen
import app.pendia.ui.Fmt
import app.pendia.ui.NumberField
import app.pendia.ui.PickerField
import app.pendia.ui.locale
import app.pendia.ui.pickDate
import app.pendia.ui.pickTime
import java.time.LocalDate
import java.time.LocalTime

@Composable
private fun DateTimeFields(date: LocalDate, time: LocalTime?, onDate: (LocalDate) -> Unit, onTime: (LocalTime) -> Unit) {
    val ctx = LocalContext.current
    val loc = locale()
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        PickerField(stringResource(R.string.entry_date), Fmt.date(date, loc), Modifier.weight(1f)) { pickDate(ctx, date, onPick = onDate) }
        if (time != null) {
            PickerField(stringResource(R.string.entry_time), Fmt.time(time, loc), Modifier.weight(1f)) { pickTime(ctx, time, onTime) }
        }
    }
}

private fun saved(ctx: android.content.Context) = Toast.makeText(ctx, R.string.saved, Toast.LENGTH_SHORT).show()

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun InjectionEntryScreen(data: AppData, onClose: () -> Unit) {
    val ctx = LocalContext.current
    val loc = locale()
    val med = data.activeMedication ?: return
    val product = Catalog.product(med.productKey)
    val lastDose = data.injections.filter { it.medicationId == med.id }.maxByOrNull { it.dateTime }?.doseMg
    val recent = data.injections.sortedByDescending { it.dateTime }.mapNotNull { Site.of(it.site) }.distinct().take(4)

    var date by rememberSaveable { mutableStateOf(LocalDate.now().toString()) }
    var time by rememberSaveable { mutableStateOf(LocalTime.now().withSecond(0).withNano(0).toString()) }
    var dose by rememberSaveable { mutableStateOf(lastDose?.let { Fmt.num(it, loc) } ?: "") }
    var site by rememberSaveable { mutableStateOf<String?>(null) }
    var note by rememberSaveable { mutableStateOf("") }
    var doseError by remember { mutableStateOf(false) }

    FormScreen(
        title = stringResource(R.string.entry_injection_title),
        onBack = onClose,
        onSave = {
            val mg = if (dose.isBlank()) null else Fmt.parse(dose)
            if (dose.isNotBlank() && (mg == null || mg <= 0)) {
                doseError = true
                return@FormScreen
            }
            ctx.repo.update {
                it.copy(
                    injections = it.injections + Injection(
                        dateTime = LocalDate.parse(date).atTime(LocalTime.parse(time)).toString(),
                        medicationId = med.id, doseMg = mg, site = site, note = note.trim(),
                    )
                )
            }
            Reminders.schedule(ctx)
            saved(ctx)
            onClose()
        },
    ) {
        Text(Catalog.displayName(med, stringResource(R.string.med_other)), style = MaterialTheme.typography.titleMedium)
        DateTimeFields(LocalDate.parse(date), LocalTime.parse(time), { date = it.toString() }, { time = it.toString() })

        Text(stringResource(R.string.entry_dose), style = MaterialTheme.typography.labelLarge)
        if (product.doses.isNotEmpty()) {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                product.doses.forEach { d ->
                    val label = Fmt.num(d, loc)
                    FilterChip(selected = Fmt.parse(dose) == d, onClick = { dose = label; doseError = false }, label = { Text("$label mg") })
                }
            }
        }
        NumberField(
            stringResource(R.string.entry_dose_custom), dose, { dose = it; doseError = false },
            if (doseError) stringResource(R.string.error_number) else null, suffix = "mg"
        )

        Text(stringResource(R.string.entry_site), style = MaterialTheme.typography.labelLarge)
        Text(stringResource(R.string.entry_site_hint), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        val selected = Site.of(site)
        BodyMap(
            selected = selected, recent = recent,
            modifier = Modifier.fillMaxWidth(0.75f).align(Alignment.CenterHorizontally),
            description = stringResource(R.string.entry_site),
            onSelect = { site = it.name },
        )
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Site.entries.forEach { s ->
                FilterChip(selected = selected == s, onClick = { site = if (selected == s) null else s.name }, label = { Text(stringResource(s.label)) })
            }
        }
        OutlinedTextField(note, { note = it }, label = { Text(stringResource(R.string.entry_note)) }, modifier = Modifier.fillMaxWidth())
    }
}

@Composable
fun WeightEntryScreen(onClose: () -> Unit) {
    val ctx = LocalContext.current
    var date by rememberSaveable { mutableStateOf(LocalDate.now().toString()) }
    var kg by rememberSaveable { mutableStateOf("") }
    var waist by rememberSaveable { mutableStateOf("") }
    var kgError by remember { mutableStateOf(false) }
    var waistError by remember { mutableStateOf(false) }

    FormScreen(
        title = stringResource(R.string.entry_weight_title),
        onBack = onClose,
        onSave = {
            val k = Fmt.parse(kg)
            val w = if (waist.isBlank()) null else Fmt.parse(waist)
            kgError = k == null || k <= 0 || k > 500
            waistError = waist.isNotBlank() && (w == null || w <= 0)
            if (kgError || waistError) return@FormScreen
            ctx.repo.update { it.copy(weights = it.weights + WeightEntry(date = date, kg = k!!, waistCm = w)) }
            saved(ctx)
            onClose()
        },
    ) {
        DateTimeFields(LocalDate.parse(date), null, { date = it.toString() }, {})
        NumberField(stringResource(R.string.entry_weight_kg), kg, { kg = it; kgError = false }, if (kgError) stringResource(R.string.error_number) else null, suffix = "kg")
        NumberField(stringResource(R.string.entry_waist), waist, { waist = it; waistError = false }, if (waistError) stringResource(R.string.error_number) else null, suffix = "cm")
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun SideEffectEntryScreen(onClose: () -> Unit) {
    val ctx = LocalContext.current
    var date by rememberSaveable { mutableStateOf(LocalDate.now().toString()) }
    var type by rememberSaveable { mutableStateOf(SideEffect.NAUSEA.name) }
    var severity by rememberSaveable { mutableStateOf(1) }
    var note by rememberSaveable { mutableStateOf("") }

    FormScreen(
        title = stringResource(R.string.entry_se_title),
        onBack = onClose,
        onSave = {
            ctx.repo.update { it.copy(sideEffects = it.sideEffects + SideEffectEntry(date = date, type = type, severity = severity, note = note.trim())) }
            saved(ctx)
            onClose()
        },
    ) {
        DateTimeFields(LocalDate.parse(date), null, { date = it.toString() }, {})
        Text(stringResource(R.string.entry_se_type), style = MaterialTheme.typography.labelLarge)
        FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            SideEffect.entries.forEach { s ->
                FilterChip(selected = type == s.name, onClick = { type = s.name }, label = { Text(stringResource(s.label)) })
            }
        }
        Text(stringResource(R.string.entry_severity), style = MaterialTheme.typography.labelLarge)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(R.string.severity_1, R.string.severity_2, R.string.severity_3).forEachIndexed { i, label ->
                FilterChip(selected = severity == i + 1, onClick = { severity = i + 1 }, label = { Text(stringResource(label)) }, modifier = Modifier.widthIn(min = 80.dp))
            }
        }
        OutlinedTextField(note, { note = it }, label = { Text(stringResource(R.string.entry_note)) }, modifier = Modifier.fillMaxWidth())
    }
}

@Composable
fun GlucoseEntryScreen(data: AppData, onClose: () -> Unit) {
    val ctx = LocalContext.current
    var date by rememberSaveable { mutableStateOf(LocalDate.now().toString()) }
    var time by rememberSaveable { mutableStateOf(LocalTime.now().withSecond(0).withNano(0).toString()) }
    var kind by rememberSaveable { mutableStateOf(GlucoseKind.FASTING) }
    var value by rememberSaveable { mutableStateOf("") }
    var hypo by rememberSaveable { mutableStateOf(false) }
    var note by rememberSaveable { mutableStateOf("") }
    var error by remember { mutableStateOf(false) }
    val unitLabel = when {
        kind == GlucoseKind.HBA1C -> stringResource(R.string.unit_percent)
        data.glucoseUnit == GlucoseUnit.MMOL -> stringResource(R.string.unit_mmol)
        else -> stringResource(R.string.unit_mgdl)
    }

    FormScreen(
        title = stringResource(R.string.entry_glucose_title),
        onBack = onClose,
        onSave = {
            val v = Fmt.parse(value)
            error = v == null || v <= 0
            if (error) return@FormScreen
            ctx.repo.update {
                it.copy(
                    glucose = it.glucose + GlucoseEntry(
                        dateTime = LocalDate.parse(date).atTime(LocalTime.parse(time)).toString(),
                        kind = kind, value = v!!,
                        unit = if (kind == GlucoseKind.HBA1C) "percent" else data.glucoseUnit,
                        hypo = hypo && kind != GlucoseKind.HBA1C, note = note.trim(),
                    )
                )
            }
            saved(ctx)
            onClose()
        },
    ) {
        DateTimeFields(LocalDate.parse(date), LocalTime.parse(time), { date = it.toString() }, { time = it.toString() })
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(GlucoseKind.FASTING to R.string.glucose_fasting, GlucoseKind.OTHER to R.string.glucose_other, GlucoseKind.HBA1C to R.string.glucose_hba1c)
                .forEach { (k, label) -> FilterChip(selected = kind == k, onClick = { kind = k }, label = { Text(stringResource(label)) }) }
        }
        NumberField(stringResource(R.string.glucose_value), value, { value = it; error = false }, if (error) stringResource(R.string.error_number) else null, suffix = unitLabel)
        if (kind != GlucoseKind.HBA1C) {
            Row(
                Modifier.fillMaxWidth().toggleable(hypo, role = Role.Checkbox) { hypo = it },
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Checkbox(checked = hypo, onCheckedChange = null)
                Spacer(Modifier.size(12.dp))
                Text(stringResource(R.string.glucose_hypo))
            }
        }
        OutlinedTextField(note, { note = it }, label = { Text(stringResource(R.string.entry_note)) }, modifier = Modifier.fillMaxWidth())
    }
}

@Composable
fun ChangeMedicationScreen(data: AppData, onClose: () -> Unit) {
    val ctx = LocalContext.current
    val loc = locale()
    var productKey by rememberSaveable { mutableStateOf(Catalog.products.first().key) }
    var customName by rememberSaveable { mutableStateOf("") }
    var interval by rememberSaveable { mutableStateOf(7) }
    var startDate by rememberSaveable { mutableStateOf(LocalDate.now().toString()) }
    var nameError by remember { mutableStateOf(false) }

    FormScreen(
        title = stringResource(R.string.settings_change_med),
        onBack = onClose,
        onSave = {
            if (productKey == Catalog.OTHER && customName.isBlank()) {
                nameError = true
                return@FormScreen
            }
            ctx.repo.update { d ->
                val ended = d.medications.map { if (it.endDate == null) it.copy(endDate = startDate) else it }
                d.copy(medications = ended + Medication(productKey = productKey, customName = customName.trim(), startDate = startDate, intervalDays = interval))
            }
            Reminders.schedule(ctx)
            saved(ctx)
            onClose()
        },
    ) {
        Text(stringResource(R.string.settings_change_med_hint), style = MaterialTheme.typography.bodyMedium)
        MedPicker(
            productKey = productKey,
            onProduct = { productKey = it; interval = Catalog.product(it).intervalDays },
            customName = customName, onCustomName = { customName = it; nameError = false },
            interval = interval, onInterval = { interval = it }, nameError = nameError,
        )
        PickerField(stringResource(R.string.setup_start_date), Fmt.date(LocalDate.parse(startDate), loc)) {
            pickDate(ctx, LocalDate.parse(startDate)) { startDate = it.toString() }
        }
    }
}
