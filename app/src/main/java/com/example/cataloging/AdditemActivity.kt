package com.example.cataloging

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import com.example.cataloging.datamodels.Product
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.activity_additem.*
import java.util.*

class AdditemActivity : AppCompatActivity() {



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_additem)

        save_product_btn.setOnClickListener {
           uploadImageToFirebase()
        }

        select_photo_btn.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, 0)
        }
    }
    var selectedPhotoUri: Uri? = null
    private fun saveProduct(profileImageUrl: String) {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: ""
        val ref = FirebaseDatabase.getInstance().getReference("/products/$uid")
        val product = Product(product_name_txt.text.toString(),profileImageUrl,product_quantity_txt.text.toString().toInt(),product_price_txt.text.toString().toDouble())
        val key = ref.push().key
        ref.child(key!!).setValue(product)
                .addOnSuccessListener {
                    Toast.makeText(this, "The Product is added", Toast.LENGTH_SHORT).show()
                    finish()
                }
                .addOnCanceledListener {
                    Toast.makeText(this, "Try Again", Toast.LENGTH_SHORT).show()
                }
    }

    private fun uploadImageToFirebase() {
        if (selectedPhotoUri == null) return
        Log.d("AddItem","Selected Photo Uri : "+selectedPhotoUri.toString())
        val filename = UUID.randomUUID().toString()
        val ref = FirebaseStorage.getInstance().getReference("/images/$filename")

        ref.putFile(selectedPhotoUri!!)
                .addOnSuccessListener { it ->
                    Log.d("AddItem", "Successfully uploaded image: ${it.metadata?.path}")

                    ref.downloadUrl.addOnSuccessListener {
                        Log.d("AddItem", "File Location: $it")

                        saveProduct(it.toString())
                    }
                }
                .addOnFailureListener {
                    Log.d("AddItem", "Failed to upload image to storage: ${it.message}")
                }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null){
            selectedPhotoUri = data.data
//            val bitmapDrawable = BitmapDrawable.createFromPath(selectedPhotoUri.toString())
            select_photo_btn.alpha = 0f
//            select_img_img.setImageDrawable(bitmapDrawable)
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)
            val bitmapDrawable = BitmapDrawable(bitmap)
            select_img_img.setImageBitmap(bitmap)
        }
    }

}