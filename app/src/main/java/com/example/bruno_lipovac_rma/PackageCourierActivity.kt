package com.example.bruno_lipovac_rma

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.bruno_lipovac_rma.models.Delivery
import com.example.bruno_lipovac_rma.models.PackageCourierViewModel
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.firestore.FirebaseFirestore

class PackageCourierActivity : AppCompatActivity() {
    lateinit var db: FirebaseFirestore
    private val model: PackageCourierViewModel by viewModels()

    private lateinit var deliveryListAdapter: DeliveryListAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_package_courier)

        db = FirebaseFirestore.getInstance()

        deliveryListAdapter = DeliveryListAdapter { delivery -> adapterOnClick(delivery) }

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
                        val deliveryGeoHashMap: Map<String, Any> =
                            document.data["deliveryLatLng"] as Map<String, Any>

                        val pickupGeoHashMap : Map<String, Any> =
                            document.data["pickupLatLng"] as Map<String, Any>

                        val deliveryLatLng = LatLng(
                            deliveryGeoHashMap["latitude"] as Double,
                            deliveryGeoHashMap["longitude"] as Double
                        )

                        val pickupLatLng = LatLng(
                            pickupGeoHashMap["latitude"] as Double,
                            pickupGeoHashMap["longitude"] as Double
                        )

                        val courierId = if (document.data["courierId"] == null) {
                            null
                        } else {
                            document.data["courierId"].toString()
                        }

                        deliveries.add(
                            Delivery(
                                document.data["pickupAddress"].toString(),
                                document.data["deliverAddress"].toString(),
                                document.data["notes"].toString(),
                                document.data["isComplete"] == "true",
                                document.data["deliveryPin"].toString(),
                                document.data["userUid"].toString(),
                                pickupLatLng,
                                deliveryLatLng,
                                document.id,
                                courierId
                            )
                        )
                    }

                    this.model.deliveries.value = deliveries
                } else {
                    Log.w("TAG", "Error getting documents.", task.exception)
                }
            }
    }

    private fun adapterOnClick(delivery: Delivery) {
        val intent = Intent(this, DeliveryDetailActivity()::class.java)

        intent.putExtra("PICKUP_ADDRESS", delivery.pickupAddress).toString()
        intent.putExtra("DELIVERY_ADDRESS", delivery.deliverAddress).toString()
        intent.putExtra("NOTES", delivery.notes).toString()
        intent.putExtra("PICKUP_LAT", delivery.pickupLatLng?.latitude)
        intent.putExtra("PICKUP_LNG", delivery.pickupLatLng?.longitude)
        intent.putExtra("DELIVERY_LAT", delivery.deliveryLatLng?.latitude)
        intent.putExtra("DELIVERY_LNG", delivery.deliveryLatLng?.longitude)
        intent.putExtra("DOCUMENT_ID", delivery.documentId)
        intent.putExtra("COURIER_ID", delivery.courierId)
        intent.putExtra("DELIVERY_PIN", delivery.deliveryPin)

        startActivity(intent)
    }
}