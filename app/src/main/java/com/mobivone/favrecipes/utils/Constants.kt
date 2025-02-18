package com.mobivone.favrecipes.utils

object Constants {

    const val DISTH_TYPE: String = "DishType"
    const val DISTH_CATEGORY: String = "DishCategory"
    const val DISTH_COOKING_TIME: String = "DishCookingTime"

    const val DISH_IMAGE_SOURCE_LOCAL: String = "Local"
    const val DISH_IMAGE_SOURCE_ONLINE: String = "Online"

    const val EXTRA_DISH_DETAILS: String = "DishDetails"

    const val ALL_ITEMS: String = "ALL"
    const val FILTER_SELCTION: String = "FILTERSELECTION"

    const val API_ENDPOINT: String = "recipes/random"
    const val API_KEY: String = "apiKey"
    const val LIMIT_LICENCE: String = "limitLicence"
    const val TAGS: String = "tags"
    const val NUMBER: String = "number"

    const val BASE_URL: String = "https://api.spoonacular.com/"
    const val API_KEY_VALUE: String = "9be736bffcf045d1bbfeabf134bab596"
    const val LIMIT_LICENSE_VALUE: Boolean = true
    const val TAGS_VALUE: String = "vegetarian, dessert"
    const val NUMBER_VALUE: Int = 1

    const val NOTIFICATION_ID = "FavDish_Nofification_Id"
    const val NOTIFICATION_NAME = "FavDish"
    const val NOTIFICATION_CHANNEL = "FavDish_Channel_01"

    fun dishTypes(): ArrayList<String> {
        val list = ArrayList<String>()
        list.add("breakfast")
        list.add("lunch")
        list.add("dinner")
        list.add("snack")
        list.add("dessert")
        list.add("other")
        return list
    }

    fun dishCategories(): ArrayList<String> {
        val list = ArrayList<String>()
        list.add("Pizza")
        list.add("BBQ")
        list.add("Burger")
        list.add("Cafe")
        list.add("Bakery")
        list.add("Other")
        return list
    }

    fun dishCookTime(): ArrayList<String> {
        val list = ArrayList<String>()
        list.add("5")
        list.add("10")
        list.add("15")
        list.add("20")
        list.add("25")
        list.add("30")
        return list
    }

}