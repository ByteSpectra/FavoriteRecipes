package com.mobivone.favrecipes.view.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.mobivone.favrecipes.R
import com.mobivone.favrecipes.databinding.FragmentDishDetailsBinding
import com.mobivone.favrecipes.model.entities.FavDish
import com.mobivone.favrecipes.utils.FavDishApplication
import com.mobivone.favrecipes.viewModel.FavDishViewModel
import com.mobivone.favrecipes.viewModel.FavDishViewModelFactory
import java.io.IOException

class DishDetailsFragment : Fragment() {

    private var mBinding: FragmentDishDetailsBinding? = null

    private val mFavDishViewModel:FavDishViewModel by viewModels {
        FavDishViewModelFactory(((requireActivity().application) as FavDishApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        mBinding = FragmentDishDetailsBinding.inflate(inflater, container, false)

        return mBinding!!.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val args: DishDetailsFragmentArgs by navArgs()

        args.let {
            try {
                Glide.with(requireActivity())
                    .load(it.dishDetails.image)
                    .centerCrop()
                    .into(mBinding!!.ivDishImage)
            } catch (ex: IOException) {
                ex.printStackTrace()
            }
            mBinding!!.tvTitle.text = it.dishDetails.title
            mBinding!!.tvType.text = it.dishDetails.type
            mBinding!!.tvCategory.text = it.dishDetails.category
            mBinding!!.tvIngredients.text = it.dishDetails.ingredients
            mBinding!!.tvCookingDirection.text = it.dishDetails.directionToCook
            mBinding!!.tvCookingTime.text = resources.getString(R.string.lbl_estimate_cooking_time, it.dishDetails.cookingTime)

            setAppropriateImage(args.dishDetails)
        }

        mBinding!!.ivFavoriteImage.setOnClickListener {
            args.dishDetails.favoriteDish = !args.dishDetails.favoriteDish

            mFavDishViewModel.update(args.dishDetails)

            setAppropriateImage(args.dishDetails)
        }
    }

    private fun setAppropriateImage(favDish: FavDish) {
        if (favDish.favoriteDish) {
            mBinding!!.ivFavoriteImage.setImageDrawable(ContextCompat.getDrawable(
                requireActivity(),
                R.drawable.ic_favorite_selected_24
            ))
        } else {
            mBinding!!.ivFavoriteImage.setImageDrawable(ContextCompat.getDrawable(
                requireActivity(),
                R.drawable.ic_favorite_unselected_24
            ))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        mBinding = null
    }
}