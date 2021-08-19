package com.example.bruno_lipovac_rma

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.lifecycle.Observer
import com.example.bruno_lipovac_rma.models.Delivery
import com.example.bruno_lipovac_rma.models.PackageCourierViewModel
import com.google.firebase.firestore.FirebaseFirestore

class PackageCourierActivity : AppCompatActivity() {
    lateinit var db: FirebaseFirestore
    private val model: PackageCourierViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_package_courier)
        db = FirebaseFirestore.getInstance()

        val deliveriesObserver = Observer<MutableList<Delivery>> {
            // todo implement adapter submit, probably have to change data type
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
                        deliveries.add(Delivery(
                            document.data["pickupAddress"].toString(),
                            document.data["deliveryAddress"].toString(),
                            document.data["notes"].toString(),
                            document.data["isComplete"] == "true",
                            document.data["deliveryPin"].toString(),
                            document.data["userUid"].toString(),
                            null,
                            null
                        ))
                    }

                    this.model.deliveries.value = deliveries
                } else {
                    Log.w("TAG", "Error getting documents.", task.exception)
                }
            }

    }
}