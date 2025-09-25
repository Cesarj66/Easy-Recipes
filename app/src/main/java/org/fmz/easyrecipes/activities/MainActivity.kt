package org.fmz.easyrecipes.activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import org.fmz.easyrecipes.R
import org.fmz.easyrecipes.fragments.HomeFragment
import org.fmz.easyrecipes.fragments.RecipeAddFragment
import org.fmz.easyrecipes.fragments.RecipeListFragment
import org.fmz.easyrecipes.support.DataStoreRepository
import org.fmz.easyrecipes.support.RecipeViewModel

class MainActivity : AppCompatActivity() {

    private lateinit var btnList : ImageButton
    private lateinit var btnAdd : ImageButton
    private lateinit var btnHome : ImageButton
    private lateinit var btnLogout: ImageButton

    val viewModel: RecipeViewModel by lazy {
        DataStoreRepository.initialize(this)
        RecipeViewModel()
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        btnList = findViewById(R.id.btnList)
        btnHome = findViewById(R.id.btnHome)
        btnAdd = findViewById(R.id.btnAdd)
        btnLogout = findViewById(R.id.btnLogout)

        // Highlight home button on startup
        btnHome.setBackgroundColor(ContextCompat.getColor(this, R.color.buttonBarSelected))

        loadData(savedInstanceState)

        setupCallbacks()

    }

    override fun onResume() {
        super.onResume()
        Thread{
            Thread.sleep(10)
            runOnUiThread {
                switchPage(viewModel.getPage())
            }
        }.start()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        saveData(outState)
    }

    private fun setupCallbacks() {
        setupButtonCallbacks()
    }

    private fun setupButtonCallbacks() {
        btnHome.setOnClickListener { switchPage(0)}
        btnList.setOnClickListener { switchPage(1) }
        btnAdd.setOnClickListener { switchPage(2) }
        btnLogout.setOnClickListener { switchPage(3) }
    }

    fun switchPage(page: Int) {
        when (page) {
            0 -> {
                switchFragment(HomeFragment())
                btnHome.setBackgroundColor(ContextCompat.getColor(this, R.color.buttonBarSelected))
                btnList.setBackgroundColor(ContextCompat.getColor(this, R.color.buttonBarBack))
                btnAdd.setBackgroundColor(ContextCompat.getColor(this, R.color.buttonBarBack))
                btnLogout.setBackgroundColor(ContextCompat.getColor(this, R.color.buttonBarBack))
            }
            1 -> {
                switchFragment(RecipeListFragment())
                btnHome.setBackgroundColor(ContextCompat.getColor(this, R.color.buttonBarBack))
                btnList.setBackgroundColor(ContextCompat.getColor(this, R.color.buttonBarSelected))
                btnAdd.setBackgroundColor(ContextCompat.getColor(this, R.color.buttonBarBack))
                btnLogout.setBackgroundColor(ContextCompat.getColor(this, R.color.buttonBarBack))
            }
            2 -> {
                switchFragment(RecipeAddFragment())
                btnHome.setBackgroundColor(ContextCompat.getColor(this, R.color.buttonBarBack))
                btnList.setBackgroundColor(ContextCompat.getColor(this, R.color.buttonBarBack))
                btnAdd.setBackgroundColor(ContextCompat.getColor(this, R.color.buttonBarSelected))
                btnLogout.setBackgroundColor(ContextCompat.getColor(this, R.color.buttonBarBack))
            }
            3 -> {
                viewModel.logout(this)
                btnHome.setBackgroundColor(ContextCompat.getColor(this, R.color.buttonBarBack))
                btnList.setBackgroundColor(ContextCompat.getColor(this, R.color.buttonBarBack))
                btnAdd.setBackgroundColor(ContextCompat.getColor(this, R.color.buttonBarBack))
                btnLogout.setBackgroundColor(ContextCompat.getColor(this, R.color.buttonBarSelected))
            }
        }
        if (page != 3) viewModel.setPage(page)
        Log.d("Page", "Page switched to index: $page")
    }

    //The function handles swaps using the buttons implemented in activity_main.xml
    private fun switchFragment(fragment : Fragment){
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.container,fragment)
        fragmentTransaction.commit()
    }

    private fun loadData(savedInstanceState: Bundle?) {
        switchPage(savedInstanceState?.getInt("PAGE", 0) ?: 0)
        viewModel.load(this)
    }

    private fun saveData(savedInstanceState: Bundle) {
        savedInstanceState.putInt("PAGE", viewModel.getPage())
        viewModel.save()
    }

    fun launchLogin() {
        startActivity(Intent(this, LoginActivity::class.java))
    }

    fun loggedOut() {
        viewModel.setToken("")
    }

}