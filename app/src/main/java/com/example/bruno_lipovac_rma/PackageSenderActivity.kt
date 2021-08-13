package com.example.bruno_lipovac_rma

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.example.bruno_lipovac_rma.databinding.ActivityPackageSenderBinding
import com.example.bruno_lipovac_rma.models.Delivery
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.random.Random

class PackageSenderActivity : AppCompatActivity() {

    lateinit var binding: ActivityPackageSenderBinding
    lateinit var db: FirebaseFirestore
    lateinit var userUid: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPackageSenderBinding.inflate(layoutInflater)

        db = FirebaseFirestore.getInstance()

        setContentView(binding.root)

        binding.sendOrderButton.setOnClickListener {
            postDelivery()
        }

        binding.pickupAddress.setOnClickListener {
            
        }

        val extra = intent.extras

        userUid = extra?.getString("USER_UID").toString()
    }

    private fun postDelivery() {
        val randomInt = Random.nextInt(0, 9999)

        val deliveryPin = randomInt.toString().padStart(4, '0')

        val delivery = Delivery(
            binding.pickupAddress.editText?.text.toString(),
            binding.deliveryAddress.editText?.text.toString(),
            binding.deliveryNote.editText?.text.toString(),
            false,
            deliveryPin,
            userUid
        )

        db.collection("deliveries")
            .add(delivery)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(baseContext,
                        "Delivery successfully posted. Delivery pin: $deliveryPin", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    Log.d("POST_DELIVERY", "delivery post failed")

                    Toast.makeText(baseContext, "Delivery posting failed", Toast.LENGTH_SHORT)
                        .show()
                }
            }
    }
}