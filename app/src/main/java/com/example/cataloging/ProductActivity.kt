package com.example.cataloging

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class ProductActivity : AppCompatActivity() {
    private var mAuth: FirebaseAuth? = null
    private var mCurrentUser: FirebaseUser? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_product)



        mAuth = FirebaseAuth.getInstance()
        mCurrentUser = mAuth!!.currentUser

        val mLogoutBtn = findViewById<Button>(R.id.logout_btn)

        mLogoutBtn.setOnClickListener {
            mAuth!!.signOut()
            sendUserToLogin()
        }

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