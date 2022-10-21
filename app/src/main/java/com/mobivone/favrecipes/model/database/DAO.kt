package com.mobivone.favrecipes.model.database

import androidx.room.*
import com.mobivone.favrecipes.model.entities.FavDish
import kotlinx.coroutines.flow.Flow

@Dao
interface DAO {

    @Insert
    suspend fun insertFavDishDetails(faveDish: FavDish)

    @Query("SELECT * FROM FavoriteDishes ORDER BY ID")
    fun getAllDishesList(): Flow<List<FavDish>>

    @Update
    suspend fun updateFavDishDetails(favDish: FavDish)

    @Query("SELECT * FROM FavoriteDishes WHERE favorite_dish == 1")
    fun getAllFavDishesList(): Flow<List<FavDish>>

    @Delete
    suspend fun deleteFavDish(faveDish: FavDish)

    //@Query()
}