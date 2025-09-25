package org.fmz.easyrecipes.api.endpoints

import com.google.gson.annotations.SerializedName

class LoginResponse {

    @SerializedName("status")
    var status: Boolean = false

    @SerializedName("token")
    lateinit var token: String

    @SerializedName("message")
    lateinit var message: String

}