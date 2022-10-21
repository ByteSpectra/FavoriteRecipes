package com.mobivone.favrecipes.model.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.mobivone.favrecipes.model.entities.FavDish

@Database(entities = [FavDish::class], version = 1)
abstract class FavDishDatabase: RoomDatabase() {

    abstract fun favDishDao(): DAO

    companion object {
        @Volatile
        private var INSTANCE: FavDishDatabase? = null

        fun getDatabase(context: Context): FavDishDatabase {
            // if the instance is not null, then return it.
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FavDishDatabase::class.java,
                    "fav_dish_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                // return instance
                instance
            }
        }
    }

}