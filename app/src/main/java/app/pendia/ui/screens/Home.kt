package app.pendia.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.pendia.R
import app.pendia.data.AppData
import app.pendia.data.Catalog
import app.pendia.data.Faq
import app.pendia.data.Site
import app.pendia.ui.BodyMap
import app.pendia.ui.Fmt
import app.pendia.ui.SectionCard
import app.pendia.ui.isGerman
import app.pendia.ui.locale
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun HomeScreen(data: AppData, pad: PaddingValues, open: (String) -> Unit) {
    val loc = locale()
    val de = isGerman()
    val med = data.activeMedication
    val today = LocalDate.now()
    val sortedInj = data.injections.sortedByDescending { it.dateTime }
    val last = sortedInj.firstOrNull()

    LazyColumn(
        Modifier.fillMaxSize().padding(pad),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        item {
            Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer), modifier = Modifier.fillMaxWidth()) {
                Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(
                        stringResource(R.string.home_next).uppercase(loc),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f),
                    )
                    if (med != null) {
                        val lastForMed = sortedInj.firstOrNull { it.medicationId == med.id }
                        if (lastForMed == null) {
                            Text(stringResource(R.string.home_no_injection), style = MaterialTheme.typography.titleLarge)
                        } else {
                            val next = LocalDateTime.parse(lastForMed.dateTime).toLocalDate().plusDays(med.intervalDays.toLong())
                            val days = ChronoUnit.DAYS.between(today, next).toInt()
                            val rel = when {
                                days == 0 -> stringResource(R.string.today)
                                days > 0 -> pluralStringResource(R.plurals.in_days, days, days)
                                else -> pluralStringResource(R.plurals.overdue_days, -days, -days)
                            }
                            Text(rel, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                            Text(Fmt.date(next, loc), style = MaterialTheme.typography.bodyMedium)
                            if (days < 0) {
                                TextButton(onClick = { open("faq:missed_dose") }, contentPadding = PaddingValues(0.dp)) {
                                    Text(stringResource(R.string.home_missed_hint))
                                }
                            }
                        }
                        Text(Catalog.displayName(med, stringResource(R.string.med_other)), style = MaterialTheme.typography.bodyMedium)
                    }
                    Button(onClick = { open("injection") }, modifier = Modifier.fillMaxWidth().padding(top = 6.dp).height(52.dp)) {
                        Icon(Icons.Filled.Add, contentDescription = null)
                        Text(stringResource(R.string.action_injection), modifier = Modifier.padding(start = 8.dp))
                    }
                }
            }
        }

        item {
            FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedButton(onClick = { open("weight") }) { Text(stringResource(R.string.action_weight)) }
                OutlinedButton(onClick = { open("side") }) { Text(stringResource(R.string.action_side_effect)) }
                if (data.diabetesMode) OutlinedButton(onClick = { open("glucose") }) { Text(stringResource(R.string.action_glucose)) }
            }
        }

        val weights = data.weights.sortedBy { it.date }
        val lastDose = med?.let { m -> sortedInj.firstOrNull { it.medicationId == m.id }?.doseMg }
        if (weights.isNotEmpty() || lastDose != null) {
            item {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    val wText = weights.lastOrNull()?.let { Fmt.num(it.kg, loc, 1) + " kg" } ?: "–"
                    val wSub = if (weights.size > 1) {
                        val diff = weights.last().kg - weights.first().kg
                        stringResource(R.string.home_weight_change, (if (diff > 0) "+" else "") + Fmt.num(diff, loc, 1) + " kg")
                    } else ""
                    StatTile(stringResource(R.string.home_weight), wText, wSub, Modifier.weight(1f)) { open("weight") }
                    val dText = lastDose?.let { Fmt.num(it, loc) + " mg" } ?: "–"
                    val dSub = med?.let { Catalog.shortName(it, stringResource(R.string.med_other)) } ?: ""
                    StatTile(stringResource(R.string.home_dose), dText, dSub, Modifier.weight(1f)) { open("injection") }
                }
            }
        }

        if (last != null) {
            item {
                SectionCard(stringResource(R.string.home_last)) {
                    val dt = LocalDateTime.parse(last.dateTime)
                    val ago = ChronoUnit.DAYS.between(dt.toLocalDate(), today).toInt()
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(Fmt.dateTime(dt, loc), style = MaterialTheme.typography.bodyLarge)
                            Text(
                                if (ago == 0) stringResource(R.string.today) else pluralStringResource(R.plurals.days_ago, ago, ago),
                                style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                            Site.of(last.site)?.let { Text(stringResource(it.label), style = MaterialTheme.typography.bodyMedium) }
                        }
                        val recent = sortedInj.mapNotNull { Site.of(it.site) }.distinct().take(4)
                        if (recent.isNotEmpty()) {
                            BodyMap(selected = null, recent = recent, modifier = Modifier.width(96.dp), description = stringResource(R.string.home_sites_recent))
                        }
                    }
                }
            }
        }

        data.pen.openedDate?.let { opened ->
            item {
                SectionCard(stringResource(R.string.home_pen)) {
                    val days = ChronoUnit.DAYS.between(LocalDate.parse(opened), today).toInt().coerceAtLeast(0)
                    Text(pluralStringResource(R.plurals.pen_open_days, days, days), style = MaterialTheme.typography.bodyLarge)
                    data.pen.limitDays?.let { limit ->
                        Text(stringResource(R.string.pen_of_limit, days, limit), style = MaterialTheme.typography.bodyMedium)
                        if (days > limit) Text(stringResource(R.string.pen_over_limit), color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodyMedium)
                    }
                }
            }
        }

        item {
            Card(
                onClick = { open("faq:" + Faq.emergency.id) },
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
                modifier = Modifier.fillMaxWidth(),
            ) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Warning, contentDescription = null)
                    Text(Faq.emergency.title(de), modifier = Modifier.padding(start = 12.dp), style = MaterialTheme.typography.bodyLarge)
                }
            }
        }

        item {
            Text(
                stringResource(R.string.home_disclaimer),
                style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
    }
}

@Composable
private fun StatTile(title: String, value: String, sub: String, modifier: Modifier = Modifier, onClick: () -> Unit) {
    Card(
        onClick = onClick,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
        modifier = modifier,
    ) {
        Column(Modifier.padding(16.dp)) {
            Text(title.uppercase(locale()), style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 8.dp))
            if (sub.isNotBlank()) Text(sub, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Medium)
        }
    }
}
