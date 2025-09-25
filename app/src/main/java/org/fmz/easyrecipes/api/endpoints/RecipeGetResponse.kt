package org.fmz.easyrecipes.api.endpoints

import com.google.gson.annotations.SerializedName
class RecipeGetResponse {

    @SerializedName("status")
    var status: Boolean = false

    @SerializedName("recipe")
    lateinit var recipe: RecipeGetResponseData

}