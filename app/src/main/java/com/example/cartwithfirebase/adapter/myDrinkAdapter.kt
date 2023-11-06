package com.example.cartwithfirebase.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.cartwithfirebase.Models.CartModel
import com.example.cartwithfirebase.Models.DrinksModel
import com.example.cartwithfirebase.R
import com.example.cartwithfirebase.eventbus.UpdateCartEvent
import com.example.cartwithfirebase.listener.ICartLoadListener
import com.example.cartwithfirebase.listener.IRecyclerClickListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.greenrobot.eventbus.EventBus

class myDrinkAdapter(private val context: Context,
                     private val list: List<DrinksModel>,
                     //lelhohonolo
                     private val cartListener:ICartLoadListener
                     ):
    RecyclerView.Adapter<myDrinkAdapter.myDrinkViewHolder>(){

    class myDrinkViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView),
        View.OnClickListener {

         var imageView: ImageView?=null
         var txtName: TextView?=null
         var txtPrice: TextView?=null

        //lehlohonolo
        private var clickListener:IRecyclerClickListener? = null

        //lehlohonolo
         fun setClickListener(clickListener: IRecyclerClickListener)
        {
            this.clickListener = clickListener;
         }

        init {
            imageView = itemView.findViewById(R.id.imageView) as ImageView
            txtName = itemView.findViewById(R.id.txtName) as TextView
            txtPrice = itemView.findViewById(R.id.txtPrice) as TextView

            //lehlohonolo
            itemView.setOnClickListener(this)
        }

        //lehlohonolo
        override fun onClick(v: View?) {
            clickListener!!.onItemClickListener(v,adapterPosition)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): myDrinkViewHolder {
        return myDrinkViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_drink_item,parent,false))
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: myDrinkViewHolder, position: Int) {
        Glide.with(context).load(list[position].image).into(holder.imageView!!)

        holder.txtName!!.text = StringBuilder().append(list[position].name)
        holder.txtPrice!!.text = StringBuilder("$").append(list[position].price)

        // lehlohonolo
        holder.setClickListener(object:IRecyclerClickListener{
            override fun onItemClickListener(view: View?, position: Int){
                addToCart(list[position])
            }

        })
    }

    //lehlohonolo
    private fun addToCart(drinkModel: DrinksModel){
        val userCart = FirebaseDatabase.getInstance()
            .getReference("Cart")
            .child("UNIQUE_USER_ID")//Here is simular user ID, you can use Firebase Auth uid here

        userCart.child(drinkModel.key!!)
            .addListenerForSingleValueEvent(object:ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) //If item already in cart, just update
                    {
                        val cartModel = snapshot.getValue(CartModel::class.java)
                        val updateData: MutableMap<String,Any> = HashMap()
                        cartModel!!.quantity = cartModel!!.quantity+1;
                        updateData["quantity"] = cartModel!!.quantity
                        updateData["totalPrice"] = cartModel!!.quantity * cartModel.price!!.toFloat()
                            //contiune
                        userCart.child(drinkModel.key!!)
                            .updateChildren(updateData)
                            .addOnSuccessListener {
                                EventBus.getDefault().postSticky(UpdateCartEvent())
                                cartListener.onCartLoadFailed("Success add to cart")
                            }
                            .addOnFailureListener{e -> cartListener.onCartLoadFailed(e.message)}
                    }
                    else //If item not in cart, add new
                    {
                        val cartModel = CartModel()
                        cartModel.key = drinkModel.key
                        cartModel.name = drinkModel.name
                        cartModel.image = drinkModel.image
                        cartModel.price = drinkModel.price
                        cartModel.quantity = 1
                        cartModel.totalPrice = drinkModel.price!!.toFloat()

                        userCart.child(drinkModel.key!!)
                            .setValue(cartModel)
                            .addOnSuccessListener {
                                EventBus.getDefault().postSticky(UpdateCartEvent())
                                cartListener.onCartLoadFailed("Success add to cart")
                            }
                            .addOnFailureListener{e -> cartListener.onCartLoadFailed(e.message)}
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    cartListener.onCartLoadFailed(error.message)
                }

            })
    }



}