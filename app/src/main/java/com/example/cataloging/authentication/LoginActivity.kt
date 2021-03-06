package com.example.cataloging.authentication

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.cataloging.ProductActivity
import com.example.cataloging.R
import com.google.firebase.FirebaseException
import com.google.firebase.auth.*
import java.util.concurrent.TimeUnit

class LoginActivity : AppCompatActivity() {

    lateinit var mAuth: FirebaseAuth
    private var mCurrentUser: FirebaseUser? = null

    lateinit var mCountryCode: EditText
    lateinit var mPhoneNumber: EditText

    lateinit var mGenerateBtn: Button
    lateinit var mLoginProgress: ProgressBar

    lateinit var mLoginFeedbackText: TextView
    private lateinit var callbacks: PhoneAuthProvider.OnVerificationStateChangedCallbacks




    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)


        mAuth = FirebaseAuth.getInstance()
        mCurrentUser = mAuth.currentUser

        callbacks = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(phoneAuthCredential: PhoneAuthCredential) {
                Log.d("LLoginActivity","onverificationcompleted called")
                signInWithPhoneAuthCredential(phoneAuthCredential)

            }

            override fun onVerificationFailed(e: FirebaseException) {
                Log.d("LLoginActivity","onverificationfailed called")
                mLoginFeedbackText.text = "Verification Failed, please try again."
                mLoginFeedbackText.visibility = View.VISIBLE
                mLoginProgress.visibility = View.INVISIBLE
                mGenerateBtn.isEnabled = true
            }

            override fun onCodeSent(s: String, forceResendingToken: PhoneAuthProvider.ForceResendingToken) {
                Log.d("LLoginActivity","oncodesend called")
                super.onCodeSent(s, forceResendingToken)
                val otpIntent = Intent(this@LoginActivity, OtpActivity::class.java)
                otpIntent.putExtra("AuthCredentials", s)
                startActivity(otpIntent)
            }
        }





        mCountryCode = findViewById(R.id.country_code_text)
        mPhoneNumber = findViewById(R.id.phone_number_text)
        mGenerateBtn = findViewById(R.id.generate_btn)
        mLoginProgress = findViewById(R.id.login_progress_bar)
        mLoginFeedbackText = findViewById(R.id.login_form_feedback)


        mGenerateBtn.setOnClickListener {

            val country_code = mCountryCode.text.toString().trim()
            val phone_number = mPhoneNumber.text.toString().trim()
            val complete_phone_number = "+$country_code$phone_number"
            if (country_code.isEmpty() || phone_number.isEmpty()) {
                mLoginFeedbackText.text = "Please fill in the form to continue."
                mLoginFeedbackText.visibility = View.VISIBLE

            } else {
                mLoginProgress.visibility = View.VISIBLE
                mGenerateBtn.isEnabled = false

                val auth = FirebaseAuth.getInstance()
                val options =PhoneAuthOptions.newBuilder(auth).setPhoneNumber(complete_phone_number)
                    .setTimeout(60L,TimeUnit.SECONDS)
                    .setActivity(this)
                    .setCallbacks(callbacks)
                    .build()
                PhoneAuthProvider.verifyPhoneNumber(options)
                Log.d("LLoginActivity","Started")
            }
        }

    }
    //checking if there is any user logged in currently or not.
    override fun onStart() {
        if (mCurrentUser != null) {
            sendUserToHome()
        }
        super.onStart()
    }

    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        Log.d("LLoginActivity","signupwithphonenumber called")
        mAuth.signInWithCredential(credential)
            .addOnCompleteListener(
                this
            ) { task ->
                if (task.isSuccessful) {
                    Log.d("LLoginActivity","task successful called")
                    sendUserToHome()
                    // ...
                } else {
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        Log.d("LLoginActivity","task was not successful called")
                        // The verification code entered was invalid
                        mLoginFeedbackText.visibility = View.VISIBLE
                        val string = "There was an error verifying OTP"
                        mLoginFeedbackText.text = string
                    }
                }
                mLoginProgress.visibility = View.INVISIBLE
                mGenerateBtn.isEnabled = true
            }
    }

    private fun sendUserToHome() {
        Log.d("LLoginActivity","sendusertohome called.")
        val homeIntent = Intent(this@LoginActivity, ProductActivity::class.java)
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK)
        startActivity(homeIntent)
        finish()
    }

}