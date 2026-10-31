package app.pendia.ui

import androidx.activity.compose.BackHandler
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import app.pendia.R
import app.pendia.data.Repository
import app.pendia.ui.screens.ChangeMedicationScreen
import app.pendia.ui.screens.DiaryScreen
import app.pendia.ui.screens.FaqDetailScreen
import app.pendia.ui.screens.GlucoseEntryScreen
import app.pendia.ui.screens.HomeScreen
import app.pendia.ui.screens.InjectionEntryScreen
import app.pendia.ui.screens.KnowledgeScreen
import app.pendia.ui.screens.ProgressScreen
import app.pendia.ui.screens.SettingsScreen
import app.pendia.ui.screens.SetupScreen
import app.pendia.ui.screens.SideEffectEntryScreen
import app.pendia.ui.screens.WeightEntryScreen
import app.pendia.ui.screens.findFaq

private data class Tab(val label: Int, val icon: ImageVector)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PendiaRoot(repo: Repository) {
    val data by repo.data.collectAsStateWithLifecycle()
    if (!data.setupDone) {
        SetupScreen()
        return
    }

    var tab by rememberSaveable { mutableIntStateOf(0) }
    // Ein Stapel aus Bildschirm-Schlüsseln über den Tabs, z. B. "settings" → "faq:about"
    var stack by rememberSaveable { mutableStateOf(listOf<String>()) }
    val open: (String) -> Unit = { stack = stack + it }
    val close: () -> Unit = { stack = stack.dropLast(1) }
    BackHandler(enabled = stack.isNotEmpty()) { close() }

    val top = stack.lastOrNull()
    when {
        top == "injection" -> InjectionEntryScreen(data, close)
        top == "weight" -> WeightEntryScreen(close)
        top == "side" -> SideEffectEntryScreen(close)
        top == "glucose" -> GlucoseEntryScreen(data, close)
        top == "settings" -> SettingsScreen(data, close, open)
        top == "changeMed" -> ChangeMedicationScreen(data, close)
        top != null && top.startsWith("faq:") -> {
            val item = findFaq(top.removePrefix("faq:"))
            if (item != null) FaqDetailScreen(item, close) else LaunchedEffect(top) { close() }
        }
        else -> {
            val tabs = listOf(
                Tab(R.string.nav_home, Icons.Filled.Home),
                Tab(R.string.nav_diary, Icons.AutoMirrored.Filled.List),
                Tab(R.string.nav_progress, ChartIcon),
                Tab(R.string.nav_knowledge, Icons.Filled.Info),
            )
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text(if (tab == 0) stringResource(R.string.app_name) else stringResource(tabs[tab].label)) },
                        actions = {
                            IconButton(onClick = { open("settings") }) {
                                Icon(Icons.Filled.Settings, contentDescription = stringResource(R.string.settings))
                            }
                        },
                    )
                },
                bottomBar = {
                    NavigationBar {
                        tabs.forEachIndexed { i, t ->
                            NavigationBarItem(
                                selected = tab == i,
                                onClick = { tab = i },
                                icon = { Icon(t.icon, contentDescription = null) },
                                label = { Text(stringResource(t.label)) },
                            )
                        }
                    }
                },
            ) { pad ->
                when (tab) {
                    0 -> HomeScreen(data, pad, open)
                    1 -> DiaryScreen(data, pad, open)
                    2 -> ProgressScreen(data, pad)
                    else -> KnowledgeScreen(pad, open)
                }
            }
        }
    }
}
