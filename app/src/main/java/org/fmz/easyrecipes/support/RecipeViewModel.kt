package org.fmz.easyrecipes.support

import android.util.Log

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope

import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

import org.fmz.easyrecipes.activities.LoginActivity
import org.fmz.easyrecipes.activities.MainActivity
import org.fmz.easyrecipes.api.RecipeApiExecutor

class RecipeViewModel: ViewModel() {

    private val logTag = "View Model"

    private val prefs = DataStoreRepository.get()

    private var token = ""
    private var page = 0

    fun login(act: LoginActivity, username: String, password: String) {
        Log.d(logTag, "Received request to login with username $username...")
        RecipeApiExecutor().login(act, username, password)
    }

    fun logout(act: MainActivity) {
        Log.d(logTag, "Received request to logout...")
        RecipeApiExecutor().logout(act, token)
    }

    fun verify(act: MainActivity) {
        Log.d(logTag, "Received request to verify token...")
        RecipeApiExecutor().verify(act, token)
    }

    fun register(act: LoginActivity, username: String, password: String) {
        Log.d(logTag, "Received request to register with username $username...")
        RecipeApiExecutor().register(act, username, password)
    }

    fun saveString(string: String, index: Int) {
        viewModelScope.launch {
            prefs.saveString(string, index)
            Log.v(logTag, "Saved string \"$string\" at index $index.")
        }
    }

    fun getToken() : String {
        return token
    }

    fun setToken(token: String) {
        this.token = token
        saveString(token, 1)
    }

    fun load(act: MainActivity) {
        viewModelScope.launch {
            prefs.token.collectLatest {
                token = it
                if (token.isEmpty()) {
                    act.launchLogin()
                } else {
                    verify(act)
                }
            }
        }
    }

    fun save() {
        viewModelScope.launch { prefs.saveString(token, 1) }
    }

    fun getPage() : Int {
        return page
    }

    fun setPage(page: Int) {
        this.page = page
    }

}
