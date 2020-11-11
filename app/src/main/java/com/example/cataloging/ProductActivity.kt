package com.example.cataloging

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.cataloging.adapters.ProductAdapter
import com.example.cataloging.authentication.LoginActivity
import com.example.cataloging.datamodels.ProductForEdit
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_product.*

class ProductActivity : AppCompatActivity() {
    private var mAuth: FirebaseAuth? = null
    private var mCurrentUser: FirebaseUser? = null
    private var productlist = mutableListOf<ProductForEdit>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product)
        // initializing the firebase components

        mAuth = FirebaseAuth.getInstance()
        mCurrentUser = mAuth!!.currentUser
        mCurrentUser?.uid?.let { Log.d("currentuser", it) }


        val uid = FirebaseAuth.getInstance().currentUser!!.uid
        val ref = FirebaseDatabase.getInstance().getReference("/products/$uid")
        ref.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for(h in snapshot.children){
                    val product = h.getValue(ProductForEdit::class.java)
                    val key = h.key.toString()
                    product!!.key = key
                    productlist.add(product)
                }
                //recyclerview
                recyclerview_product.layoutManager = LinearLayoutManager(this@ProductActivity)
                recyclerview_product.adapter = ProductAdapter(productlist)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ProductActivity, "No product in the list or check the internet", Toast.LENGTH_SHORT).show()
            }

        })

        recyclerview_product
    }




    //  inflating the menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu,menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.logout_menu -> {
            // do stuff
            mAuth!!.signOut()
            sendUserToLogin()
            true
        }
        R.id.add_item_menu ->{
            val intent = Intent(this@ProductActivity,AdditemActivity::class.java)
            startActivity(intent)
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    override fun onStart() {
        super.onStart()
        if (mCurrentUser == null) {
            sendUserToLogin()
        }
    }

    private fun sendUserToLogin() {
        val loginIntent = Intent(this, LoginActivity::class.java)
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        loginIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(loginIntent)
        finish()
    }
}