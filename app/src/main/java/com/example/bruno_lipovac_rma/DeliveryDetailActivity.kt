package com.example.bruno_lipovac_rma

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.bruno_lipovac_rma.databinding.ActivityDeliveryDetailsBinding
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlin.math.abs

class DeliveryDetailActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityDeliveryDetailsBinding

    private lateinit var pickupAddress: String
    private lateinit var deliveryAddress: String
    private lateinit var deliveryNotes: String
    private lateinit var pickupLat: String
    private lateinit var pickupLng: String
    private lateinit var deliveryLat: String
    private lateinit var deliveryLng: String


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDeliveryDetailsBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val extra = intent.extras

        pickupAddress = extra?.getString("PICKUP_ADDRESS").toString()
        deliveryAddress = extra?.getString("DELIVERY_ADDRESS").toString()
        deliveryNotes = extra?.getString("NOTES").toString()
        pickupLat = extra?.getString("PICKUP_LAT").toString()
        pickupLng = extra?.getString("PICKUP_LNG").toString()
        deliveryLat = extra?.getString("DELIVERY_LAT").toString()
        deliveryLng = extra?.getString("DELIVERY_LNG").toString()

        binding.pickupAddress.text = pickupAddress
        binding.deliveryAddress.text = deliveryAddress
        binding.deliveryNote.text = deliveryNotes

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        if (
            pickupLat.isNotEmpty()
            && pickupLng.isNotEmpty()
            && deliveryLat.isNotEmpty()
            && deliveryLng.isNotEmpty()
        ) {
            val pickupLatNumber: Double
            val pickupLngNumber: Double
            val deliveryLatNumber: Double
            val deliveryLngNumber: Double
            try {
                pickupLatNumber = pickupLat.toDouble()
                pickupLngNumber = pickupLng.toDouble()
                deliveryLatNumber = deliveryLat.toDouble()
                deliveryLngNumber = deliveryLng.toDouble()

                val pickupGeoPoint = LatLng(pickupLatNumber, pickupLngNumber)
                val deliveryGeoPoint = LatLng(deliveryLatNumber, deliveryLngNumber)

                val zoomLat: Double = (pickupLatNumber + deliveryLatNumber) / 2.00
                val zoomLng: Double = (pickupLngNumber + deliveryLngNumber) / 2.00

                googleMap.addMarker(
                    MarkerOptions()
                        .position(pickupGeoPoint)
                        .title("Pickup Address: $pickupAddress")
                )

                googleMap.addMarker(
                    MarkerOptions()
                        .position(deliveryGeoPoint)
                        .title("Delivery Address: $deliveryAddress")
                )

                val cameraUpdate: CameraUpdate = CameraUpdateFactory
                    .newLatLngZoom(LatLng(zoomLat, zoomLng), 12F)

                googleMap.animateCamera(cameraUpdate)
            } catch (error: Exception) {
                Log.d("DOUBLE CONVERSION ERROR", error.message.toString())
            }
        }
    }
}
