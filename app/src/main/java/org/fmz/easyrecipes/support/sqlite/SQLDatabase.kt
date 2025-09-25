package org.fmz.easyrecipes.support.sqlite

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [SQLRecipe::class], version = 1)
abstract class SQLDatabase : RoomDatabase() {
    abstract fun dao(): SQLDao

}
