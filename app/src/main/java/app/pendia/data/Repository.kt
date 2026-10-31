package app.pendia.data

import android.content.Context
import android.util.AtomicFile
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.serialization.json.Json
import java.io.File

/**
 * Speichert alles in einer Datei im privaten App-Speicher.
 * Schreibt atomar, damit bei Absturz oder leerem Akku nichts kaputtgeht.
 */
class Repository(context: Context) {

    private val file = AtomicFile(File(context.filesDir, "pendia.json"))
    private val _data = MutableStateFlow(load())
    val data: StateFlow<AppData> = _data.asStateFlow()

    private fun load(): AppData = try {
        if (file.baseFile.exists()) json.decodeFromString(AppData.serializer(), String(file.readFully(), Charsets.UTF_8))
        else AppData()
    } catch (e: Exception) {
        AppData()
    }

    @Synchronized
    fun update(transform: (AppData) -> AppData) {
        val next = transform(_data.value)
        write(next)
        _data.value = next
    }

    private fun write(d: AppData) {
        val out = file.startWrite()
        try {
            out.write(json.encodeToString(AppData.serializer(), d).toByteArray(Charsets.UTF_8))
            file.finishWrite(out)
        } catch (e: Exception) {
            file.failWrite(out)
            throw e
        }
    }

    fun exportJson(): String = json.encodeToString(AppData.serializer(), _data.value)

    /** Wirft eine Ausnahme, wenn die Datei kein gültiges Pendia-Backup ist. */
    fun importJson(text: String) {
        val parsed = json.decodeFromString(AppData.serializer(), text)
        update { parsed }
    }

    fun deleteAll() {
        update { AppData() }
    }

    companion object {
        val json = Json {
            ignoreUnknownKeys = true
            encodeDefaults = true
            prettyPrint = true
        }
    }
}
