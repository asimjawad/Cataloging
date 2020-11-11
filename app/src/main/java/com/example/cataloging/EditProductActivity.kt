package com.example.cataloging

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.cataloging.datamodels.ProductForEdit
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_edit_product.*
import java.util.*
import kotlin.collections.HashMap


class EditProductActivity : AppCompatActivity() {

    lateinit var url : String
    lateinit var productForEdit: ProductForEdit
    lateinit var refdel : DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_product)

        //getting the data of the selected product for editing or deleting purposes
        productForEdit = intent.getParcelableExtra("Value")!!
        val ref = FirebaseDatabase.getInstance()
        val uid = FirebaseAuth.getInstance().currentUser?.uid
        val ref1 = ref.reference.child("products/$uid")
        val ref2 = ref1.child(productForEdit.key)
        //reference for removing the product the specific product
        refdel  =ref2
//        Log.d("EditProduct",ref2.toString())
        //hashmap for inputting the data

//        hashMap["name"] = update_name_txt.text.toString()
        //loading the values and the image
        url = productForEdit.image
        //setting the values in the edittext
       setValues()

        //selecting the photo
        update_photo_btn.setOnClickListener {
            selectNewPhoto()
        }

        save_update_btn.setOnClickListener {
            saveTheChanges()
        }

    }

    private fun saveTheChanges() {
        if(update_name_txt.text.toString() != productForEdit.name){
            val hashMap1 : HashMap<String, Any> = HashMap()
            hashMap1["name"] = update_name_txt.text.toString()
            refdel.updateChildren(hashMap1)
        }
        if(update_price_txt.text.toString() != productForEdit.price.toString()){
            val hashMap3 : HashMap<String, Any> = HashMap()
            hashMap3["price"] = update_price_txt.text.toString().toDouble()
            refdel.updateChildren(hashMap3)
        }
        if(update_quantity_txt.text.toString() != productForEdit.quantity.toString()){
            val hashMap2 : HashMap<String, Any> = HashMap()
            hashMap2["quantity"] = update_quantity_txt.text.toString().toInt()
            refdel.updateChildren(hashMap2)
        }
    }

    //updating the image on the firebase
    private fun deleteTheImage() {
        val photoRef : StorageReference = FirebaseStorage.getInstance().getReferenceFromUrl(productForEdit.image)
    photoRef.delete().addOnSuccessListener { // File deleted successfully
        Toast.makeText(this, "The previous photo is deleted", Toast.LENGTH_SHORT).show()
        uploadImageToFirebase()
       // Log.d(TAG, "onSuccess: deleted file")
    }.addOnFailureListener { // Uh-oh, an error occurred!
        Toast.makeText(this, "Try again", Toast.LENGTH_SHORT).show()
       // Log.d(TAG, "onFailure: did not delete file")
    }
}

    private fun selectNewPhoto() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, 0)
    }
    var selectedPhotoUri: Uri? = null

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 0 && resultCode == Activity.RESULT_OK && data != null){
            selectedPhotoUri = data.data
//            val bitmapDrawable = BitmapDrawable.createFromPath(selectedPhotoUri.toString())
//            update_photo_btn.alpha = 0f
//            select_img_img.setImageDrawable(bitmapDrawable)
            val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedPhotoUri)
            val bitmapDrawable = BitmapDrawable(bitmap)
            update_img_img.setImageBitmap(bitmap)
            deleteTheImage()
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
                        val hashMap : HashMap<String, Any> = HashMap()
                        hashMap["image"] = it.toString()
                        hashMap["name"] = update_name_txt.text.toString()
                        hashMap["price"] = update_price_txt.text.toString().toDouble()
                        hashMap["quantity"] = update_quantity_txt.text.toString().toInt()
                        refdel.setValue(hashMap)
                    }
                }
                .addOnFailureListener {
                    Log.d("AddItem", "Failed to upload image to storage: ${it.message}")
                }
    }





    private fun setValues(){
        Picasso.get().load(url).into(update_img_img)
        update_name_txt.setText(productForEdit.name)
        update_price_txt.setText(productForEdit.price.toString())
        update_quantity_txt.setText(productForEdit.quantity.toString())

    }



    //  inflating the menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.edit_product_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean = when (item.itemId) {
        R.id.remove_product -> {
            // remove the product
            refdel.removeValue()
            Toast.makeText(this, "The product is removed", Toast.LENGTH_SHORT).show()
            finish()
            true
        }
        else -> super.onOptionsItemSelected(item)

    }


}