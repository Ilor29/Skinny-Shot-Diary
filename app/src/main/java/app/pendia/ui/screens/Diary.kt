package app.pendia.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import app.pendia.R
import app.pendia.data.AppData
import app.pendia.data.Catalog
import app.pendia.data.GlucoseKind
import app.pendia.data.GlucoseUnit
import app.pendia.data.SideEffect
import app.pendia.data.Site
import app.pendia.reminder.Reminders
import app.pendia.repo
import app.pendia.ui.ConfirmDialog
import app.pendia.ui.Fmt
import app.pendia.ui.locale
import java.time.LocalDate
import java.time.LocalDateTime

private data class DiaryRow(val id: String, val kind: String, val at: LocalDateTime, val title: String, val detail: String, val dateLabel: String)

@Composable
fun DiaryScreen(data: AppData, pad: PaddingValues, open: (String) -> Unit) {
    val ctx = LocalContext.current
    val loc = locale()
    val other = stringResource(R.string.med_other)
    val sev = listOf(stringResource(R.string.severity_1), stringResource(R.string.severity_2), stringResource(R.string.severity_3))
    val tInj = stringResource(R.string.type_injection)
    val tW = stringResource(R.string.type_weight)
    val tSe = stringResource(R.string.type_side_effect)
    val tGl = stringResource(R.string.type_glucose)
    val hypo = stringResource(R.string.hypo_flag)
    val mgdl = stringResource(R.string.unit_mgdl)
    val mmol = stringResource(R.string.unit_mmol)
    val hba1c = stringResource(R.string.glucose_hba1c)
    val siteLabels = Site.entries.associateWith { stringResource(it.label) }
    val seLabels = SideEffect.entries.associateWith { stringResource(it.label) }

    val rows = buildList {
        data.injections.forEach { i ->
            val at = LocalDateTime.parse(i.dateTime)
            val med = data.medications.firstOrNull { it.id == i.medicationId }
            val parts = listOfNotNull(
                med?.let { Catalog.shortName(it, other) },
                i.doseMg?.let { Fmt.num(it, loc) + " mg" },
                Site.of(i.site)?.let { siteLabels[it] },
                i.note.ifBlank { null },
            )
            add(DiaryRow(i.id, "inj", at, tInj, parts.joinToString(" · "), Fmt.dateTime(at, loc)))
        }
        data.weights.forEach { w ->
            val d = LocalDate.parse(w.date)
            val detail = Fmt.num(w.kg, loc, 1) + " kg" + (w.waistCm?.let { " · " + Fmt.num(it, loc, 1) + " cm" } ?: "")
            add(DiaryRow(w.id, "w", d.atTime(12, 0), tW, detail, Fmt.date(d, loc)))
        }
        data.sideEffects.forEach { s ->
            val d = LocalDate.parse(s.date)
            val detail = listOfNotNull(seLabels[SideEffect.of(s.type)], sev[(s.severity - 1).coerceIn(0, 2)], s.note.ifBlank { null }).joinToString(" · ")
            add(DiaryRow(s.id, "se", d.atTime(12, 0), tSe, detail, Fmt.date(d, loc)))
        }
        data.glucose.forEach { g ->
            val at = LocalDateTime.parse(g.dateTime)
            val value = when {
                g.kind == GlucoseKind.HBA1C -> "$hba1c " + Fmt.num(g.value, loc, 1) + " %"
                g.unit == GlucoseUnit.MMOL -> Fmt.num(g.value, loc, 1) + " " + mmol
                else -> Fmt.num(g.value, loc, 0) + " " + mgdl
            }
            val detail = listOfNotNull(value, if (g.hypo) hypo else null, g.note.ifBlank { null }).joinToString(" · ")
            add(DiaryRow(g.id, "gl", at, tGl, detail, Fmt.dateTime(at, loc)))
        }
    }.sortedByDescending { it.at }

    var toDelete by remember { mutableStateOf<DiaryRow?>(null) }

    if (rows.isEmpty()) {
        Column(
            Modifier.fillMaxSize().padding(pad).padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(stringResource(R.string.diary_empty_title), style = MaterialTheme.typography.titleLarge)
            Text(stringResource(R.string.diary_empty_text), style = MaterialTheme.typography.bodyLarge, modifier = Modifier.padding(vertical = 12.dp))
            Button(onClick = { open("injection") }) { Text(stringResource(R.string.action_injection)) }
        }
        return
    }

    LazyColumn(Modifier.fillMaxSize().padding(pad)) {
        items(rows, key = { it.id }) { r ->
            ListItem(
                overlineContent = { Text(r.dateLabel) },
                headlineContent = { Text(r.title) },
                supportingContent = { if (r.detail.isNotBlank()) Text(r.detail) },
                trailingContent = {
                    IconButton(onClick = { toDelete = r }) {
                        Icon(Icons.Filled.Delete, contentDescription = stringResource(R.string.delete) + " " + r.title + " " + r.dateLabel)
                    }
                },
            )
            HorizontalDivider()
        }
    }

    toDelete?.let { r ->
        ConfirmDialog(
            title = stringResource(R.string.delete_confirm_title),
            text = stringResource(R.string.delete_confirm_text, r.title, r.dateLabel),
            confirm = stringResource(R.string.delete),
            onConfirm = {
                ctx.repo.update { d ->
                    when (r.kind) {
                        "inj" -> d.copy(injections = d.injections.filterNot { it.id == r.id })
                        "w" -> d.copy(weights = d.weights.filterNot { it.id == r.id })
                        "se" -> d.copy(sideEffects = d.sideEffects.filterNot { it.id == r.id })
                        else -> d.copy(glucose = d.glucose.filterNot { it.id == r.id })
                    }
                }
                if (r.kind == "inj") Reminders.schedule(ctx)
                toDelete = null
            },
            onDismiss = { toDelete = null },
        )
    }
}
