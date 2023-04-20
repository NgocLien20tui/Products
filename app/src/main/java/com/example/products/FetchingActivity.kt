package com.example.products

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.products.adapter.ProductAdapter
import com.example.products.databinding.ActivityFetchingBinding
import com.google.firebase.database.*
import com.google.firebase.database.R

class FetchingActivity : AppCompatActivity() {
    private lateinit var ds: ArrayList<ProductModel>
    private lateinit var dbRef: DatabaseReference
    private lateinit var binding: ActivityFetchingBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFetchingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.rvEmp.layoutManager = LinearLayoutManager(this)
        binding.rvEmp.setHasFixedSize(true)
        ds = arrayListOf<ProductModel>()
        getInfoEmp()

    }

    private fun getInfoEmp() {
        binding.rvEmp.visibility = View.GONE
        binding.txtLoadingData.visibility = View.VISIBLE

        dbRef = FirebaseDatabase.getInstance().getReference("Products")
        dbRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                ds.clear()
                if(snapshot.exists()){
                    for(empSnap in snapshot.children){
                        val empData = empSnap.getValue(ProductModel::class.java)
                        ds.add(empData!!)
                    }
                    val mAdapter = ProductAdapter(ds)
                    binding.rvEmp.adapter = mAdapter

                    mAdapter.setOnItemClickListener(object : ProductAdapter.OnItemClickListener{
                        override fun onItemCLick(position: Int) {
                            val i = Intent(this@FetchingActivity, ProductDetailActivity::class.java)
                            i.putExtra("prdId", ds[position].PrdId)
                            i.putExtra("prdName", ds[position].PrdName.toString())
                            i.putExtra("prdCategory", ds[position].PrdCategory)
                            i.putExtra("prdPrice", ds[position].PrdPrice)
                            i.putExtra("prdImg", ds[position].PrdImg)
                            startActivity(i)
                        }
                    })

                    binding.rvEmp.visibility = View.VISIBLE
                    binding.txtLoadingData.visibility = View.GONE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                TODO("Not yet implemented")
            }
        })
    }
}