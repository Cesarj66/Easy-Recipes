package org.fmz.easyrecipes.activities

import android.content.res.Configuration
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.squareup.picasso.Picasso
import org.fmz.easyrecipes.R

class RecipeActivity : AppCompatActivity() {

    // You're listening to Oncle Jazz

    private lateinit var recipeImage: ImageView
    private lateinit var recipeTitle: TextView
    private lateinit var recipeContent: EditText

    private lateinit var btnBack: ImageButton
    private lateinit var btnIngredients: Button
    private lateinit var btnDirections: Button

    private lateinit var ingredients: String
    private lateinit var directions: String
    private lateinit var image: String

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        setContentView(if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            R.layout.recipe_landscape
        } else {
            R.layout.recipe
        })

        val layout: ConstraintLayout = findViewById(R.id.layoutRecipe)

        layout.removeView(findViewById(R.id.btnImage))
        layout.removeView(findViewById(R.id.btnSave))

        btnBack = findViewById(R.id.btnBack)
        btnIngredients = findViewById(R.id.btnIngredients)
        btnDirections = findViewById(R.id.btnDirections)

        recipeImage = findViewById(R.id.recipeImage)
        recipeTitle = findViewById(R.id.recipeTitle)
        recipeContent = findViewById(R.id.recipeContent)

        val extras = intent.extras
        if (extras != null) {
            recipeTitle.text = extras.getString("TITLE").toString()
            ingredients = extras.getString("INGREDIENTS").toString()
            directions = extras.getString("DIRECTIONS").toString()
            image = extras.getString("IMAGE").toString()
        }

        if (image != "null") Picasso.get().load(image).into(recipeImage)

        setupCallbacks()

        switchContent(0)

    }

    private fun setupCallbacks() {
        setupButtonCallbacks()
    }

    private fun setupButtonCallbacks() {

        btnBack.setOnClickListener {
            finish()
        }

        btnIngredients.setOnClickListener {
            switchContent(0)
        }

        btnDirections.setOnClickListener {
            switchContent(1)
        }

    }

    private fun switchContent(content: Int) {
        if (content == 0) {
            btnIngredients.setBackgroundColor(ContextCompat.getColor(this, R.color.buttonBarSelected))
            btnDirections.setBackgroundColor(ContextCompat.getColor(this, R.color.buttonBarBack))
            recipeContent.setText(ingredients)
        } else {
            btnIngredients.setBackgroundColor(ContextCompat.getColor(this, R.color.buttonBarBack))
            btnDirections.setBackgroundColor(ContextCompat.getColor(this, R.color.buttonBarSelected))
            recipeContent.setText(directions)
        }
    }

}