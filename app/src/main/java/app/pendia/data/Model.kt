package app.pendia.data

import kotlinx.serialization.Serializable
import java.util.UUID

fun newId(): String = UUID.randomUUID().toString()

/**
 * Alle Daten der App. Liegen als eine JSON-Datei im privaten App-Speicher,
 * nie auf einem Server.
 */
@Serializable
data class AppData(
    val schema: Int = 1,
    val setupDone: Boolean = false,
    val disclaimerAccepted: Boolean = false,
    val diabetesMode: Boolean = false,
    val glucoseUnit: String = GlucoseUnit.MGDL,
    val medications: List<Medication> = emptyList(),
    val injections: List<Injection> = emptyList(),
    val weights: List<WeightEntry> = emptyList(),
    val sideEffects: List<SideEffectEntry> = emptyList(),
    val glucose: List<GlucoseEntry> = emptyList(),
    val reminder: Reminder = Reminder(),
    val pen: PenInfo = PenInfo(),
) {
    /** Das Medikament ohne Enddatum, sonst das zuletzt begonnene. */
    val activeMedication: Medication?
        get() = medications.lastOrNull { it.endDate == null } ?: medications.maxByOrNull { it.startDate }
}

@Serializable
data class Medication(
    val id: String = newId(),
    val productKey: String,
    val customName: String = "",
    /** ISO-Datum der ersten Spritze */
    val startDate: String,
    val endDate: String? = null,
    val intervalDays: Int = 7,
)

@Serializable
data class Injection(
    val id: String = newId(),
    /** ISO LocalDateTime */
    val dateTime: String,
    val medicationId: String,
    val doseMg: Double? = null,
    val site: String? = null,
    val note: String = "",
)

@Serializable
data class WeightEntry(
    val id: String = newId(),
    val date: String,
    val kg: Double,
    val waistCm: Double? = null,
)

@Serializable
data class SideEffectEntry(
    val id: String = newId(),
    val date: String,
    val type: String,
    /** 1 = leicht, 2 = mittel, 3 = stark */
    val severity: Int,
    val note: String = "",
)

@Serializable
data class GlucoseEntry(
    val id: String = newId(),
    val dateTime: String,
    val kind: String,
    /** Blutzucker in der gewählten Einheit, HbA1c in Prozent */
    val value: Double,
    val unit: String,
    val hypo: Boolean = false,
    val note: String = "",
)

object GlucoseUnit {
    const val MGDL = "mgdl"
    const val MMOL = "mmol"
}

object GlucoseKind {
    const val FASTING = "fasting"
    const val OTHER = "other"
    const val HBA1C = "hba1c"
}

@Serializable
data class Reminder(
    val enabled: Boolean = false,
    /** 1 = Montag … 7 = Sonntag (java.time.DayOfWeek) */
    val dayOfWeek: Int = 1,
    val hour: Int = 9,
    val minute: Int = 0,
)

@Serializable
data class PenInfo(
    val openedDate: String? = null,
    val unrefrigeratedSince: String? = null,
    /** Haltbarkeit nach Anbruch laut Packungsbeilage, vom Nutzer eingetragen */
    val limitDays: Int? = null,
)
