package com.example.bruno_lipovac_rma

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bruno_lipovac_rma.models.Delivery
import com.example.bruno_lipovac_rma.models.PackageCourierViewModel
import com.google.firebase.firestore.FirebaseFirestore

class PackageCourierActivity : AppCompatActivity() {
    lateinit var db: FirebaseFirestore
    private val model: PackageCourierViewModel by viewModels()

    private lateinit var deliveryListAdapter: DeliveryListAdapter

    @SuppressLint("NotifyDataSetChanged")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_package_courier)

        db = FirebaseFirestore.getInstance()

        deliveryListAdapter = DeliveryListAdapter()

        val recyclerView: RecyclerView = findViewById(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = deliveryListAdapter

        val deliveriesObserver = Observer<MutableList<Delivery>> { delivery ->
            Log.d("DELIVERIES_OBSERVER", "EYOOOOO")
            deliveryListAdapter.submitList(delivery)
        }

        model.deliveries.observe(this, deliveriesObserver)

        db.collection("deliveries")
            .get()
            .addOnFailureListener { exception ->
                Log.d("USER_FETCH", "user fetch failed: ", exception)
            }
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val deliveries: MutableList<Delivery> = mutableListOf<Delivery>()

                    for (document in task.result) {
                        deliveries.add(
                            Delivery(
                                document.data["pickupAddress"].toString(),
                                document.data["deliverAddress"].toString(),
                                document.data["notes"].toString(),
                                document.data["isComplete"] == "true",
                                document.data["deliveryPin"].toString(),
                                document.data["userUid"].toString(),
                                null,
                                null
                            )
                        )
                    }

                    this.model.deliveries.value = deliveries
                } else {
                    Log.w("TAG", "Error getting documents.", task.exception)
                }
            }
    }
}