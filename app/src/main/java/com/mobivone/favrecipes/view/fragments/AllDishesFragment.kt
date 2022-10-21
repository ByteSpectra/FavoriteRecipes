package com.mobivone.favrecipes.view.fragments

import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.mobivone.favrecipes.R
import com.mobivone.favrecipes.databinding.DialogCustomListBinding
import com.mobivone.favrecipes.databinding.FragmentAllDishesBinding
import com.mobivone.favrecipes.model.entities.FavDish
import com.mobivone.favrecipes.utils.Constants
import com.mobivone.favrecipes.utils.FavDishApplication
import com.mobivone.favrecipes.view.activities.AddUpdateRecipes
import com.mobivone.favrecipes.view.activities.MainActivity
import com.mobivone.favrecipes.view.adapters.AllDishesFragmentAdapter
import com.mobivone.favrecipes.view.adapters.CustomListItemAdapter
import com.mobivone.favrecipes.viewModel.FavDishViewModel
import com.mobivone.favrecipes.viewModel.FavDishViewModelFactory

class AllDishesFragment : Fragment() {

    private var _binding: FragmentAllDishesBinding? = null

    private lateinit var mFavDishAdapter: AllDishesFragmentAdapter
    private lateinit var mCustomDialog: Dialog

    private val mFavDishViewModel: FavDishViewModel by viewModels {
        FavDishViewModelFactory((requireActivity().application as FavDishApplication).repository)
    }

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentAllDishesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        _binding!!.rvDishesList.layoutManager = GridLayoutManager(requireActivity(), 2)
        mFavDishAdapter = AllDishesFragmentAdapter(this@AllDishesFragment)

        _binding!!.rvDishesList.adapter = mFavDishAdapter

        mFavDishViewModel.allDishesList.observe(viewLifecycleOwner){ dishes ->
            dishes.let {
                if (dishes.isNotEmpty()){
                    _binding!!.rvDishesList.visibility = View.VISIBLE
                    _binding!!.tvNoDishesAddedYet.visibility = View.GONE

                    mFavDishAdapter.dishesList(it)
                } else {
                    _binding!!.rvDishesList.visibility = View.GONE
                    _binding!!.tvNoDishesAddedYet.visibility = View.VISIBLE
                }
            }
        }
    }

    fun DeleteDish(dish: FavDish) {

        val builder = AlertDialog.Builder(requireActivity())
        builder.setTitle(resources.getString(R.string.adpater_menu_delete_dish))
        builder.setMessage(resources.getString(R.string.delete_dialog_msg, dish.title))
        builder.setIcon(android.R.drawable.ic_dialog_alert)

        builder.setPositiveButton(resources.getString(R.string.lbl_yes)){ dialog, _ ->
            mFavDishViewModel.delete(dish)
            dialog.dismiss()
        }
        builder.setNegativeButton(resources.getString(R.string.lbl_no)){ dialog, _ ->
            dialog.dismiss()
        }

        val alertDialog: AlertDialog = builder.create()
        alertDialog.setCancelable(false)
        alertDialog.show()
    }

    fun FilterDishesDialog()
    {
        mCustomDialog = Dialog(requireActivity())
        val binding: DialogCustomListBinding = DialogCustomListBinding.inflate(layoutInflater)
        binding.tvTitle.text = resources.getString(R.string.title_select_item_to_filter)
        mCustomDialog.setContentView(binding.root)

        val dishTypes = Constants.dishTypes()
        dishTypes.add(0, Constants.ALL_ITEMS)

        binding.rvList.layoutManager = LinearLayoutManager(requireActivity())
        val adapter = CustomListItemAdapter(requireActivity(), this@AllDishesFragment, dishTypes, Constants.FILTER_SELCTION)
        binding.rvList.adapter = adapter

        mCustomDialog.show()
    }

    fun GoToDishDetailsFragment(favDish: FavDish){
        findNavController().navigate(AllDishesFragmentDirections.actionAllDishesToDishDetails(favDish))

        if (requireActivity() is MainActivity){
            (activity as MainActivity).hideBottomNavView()
        }
    }

    override fun onResume() {
        super.onResume()

        if (requireActivity() is MainActivity)
            (activity as MainActivity).showBottomNavView()
    }

    fun filterSelection(filterItem: String){
        mCustomDialog.dismiss()

        Log.i("Filter Selection", filterItem)

        if (filterItem.equals(Constants.ALL_ITEMS)) {
            mFavDishViewModel.allDishesList.observe(viewLifecycleOwner){ dishes ->
                dishes.let {
                    if (dishes.isNotEmpty()){
                        _binding!!.rvDishesList.visibility = View.VISIBLE
                        _binding!!.tvNoDishesAddedYet.visibility = View.GONE

                        mFavDishAdapter.dishesList(it)
                    } else {
                        _binding!!.rvDishesList.visibility = View.GONE
                        _binding!!.tvNoDishesAddedYet.visibility = View.VISIBLE
                    }
                }
            }
        } else {

        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.all_dishes_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        when(item.itemId)
        {
            R.id.add_update_dish -> {
                startActivity(Intent(requireActivity(), AddUpdateRecipes::class.java))
                return true
            }
            R.id.action_filter_dishes -> {
                FilterDishesDialog()
                return true
            }

        }

        return super.onOptionsItemSelected(item)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}