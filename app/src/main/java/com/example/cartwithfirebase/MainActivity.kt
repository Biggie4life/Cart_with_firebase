package com.example.cartwithfirebase

import android.content.Intent
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
import com.example.cartwithfirebase.eventbus.UpdateCartEvent
import com.example.cartwithfirebase.listener.ICartLoadListener
import com.example.cartwithfirebase.listener.IDrinkLoadListener
import com.example.cartwithfirebase.utils.SpaceItemDecoration
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.nex3z.notificationbadge.NotificationBadge
import kotlinx.android.synthetic.main.activity_main.badge
import kotlinx.android.synthetic.main.activity_main.btnCart
import kotlinx.android.synthetic.main.activity_main.mainLayout
import kotlinx.android.synthetic.main.activity_main.recycler_drink
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

class MainActivity : AppCompatActivity(), IDrinkLoadListener, ICartLoadListener {


    lateinit var drinkLoadListener: IDrinkLoadListener
    //lehlohonolo
    lateinit var cartLoadListener: ICartLoadListener

    //lehlohonolo
    override fun onStart() {
        super.onStart()
        EventBus.getDefault().register(this )
    }

    //lehlohonolo
    override fun onStop() {
        super.onStop()
        if (EventBus.getDefault().hasSubscriberForEvent(UpdateCartEvent::class.java))
            EventBus.getDefault().removeStickyEvent(UpdateCartEvent::class.java)
        EventBus.getDefault().unregister(this)
    }

    //lehlohonolo
    @Subscribe(threadMode = ThreadMode.MAIN, sticky = true)
    public fun onUpdateCartEvent(event:UpdateCartEvent)
    {
        countCartFromFirebase()
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        init()
        loadDrinksFromFirebase()
        countCartFromFirebase()
    }

    //lehlohonolo
    private fun countCartFromFirebase() {
        val cartModels : MutableList<CartModel> = ArrayList()
        FirebaseDatabase.getInstance()
            .getReference("Cart")
            .child("UNIQUE_USER_ID")
            .addListenerForSingleValueEvent(object:ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (cartSnapshot in snapshot.children)
                    {
                        val cartModel = cartSnapshot.getValue(CartModel::class.java)
                        cartModel!!.key = cartSnapshot.key
                        cartModels.add(cartModel)
                    }
                    cartLoadListener.onCartLoadSuccess(cartModels)
                }

                override fun onCancelled(error: DatabaseError) {
                    cartLoadListener.onCartLoadFailed(error.message)
                }

            })
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
        //lehlohonolo
        cartLoadListener = this

        val gridLayoutManager = GridLayoutManager(this,2)
        recycler_drink.layoutManager = gridLayoutManager
        recycler_drink.addItemDecoration(SpaceItemDecoration())

        btnCart.setOnClickListener { startActivity(Intent(this,CartActivity::class.java)) }
    }

    override fun onDrinkLoadSuccess(drinkModelList: List<DrinksModel>?) {
        val adapter = myDrinkAdapter(this,drinkModelList!!,cartLoadListener)
        recycler_drink.adapter = adapter
    }

    override fun onDrinkLoadFailed(message: String?) {
        Snackbar.make(mainLayout,message!!,Snackbar.LENGTH_LONG).show()
    }

    override fun onCartLoadSuccess(cartModelList: List<CartModel>) {
        //leholohonolo
        var cartSum = 0
            for (cartModel in cartModelList!!) cartSum+= cartModel!!.quantity
            badge!!.setNumber(cartSum)
    }

    override fun onCartLoadFailed(message: String?) {
        Snackbar.make(mainLayout,message!!,Snackbar.LENGTH_LONG).show()
    }

}