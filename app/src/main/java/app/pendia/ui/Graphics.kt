package app.pendia.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import app.pendia.data.Site
import app.pendia.ui.theme.Coral
import kotlin.math.hypot

/**
 * Körperkarte von vorne. Ausgewählte Stelle in der Hauptfarbe,
 * die letzten Stellen in Koralle (die neueste am kräftigsten).
 */
@Composable
fun BodyMap(
    selected: Site?,
    recent: List<Site>,
    modifier: Modifier = Modifier,
    description: String = "",
    onSelect: ((Site) -> Unit)? = null,
) {
    val body = MaterialTheme.colorScheme.surfaceVariant
    val outline = MaterialTheme.colorScheme.outline
    val primary = MaterialTheme.colorScheme.primary
    val label = MaterialTheme.colorScheme.onSurfaceVariant
    val measurer = rememberTextMeasurer()
    val tap = if (onSelect != null) Modifier.pointerInput(Unit) {
        detectTapGestures { p ->
            val w = size.width.toFloat()
            val h = size.height.toFloat()
            val nearest = Site.entries.minBy { hypot(it.x * w - p.x, it.y * h - p.y) }
            if (hypot(nearest.x * w - p.x, nearest.y * h - p.y) < w * 0.14f) onSelect(nearest)
        }
    } else Modifier

    Canvas(
        modifier
            .aspectRatio(0.62f)
            .semantics { contentDescription = description }
            .then(tap)
    ) {
        val w = size.width
        val h = size.height
        fun rr(x1: Float, y1: Float, x2: Float, y2: Float, r: Float) = drawRoundRect(
            body, topLeft = Offset(x1 * w, y1 * h), size = Size((x2 - x1) * w, (y2 - y1) * h), cornerRadius = CornerRadius(r * w)
        )
        drawCircle(body, radius = 0.075f * w, center = Offset(0.5f * w, 0.08f * h))
        rr(0.32f, 0.165f, 0.68f, 0.585f, 0.09f)
        rr(0.13f, 0.18f, 0.29f, 0.52f, 0.07f)
        rr(0.71f, 0.18f, 0.87f, 0.52f, 0.07f)
        rr(0.33f, 0.56f, 0.495f, 0.98f, 0.07f)
        rr(0.505f, 0.56f, 0.67f, 0.98f, 0.07f)

        val style = TextStyle(fontSize = 13.sp, color = label)
        drawText(measurer, "R", Offset(0.03f * w, 0.02f * h), style)
        drawText(measurer, "L", Offset(0.92f * w, 0.02f * h), style)

        val r = 0.05f * w
        Site.entries.forEach { s ->
            val c = Offset(s.x * w, s.y * h)
            val idx = recent.indexOf(s)
            when {
                s == selected -> {
                    drawCircle(primary, r * 1.15f, c)
                    drawCircle(androidx.compose.ui.graphics.Color.White, r * 0.4f, c)
                }
                idx >= 0 -> drawCircle(Coral.copy(alpha = 1f - idx * 0.22f), r, c)
                else -> drawCircle(outline, r * 0.8f, c, style = Stroke(width = 2.dp.toPx()))
            }
        }
    }
}

/** Einfache Linienkurve. Beschriftet kleinsten und größten Wert. */
@Composable
fun LineChart(values: List<Double>, labelMin: String, labelMax: String, modifier: Modifier = Modifier, description: String = "") {
    val line = MaterialTheme.colorScheme.primary
    val grid = MaterialTheme.colorScheme.outlineVariant
    val text = MaterialTheme.colorScheme.onSurfaceVariant
    val measurer = rememberTextMeasurer()
    Canvas(modifier.fillMaxWidth().height(180.dp).semantics { contentDescription = description }) {
        if (values.size < 2) return@Canvas
        val left = 8.dp.toPx()
        val top = 22.dp.toPx()
        val bottom = size.height - 22.dp.toPx()
        val right = size.width - 8.dp.toPx()
        val min = values.min()
        val max = values.max()
        val span = (max - min).takeIf { it > 0.0001 } ?: 1.0
        fun x(i: Int) = left + (right - left) * i / (values.size - 1)
        fun y(v: Double) = (bottom - (bottom - top) * ((v - min) / span)).toFloat()

        drawLine(grid, Offset(left, top), Offset(right, top))
        drawLine(grid, Offset(left, bottom), Offset(right, bottom))
        val style = TextStyle(fontSize = 11.sp, color = text)
        drawText(measurer, labelMax, Offset(left, 2.dp.toPx()), style)
        drawText(measurer, labelMin, Offset(left, bottom + 4.dp.toPx()), style)

        val path = Path()
        values.forEachIndexed { i, v -> if (i == 0) path.moveTo(x(i), y(v)) else path.lineTo(x(i), y(v)) }
        drawPath(path, line, style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round))
        values.forEachIndexed { i, v -> drawCircle(line, 3.5.dp.toPx(), Offset(x(i), y(v))) }
    }
}
