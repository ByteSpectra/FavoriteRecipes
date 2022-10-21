package com.mobivone.favrecipes.utils

import android.app.Application
import com.mobivone.favrecipes.model.database.FavDishDatabase
import com.mobivone.favrecipes.model.database.FavDishRepository

class FavDishApplication: Application() {

    private val database by lazy {
        FavDishDatabase.getDatabase(this@FavDishApplication)
    }

    val repository by lazy { FavDishRepository(database.favDishDao()) }
}