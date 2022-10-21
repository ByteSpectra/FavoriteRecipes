package com.mobivone.favrecipes.view.adapters

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.mobivone.favrecipes.R
import com.mobivone.favrecipes.databinding.RowCardAllDishesFragmentBinding
import com.mobivone.favrecipes.model.entities.FavDish
import com.mobivone.favrecipes.utils.Constants
import com.mobivone.favrecipes.view.activities.AddUpdateRecipes
import com.mobivone.favrecipes.view.fragments.AllDishesFragment
import com.mobivone.favrecipes.view.fragments.DishDetailsFragment
import com.mobivone.favrecipes.view.fragments.FavoriteDishesFragment

class AllDishesFragmentAdapter(private val fragment: Fragment): RecyclerView.Adapter<AllDishesFragmentAdapter.ViewHolder>() {

    private var dishes: List<FavDish> = listOf()

    class ViewHolder(view: RowCardAllDishesFragmentBinding): RecyclerView.ViewHolder(view.root) {
        // Holds the textview that will add each item to
        val ivDishImage = view.ivDishImage
        val tvTitle = view.tvDishTitle
        val ibMore = view.btnMore
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding: RowCardAllDishesFragmentBinding = RowCardAllDishesFragmentBinding.inflate(
            LayoutInflater.from(fragment.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val dish = dishes[position]
        Glide.with(fragment)
            .load(dish.image)
            .into(holder.ivDishImage)

        holder.tvTitle.text = dish.title

        holder.itemView.setOnClickListener {
            if (fragment is AllDishesFragment)
                fragment.GoToDishDetailsFragment(dish)
            else if (fragment is FavoriteDishesFragment)
                fragment.GoToDishDetailsFragment(dish)
        }

        holder.ibMore.setOnClickListener {
            val popup = PopupMenu(fragment.requireContext(), holder.ibMore)
            popup.menuInflater.inflate(R.menu.adapter_menu, popup.menu)

            popup.setOnMenuItemClickListener {
                if (it.itemId == R.id.action_edit_dish){

                    val intent = Intent(fragment.requireActivity(), AddUpdateRecipes::class.java)
                    intent.putExtra(Constants.EXTRA_DISH_DETAILS, dish)
                    fragment.requireActivity().startActivity(intent)

                }
                else if (it.itemId == R.id.action_delete_dish) {
                    if (fragment is AllDishesFragment)
                        fragment.DeleteDish(dish)
                }
                // return true finally
                true
            }

            popup.show()
        }
        if (fragment is AllDishesFragment)
            holder.ibMore.visibility = View.VISIBLE
        else holder.ibMore.visibility = View.GONE
    }

    override fun getItemCount(): Int {
        return dishes.size
    }

    fun dishesList(list: List<FavDish>) {
        dishes = list
        notifyDataSetChanged()
    }
}