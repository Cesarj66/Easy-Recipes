package org.fmz.easyrecipes.support.sqlite

import android.util.Log
import androidx.fragment.app.Fragment
import androidx.room.Room
import androidx.room.RoomDatabase

private const val TAG = "SQL Repository"
private const val DATABASE_NAME = "recipe-db"

class SQLRepository(fragment: Fragment) {

    private var database: SQLDatabase

    init {

        database = Room.databaseBuilder(
            fragment.requireContext().applicationContext,
            SQLDatabase::class.java,
            DATABASE_NAME
        ).fallbackToDestructiveMigration().build()

        log("Loaded SQLite repository.")

    }

    val dao = database.dao()

    fun log(message: String) {
        Log.d(TAG, message)
    }

}