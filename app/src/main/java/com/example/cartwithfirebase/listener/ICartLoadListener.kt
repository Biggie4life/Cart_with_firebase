package com.example.cartwithfirebase.listener

import com.example.cartwithfirebase.Models.CartModel
import com.example.cartwithfirebase.Models.DrinksModel

interface ICartLoadListener {
    fun onCartLoadSuccess(cartModelList: List<CartModel>)
    fun onCartLoadFailed(message:String?)
}