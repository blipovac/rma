package com.example.bruno_lipovac_rma

import android.os.Bundle
import android.util.Log
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.doBeforeTextChanged
import com.example.bruno_lipovac_rma.databinding.ActivityPackageSenderBinding
import com.example.bruno_lipovac_rma.models.Delivery
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.random.Random
import com.google.android.gms.maps.SupportMapFragment

class PackageSenderActivity : AppCompatActivity() {

    lateinit var binding: ActivityPackageSenderBinding
    lateinit var db: FirebaseFirestore
    lateinit var userUid: String
    private lateinit var mMapFragment: SupportMapFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPackageSenderBinding.inflate(layoutInflater)

        db = FirebaseFirestore.getInstance()

        setContentView(binding.root)

        binding.sendOrderButton.setOnClickListener {
            postDelivery()
        }

        binding.showMapButton.setOnClickListener {
            openMap()
        }

        val extra = intent.extras

        userUid = extra?.getString("USER_UID").toString()

        mMapFragment = supportFragmentManager.findFragmentById(binding.mapFragment.id) as SupportMapFragment

        val fragmentTransaction = this.supportFragmentManager.beginTransaction()
        fragmentTransaction.hide(mMapFragment)
        fragmentTransaction.commit()
    }

    private fun openMap() {
        Log.d("OPEN_AMP", "TRYING TO SHOW MAP")
        val fragmentTransaction = this.supportFragmentManager.beginTransaction()
        fragmentTransaction.show(mMapFragment)
        fragmentTransaction.commit()
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