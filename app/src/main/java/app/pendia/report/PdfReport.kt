package app.pendia.report

import android.content.Context
import android.content.Intent
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.graphics.pdf.PdfDocument
import androidx.core.content.FileProvider
import app.pendia.R
import app.pendia.data.AppData
import app.pendia.data.Catalog
import app.pendia.data.GlucoseKind
import app.pendia.data.GlucoseUnit
import app.pendia.data.SideEffect
import app.pendia.data.Site
import app.pendia.ui.Fmt
import java.io.File
import java.time.LocalDate
import java.time.LocalDateTime

/** Erstellt einen einfachen A4-Bericht für die Arztpraxis. */
object PdfReport {

    private const val W = 595
    private const val H = 842
    private const val M = 40f

    fun create(ctx: Context, data: AppData): File {
        val loc = ctx.resources.configuration.locales[0]
        val doc = PdfDocument()
        var pageNo = 0
        var page: PdfDocument.Page? = null
        var c: Canvas? = null
        var y = 0f

        fun paint(size: Float, bold: Boolean = false, color: Int = 0xFF1B1F1F.toInt()) = Paint().apply {
            textSize = size
            isAntiAlias = true
            this.color = color
            typeface = if (bold) Typeface.DEFAULT_BOLD else Typeface.DEFAULT
        }
        val title = paint(20f, true, 0xFF0F5E63.toInt())
        val h2 = paint(13f, true, 0xFF0F5E63.toInt())
        val body = paint(9.5f)
        val head = paint(9.5f, true)
        val small = paint(8f, color = 0xFF5F6B6B.toInt())
        val rule = Paint().apply { color = 0xFFD5DCDB.toInt(); strokeWidth = 0.7f }

        fun newPage() {
            page?.let { doc.finishPage(it) }
            pageNo++
            val p = doc.startPage(PdfDocument.PageInfo.Builder(W, H, pageNo).create())
            page = p
            c = p.canvas
            y = M
            c!!.drawText("Pendia · $pageNo", W - M - 50f, H - 20f, small)
        }

        fun ensure(h: Float) {
            if (y + h > H - M) newPage()
        }

        fun fit(text: String, p: Paint, max: Float): String {
            if (p.measureText(text) <= max) return text
            var t = text
            while (t.isNotEmpty() && p.measureText("$t…") > max) t = t.dropLast(1)
            return "$t…"
        }

        fun line(text: String, p: Paint = body, gapBefore: Float = 0f) {
            y += gapBefore
            ensure(p.textSize + 5)
            y += p.textSize + 5
            c!!.drawText(fit(text, p, W - 2 * M), M, y, p)
        }

        fun row(cols: List<String>, xs: List<Float>, p: Paint = body) {
            ensure(p.textSize + 6)
            y += p.textSize + 6
            cols.forEachIndexed { i, s ->
                val right = if (i + 1 < xs.size) M + xs[i + 1] - 8 else W - M
                c!!.drawText(fit(s, p, right - (M + xs[i])), M + xs[i], y, p)
            }
        }

        fun section(name: String) {
            y += 14
            ensure(40f)
            line(name, h2)
            y += 3
            c!!.drawLine(M, y, W - M, y, rule)
        }

        newPage()
        line(ctx.getString(R.string.report_title), title)
        line(ctx.getString(R.string.report_created, Fmt.date(LocalDate.now(), loc)), small, 4f)
        line(ctx.getString(R.string.report_disclaimer), small)

        val other = ctx.getString(R.string.med_other)

        section(ctx.getString(R.string.report_meds))
        if (data.medications.isEmpty()) line(ctx.getString(R.string.report_none))
        data.medications.sortedBy { it.startDate }.forEach { m ->
            val since = ctx.getString(R.string.report_since, Fmt.date(LocalDate.parse(m.startDate), loc))
            val until = m.endDate?.let { ", " + ctx.getString(R.string.report_until, Fmt.date(LocalDate.parse(it), loc)) } ?: ""
            line("• ${Catalog.displayName(m, other)}: $since$until")
        }

        section(ctx.getString(R.string.report_injections))
        val injections = data.injections.sortedByDescending { it.dateTime }
        if (injections.isEmpty()) line(ctx.getString(R.string.report_none))
        else {
            val xs = listOf(0f, 105f, 215f, 275f, 390f)
            row(
                listOf(
                    ctx.getString(R.string.entry_date), ctx.getString(R.string.settings_med),
                    ctx.getString(R.string.entry_dose), ctx.getString(R.string.entry_site), ctx.getString(R.string.entry_note)
                ), xs, head
            )
            injections.forEach { inj ->
                val med = data.medications.firstOrNull { it.id == inj.medicationId }
                row(
                    listOf(
                        Fmt.dateTime(LocalDateTime.parse(inj.dateTime), loc),
                        med?.let { Catalog.shortName(it, other) } ?: "–",
                        inj.doseMg?.let { Fmt.num(it, loc) + " mg" } ?: "–",
                        Site.of(inj.site)?.let { ctx.getString(it.label) } ?: "–",
                        inj.note,
                    ), xs
                )
            }
        }

        section(ctx.getString(R.string.report_weights))
        val weights = data.weights.sortedByDescending { it.date }
        if (weights.isEmpty()) line(ctx.getString(R.string.report_none))
        else {
            val xs = listOf(0f, 105f, 215f)
            row(listOf(ctx.getString(R.string.entry_date), ctx.getString(R.string.entry_weight_kg), ctx.getString(R.string.entry_waist)), xs, head)
            weights.forEach { w ->
                row(listOf(Fmt.date(LocalDate.parse(w.date), loc), Fmt.num(w.kg, loc, 1), w.waistCm?.let { Fmt.num(it, loc, 1) } ?: "–"), xs)
            }
        }

        section(ctx.getString(R.string.report_side_effects))
        val ses = data.sideEffects.sortedByDescending { it.date }
        if (ses.isEmpty()) line(ctx.getString(R.string.report_none))
        else {
            val xs = listOf(0f, 105f, 260f, 330f)
            row(listOf(ctx.getString(R.string.entry_date), ctx.getString(R.string.entry_se_type), ctx.getString(R.string.entry_severity), ctx.getString(R.string.entry_note)), xs, head)
            val sev = listOf(R.string.severity_1, R.string.severity_2, R.string.severity_3)
            ses.forEach { s ->
                row(
                    listOf(
                        Fmt.date(LocalDate.parse(s.date), loc),
                        ctx.getString(SideEffect.of(s.type).label),
                        ctx.getString(sev[(s.severity - 1).coerceIn(0, 2)]),
                        s.note,
                    ), xs
                )
            }
        }

        if (data.glucose.isNotEmpty() || data.diabetesMode) {
            section(ctx.getString(R.string.report_glucose))
            val gl = data.glucose.sortedByDescending { it.dateTime }
            if (gl.isEmpty()) line(ctx.getString(R.string.report_none))
            else {
                val xs = listOf(0f, 105f, 215f, 300f)
                row(listOf(ctx.getString(R.string.entry_date), ctx.getString(R.string.entry_se_type), ctx.getString(R.string.glucose_value), ctx.getString(R.string.entry_note)), xs, head)
                gl.forEach { g ->
                    val kind = when (g.kind) {
                        GlucoseKind.FASTING -> R.string.glucose_fasting
                        GlucoseKind.HBA1C -> R.string.glucose_hba1c
                        else -> R.string.glucose_other
                    }
                    val unit = when {
                        g.kind == GlucoseKind.HBA1C -> "%"
                        g.unit == GlucoseUnit.MMOL -> ctx.getString(R.string.unit_mmol)
                        else -> ctx.getString(R.string.unit_mgdl)
                    }
                    val note = listOfNotNull(
                        if (g.hypo) ctx.getString(R.string.hypo_flag) else null,
                        g.note.ifBlank { null },
                    ).joinToString(" · ")
                    row(listOf(Fmt.dateTime(LocalDateTime.parse(g.dateTime), loc), ctx.getString(kind), Fmt.num(g.value, loc, 1) + " " + unit, note), xs)
                }
            }
        }

        page?.let { doc.finishPage(it) }
        val dir = File(ctx.cacheDir, "reports").apply { mkdirs() }
        dir.listFiles()?.forEach { it.delete() }
        val file = File(dir, "pendia_${LocalDate.now()}.pdf")
        file.outputStream().use { doc.writeTo(it) }
        doc.close()
        return file
    }

    fun share(ctx: Context, file: File) {
        val uri = FileProvider.getUriForFile(ctx, ctx.packageName + ".files", file)
        val send = Intent(Intent.ACTION_SEND).apply {
            type = "application/pdf"
            putExtra(Intent.EXTRA_STREAM, uri)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
        ctx.startActivity(Intent.createChooser(send, ctx.getString(R.string.report_pdf)))
    }
}
