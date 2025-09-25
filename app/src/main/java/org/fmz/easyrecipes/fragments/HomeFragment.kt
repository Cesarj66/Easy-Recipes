package org.fmz.easyrecipes.fragments

import android.content.res.Configuration
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import org.fmz.easyrecipes.R
import org.fmz.easyrecipes.activities.MainActivity

class HomeFragment : Fragment() {

    private lateinit var mainActivity: MainActivity
    private lateinit var myRecipesButton: ImageButton
    private lateinit var addButton: ImageButton

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val view: View = if (resources.configuration.orientation == Configuration.ORIENTATION_LANDSCAPE) {
            inflater.inflate(R.layout.home_landscape, container, false)
        } else {
            inflater.inflate(R.layout.home, container, false)
        }

        // Initialize mainActivity
        mainActivity = activity as MainActivity

        // Initialize buttons
        myRecipesButton = view.findViewById(R.id.myRecipesButton)
        addButton = view.findViewById(R.id.addButton)

        setupCallbacks()

        return view
    }

    private fun setupCallbacks() {
        setupButtonCallbacks()
    }


    private fun setupButtonCallbacks() {
        myRecipesButton.setOnClickListener { mainActivity.switchPage(1) }
        addButton.setOnClickListener { mainActivity.switchPage(2) }
    }


}