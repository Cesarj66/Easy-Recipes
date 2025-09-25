package org.fmz.easyrecipes.fragments

import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast

import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import org.fmz.easyrecipes.R
import org.fmz.easyrecipes.activities.MainActivity
import org.fmz.easyrecipes.activities.RecipeActivity
import org.fmz.easyrecipes.api.RecipeApiExecutor
import org.fmz.easyrecipes.support.ds.CompleteRecipe
import org.fmz.easyrecipes.support.ds.SimpleRecipe
import org.fmz.easyrecipes.support.sqlite.SQLRepository
import org.fmz.easyrecipes.support.sqlite.SQLViewModel

//references to the dependencies can be found here
//https://developer.android.com/reference/android/app/AlertDialog
//https://developer.android.com/guide/topics/ui/notifiers/toasts
class RecipeListFragment : Fragment() {

    private val logTag = "Recipe Lister"

    private lateinit var sqlModel: SQLViewModel

    private lateinit var view: View
    private lateinit var mainActivity: MainActivity

    private val recipeMap : HashMap<String, Int> = HashMap()
    private val localRecipeMap : HashMap<String, Int> = HashMap()
    private val localRecipeData: HashMap<Int, CompleteRecipe> = HashMap()
    private var local: Boolean = false
    private var localLoaded: Boolean = false

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        view = inflater.inflate(R.layout.recipe_list, container, false)

        mainActivity = activity as MainActivity

        if (isOnline(requireContext())) {
            RecipeApiExecutor().listRecipes(this, mainActivity.viewModel)
        } else {
            local = true
        }

        sqlModel = SQLViewModel(SQLRepository(this))

        setupObservers()

        return view

    }

    fun loadRecipe(name: String) {
        if (local) {
            if (localRecipeMap[name] != null) {
                val id: Int = localRecipeMap[name]!!
                if (localRecipeData[id] != null) recipeLoaded(localRecipeData[id]!!)
            }
        } else {
            if (recipeMap[name] != null) {
                RecipeApiExecutor().getRecipe(this, recipeMap[name]!!, mainActivity.viewModel)
            }
        }
    }

    private fun deleteRecipe(name: String) {
        if (local) {
            if (localRecipeMap[name] != null) {
                val id: Int = localRecipeMap[name]!!
                sqlModel.deleteRecipe(id)
            }
        } else {
            if (recipeMap[name] != null) {
                RecipeApiExecutor().deleteRecipe(recipeMap[name]!!, mainActivity.viewModel)
            }
        }
    }

    private fun setupObservers() {
        sqlModel.recipes.observe(viewLifecycleOwner) { recipes ->
            for (recipe in recipes) {
                val r = CompleteRecipe(
                    recipe.title, recipe.ingredients,
                    recipe.directions, recipe.image
                )
                localRecipeData[recipe.id] = r
                localRecipeMap[recipe.title] = recipe.id
            }
            if (!localLoaded && local) {
                localLoaded = true
                loadInternalRecipes()
            }
        }
    }

    fun loadExternalRecipes(recipes: List<SimpleRecipe>) {

        Log.d(logTag, "Loading external recipes...")

        val lblListHeader: TextView = view.findViewById(R.id.lblListHeader)
        val lblListMessage: TextView = view.findViewById(R.id.lblListMessage)

        if (recipes.isNotEmpty()) {

            val layout: LinearLayout = view.findViewById(R.id.layoutList)

            layout.removeView(lblListHeader)
            layout.removeView(lblListMessage)

            val items = ArrayList<String>()
            for (r in  recipes) {
                recipeMap[r.title] = r.id
                items.add(r.title)
            }

            val adapter = RecipeListFragmentAdapter(this, items)

            // Set the long click listener with the message popup for deletion
            adapter.onItemLongClicked = { position ->
                AlertDialog.Builder(requireContext())
                    .setTitle(R.string.confirm_delete)
                    .setMessage(R.string.confirm_delete_instruction)
                    .setPositiveButton(R.string.delete) { _, _ ->
                        val recipeName = items[position]
                        deleteRecipe(recipeName)
                        items.removeAt(position)
                        adapter.notifyItemRemoved(position)
                        Toast.makeText(requireContext(), R.string.recipe_deleted, Toast.LENGTH_SHORT).show()
                        Log.i("Recipe Manager", "Recipe deleted: $recipeName")
                        if (items.isEmpty()) {
                            layout.addView(lblListHeader)
                            layout.addView(lblListMessage)
                            lblListHeader.visibility = View.VISIBLE
                            lblListMessage.visibility = View.VISIBLE
                        }
                    }
                    .setNegativeButton(R.string.cancel, null)
                    .show()
            }

            val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
            recyclerView.layoutManager = LinearLayoutManager(context)
            recyclerView.adapter = adapter

        } else {

            lblListHeader.visibility = View.VISIBLE
            lblListMessage.visibility = View.VISIBLE

        }

    }

    private fun loadInternalRecipes() {

        Log.d(logTag, "Loading internal recipes...")

        val lblListHeader: TextView = view.findViewById(R.id.lblListHeader)
        val lblListMessage: TextView = view.findViewById(R.id.lblListMessage)

        if (localRecipeData.isNotEmpty()) {

            val layout: LinearLayout = view.findViewById(R.id.layoutList)

            layout.removeView(lblListHeader)
            layout.removeView(lblListMessage)

            val items = ArrayList<String>()
            for (r in localRecipeMap.keys) items.add(r)

            val adapter = RecipeListFragmentAdapter(this, items)

            // Set the long click listener with the message popup for deletion
            adapter.onItemLongClicked = { position ->
                AlertDialog.Builder(requireContext())
                    .setTitle(R.string.confirm_delete)
                    .setMessage(R.string.confirm_delete_instruction)
                    .setPositiveButton(R.string.delete) { _, _ ->
                        val recipeName = items[position]
                        deleteRecipe(recipeName)
                        items.removeAt(position)
                        adapter.notifyItemRemoved(position)
                        Toast.makeText(requireContext(), R.string.recipe_deleted, Toast.LENGTH_SHORT).show()
                        Log.i("Recipe Manager", "Recipe deleted: $recipeName")
                        if (items.isEmpty()) {
                            layout.addView(lblListHeader)
                            layout.addView(lblListMessage)
                            lblListHeader.visibility = View.VISIBLE
                            lblListMessage.visibility = View.VISIBLE
                        }
                    }
                    .setNegativeButton(R.string.cancel, null)
                    .show()
            }

            val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerView)
            recyclerView.layoutManager = LinearLayoutManager(context)
            recyclerView.adapter = adapter

        } else {

            lblListHeader.visibility = View.VISIBLE
            lblListMessage.visibility = View.VISIBLE

        }

    }

    fun recipeLoaded(id: Int, recipe: CompleteRecipe) {
        if (!localRecipeData.contains(id)) {
            sqlModel.addRecipe(id, recipe.title, recipe.ingredients, recipe.directions, recipe.image)
        }
        recipeLoaded(recipe)
    }

    private fun recipeLoaded(recipe: CompleteRecipe) {
        val intent = Intent(requireContext(), RecipeActivity::class.java)
        intent.putExtra("TITLE", recipe.title)
        intent.putExtra("INGREDIENTS", recipe.ingredients)
        intent.putExtra("DIRECTIONS", recipe.directions)
        intent.putExtra("IMAGE", recipe.image)
        requireContext().startActivity(intent)
    }

    // https://stackoverflow.com/questions/51141970/check-internet-connectivity-android-in-kotlin
    private fun isOnline(context: Context): Boolean {
        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) {
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) {
                return true
            } else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) {
                return true
            }
        }
        return false
    }

}

