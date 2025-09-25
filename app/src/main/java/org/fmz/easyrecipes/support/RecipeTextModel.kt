package org.fmz.easyrecipes.support

import android.content.Context
import android.util.Log
import java.io.File

class RecipeTextModel(context: Context) {

    private val logTag = "Text Model"

    private var c = context

    private fun makeFile(filename: String): File {
        return File(c.filesDir, filename)
    }

    fun saveText(s: String, filename: String) {
        val file = this.makeFile(filename)
        Log.v(logTag, "Saving to $file")
        file.delete()
        file.writeText(s)
    }

    fun loadText(filename: String): String {
        val file = this.makeFile(filename)
        val s: String = if (file.exists()) {
            file.readText()
        } else{
            ""
        }
        return s
    }
}