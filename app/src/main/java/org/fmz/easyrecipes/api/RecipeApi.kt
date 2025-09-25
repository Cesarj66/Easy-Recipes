package org.fmz.easyrecipes.api

import org.fmz.easyrecipes.api.endpoints.LoginResponse
import org.fmz.easyrecipes.api.endpoints.LogoutResponse
import org.fmz.easyrecipes.api.endpoints.RecipeAddResponse
import org.fmz.easyrecipes.api.endpoints.RecipeDeleteResponse
import org.fmz.easyrecipes.api.endpoints.RecipeGetResponse
import org.fmz.easyrecipes.api.endpoints.RecipeListResponse
import org.fmz.easyrecipes.api.endpoints.RegisterResponse
import org.fmz.easyrecipes.api.endpoints.VerifyResponse
import org.fmz.easyrecipes.support.ds.CompleteRecipe

import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface RecipeApi {

    @GET("/user/register")
    fun register(
        @Query("username") username: String,
        @Query("password") password: String
    ): Call<RegisterResponse>

    @GET("/user/login")
    fun login(
        @Query("username") username: String,
        @Query("password") password: String
    ): Call<LoginResponse>

    @GET("/user/logout")
    fun logout(
        @Header("Authorization") token: String
    ): Call<LogoutResponse>

    @GET("/verify")
    fun verify(
        @Header("Authorization") token: String
    ): Call<VerifyResponse>

    @POST("/recipes/add")
    fun addRecipe(
        @Header("Authorization") token: String,
        @Body recipe: CompleteRecipe
    ): Call<RecipeAddResponse>

    @GET("/recipes/delete/{id}")
    fun deleteRecipe(
        @Path("id") id: Int,
        @Header("Authorization") token: String
    ): Call<RecipeDeleteResponse>

    @GET("/recipes/list")
    fun listRecipes(
        @Header("Authorization") token: String
    ): Call<RecipeListResponse>

    @GET("/recipes/get/{id}")
    fun getRecipe(
        @Path("id") id: Int,
        @Header("Authorization") token: String
    ): Call<RecipeGetResponse>

}