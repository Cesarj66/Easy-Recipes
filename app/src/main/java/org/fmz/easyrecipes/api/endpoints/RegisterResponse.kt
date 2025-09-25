package org.fmz.easyrecipes.api.endpoints

import com.google.gson.annotations.SerializedName

class RegisterResponse {
    @SerializedName("status")
    var status: Boolean = false

    @SerializedName("message")
    lateinit var message: String

}