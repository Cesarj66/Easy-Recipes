package org.fmz.easyrecipes.api.endpoints

import com.google.gson.annotations.SerializedName

class RecipeGetResponseData {

    @SerializedName("title")
    lateinit var title: String

    @SerializedName("ingredients")
    lateinit var ingredients: String

    @SerializedName("directions")
    lateinit var directions: String

    @SerializedName("image")
    var image: Int = -1

}