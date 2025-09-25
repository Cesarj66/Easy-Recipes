package org.fmz.easyrecipes.fragments

import android.content.res.Configuration
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import android.media.MediaPlayer
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import org.fmz.easyrecipes.R
import org.fmz.easyrecipes.activities.MainActivity
import org.fmz.easyrecipes.api.RecipeApiExecutor
import org.fmz.easyrecipes.support.ds.CompleteRecipe
import org.fmz.easyrecipes.support.RecipeTextModel


class RecipeAddFragment : Fragment() {

    private val logTag = "Recipe Adder"

    private lateinit var fileStore: RecipeTextModel

    private lateinit var recipeImage : ImageView
    private lateinit var recipeTitle : TextView
    private lateinit var recipeContent: EditText

    private lateinit var backBtn: ImageButton
    private lateinit var ingredientBtn: Button
    private lateinit var imageBtn: Button
    private lateinit var directionBtn: Button
    private lateinit var saveBtn: Button

    lateinit var saveLbl: TextView

    private var title: String = ""
    private var ingredients: String = ""
    private var directions: String = ""
    private lateinit var imagePicker: ActivityResultLauncher<PickVisualMediaRequest>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val view: View = if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            inflater.inflate(R.layout.recipe_landscape, container, false)
        } else {
            inflater.inflate(R.layout.recipe, container, false)
        }

        fileStore = RecipeTextModel(requireContext())
        title = fileStore.loadText("title")
        if (title == "") { title = resources.getString(R.string.recipeName) }

        ingredients = fileStore.loadText("ingredients")
        directions = fileStore.loadText("directions")

        recipeTitle = view.findViewById(R.id.recipeTitle)
        recipeTitle.text = title
        recipeImage = view.findViewById(R.id.recipeImage)
        recipeContent = view.findViewById(R.id.recipeContent)
        ingredientBtn = view.findViewById(R.id.btnIngredients)
        imageBtn = view.findViewById(R.id.btnImage)
        directionBtn = view.findViewById(R.id.btnDirections)
        saveBtn = view.findViewById(R.id.btnSave)
        saveLbl = view.findViewById(R.id.lblSaving)
        backBtn = view.findViewById(R.id.btnBack)
        backBtn.setVisibility(View.INVISIBLE)

        setupCallbacks()

        return view

    }

    private fun setupCallbacks() {
        createButtonCallbacks()
        setupImagePickerCallback()
    }

    private fun setupImagePickerCallback() {
        imagePicker = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->
            if (uri != null) {
                fileStore.saveText(encodeImageToBase64(uri), "image")
                Log.d("Photo Picker", "Photo selected!")
            } else {
                Log.d("Photo Picker", "No photo selected.")
            }
        }
    }

    private fun createButtonCallbacks() {

        backBtn.setOnClickListener {
            (activity as MainActivity).switchPage(1)
        }

        ingredientBtn.setOnClickListener {
            showTextInput("ingredients")
        }

        imageBtn.setOnClickListener {
            imagePicker.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        }

        directionBtn.setOnClickListener {
            showTextInput("directions")
        }

        saveBtn.setOnClickListener {
            saveLbl.visibility = View.VISIBLE

            val successSound = MediaPlayer.create(context, R.raw.success_sound)

            successSound.setOnPreparedListener{
                Log.d(logTag, "MediaPlayer object created")
                successSound.start()
                Log.d(logTag, "Success sound playback started")
            }

            successSound.setOnCompletionListener {
                Log.d(logTag, "Success sound playback completed")
                // releases resources associated with this MediaPlayer object
                it.release()
            }

            if (title.trim().isEmpty()) {
                title = resources.getString(R.string.default_recipe_name)
                recipeTitle.text = title // Update the TextView to show the default name saved in string.xml
            }

            var image = fileStore.loadText("image")
            if (image == "") image = "null"

            val activity = context as MainActivity
            val recipe = CompleteRecipe(title, ingredients, directions, image)
            RecipeApiExecutor().addRecipe(this, activity.viewModel.getToken(), recipe)

        }

        recipeTitle.setOnClickListener{
            showTextInput("title")
        }

    }

    fun recipeAdded() {
        fileStore.saveText("", "title")
        fileStore.saveText("", "ingredients")
        fileStore.saveText("", "directions")
        fileStore.saveText("", "image")
        Toast.makeText(requireContext(), R.string.recipe_added, Toast.LENGTH_SHORT).show()
        (activity as MainActivity).switchPage(1)
    }

    private fun encodeImageToBase64(uri: Uri): String {
        val inputStream = requireContext().contentResolver.openInputStream(uri)
        val bytes = inputStream?.readBytes()
        inputStream?.close()
        return Base64.encodeToString(bytes, Base64.DEFAULT)
    }

    private fun showTextInput(s: String) {

        //popup input for inputting ingredient

        val builder = AlertDialog.Builder(requireContext())
        val inflater = layoutInflater

        val dialogLayout: View
        val editText: EditText
        if (s == "title") {
            dialogLayout = inflater.inflate(R.layout.small_input_layout, null)
            editText = dialogLayout.findViewById(R.id.et_editText)
        } else {
            dialogLayout = inflater.inflate(R.layout.big_input_layout, null)
            editText = dialogLayout.findViewById(R.id.et_editText)
        }

        with(builder) {
            if (s == "ingredients") {
                editText.setText(ingredients)
                setTitle(R.string.enter_ingredients)
                setPositiveButton(R.string.add) { _, _ ->
                    ingredients = editText.text.toString()
                    fileStore.saveText(ingredients, "ingredients")
                    Log.d(logTag, "Added ingredients")
                }
                setNegativeButton(R.string.cancel) { _, _ -> }
            }
            if (s == "directions") {
                editText.setText(directions)
                setTitle(R.string.enter_directions)
                setPositiveButton(R.string.add) { _, _ ->
                    directions = editText.text.toString()
                    fileStore.saveText(directions, "directions")
                    Log.d(logTag, "Added directions")
                }
                setNegativeButton(R.string.cancel) { _, _ -> }
            }
            if (s == "title") {
                editText.setText(title)
                setTitle(R.string.enter_name)
                setPositiveButton(R.string.add) { _, _ ->
                    title = editText.text.toString()
                    recipeTitle.text = title
                    fileStore.saveText(title, "title")
                    Log.d(logTag, "Added title")
                }
                setNegativeButton(R.string.cancel) { _, _ -> }
            }
            editText.requestFocus()
            setView(dialogLayout)
            show()
        }
    }

}
