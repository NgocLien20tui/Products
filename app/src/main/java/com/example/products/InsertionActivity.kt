package com.example.products

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Base64
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.example.products.databinding.ActivityInsertionBinding
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import java.io.ByteArrayOutputStream
import java.io.IOException

class InsertionActivity : AppCompatActivity() {
    private val PICK_IMAGE_REQUEST = 1
    private var filePath: Uri? = null

    private lateinit var dbRef: DatabaseReference
    private lateinit var binding: ActivityInsertionBinding
    private lateinit var img: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityInsertionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        img = ""
        dbRef = FirebaseDatabase.getInstance().getReference("Products")

        binding.btnSaveData.setOnClickListener {
            saveProductData()
        }
        binding.btnImage.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select Image"), PICK_IMAGE_REQUEST)
        }
    }
    @Deprecated("Deprecated in Java")
    @SuppressLint("Recycle")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.data != null) {
            filePath = data.data
            try {
                val inputStream = contentResolver.openInputStream(filePath!!)
                val bytes = ByteArrayOutputStream()
                val buffer = ByteArray(1024)
                var bytesRead: Int
                while (inputStream!!.read(buffer).also { bytesRead = it } != -1) {
                    bytes.write(buffer, 0, bytesRead)
                }
                img = Base64.encodeToString(bytes.toByteArray(), Base64.DEFAULT)
                val imageBytes = Base64.decode(img, Base64.DEFAULT)
                val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
                Glide.with(this).load(decodedImage).diskCacheStrategy(
                    DiskCacheStrategy.ALL).circleCrop().into(binding.imgProduct)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }
    private fun saveProductData() {
        val prdName = binding.editTextName.text.toString()
        val prdCategory = binding.editTextCategory.text.toString()
        val prdPrice = binding.editTextProductPrice.text.toString()

        if(prdName.isEmpty()){
            binding.editTextName.error = "Please Enter Name"
        }
        if(prdCategory.isEmpty()){
            binding.editTextCategory.error = "Please Enter Category"
        }
        if(prdPrice.isEmpty()){
            binding.editTextProductPrice.error = "Please Enter Price"
        }


        val prdId = dbRef.push().key!!
        val product = ProductModel(prdId, prdName, prdCategory, prdPrice, img)

        dbRef.child(prdId).setValue(product)
            .addOnCanceledListener {
                Toast.makeText(this, "Cancel saved", Toast.LENGTH_SHORT).show()

            }
            .addOnSuccessListener {
                Toast.makeText(this, "Data saved Success", Toast.LENGTH_SHORT).show()
            }
            .addOnCompleteListener{
                Toast.makeText(this, "Data saved", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener{
                Toast.makeText(this, "Insert Failed", Toast.LENGTH_SHORT).show()
            }
    }
}