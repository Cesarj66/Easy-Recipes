package org.fmz.easyrecipes.support.sqlite

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class SQLViewModel(private val db: SQLRepository) : ViewModel() {

    val recipes: LiveData<List<SQLRecipe>> = db.dao.getRecipes().asLiveData()

    fun getRecipeById(recipeId: Int): LiveData<SQLRecipe?> {
        db.log("Received a request to retrieve a recipe with the ID of $recipeId.")
        return db.dao.getRecipeById(recipeId).asLiveData()
    }

    fun addRecipe(id: Int, title: String, ingredients: String, directions: String, images: String) {
        db.log("Received a request to add a recipe to the local database.")
        viewModelScope.launch {
            db.dao.addRecipe(SQLRecipe(id, title, ingredients, directions, images))
        }
    }

    fun deleteRecipe(id: Int) {
        db.log("Received a request to remove a recipe $id from the local database.")
        viewModelScope.launch {
            db.dao.deleteRecipeById(id)
        }
    }

}