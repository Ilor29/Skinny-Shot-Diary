package app.pendia.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.pendia.R
import app.pendia.data.AppData
import app.pendia.data.Catalog
import app.pendia.data.GlucoseKind
import app.pendia.data.GlucoseUnit
import app.pendia.data.SideEffect
import app.pendia.ui.Fmt
import app.pendia.ui.LineChart
import app.pendia.ui.SectionCard
import app.pendia.ui.locale
import java.time.LocalDate
import java.time.LocalDateTime

@Composable
fun ProgressScreen(data: AppData, pad: PaddingValues) {
    val loc = locale()
    val other = stringResource(R.string.med_other)
    val since28 = LocalDate.now().minusDays(28)

    LazyColumn(
        Modifier.fillMaxSize().padding(pad),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            SectionCard(stringResource(R.string.progress_weight)) {
                val w = data.weights.sortedBy { it.date }
                if (w.size < 2) {
                    Text(stringResource(R.string.progress_weight_empty), style = MaterialTheme.typography.bodyMedium)
                } else {
                    val values = w.map { it.kg }
                    LineChart(
                        values,
                        labelMin = Fmt.num(values.min(), loc, 1) + " kg",
                        labelMax = Fmt.num(values.max(), loc, 1) + " kg",
                        description = stringResource(R.string.progress_weight),
                    )
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(Fmt.date(LocalDate.parse(w.first().date), loc), style = MaterialTheme.typography.bodySmall)
                        Text(Fmt.date(LocalDate.parse(w.last().date), loc), style = MaterialTheme.typography.bodySmall)
                    }
                    val diff = w.last().kg - w.first().kg
                    val sign = if (diff > 0) "+" else ""
                    Text(stringResource(R.string.progress_total, sign + Fmt.num(diff, loc, 1) + " kg"), fontWeight = FontWeight.SemiBold)
                }
            }
        }

        item {
            SectionCard(stringResource(R.string.progress_doses)) {
                val inj = data.injections.sortedBy { it.dateTime }
                if (inj.isEmpty()) Text(stringResource(R.string.progress_doses_empty), style = MaterialTheme.typography.bodyMedium)
                // Aufeinanderfolgende Spritzen mit gleichem Medikament und gleicher Dosis zusammenfassen
                val steps = mutableListOf<Step>()
                inj.forEach { i ->
                    val d = LocalDateTime.parse(i.dateTime).toLocalDate()
                    val lastStep = steps.lastOrNull()
                    if (lastStep != null && lastStep.medId == i.medicationId && lastStep.dose == i.doseMg) lastStep.count++
                    else steps += Step(i.medicationId, i.doseMg, d, 1)
                }
                steps.reversed().forEach { s ->
                    val med = data.medications.firstOrNull { it.id == s.medId }
                    val name = med?.let { Catalog.shortName(it, other) } ?: "–"
                    val dose = s.dose?.let { Fmt.num(it, loc) + " mg" } ?: "–"
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("$name · $dose", style = MaterialTheme.typography.bodyLarge)
                        Text(stringResource(R.string.report_since, Fmt.date(s.from, loc)) + " · ${s.count}×", style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }

        item {
            SectionCard(stringResource(R.string.progress_side_effects)) {
                val recent = data.sideEffects.filter { !LocalDate.parse(it.date).isBefore(since28) }
                if (recent.isEmpty()) Text(stringResource(R.string.progress_none), style = MaterialTheme.typography.bodyMedium)
                recent.groupBy { SideEffect.of(it.type) }.toList().sortedByDescending { it.second.size }.forEach { (type, list) ->
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(stringResource(type.label), style = MaterialTheme.typography.bodyLarge)
                        Text("${list.size}×", style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        }

        if (data.diabetesMode || data.glucose.isNotEmpty()) {
            item {
                SectionCard(stringResource(R.string.progress_glucose)) {
                    val readings = data.glucose.filter { it.kind != GlucoseKind.HBA1C && it.unit == data.glucoseUnit }.sortedBy { it.dateTime }
                    val unit = stringResource(if (data.glucoseUnit == GlucoseUnit.MMOL) R.string.unit_mmol else R.string.unit_mgdl)
                    if (readings.size >= 2) {
                        val v = readings.map { it.value }
                        LineChart(v, Fmt.num(v.min(), loc, 1) + " " + unit, Fmt.num(v.max(), loc, 1) + " " + unit, description = stringResource(R.string.progress_glucose))
                    }
                    data.glucose.filter { it.kind == GlucoseKind.HBA1C }.maxByOrNull { it.dateTime }?.let {
                        Text(stringResource(R.string.progress_hba1c_last, Fmt.num(it.value, loc, 1) + " % (" + Fmt.date(LocalDateTime.parse(it.dateTime).toLocalDate(), loc) + ")"))
                    }
                    val hypos = data.glucose.count { it.hypo && !LocalDateTime.parse(it.dateTime).toLocalDate().isBefore(since28) }
                    Text(stringResource(R.string.progress_hypos, hypos))
                }
            }
        }
    }
}

private data class Step(val medId: String, val dose: Double?, val from: LocalDate, var count: Int)
