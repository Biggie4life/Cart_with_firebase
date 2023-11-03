package com.example.cartwithfirebase.listener

import com.example.cartwithfirebase.Models.DrinksModel

interface IDrinkLoadListener {
    fun onDrinkLoadSuccess(drinkModelList: List<DrinksModel>?)
    fun onDrinkLoadFailed(message: String?)

}