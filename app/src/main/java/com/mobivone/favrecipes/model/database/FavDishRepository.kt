package com.mobivone.favrecipes.model.database

import androidx.annotation.WorkerThread
import com.mobivone.favrecipes.model.entities.FavDish
import kotlinx.coroutines.flow.Flow

class FavDishRepository(private val favDishDao: DAO) {

    @WorkerThread
    suspend fun insertFavDish(favDish: FavDish) {
        favDishDao.insertFavDishDetails(favDish)
    }

    val allDishesList: Flow<List<FavDish>> = favDishDao.getAllDishesList()

    @WorkerThread
    suspend fun updateFavDishData(favDish: FavDish)
    {
        favDishDao.updateFavDishDetails(favDish)
    }

    val allFavDishesList: Flow<List<FavDish>> = favDishDao.getAllFavDishesList()

    @WorkerThread
    suspend fun deleteFavDishData(favDish: FavDish){
        favDishDao.deleteFavDish(favDish)
    }
}