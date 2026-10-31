package app.pendia.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.selection.SelectionContainer
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import app.pendia.R
import app.pendia.data.Faq
import app.pendia.data.FaqItem
import app.pendia.ui.FormScreen
import app.pendia.ui.isGerman

@Composable
fun KnowledgeScreen(pad: PaddingValues, open: (String) -> Unit) {
    val de = isGerman()
    LazyColumn(Modifier.fillMaxSize().padding(pad), contentPadding = PaddingValues(vertical = 8.dp)) {
        item {
            Card(
                onClick = { open("faq:" + Faq.emergency.id) },
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer),
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp),
            ) {
                Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Warning, contentDescription = null)
                    Text(Faq.emergency.title(de), modifier = Modifier.padding(start = 12.dp), fontWeight = FontWeight.SemiBold)
                }
            }
        }
        items(Faq.items, key = { it.id }) { item ->
            ListItem(
                headlineContent = { Text(item.title(de)) },
                trailingContent = { Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null) },
                modifier = Modifier.clickable { open("faq:" + item.id) },
            )
            HorizontalDivider()
        }
        item {
            Text(
                stringResource(R.string.knowledge_asof, Faq.AS_OF),
                style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(16.dp),
            )
        }
    }
}

fun findFaq(id: String): FaqItem? = if (id == Faq.emergency.id) Faq.emergency else Faq.items.firstOrNull { it.id == id }

@Composable
fun FaqDetailScreen(item: FaqItem, onBack: () -> Unit) {
    val de = isGerman()
    FormScreen(title = item.title(de), onBack = onBack, onSave = null) {
        SelectionContainer {
            Text(item.body(de), style = MaterialTheme.typography.bodyLarge)
        }
        if (item.sources.isNotEmpty()) {
            Text(stringResource(R.string.sources), style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
            SelectionContainer {
                Text(item.sources.joinToString("\n\n"), style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
        }
        Text(
            stringResource(R.string.knowledge_asof, Faq.AS_OF),
            style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
    }
}
