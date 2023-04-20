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
import com.example.products.databinding.ActivityProductDetailBinding
import com.google.firebase.database.FirebaseDatabase
import java.io.ByteArrayOutputStream
import java.io.IOException

class ProductDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityProductDetailBinding
    private val PICK_IMAGE_REQUEST = 1
    private var filePath: Uri? = null
    private lateinit var img: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        img = intent.getStringExtra("prdImg").toString()
        setValueToView()
        val imageBytes = Base64.decode(img, Base64.DEFAULT)
        val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        Glide.with(this).load(decodedImage).diskCacheStrategy(
            DiskCacheStrategy.ALL).circleCrop().into(binding.imgProduct)
        binding.btnDelete.setOnClickListener {
            deleteRecord(
                intent.getStringExtra("prdId").toString()
            )
        }

        binding.btnUpdate.setOnClickListener {
            val prdId = binding.editTextId.text.toString()
            val prdName = binding.editTextName.text.toString()
            val prdCategory = binding.editTextAge.text.toString()
            val prdPrice = binding.editTextSalary.text.toString()
            updateEmpData(
                prdId,
                prdName,
                prdCategory,
                prdPrice
            )
        }

        binding.imgProduct.setOnClickListener {
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
    private fun updateEmpData(id: String, name: String, age: String, salary: String) {
        val dbRef = FirebaseDatabase.getInstance().getReference("Products").child(id)
        val empData = ProductModel(id, name, age, salary)
        dbRef.setValue(empData)
        Toast.makeText(this, "Update success", Toast.LENGTH_SHORT).show()
    }

    private fun deleteRecord(id: String) {
        val dbRef = FirebaseDatabase.getInstance().getReference("Products").child(id)
        val mTask = dbRef.removeValue()
        mTask.addOnSuccessListener {
            Toast.makeText(this, "Delete Success", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, FetchingActivity::class.java)
            finish()
            startActivity(intent)
        }.addOnFailureListener{
            Toast.makeText(this, "Delete Failure", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setValueToView() {
        binding.editTextId.setText(intent.getStringExtra("prdId"))
        binding.editTextName.setText(intent.getStringExtra("prdName"))
        binding.editTextAge.setText(intent.getStringExtra("prdCategory"))
        binding.editTextSalary.setText(intent.getStringExtra("prdPrice"))
        val imageBytes = Base64.decode(intent.getStringExtra("prdImage").toString(), Base64.DEFAULT)
        val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        Glide.with(this).load(decodedImage).diskCacheStrategy(
            DiskCacheStrategy.ALL).circleCrop().into(binding.imgProduct)
    }
}