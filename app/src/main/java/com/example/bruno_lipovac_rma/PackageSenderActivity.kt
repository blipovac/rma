package com.example.bruno_lipovac_rma

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bruno_lipovac_rma.databinding.ActivityPackageSenderBinding
import com.example.bruno_lipovac_rma.models.Delivery
import com.google.android.gms.common.api.Status
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.AutocompleteSupportFragment
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlin.random.Random

class PackageSenderActivity : AppCompatActivity() {

    private lateinit var googleMap: GoogleMap
    lateinit var binding: ActivityPackageSenderBinding
    lateinit var db: FirebaseFirestore
    lateinit var userUid: String

    private lateinit var mSearchPickupFragment: AutocompleteSupportFragment
    private lateinit var mSearchDeliveryFragment: AutocompleteSupportFragment

    private lateinit var pickupAddress: String
    private lateinit var deliveryAddress: String

    private var pickupLatLng: LatLng? = null
    private var deliveryLatLng: LatLng? = null

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPackageSenderBinding.inflate(layoutInflater)

        auth = FirebaseAuth.getInstance()

        binding.signOut.setOnClickListener {
            auth.signOut()

            val intent = Intent(this, MainActivity()::class.java)
            startActivity(intent)
        }

        db = FirebaseFirestore.getInstance()

        Places.initialize(applicationContext, "AIzaSyDpZAazNKA0TKVIbR34LxOObb6eCO2ECWI")

        Places.createClient(this)

        setContentView(binding.root)

        binding.sendOrderButton.setOnClickListener {
            postDelivery()
        }

        val extra = intent.extras

        userUid = extra?.getString("USER_UID").toString()

        mSearchPickupFragment =
            supportFragmentManager.findFragmentById(binding.pickupAddress.id) as AutocompleteSupportFragment

        mSearchPickupFragment.setPlaceFields(
            listOf(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.LAT_LNG,
                Place.Field.ADDRESS
            )
        )

        mSearchPickupFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                Log.i(
                    "PLACE API STUFF",
                    "Place: ${place.name}, ${place.id}, ${place.latLng}, ${place.address}"
                )
                pickupAddress = "${place.name}, ${place.address}"
                pickupLatLng = place.latLng
            }

            override fun onError(status: Status) {
                Log.i("PLACE API STUFF", "An error occurred: $status")
            }
        })

        mSearchDeliveryFragment =
            supportFragmentManager.findFragmentById(binding.deliveryAddress.id) as AutocompleteSupportFragment

        mSearchDeliveryFragment.setPlaceFields(
            listOf(
                Place.Field.ID,
                Place.Field.NAME,
                Place.Field.LAT_LNG,
                Place.Field.ADDRESS
            )
        )

        mSearchDeliveryFragment.setOnPlaceSelectedListener(object : PlaceSelectionListener {
            override fun onPlaceSelected(place: Place) {
                Log.i(
                    "PLACE API STUFF",
                    "Place: ${place.name}, ${place.id}, ${place.latLng}, ${place.address}"
                )
                deliveryAddress = "${place.name}, ${place.address}"
                deliveryLatLng = place.latLng
            }

            override fun onError(status: Status) {
                Log.i("PLACE API STUFF", "An error occurred: $status")
            }
        })
    }

    private fun postDelivery() {
        val randomInt = Random.nextInt(0, 9999)

        val deliveryPin = randomInt.toString().padStart(4, '0')

        val delivery = Delivery(
            pickupAddress,
            deliveryAddress,
            binding.deliveryNote.editText?.text.toString(),
            false,
            deliveryPin,
            userUid,
            pickupLatLng,
            deliveryLatLng,
            null,
            null
        )

        db.collection("deliveries")
            .add(delivery)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val sendIntent = Intent().apply {
                        action = Intent.ACTION_SEND
                        putExtra(
                            Intent.EXTRA_TEXT,
                            "Hello there, I am sending you a package when the courier arrives" +
                                    " please confirm the delivery with this pin $deliveryPin."
                        )
                        type = "text/plain"
                    }

                    val shareIntent =  Intent.createChooser(sendIntent, null)
                    startActivity(shareIntent)

                    mSearchDeliveryFragment.setText("")
                    mSearchPickupFragment.setText("")
                    binding.deliveryNote.editText?.setText("")
                } else {
                    Log.d("POST_DELIVERY", "delivery post failed")

                    Toast.makeText(baseContext, "Delivery posting failed", Toast.LENGTH_SHORT)
                        .show()
                }
            }
    }
}