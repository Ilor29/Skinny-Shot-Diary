package app.pendia

import android.graphics.Bitmap
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import app.pendia.data.AppData
import app.pendia.data.GlucoseEntry
import app.pendia.data.GlucoseKind
import app.pendia.data.Injection
import app.pendia.data.Medication
import app.pendia.data.PenInfo
import app.pendia.data.Reminder
import app.pendia.data.SideEffectEntry
import app.pendia.data.Site
import app.pendia.data.WeightEntry
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.io.File
import java.time.LocalDate

/**
 * Startet die echten Bildschirme in Robolectric, klickt sich durch
 * und speichert Screenshots nach app/build/screens.
 */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [34], qualifiers = "de-rDE-w400dp-h860dp-xxhdpi")
class ScreenshotTest {

    @get:Rule
    val rule = createAndroidComposeRule<MainActivity>()

    private fun shot(name: String) {
        rule.waitForIdle()
        var bmp: Bitmap? = null
        rule.runOnUiThread {
            val view = rule.activity.window.decorView
            bmp = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888).also { view.draw(android.graphics.Canvas(it)) }
        }
        val dir = File("build/screens").apply { mkdirs() }
        File(dir, "$name.png").outputStream().use { bmp!!.compress(Bitmap.CompressFormat.PNG, 100, it) }
    }

    private fun seed() {
        val today = LocalDate.now()
        val start = today.minusWeeks(10)
        val med = Medication(productKey = "tirz_mounjaro", startDate = start.toString())
        val sites = listOf(Site.BELLY_LEFT_LOWER, Site.BELLY_RIGHT_LOWER, Site.THIGH_LEFT, Site.THIGH_RIGHT, Site.BELLY_LEFT_UPPER)
        val injections = (0..9).map { w ->
            Injection(
                dateTime = start.plusWeeks(w.toLong()).atTime(19, 30).toString(),
                medicationId = med.id,
                doseMg = if (w < 4) 2.5 else if (w < 8) 5.0 else 7.5,
                site = sites[w % sites.size].name,
            )
        }
        val weights = (0..10).map { w -> WeightEntry(date = start.plusWeeks(w.toLong()).toString(), kg = 104.0 - w * 0.9 - (w % 3) * 0.2) }
        rule.activity.repo.update {
            AppData(
                setupDone = true, disclaimerAccepted = true, diabetesMode = true,
                medications = listOf(med), injections = injections, weights = weights,
                sideEffects = listOf(
                    SideEffectEntry(date = today.minusDays(3).toString(), type = "NAUSEA", severity = 2, note = "nach dem Abendessen"),
                    SideEffectEntry(date = today.minusDays(10).toString(), type = "CONSTIPATION", severity = 1),
                ),
                glucose = listOf(
                    GlucoseEntry(dateTime = today.minusDays(14).atTime(7, 0).toString(), kind = GlucoseKind.FASTING, value = 142.0, unit = "mgdl"),
                    GlucoseEntry(dateTime = today.minusDays(7).atTime(7, 0).toString(), kind = GlucoseKind.FASTING, value = 128.0, unit = "mgdl"),
                    GlucoseEntry(dateTime = today.minusDays(1).atTime(7, 0).toString(), kind = GlucoseKind.FASTING, value = 117.0, unit = "mgdl"),
                    GlucoseEntry(dateTime = today.minusDays(5).atTime(9, 0).toString(), kind = GlucoseKind.HBA1C, value = 7.1, unit = "percent"),
                ),
                reminder = Reminder(true, start.dayOfWeek.value, 19, 0),
                pen = PenInfo(openedDate = today.minusDays(5).toString(), limitDays = 21),
            )
        }
    }

    @Test
    fun setupFlow() {
        shot("01_einrichtung_1")
        rule.onNodeWithText("Ich habe das gelesen und verstanden").performClick()
        rule.onNodeWithText("Weiter").performClick()
        shot("02_einrichtung_2")
        rule.onNodeWithText("Weiter").performClick()
        shot("03_einrichtung_3")
        rule.onNodeWithText("Pendia starten").performClick()
        rule.waitForIdle()
        rule.onNodeWithText("Nächste Spritze").assertExists()
        shot("04_start_leer")
    }

    @Test
    fun mainScreens() {
        seed()
        shot("10_start")
        rule.onNodeWithText("Tagebuch").performClick()
        shot("11_tagebuch")
        rule.onNodeWithText("Verlauf").performClick()
        shot("12_verlauf")
        rule.onNodeWithText("Wissen").performClick()
        shot("13_wissen")
        rule.onNodeWithText("Spritze vergessen – was jetzt?").performClick()
        shot("14_wissen_eintrag")
        rule.onNodeWithContentDescription("Zurück").performClick()
        rule.onNodeWithText("Start").performClick()
        rule.onNodeWithText("Spritze eintragen").performClick()
        shot("15_spritze_eintragen")
        rule.onNodeWithText("Bauch links oben").performScrollTo().performClick()
        rule.onNodeWithText("Speichern").performClick()
        rule.waitForIdle()
        rule.onNodeWithText("Heute").assertExists()
        rule.onNodeWithContentDescription("Einstellungen").performClick()
        shot("16_einstellungen")
    }

    @Test
    @Config(qualifiers = "en-rGB-w400dp-h860dp-xxhdpi")
    fun englishScreens() {
        seed()
        shot("20_home_en")
        rule.onNodeWithText("Knowledge").performClick()
        shot("21_knowledge_en")
    }
}
