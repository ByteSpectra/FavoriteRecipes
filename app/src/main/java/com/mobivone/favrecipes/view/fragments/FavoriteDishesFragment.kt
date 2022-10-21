package com.mobivone.favrecipes.view.fragments

import android.app.Application
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import com.mobivone.favrecipes.databinding.FragmentFavoriteDishesBinding
import com.mobivone.favrecipes.model.entities.FavDish
import com.mobivone.favrecipes.utils.FavDishApplication
import com.mobivone.favrecipes.view.activities.MainActivity
import com.mobivone.favrecipes.view.adapters.AllDishesFragmentAdapter
import com.mobivone.favrecipes.viewModel.DashboardViewModel
import com.mobivone.favrecipes.viewModel.FavDishViewModel
import com.mobivone.favrecipes.viewModel.FavDishViewModelFactory

class FavoriteDishesFragment : Fragment() {

    private var _binding: FragmentFavoriteDishesBinding? = null

    private val mFavDishViewModel: FavDishViewModel by viewModels {
        FavDishViewModelFactory(((requireActivity().application) as FavDishApplication).repository)
    }

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFavoriteDishesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding!!.rvFavDishesList.layoutManager = GridLayoutManager(requireActivity(), 2)
        val adapter = AllDishesFragmentAdapter(this@FavoriteDishesFragment)

        _binding!!.rvFavDishesList.adapter = adapter

        mFavDishViewModel.favDishesList.observe(viewLifecycleOwner) { dishes ->
            dishes.let {
                if (it.isNotEmpty()){
                    _binding!!.rvFavDishesList.visibility = View.VISIBLE
                    _binding!!.tvNoFavDishesAddedYet.visibility = View.GONE

                    adapter.dishesList(it)
                } else {
                    _binding!!.rvFavDishesList.visibility = View.GONE
                    _binding!!.tvNoFavDishesAddedYet.visibility = View.VISIBLE
                }
            }
        }
    }

    fun GoToDishDetailsFragment(favDish: FavDish){
        findNavController().navigate(FavoriteDishesFragmentDirections.actionFavoriteDishesToDishDetails(favDish))

        if (requireActivity() is MainActivity){
            (activity as MainActivity).hideBottomNavView()
        }
    }

    override fun onResume() {
        super.onResume()

        if (requireActivity() is MainActivity)
            (activity as MainActivity).showBottomNavView()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}