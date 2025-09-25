package org.fmz.easyrecipes.api.endpoints

import com.google.gson.annotations.SerializedName

class RecipeAddResponse {

    @SerializedName("status")
    var status: Boolean = false

    @SerializedName("id")
    var id: Int = 0

}