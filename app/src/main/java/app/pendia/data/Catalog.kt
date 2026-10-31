package app.pendia.data

import androidx.annotation.StringRes
import app.pendia.R

/**
 * Auswahlliste fürs Tagebuch. Die Stärken sind die in der EU zugelassenen
 * Pen-Stärken. Die App empfiehlt keine Dosis, sie bietet nur die Auswahl an.
 */
data class Product(
    val key: String,
    val brand: String,
    val substance: String,
    val intervalDays: Int,
    val doses: List<Double>,
)

object Catalog {
    const val OTHER = "other"

    val products = listOf(
        Product("sema_ozempic", "Ozempic", "Semaglutid", 7, listOf(0.25, 0.5, 1.0, 2.0)),
        Product("sema_wegovy", "Wegovy", "Semaglutid", 7, listOf(0.25, 0.5, 1.0, 1.7, 2.4, 7.2)),
        Product("tirz_mounjaro", "Mounjaro", "Tirzepatid", 7, listOf(2.5, 5.0, 7.5, 10.0, 12.5, 15.0)),
        Product("dula_trulicity", "Trulicity", "Dulaglutid", 7, listOf(0.75, 1.5, 3.0, 4.5)),
        Product("lira_saxenda", "Saxenda", "Liraglutid", 1, listOf(0.6, 1.2, 1.8, 2.4, 3.0)),
        Product("lira_victoza", "Victoza", "Liraglutid", 1, listOf(0.6, 1.2, 1.8)),
        Product(OTHER, "", "", 7, emptyList()),
    )

    fun product(key: String): Product = products.firstOrNull { it.key == key } ?: products.last()

    fun displayName(med: Medication, otherLabel: String): String {
        val p = product(med.productKey)
        return if (p.key == OTHER) med.customName.ifBlank { otherLabel } else "${p.brand} (${p.substance})"
    }

    fun shortName(med: Medication, otherLabel: String): String {
        val p = product(med.productKey)
        return if (p.key == OTHER) med.customName.ifBlank { otherLabel } else p.brand
    }
}

/** Körperkarte von vorne gesehen: die linke Körperseite liegt rechts im Bild. */
enum class Site(@StringRes val label: Int, val x: Float, val y: Float) {
    ARM_LEFT(R.string.site_arm_left, 0.80f, 0.33f),
    ARM_RIGHT(R.string.site_arm_right, 0.20f, 0.33f),
    BELLY_LEFT_UPPER(R.string.site_belly_left_upper, 0.58f, 0.40f),
    BELLY_RIGHT_UPPER(R.string.site_belly_right_upper, 0.42f, 0.40f),
    BELLY_LEFT_LOWER(R.string.site_belly_left_lower, 0.58f, 0.50f),
    BELLY_RIGHT_LOWER(R.string.site_belly_right_lower, 0.42f, 0.50f),
    THIGH_LEFT(R.string.site_thigh_left, 0.59f, 0.70f),
    THIGH_RIGHT(R.string.site_thigh_right, 0.41f, 0.70f);

    companion object {
        fun of(name: String?): Site? = entries.firstOrNull { it.name == name }
    }
}

enum class SideEffect(@StringRes val label: Int) {
    NAUSEA(R.string.se_nausea),
    VOMITING(R.string.se_vomiting),
    DIARRHEA(R.string.se_diarrhea),
    CONSTIPATION(R.string.se_constipation),
    HEARTBURN(R.string.se_heartburn),
    BLOATING(R.string.se_bloating),
    FATIGUE(R.string.se_fatigue),
    HEADACHE(R.string.se_headache),
    DIZZINESS(R.string.se_dizziness),
    INJECTION_SITE(R.string.se_injection_site),
    HAIR_LOSS(R.string.se_hair_loss),
    OTHER(R.string.se_other);

    companion object {
        fun of(name: String): SideEffect = entries.firstOrNull { it.name == name } ?: OTHER
    }
}
