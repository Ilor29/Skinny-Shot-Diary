package app.pendia

import android.graphics.Bitmap
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.pendia.data.AppData
import app.pendia.data.Injection
import app.pendia.data.Medication
import app.pendia.data.PenInfo
import app.pendia.data.Site
import app.pendia.data.WeightEntry
import app.pendia.ui.PendiaRoot
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.Shadows.shadowOf
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import android.os.Looper
import java.io.File
import java.time.LocalDate

// ---- Palette aus dem Vorschlag (Salbei & Soft Teal), unverändert übernommen ----
private val LightColors = lightColorScheme(
    primary = Color(0xFF2D6A4F),
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD8F3DC),
    onPrimaryContainer = Color(0xFF081C15),
    secondary = Color(0xFF1A759F),
    onSecondary = Color.White,
    surface = Color(0xFFF8F9FA),
    onSurface = Color(0xFF212529),
    error = Color(0xFFBA1A1A),
    onError = Color.White,
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF52B788),
    onPrimary = Color(0xFF081C15),
    primaryContainer = Color(0xFF1B4332),
    onPrimaryContainer = Color(0xFFD8F3DC),
    secondary = Color(0xFF34A0A4),
    onSecondary = Color(0xFF003543),
    surface = Color(0xFF121212),
    onSurface = Color(0xFFE3E3E3),
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
)

@Composable
private fun HealthAppTheme(darkTheme: Boolean, content: @Composable () -> Unit) {
    MaterialTheme(colorScheme = if (darkTheme) DarkColors else LightColors, content = content)
}

// ---- Dashboard aus dem Vorschlag, unverändert bis auf das AutoMirrored-List-Icon ----
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun HealthDashboardScreen() {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text("Gesundheit", style = MaterialTheme.typography.titleLarge) },
                actions = {
                    IconButton(onClick = { }) {
                        Icon(Icons.Default.Notifications, contentDescription = "Benachrichtigungen")
                    }
                },
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(selected = true, onClick = {}, icon = { Icon(Icons.Default.Home, null) }, label = { Text("Übersicht") })
                NavigationBarItem(selected = false, onClick = {}, icon = { Icon(Icons.AutoMirrored.Filled.List, null) }, label = { Text("Protokoll") })
                NavigationBarItem(selected = false, onClick = {}, icon = { Icon(Icons.Default.Person, null) }, label = { Text("Profil") })
            }
        },
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(innerPadding).padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Text("NÄCHSTE EINNAHME", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.7f))
                        Spacer(modifier = Modifier.height(4.dp))
                        Text("Inhalator & Herztablette", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text("Heute um 14:00 Uhr (In 2 Stunden)", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = { }, colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)) {
                            Text("Als eingenommen markieren")
                        }
                    }
                }
            }
            item { Text("Deine Vitalwerte", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.SemiBold) }
            item {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                    VitalStatCard("Puls", "72 bpm", "Normal", Modifier.weight(1.5f))
                    VitalStatCard("Blutdruck", "145/90", "Erhöht", Modifier.weight(1.5f), isCritical = true)
                }
            }
            item {
                OutlinedCard(modifier = Modifier.fillMaxWidth()) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Column {
                            Text("Schlafdauer", style = MaterialTheme.typography.labelLarge)
                            Text("7 Std. 42 Min.", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        }
                        Text("88% Schlafqualität", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.secondary)
                    }
                }
            }
        }
    }
}

@Composable
private fun VitalStatCard(title: String, value: String, status: String, modifier: Modifier = Modifier, isCritical: Boolean = false) {
    val statusColor = if (isCritical) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.4f)), modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Icon(Icons.Default.Favorite, contentDescription = null, tint = statusColor, modifier = Modifier.size(16.dp))
                Text(text = title, style = MaterialTheme.typography.labelMedium)
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = value, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = status, style = MaterialTheme.typography.bodySmall, color = statusColor, fontWeight = FontWeight.Medium)
        }
    }
}

/** Rendert den Vorschlag und Pendia mit der Salbei-Palette nach app/build/mockups. */
@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [34], qualifiers = "de-rDE-w400dp-h860dp-xxhdpi")
class MockupTest {

    private fun render(name: String, content: @Composable () -> Unit, before: (ComponentActivity) -> Unit = {}) {
        val controller = Robolectric.buildActivity(ComponentActivity::class.java).setup()
        val activity = controller.get()
        before(activity)
        activity.setContent { content() }
        repeat(5) { shadowOf(Looper.getMainLooper()).idle() }
        val view = activity.window.decorView
        val bmp = Bitmap.createBitmap(view.width, view.height, Bitmap.Config.ARGB_8888)
        view.draw(android.graphics.Canvas(bmp))
        val dir = File("build/mockups").apply { mkdirs() }
        File(dir, "$name.png").outputStream().use { bmp.compress(Bitmap.CompressFormat.PNG, 100, it) }
        controller.pause().stop().destroy()
    }

    @Test
    fun vorschlagHell() = render("01_vorschlag_hell", { HealthAppTheme(false) { HealthDashboardScreen() } })

    @Test
    fun vorschlagDunkel() = render("02_vorschlag_dunkel", { HealthAppTheme(true) { HealthDashboardScreen() } })

    @Test
    fun pendiaMitSalbei() = render("03_pendia_v2_hell", { app.pendia.ui.theme.PendiaTheme(false) { PendiaRoot(app().repo) } }) { seed() }

    @Test
    fun pendiaMitSalbeiDunkel() = render("04_pendia_v2_dunkel", { app.pendia.ui.theme.PendiaTheme(true) { PendiaRoot(app().repo) } }) { seed() }

    private fun app() = org.robolectric.RuntimeEnvironment.getApplication()

    private fun seed() {
        val today = LocalDate.now()
        val start = today.minusWeeks(10)
        val med = Medication(productKey = "tirz_mounjaro", startDate = start.toString())
        val sites = listOf(Site.BELLY_LEFT_LOWER, Site.BELLY_RIGHT_LOWER, Site.THIGH_LEFT, Site.THIGH_RIGHT, Site.BELLY_LEFT_UPPER)
        app().repo.update {
            AppData(
                setupDone = true, disclaimerAccepted = true,
                medications = listOf(med),
                injections = (0..9).map { w ->
                    Injection(
                        dateTime = start.plusWeeks(w.toLong()).atTime(19, 30).toString(), medicationId = med.id,
                        doseMg = if (w < 4) 2.5 else if (w < 8) 5.0 else 7.5, site = sites[w % sites.size].name,
                    )
                },
                weights = (0..10).map { w -> WeightEntry(date = start.plusWeeks(w.toLong()).toString(), kg = 104.0 - w * 0.9) },
                pen = PenInfo(openedDate = today.minusDays(5).toString(), limitDays = 21),
            )
        }
    }
}
