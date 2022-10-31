package com.mobivone.favrecipes.view.fragments

import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.mobivone.favrecipes.R
import com.mobivone.favrecipes.databinding.FragmentRandomDishesBinding
import com.mobivone.favrecipes.model.entities.RandomDish
import com.mobivone.favrecipes.viewModel.NotificationsViewModel
import com.mobivone.favrecipes.viewModel.RandomDishViewModel

class RandomDishesFragment : Fragment() {

    private var _binding: FragmentRandomDishesBinding? = null

    private lateinit var mRandomDishViewModel: RandomDishViewModel

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentRandomDishesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mRandomDishViewModel = ViewModelProvider(this).get(RandomDishViewModel::class.java)
        mRandomDishViewModel.getRandomDishFromApi()

        randomDishViewModelObserver()
    }

    private fun randomDishViewModelObserver(){
        mRandomDishViewModel.randomDishResponse.observe(viewLifecycleOwner, { randomDishResponse ->
            randomDishResponse?.let {
                setRandomDishResponseInUI(randomDishResponse.recipes[0])
            }
        })

        mRandomDishViewModel.randomDishLoadingError.observe(viewLifecycleOwner, { dataError ->
            dataError?.let {

            }
        })

        mRandomDishViewModel.loadRandomDish.observe(viewLifecycleOwner, { loadRandomDish ->
            loadRandomDish?.let {

            }
        })

    }

    private fun setRandomDishResponseInUI(recipe: RandomDish.Recipe){
        Glide.with(requireActivity())
            .load(recipe.image)
            .centerCrop()
            .into(_binding!!.ivDishImage)

        _binding!!.tvTitle.text = recipe.title

        if (recipe.dishTypes.isNotEmpty()){
            _binding!!.tvType.text = recipe.dishTypes[0]
        }

        _binding!!.tvCategory.text = "Other"

        var ingredients = ""
        for (value in recipe.extendedIngredients){
            if (ingredients.isEmpty())
                ingredients = value.original
            else
                ingredients = ingredients + ", \n" + value.original
        }

        _binding!!.tvIngredients.text = ingredients

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            _binding!!.tvCookingDirection.text = Html.fromHtml(
                recipe.instructions,
                Html.FROM_HTML_MODE_COMPACT
            )
        } else {
            @Suppress("Deprecated")
          _binding!!.tvCookingDirection.text = Html.fromHtml(recipe.instructions)
        }

        _binding!!.tvCookingTime.text = resources.getString(
            R.string.lbl_estimate_cooking_time,
            recipe.readyInMinutes.toString()
        )

    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}