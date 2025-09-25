package org.fmz.easyrecipes.fragments

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import org.fmz.easyrecipes.R

//change to constructor from List to mutablelist so we dont run into mutation issues
class RecipeListFragmentAdapter(private val frag: RecipeListFragment, private val items: MutableList<String>) : RecyclerView.Adapter<RecipeListFragmentAdapter.ViewHolder>() {

    var onItemLongClicked: ((Int) -> Unit)? = null

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textView: TextView = view.findViewById(R.id.textItem)

        fun bind(frag: RecipeListFragment, onItemLongClicked: ((Int) -> Unit)?) {
            itemView.setOnClickListener {
                frag.loadRecipe(textView.text.toString())
            }
            itemView.setOnLongClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onItemLongClicked?.invoke(adapterPosition)
                }
                true //confirm longclick is actually being handled
            }
        }
    }

    //overridden built in function that creates the "ViewHolder" object
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_layout, parent, false)
        return ViewHolder(view)
    }

    //overridden built in function that binds the data to the ViewHolder object in it's parameter
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.textView.text = items[position]
        holder.bind(frag, onItemLongClicked)
    }

    override fun getItemCount() = items.size
}


