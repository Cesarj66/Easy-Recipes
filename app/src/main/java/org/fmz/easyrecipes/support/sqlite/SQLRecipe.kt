package org.fmz.easyrecipes.support.sqlite

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recipes")
data class SQLRecipe (
    @PrimaryKey val id: Int,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "ingredients") val ingredients: String,
    @ColumnInfo(name = "directions") val directions: String,
    @ColumnInfo(name = "image") val image: String
)