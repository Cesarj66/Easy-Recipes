package org.fmz.easyrecipes.api

import android.util.Log
import android.view.View

import androidx.appcompat.app.AlertDialog
import org.fmz.easyrecipes.R

import org.fmz.easyrecipes.activities.LoginActivity
import org.fmz.easyrecipes.activities.MainActivity
import org.fmz.easyrecipes.fragments.RecipeAddFragment
import org.fmz.easyrecipes.api.endpoints.LoginResponse
import org.fmz.easyrecipes.api.endpoints.LogoutResponse
import org.fmz.easyrecipes.api.endpoints.RecipeAddResponse
import org.fmz.easyrecipes.api.endpoints.RecipeDeleteResponse
import org.fmz.easyrecipes.api.endpoints.RecipeGetResponse
import org.fmz.easyrecipes.api.endpoints.RecipeGetResponseData
import org.fmz.easyrecipes.api.endpoints.RecipeListResponse
import org.fmz.easyrecipes.api.endpoints.RegisterResponse
import org.fmz.easyrecipes.api.endpoints.VerifyResponse
import org.fmz.easyrecipes.fragments.RecipeListFragment
import org.fmz.easyrecipes.support.ds.CompleteRecipe
import org.fmz.easyrecipes.support.RecipeViewModel

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class RecipeApiExecutor {

    private val logTag = "API Communicator"

    private val api: RecipeApi

    init {
        val retrofit: Retrofit = Retrofit.Builder()
            .baseUrl("https://frapi.usernamedev.com/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        api = retrofit.create(RecipeApi::class.java)
    }

    fun register(act: LoginActivity, username: String, password: String) {

        val request: Call<RegisterResponse> = api.register(username, password)

        Log.d(logTag, "Enqueuing request to the /user/register endpoint.")

        request.enqueue(object: Callback<RegisterResponse> {

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                Log.e(logTag, "Failed to get a response from the /user/register endpoint.")
            }

            override fun onResponse(
                call: Call<RegisterResponse>,
                response: Response<RegisterResponse>
            ) {

                Log.d(logTag, "Response received from the /user/register endpoint.")

                val apiResponse: RegisterResponse? = response.body()

                if (apiResponse != null) {

                    if (!apiResponse.status) {

                        Log.v(logTag, apiResponse.message)
                        if (apiResponse.message.contains("already exists")) {
                            val builder = AlertDialog.Builder(act)
                            builder.setTitle(R.string.register_failed)
                                .setMessage(R.string.register_username_taken)
                                .setPositiveButton(R.string.ok) { _, _ -> }
                            val dialog: AlertDialog = builder.create()
                            dialog.show()
                        } else if (apiResponse.message.contains("username")) {
                            val builder = AlertDialog.Builder(act)
                            builder.setTitle(R.string.register_failed)
                                .setMessage(R.string.register_check_username)
                                .setPositiveButton(R.string.ok) { _, _ -> }
                            val dialog: AlertDialog = builder.create()
                            dialog.show()
                        } else if (apiResponse.message.contains("password")) {
                            val builder = AlertDialog.Builder(act)
                            builder.setTitle(R.string.register_failed)
                                .setMessage(R.string.register_check_password)
                                .setPositiveButton(R.string.ok) { _, _ -> }
                            val dialog: AlertDialog = builder.create()
                            dialog.show()
                        }
                    } else {
                        Log.d(logTag, "Successfully registered a new account.")
                        login(act, username, password)
                    }

                } else {
                    Log.d(logTag, "Request to register failed.")
                }
            }
        })

    }

    fun logout(act: MainActivity, token: String) {

        val request: Call<LogoutResponse> = api.logout(token)

        Log.d(logTag, "Enqueuing request to the /user/logout endpoint.")

        request.enqueue(object: Callback<LogoutResponse> {

            override fun onFailure(call: Call<LogoutResponse>, t: Throwable) {
                Log.e(logTag, "Failed to get a response from the /user/logout endpoint.")
            }

            override fun onResponse(
                call: Call<LogoutResponse>,
                response: Response<LogoutResponse>
            ) {
                Log.v(logTag, "Response received from the /user/logout endpoint.")
                val apiResponse: LogoutResponse? = response.body()
                if (apiResponse != null) {
                    if (apiResponse.status) {
                        Log.d(logTag, "Successfully logged out.")
                        act.loggedOut()
                    }
                }
            }
        })

    }

    fun login(act: LoginActivity, username: String, password: String) {

        val request: Call<LoginResponse> = api.login(username, password)

        Log.d(logTag, "Enqueuing request to the /user/login endpoint.")

        request.enqueue(object: Callback<LoginResponse> {

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Log.e(logTag, "Failed to get a response from the /user/login endpoint.")
            }

            override fun onResponse(
                call: Call<LoginResponse>,
                response: Response<LoginResponse>
            ) {

                Log.v(logTag, "Response received from the /user/login endpoint.")
                val apiResponse: LoginResponse? = response.body()
                val builder = AlertDialog.Builder(act)

                if (apiResponse != null) {
                    if (apiResponse.status) {
                        Log.d(logTag, "Successfully logged in. (Token: ${apiResponse.token})")
                        act.setToken(apiResponse.token)
                    } else {
                        Log.d(logTag, "Failed to login: " + apiResponse.message)
                        builder.setTitle(R.string.login_failed)
                            .setMessage(R.string.login_failed_details)
                            .setPositiveButton(R.string.ok) { _, _ -> }
                        val dialog: AlertDialog = builder.create()
                        dialog.show()
                    }
                }

            }
        })

    }

    fun verify(act: MainActivity, token: String) {

        val request: Call<VerifyResponse> = api.verify(token)

        Log.d(logTag, "Enqueuing request to the /verify endpoint.")

        request.enqueue(object: Callback<VerifyResponse> {

            override fun onFailure(call: Call<VerifyResponse>, t: Throwable) {
                Log.e(logTag, "Failed to get a response from the /verify endpoint.")
            }

            override fun onResponse(
                call: Call<VerifyResponse>,
                response: Response<VerifyResponse>
            ) {
                Log.v(logTag, "Response received from the /verify endpoint.")
                val apiResponse: VerifyResponse? = response.body()
                if (apiResponse != null) {
                    if (apiResponse.status) {
                        Log.d(logTag, "Successfully verified token.")
                    } else {
                        Log.d(logTag, "Token not verified, prompting use to login again...")
                        act.loggedOut()
                    }
                }
            }
        })

    }

    fun listRecipes(frag: RecipeListFragment, model: RecipeViewModel) {

        val request: Call<RecipeListResponse> = api.listRecipes(model.getToken())

        Log.d(logTag, "Enqueuing request to the /recipes/list endpoint.")

        request.enqueue(object: Callback<RecipeListResponse> {

            override fun onFailure(call: Call<RecipeListResponse>, t: Throwable) {
                Log.e(logTag, "Failed to get a response from the /recipes/list endpoint.")
            }

            override fun onResponse(
                call: Call<RecipeListResponse>,
                response: Response<RecipeListResponse>
            ) {

                Log.v(logTag, "Response received from the /recipes/list endpoint.")
                val apiResponse: RecipeListResponse? = response.body()

                if (apiResponse != null) {
                    if (apiResponse.status) frag.loadExternalRecipes(apiResponse.getRecipes()!!)
                }

            }
        })

    }

    fun getRecipe(frag: RecipeListFragment, id: Int, model: RecipeViewModel) {

        val request: Call<RecipeGetResponse> = api.getRecipe(id, model.getToken())

        Log.d(logTag, "Enqueuing request to the /recipes/get endpoint.")

        request.enqueue(object: Callback<RecipeGetResponse> {

            override fun onFailure(call: Call<RecipeGetResponse>, t: Throwable) {
                Log.e(logTag, "Failed to get a response from the /recipes/get endpoint.")
            }

            override fun onResponse(
                call: Call<RecipeGetResponse>,
                response: Response<RecipeGetResponse>
            ) {

                Log.v(logTag, "Response received from the /recipes/get endpoint.")
                val apiResponse: RecipeGetResponse? = response.body()

                if (apiResponse != null) {
                    if (apiResponse.status) {
                        val recipeData: RecipeGetResponseData = apiResponse.recipe
                        val image: String = if (recipeData.image == -1) {
                            "null"
                        } else {
                            "https://frapi.usernamedev.com/image/" + recipeData.image
                        }
                        val recipe = CompleteRecipe(recipeData.title, recipeData.ingredients,
                            recipeData.directions, image)
                        frag.recipeLoaded(id, recipe)
                    }
                }

            }
        })

    }

    fun addRecipe(frag: RecipeAddFragment, token: String, recipe: CompleteRecipe) {

        val request: Call<RecipeAddResponse> = api.addRecipe(token, recipe)

        Log.d(logTag, "Enqueuing request to the /recipes/add endpoint.")

        request.enqueue(object: Callback<RecipeAddResponse> {

            val builder = AlertDialog.Builder(frag.requireContext())

            override fun onFailure(call: Call<RecipeAddResponse>, t: Throwable) {
                builder.setTitle(R.string.failed_add)
                    .setMessage(R.string.add_error)
                    .setPositiveButton(R.string.ok) { _, _ -> }
                val dialog: AlertDialog = builder.create()
                dialog.show()
                Log.e(logTag, "Failed to get a response from the /recipes/add endpoint.")
            }

            override fun onResponse(
                call: Call<RecipeAddResponse>,
                response: Response<RecipeAddResponse>
            ) {

                Log.v(logTag, "Response received from the /recipes/add endpoint.")
                val apiResponse: RecipeAddResponse? = response.body()

                frag.saveLbl.visibility = View.INVISIBLE

                if (apiResponse != null) {
                    if (apiResponse.status) {
                        Log.d(logTag, "Successfully added recipe. (Id: " + apiResponse.id + ")")
                        frag.recipeAdded()
                    } else {
                        Log.d(logTag, "Failed to add recipe.")
                        builder.setTitle(R.string.failed_add)
                            .setMessage(R.string.failed_add_instruction)
                            .setPositiveButton(R.string.ok) { _, _ -> }
                        val dialog: AlertDialog = builder.create()
                        dialog.show()
                    }
                }

            }
        })

    }

    fun deleteRecipe(id: Int, model: RecipeViewModel) {

        val request: Call<RecipeDeleteResponse> = api.deleteRecipe(id, model.getToken())

        Log.d(logTag, "Enqueuing request to the /recipes/delete endpoint.")

        request.enqueue(object: Callback<RecipeDeleteResponse> {

            override fun onFailure(call: Call<RecipeDeleteResponse>, t: Throwable) {
                Log.e(logTag, "Failed to get a response from the /recipes/delete endpoint.")
            }

            override fun onResponse(
                call: Call<RecipeDeleteResponse>,
                response: Response<RecipeDeleteResponse>
            ) {
                Log.v(logTag, "Response received from the /recipes/delete endpoint.")
                val apiResponse: RecipeDeleteResponse? = response.body()
                if (apiResponse != null) {
                    if (apiResponse.status) {
                        Log.d(logTag, "Successfully deleted recipe.")
                    }
                }
            }
        })

    }

}