package org.fmz.easyrecipes.api.endpoints

import com.google.gson.annotations.SerializedName

import org.fmz.easyrecipes.support.ds.SimpleRecipe

class RecipeListResponse {

    @SerializedName("status")
    var status: Boolean = false

    @SerializedName("recipes")
    var ids: List<SimpleRecipe>? = null

    fun getRecipes(): List<SimpleRecipe>? {
        return ids
    }

}