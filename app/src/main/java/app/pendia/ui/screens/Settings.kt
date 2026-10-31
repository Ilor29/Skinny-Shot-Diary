package app.pendia.ui.screens

import android.Manifest
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatDelegate
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectable
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.core.os.LocaleListCompat
import app.pendia.BuildConfig
import app.pendia.R
import app.pendia.data.AppData
import app.pendia.data.Catalog
import app.pendia.data.GlucoseUnit
import app.pendia.reminder.Reminders
import app.pendia.report.PdfReport
import app.pendia.repo
import app.pendia.ui.ConfirmDialog
import app.pendia.ui.FormScreen
import app.pendia.ui.Fmt
import app.pendia.ui.NumberField
import app.pendia.ui.PickerField
import app.pendia.ui.SectionCard
import app.pendia.ui.locale
import app.pendia.ui.pickDate
import java.time.LocalDate

@Composable
private fun RadioRow(label: String, selected: Boolean, onClick: () -> Unit) {
    Row(
        Modifier.fillMaxWidth().selectable(selected, role = Role.RadioButton, onClick = onClick).padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        RadioButton(selected = selected, onClick = null)
        Spacer(Modifier.size(12.dp))
        Text(label, style = MaterialTheme.typography.bodyLarge)
    }
}

@Composable
fun SettingsScreen(data: AppData, onBack: () -> Unit, open: (String) -> Unit) {
    val ctx = LocalContext.current
    val loc = locale()
    val repo = ctx.repo
    var confirmDeleteAll by remember { mutableStateOf(false) }
    var pendingImport by remember { mutableStateOf<String?>(null) }

    val notifPermission = rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
        if (!granted) Toast.makeText(ctx, R.string.notif_denied, Toast.LENGTH_LONG).show()
    }
    val exportLauncher = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument("application/json")) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        try {
            ctx.contentResolver.openOutputStream(uri)?.use { it.write(repo.exportJson().toByteArray(Charsets.UTF_8)) }
            Toast.makeText(ctx, R.string.export_ok, Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(ctx, R.string.export_failed, Toast.LENGTH_LONG).show()
        }
    }
    val importLauncher = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult
        pendingImport = try {
            ctx.contentResolver.openInputStream(uri)?.use { String(it.readBytes(), Charsets.UTF_8) }
        } catch (e: Exception) {
            null
        }
        if (pendingImport == null) Toast.makeText(ctx, R.string.import_failed, Toast.LENGTH_LONG).show()
    }

    FormScreen(title = stringResource(R.string.settings), onBack = onBack, onSave = null) {
        SectionCard(stringResource(R.string.settings_language)) {
            val current = AppCompatDelegate.getApplicationLocales().toLanguageTags()
            listOf("" to stringResource(R.string.lang_system), "de" to stringResource(R.string.lang_de), "en" to stringResource(R.string.lang_en))
                .forEach { (tag, label) ->
                    RadioRow(label, current.startsWith(tag) && (tag.isNotEmpty() || current.isEmpty())) {
                        AppCompatDelegate.setApplicationLocales(
                            if (tag.isEmpty()) LocaleListCompat.getEmptyLocaleList() else LocaleListCompat.forLanguageTags(tag)
                        )
                    }
                }
        }

        SectionCard(stringResource(R.string.settings_reminder)) {
            ReminderEditor(data.reminder, Reminders.isDaily(data)) { r ->
                repo.update { it.copy(reminder = r) }
                Reminders.schedule(ctx)
                if (r.enabled && Build.VERSION.SDK_INT >= 33 && !Reminders.canNotify(ctx)) {
                    notifPermission.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            }
        }

        SectionCard(stringResource(R.string.settings_med)) {
            data.activeMedication?.let {
                Text(Catalog.displayName(it, stringResource(R.string.med_other)), style = MaterialTheme.typography.bodyLarge)
            }
            OutlinedButton(onClick = { open("changeMed") }) { Text(stringResource(R.string.settings_change_med)) }
        }

        SectionCard {
            SwitchRow(stringResource(R.string.settings_diabetes), stringResource(R.string.setup_diabetes_hint), data.diabetesMode) { on ->
                repo.update { it.copy(diabetesMode = on) }
            }
            if (data.diabetesMode) {
                Text(stringResource(R.string.settings_glucose_unit), style = MaterialTheme.typography.labelLarge)
                RadioRow(stringResource(R.string.unit_mgdl), data.glucoseUnit == GlucoseUnit.MGDL) { repo.update { it.copy(glucoseUnit = GlucoseUnit.MGDL) } }
                RadioRow(stringResource(R.string.unit_mmol), data.glucoseUnit == GlucoseUnit.MMOL) { repo.update { it.copy(glucoseUnit = GlucoseUnit.MMOL) } }
            }
        }

        SectionCard(stringResource(R.string.settings_pen)) {
            val notSet = stringResource(R.string.not_set)
            PickerField(stringResource(R.string.pen_opened), data.pen.openedDate?.let { Fmt.date(LocalDate.parse(it), loc) } ?: notSet) {
                pickDate(ctx, data.pen.openedDate?.let(LocalDate::parse) ?: LocalDate.now()) { d -> repo.update { it.copy(pen = it.pen.copy(openedDate = d.toString())) } }
            }
            PickerField(stringResource(R.string.pen_unrefrigerated), data.pen.unrefrigeratedSince?.let { Fmt.date(LocalDate.parse(it), loc) } ?: notSet) {
                pickDate(ctx, data.pen.unrefrigeratedSince?.let(LocalDate::parse) ?: LocalDate.now()) { d -> repo.update { it.copy(pen = it.pen.copy(unrefrigeratedSince = d.toString())) } }
            }
            var limit by remember(data.pen.limitDays) { mutableStateOf(data.pen.limitDays?.toString() ?: "") }
            NumberField(stringResource(R.string.pen_limit), limit, { v ->
                limit = v.filter { it.isDigit() }
                repo.update { it.copy(pen = it.pen.copy(limitDays = limit.toIntOrNull()?.takeIf { d -> d in 1..365 })) }
            }, null)
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = { repo.update { it.copy(pen = it.pen.copy(openedDate = LocalDate.now().toString(), unrefrigeratedSince = null)) } }) {
                    Text(stringResource(R.string.pen_new))
                }
                TextButton(onClick = { repo.update { it.copy(pen = it.pen.copy(openedDate = null, unrefrigeratedSince = null)) } }) {
                    Text(stringResource(R.string.pen_clear))
                }
            }
        }

        SectionCard(stringResource(R.string.settings_data)) {
            OutlinedButton(onClick = {
                try {
                    PdfReport.share(ctx, PdfReport.create(ctx, data))
                } catch (e: Exception) {
                    Toast.makeText(ctx, R.string.report_failed, Toast.LENGTH_LONG).show()
                }
            }, modifier = Modifier.fillMaxWidth()) { Text(stringResource(R.string.report_pdf)) }
            OutlinedButton(onClick = { exportLauncher.launch("pendia_backup_${LocalDate.now()}.json") }, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.export_json))
            }
            OutlinedButton(onClick = { importLauncher.launch(arrayOf("application/json", "text/plain", "application/octet-stream")) }, modifier = Modifier.fillMaxWidth()) {
                Text(stringResource(R.string.import_json))
            }
            TextButton(onClick = { confirmDeleteAll = true }) {
                Text(stringResource(R.string.delete_all), color = MaterialTheme.colorScheme.error)
            }
        }

        SectionCard(stringResource(R.string.settings_about)) {
            Text(stringResource(R.string.privacy_title), style = MaterialTheme.typography.labelLarge)
            Text(stringResource(R.string.privacy_text), style = MaterialTheme.typography.bodyMedium)
            TextButton(onClick = { open("faq:about") }) { Text(stringResource(R.string.disclaimer_title) + ": " + stringResource(R.string.settings_about)) }
            Text(stringResource(R.string.med_trademarks), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(stringResource(R.string.version, BuildConfig.VERSION_NAME), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }

    if (confirmDeleteAll) {
        ConfirmDialog(
            title = stringResource(R.string.delete_all_confirm_title),
            text = stringResource(R.string.delete_all_confirm_text),
            confirm = stringResource(R.string.delete_all_button),
            onConfirm = {
                repo.deleteAll()
                Reminders.schedule(ctx)
                confirmDeleteAll = false
            },
            onDismiss = { confirmDeleteAll = false },
        )
    }

    pendingImport?.let { text ->
        ConfirmDialog(
            title = stringResource(R.string.import_confirm_title),
            text = stringResource(R.string.import_confirm_text),
            confirm = stringResource(R.string.import_replace),
            onConfirm = {
                try {
                    repo.importJson(text)
                    Reminders.schedule(ctx)
                    Toast.makeText(ctx, R.string.import_ok, Toast.LENGTH_SHORT).show()
                } catch (e: Exception) {
                    Toast.makeText(ctx, R.string.import_failed, Toast.LENGTH_LONG).show()
                }
                pendingImport = null
            },
            onDismiss = { pendingImport = null },
        )
    }
}
