package com.mobivone.favrecipes.view.fragments

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.mobivone.favrecipes.R
import com.mobivone.favrecipes.databinding.FragmentRandomDishesBinding
import com.mobivone.favrecipes.model.entities.FavDish
import com.mobivone.favrecipes.model.entities.RandomDish
import com.mobivone.favrecipes.utils.Constants
import com.mobivone.favrecipes.utils.FavDishApplication
import com.mobivone.favrecipes.viewModel.FavDishViewModel
import com.mobivone.favrecipes.viewModel.FavDishViewModelFactory
import com.mobivone.favrecipes.viewModel.NotificationsViewModel
import com.mobivone.favrecipes.viewModel.RandomDishViewModel

class RandomDishesFragment : Fragment() {

    private var _binding: FragmentRandomDishesBinding? = null

    private lateinit var mRandomDishViewModel: RandomDishViewModel

    private var mProgressDialog: Dialog? = null

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

    private fun showCustomDialog() {
        mProgressDialog = Dialog(requireActivity())

        mProgressDialog?.let {
            it.setContentView(R.layout.dialog_custom_progress)
            it.show()
        }
    }

    private fun hideProgressDialog()
    {
        mProgressDialog?.let {
            it.dismiss()
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        mRandomDishViewModel = ViewModelProvider(this).get(RandomDishViewModel::class.java)
        mRandomDishViewModel.getRandomDishFromApi()

        randomDishViewModelObserver()

        _binding!!.srlDishMain.setOnRefreshListener {
            mRandomDishViewModel.getRandomDishFromApi()
        }
    }

    private fun randomDishViewModelObserver(){
        mRandomDishViewModel.randomDishResponse.observe(viewLifecycleOwner, { randomDishResponse ->
            randomDishResponse?.let {

                if (_binding!!.srlDishMain.isRefreshing) {
                    _binding!!.srlDishMain.isRefreshing = false
                }
                setRandomDishResponseInUI(randomDishResponse.recipes[0])
            }
        })

        mRandomDishViewModel.randomDishLoadingError.observe(viewLifecycleOwner, { dataError ->
            dataError?.let {
                if (_binding!!.srlDishMain.isRefreshing) {
                    _binding!!.srlDishMain.isRefreshing = false
                }
            }
        })

        mRandomDishViewModel.loadRandomDish.observe(viewLifecycleOwner, { loadRandomDish ->
            loadRandomDish?.let {
                if (loadRandomDish && !_binding!!.srlDishMain.isRefreshing)
                    showCustomDialog()
                else hideProgressDialog()
            }
        })

    }

    private fun setRandomDishResponseInUI(recipe: RandomDish.Recipe){
        Glide.with(requireActivity())
            .load(recipe.image)
            .centerCrop()
            .into(_binding!!.ivDishImage)

        _binding!!.tvTitle.text = recipe.title

        var dishType: String = "other"
        if (recipe.dishTypes.isNotEmpty()){
            dishType = recipe.dishTypes[0]
            _binding!!.tvType.text = dishType
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

        _binding!!.ivFavoriteImage.setImageDrawable(
            ContextCompat.getDrawable(
                requireActivity(), R.drawable.ic_favorite_unselected_24
            )
        )

        var addedToFavorite = false

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

        _binding!!.ivFavoriteImage.setOnClickListener {

            if (addedToFavorite) {
                Toast.makeText(requireActivity(), resources.getString(R.string.msg_already_added_to_favorite), Toast.LENGTH_SHORT).show()
            }
            else {
                val randomDish = FavDish(
                    recipe.image,
                    Constants.DISH_IMAGE_SOURCE_ONLINE,
                    recipe.title,
                    dishType,
                    "Other",
                    ingredients,
                    recipe.readyInMinutes.toString(),
                    recipe.instructions,
                    true
                )

                val mFavDishViewModel: FavDishViewModel by viewModels {
                    FavDishViewModelFactory((requireActivity().application as FavDishApplication).repository)
                }

                mFavDishViewModel.insert(randomDish)

                addedToFavorite = true

                _binding!!.ivFavoriteImage.setImageDrawable(
                    ContextCompat.getDrawable(
                        requireActivity(), R.drawable.ic_favorite_selected_24
                    ))

                Toast.makeText(requireActivity(), resources.getString(R.string.title_favorite_dishes), Toast.LENGTH_SHORT).show()
            }

        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}