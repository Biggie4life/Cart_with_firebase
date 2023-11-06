package com.example.cartwithfirebase

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cartwithfirebase.Models.CartModel
import com.example.cartwithfirebase.adapter.MyCartAdapter
import com.example.cartwithfirebase.listener.ICartLoadListener
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_cart.btnBack
import kotlinx.android.synthetic.main.activity_cart.recycler_cart
import kotlinx.android.synthetic.main.activity_cart.txtTotal
import kotlinx.android.synthetic.main.activity_main.mainLayout

class CartActivity : AppCompatActivity(), ICartLoadListener {
    var cartLoadListener:ICartLoadListener?= null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)
        init()
        loadCartFromFirebase()
    }

    private fun loadCartFromFirebase() {
        val cartModels : MutableList<CartModel> = ArrayList()
        FirebaseDatabase.getInstance()
            .getReference("Cart")
            .child("UNIQUE_USER_ID")
            .addListenerForSingleValueEvent(object: ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (cartSnapshot in snapshot.children)
                    {
                        val cartModel = cartSnapshot.getValue(CartModel::class.java)
                        cartModel!!.key = cartSnapshot.key
                        cartModels.add(cartModel)
                    }
                    cartLoadListener!!.onCartLoadSuccess(cartModels)
                }

                override fun onCancelled(error: DatabaseError) {
                    cartLoadListener!!.onCartLoadFailed(error.message)
                }

            })
    }

    private fun init(){
        cartLoadListener = this
        val layoutManager = LinearLayoutManager(this)
        recycler_cart!!.layoutManager = layoutManager
        recycler_cart!!.addItemDecoration(DividerItemDecoration(this,layoutManager.orientation))
        btnBack!!.setOnClickListener{finish()}
    }

    override fun onCartLoadSuccess(cartModelList: List<CartModel>) {
         var sum = 0.0
        for (cartModel in cartModelList!!){
            sum+= cartModel!!.totalPrice
        }
        txtTotal.text = StringBuilder("$").append(sum)
        val adapter = MyCartAdapter(this,cartModelList)
        recycler_cart!!.adapter = adapter
    }

    override fun onCartLoadFailed(message: String?) {
        Snackbar.make(mainLayout,message!!, Snackbar.LENGTH_LONG).show()
    }
}