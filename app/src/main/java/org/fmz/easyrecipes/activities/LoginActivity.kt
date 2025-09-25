package org.fmz.easyrecipes.activities

import android.content.res.Configuration
import android.os.Bundle
import android.widget.Button
import android.widget.EditText

import androidx.appcompat.app.AppCompatActivity

import org.fmz.easyrecipes.R
import org.fmz.easyrecipes.support.DataStoreRepository
import org.fmz.easyrecipes.support.RecipeViewModel

import java.security.MessageDigest

class LoginActivity : AppCompatActivity() {

    private val viewModel: RecipeViewModel by lazy {
        DataStoreRepository.initialize(this)
        RecipeViewModel()
    }

    private lateinit var editUsername: EditText
    private lateinit var editPassword: EditText

    private lateinit var btnLogin: Button
    private lateinit var btnRegister: Button

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            R.layout.login_landscape
        } else {
            R.layout.login
        })

        editUsername = findViewById(R.id.editUsername)
        editPassword = findViewById(R.id.editPassword)

        btnLogin = findViewById(R.id.btnLogin)
        btnRegister = findViewById(R.id.btnRegister)

        setupCallbacks()

    }

    private fun setupCallbacks() {
        createButtonCallbacks()
    }

    private fun createButtonCallbacks() {

        btnLogin.setOnClickListener {
            viewModel.login(this, editUsername.text.toString(), getPassword(editPassword.text.toString()))
        }

        btnRegister.setOnClickListener {
            viewModel.register(this, editUsername.text.toString(), getPassword(editPassword.text.toString()))
        }

    }

    private fun getPassword(password: String): String {
        if (password.isNotEmpty()) {
            val md = MessageDigest.getInstance("MD5")
            val hashBytes = md.digest(password.toByteArray())
            return hashBytes.joinToString("") { "%02x".format(it) }
        }
        return ""
    }

    fun setToken(token: String) {
        viewModel.setToken(token)
        finish()
    }

}