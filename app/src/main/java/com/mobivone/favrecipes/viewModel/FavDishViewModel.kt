package com.mobivone.favrecipes.viewModel

import androidx.lifecycle.*
import com.mobivone.favrecipes.model.database.FavDishRepository
import com.mobivone.favrecipes.model.entities.FavDish
import kotlinx.coroutines.launch
import java.lang.IllegalArgumentException

class FavDishViewModel(private val repository: FavDishRepository): ViewModel() {

    fun insert(dish: FavDish) = viewModelScope.launch {
        repository.insertFavDish(dish)
    }

    val allDishesList: LiveData<List<FavDish>> = repository.allDishesList.asLiveData()

    fun update(dish: FavDish) = viewModelScope.launch {
        repository.updateFavDishData(dish)
    }

    val favDishesList: LiveData<List<FavDish>> = repository.allFavDishesList.asLiveData()

    fun delete(favDish: FavDish) = viewModelScope.launch {
        repository.deleteFavDishData(favDish)
    }
}


class FavDishViewModelFactory(private val repository: FavDishRepository): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        //return super.create(modelClass)
        if (modelClass.isAssignableFrom(FavDishViewModel::class.java)){
            @Suppress("UNCHECKED_CAST")
            return FavDishViewModel(repository) as T
        }

        throw IllegalArgumentException("Unknown ViewModel Class")
    }

}