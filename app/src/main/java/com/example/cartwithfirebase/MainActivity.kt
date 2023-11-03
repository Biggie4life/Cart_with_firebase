package com.example.cartwithfirebase

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.FrameLayout
import android.widget.RelativeLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.ButterKnife
import com.example.cartwithfirebase.Models.CartModel
import com.example.cartwithfirebase.Models.DrinksModel
import com.example.cartwithfirebase.adapter.myDrinkAdapter
import com.example.cartwithfirebase.listener.ICartLoadListener
import com.example.cartwithfirebase.listener.IDrinkLoadListener
import com.example.cartwithfirebase.utils.SpaceItemDecoration
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.nex3z.notificationbadge.NotificationBadge
import kotlinx.android.synthetic.main.activity_main.mainLayout
import kotlinx.android.synthetic.main.activity_main.recycler_drink

class MainActivity : AppCompatActivity(), IDrinkLoadListener, ICartLoadListener {


    lateinit var drinkLoadListener: IDrinkLoadListener
    var cartLoadListener: ICartLoadListener? = null



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        init()
        loadDrinksFromFirebase()
    }

    private fun loadDrinksFromFirebase() {
        val drinkModels : MutableList<DrinksModel> = ArrayList()

        FirebaseDatabase.getInstance().getReference("drink")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {

                    if (dataSnapshot.exists())
                    {
                        for (drinkSnapshot in dataSnapshot.children)
                        {
                            val drinksModels = drinkSnapshot.getValue(DrinksModel::class.java)
                            drinksModels!!.key = drinkSnapshot.key
                            drinkModels.add(drinksModels)
                        }
                        drinkLoadListener.onDrinkLoadSuccess(drinkModels)
                    }else
                    {
                        drinkLoadListener.onDrinkLoadFailed("Drink item not exits")
                    }

                }

                override fun onCancelled(databaseError: DatabaseError) {
                    drinkLoadListener.onDrinkLoadFailed(databaseError.message)
                }

            })
    }


    private fun init() {
        ButterKnife.bind(this)

        drinkLoadListener = this
        cartLoadListener = this

        val gridLayoutManager = GridLayoutManager(this,2)
        recycler_drink.layoutManager = gridLayoutManager
        recycler_drink.addItemDecoration(SpaceItemDecoration())
    }

    override fun onDrinkLoadSuccess(drinkModelList: List<DrinksModel>?) {
        val adapter = myDrinkAdapter(this,drinkModelList!!)
        recycler_drink.adapter = adapter
    }

    override fun onDrinkLoadFailed(message: String?) {
        Snackbar.make(mainLayout,message!!,Snackbar.LENGTH_LONG).show()
    }

    override fun onCartLoadSuccess(cartModelList: List<CartModel>) {
        TODO("Not yet implemented")
    }

    override fun onCartLoadFailed(message: String) {
        TODO("Not yet implemented")
    }

}