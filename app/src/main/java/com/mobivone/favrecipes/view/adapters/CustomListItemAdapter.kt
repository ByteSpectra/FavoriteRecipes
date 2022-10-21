package com.mobivone.favrecipes.view.adapters

import android.app.Activity
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.mobivone.favrecipes.databinding.RowCustomItemListBinding
import com.mobivone.favrecipes.view.activities.AddUpdateRecipes
import com.mobivone.favrecipes.view.fragments.AllDishesFragment

class CustomListItemAdapter(private val activity: Activity,
                            val fragment: Fragment?,
                            private val listItem: List<String>,
                            private val selection: String)
    : RecyclerView.Adapter<CustomListItemAdapter.ViewHolder>()
{
        class ViewHolder(view: RowCustomItemListBinding): RecyclerView.ViewHolder(view.root)
        {
            val tvText = view.tvText
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: RowCustomItemListBinding = RowCustomItemListBinding.inflate(LayoutInflater.from(activity), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = listItem[position]
        holder.tvText.text = item

        holder.itemView.setOnClickListener {
            if (activity is AddUpdateRecipes)
                activity.selectedListItem(item, selection)
            else if (fragment is AllDishesFragment)
                fragment.filterSelection(item)
        }
    }

    override fun getItemCount(): Int {
        return listItem.size
    }
}