package com.example.cataloging

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() { 
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
    }
    //checking if there is any user logged in currently or not.
    override fun onStart() {
        if (FirebaseAuth.getInstance().currentUser != null){
            val intent = Intent(this,ProductActivity::class.java)
            startActivity(intent)
        }else{
            Log.d("LoginActivity","Pingged the firebase")
        }
        super.onStart()
    }
}