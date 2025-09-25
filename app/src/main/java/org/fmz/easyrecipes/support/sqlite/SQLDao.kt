package org.fmz.easyrecipes.support.sqlite

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

import kotlinx.coroutines.flow.Flow

@Dao
interface SQLDao {

    @Query("SELECT * FROM recipes WHERE id = :recipeId")
    fun getRecipeById(recipeId: Int): Flow<SQLRecipe?>

    @Query("SELECT * FROM recipes")
    fun getRecipes(): Flow<List<SQLRecipe>>

    @Query("DELETE FROM recipes WHERE id = :recipeId")
    suspend fun deleteRecipeById(recipeId: Int)

    @Insert
    suspend fun addRecipe(recipe: SQLRecipe)

}
