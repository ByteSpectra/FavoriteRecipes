package com.mobivone.favrecipes.view.fragments

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.*
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.mobivone.favrecipes.R
import com.mobivone.favrecipes.databinding.FragmentDishDetailsBinding
import com.mobivone.favrecipes.model.entities.FavDish
import com.mobivone.favrecipes.utils.Constants
import com.mobivone.favrecipes.utils.FavDishApplication
import com.mobivone.favrecipes.viewModel.FavDishViewModel
import com.mobivone.favrecipes.viewModel.FavDishViewModelFactory
import java.io.IOException

class DishDetailsFragment : Fragment() {

    private var mBinding: FragmentDishDetailsBinding? = null

    private var mFavDishDetails: FavDish? = null

    private val mFavDishViewModel:FavDishViewModel by viewModels {
        FavDishViewModelFactory(((requireActivity().application) as FavDishApplication).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_share, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId) {
            R.id.action_share -> {
                val type = "text/plain"
                val subject = "Checkout this dish recipe"
                var extraText = ""
                val shareWith = "Share With"

                mFavDishDetails?.let {
                    var image = ""
                    if (it.imageSource == Constants.DISH_IMAGE_SOURCE_ONLINE)
                        image = it.image

                    var cookingInstruction = ""
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                        cookingInstruction = Html.fromHtml(
                            it.directionToCook,
                            Html.FROM_HTML_MODE_COMPACT
                        ).toString()
                    } else {
                        @Suppress("Deprecated")
                        cookingInstruction = Html.fromHtml(it.directionToCook).toString()
                    }

                    extraText = "$image \n" +
                                    "\n Title:  ${it.title} \n\n Type:  ${it.type} \n\n Category:  ${it.category}" +
                                    "\n\n Ingredients:  ${it.ingredients} \n\n Instruction to Cook:  ${it.directionToCook}" +
                                    "\n\n Time required to cook the dish approx ${it.cookingTime} minutes."
                }

                val intent = Intent(Intent.ACTION_SEND)
                intent.type = type
                intent.putExtra(Intent.EXTRA_SUBJECT, subject)
                intent.putExtra(Intent.EXTRA_TEXT, extraText)
                startActivity(Intent.createChooser(intent, shareWith))

                return true
            }
        }

        return super.onOptionsItemSelected(item)
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

        mFavDishDetails = args.dishDetails

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
            //mBinding!!.tvCookingDirection.text = it.dishDetails.directionToCook

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
                mBinding!!.tvCookingDirection.text = Html.fromHtml(
                    it.dishDetails.directionToCook,
                    Html.FROM_HTML_MODE_COMPACT
                )
            } else {
                @Suppress("Deprecated")
                mBinding!!.tvCookingDirection.text = Html.fromHtml(it.dishDetails.directionToCook)
            }

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