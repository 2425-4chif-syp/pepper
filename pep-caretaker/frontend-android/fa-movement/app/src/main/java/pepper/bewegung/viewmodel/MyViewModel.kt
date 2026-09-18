package pepper.bewegung.viewmodel

import android.content.Context
import android.util.Log
import pepper.bewegung.R

class MyViewModel {

    fun getRawFileNames(context: Context): List<String> {
        val rawFiles = mutableListOf<String>()
        try {
            // Durch alle öffentlichen Felder der R.raw Klasse iterieren
            val fields = R.raw::class.java.fields
            for (field in fields) {
                // Jeder Feldname entspricht einer Datei im res/raw Ordner
                rawFiles.add(field.name)
            }
        } catch (e: Exception) {
            Log.e("ResourceHelper", "Fehler beim Abrufen der Raw-Dateinamen", e)
        }
        return rawFiles
    }
}
