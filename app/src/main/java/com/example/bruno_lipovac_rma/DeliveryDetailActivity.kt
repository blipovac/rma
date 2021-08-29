package com.example.bruno_lipovac_rma

import android.content.Intent
import android.content.res.ColorStateList
import android.graphics.Color
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.example.bruno_lipovac_rma.databinding.ActivityDeliveryDetailsBinding
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class DeliveryDetailActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var binding: ActivityDeliveryDetailsBinding

    private var pickupAddress: String? = null
    private var deliveryAddress: String? = null
    private var deliveryNotes: String? = null
    private var pickupLat: Double? = null
    private var pickupLng: Double? = null
    private var deliveryLat: Double? = null
    private var deliveryLng: Double? = null
    private var documentId: String? = null
    private var courierId: String? = null
    private var deliveryPin: String? = null

    private var canAcceptDelivery: Boolean = true

    private lateinit var db: FirebaseFirestore

    private lateinit var auth: FirebaseAuth


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        db = FirebaseFirestore.getInstance()

        auth = FirebaseAuth.getInstance()

        binding = ActivityDeliveryDetailsBinding.inflate(layoutInflater)

        setContentView(binding.root)

        binding.orderActionButton.setOnClickListener {
            deliveryAction()
        }

        binding.completeDeliveryButton.setOnClickListener {
            if (canAcceptDelivery) {
                Toast.makeText(this, "You have not accepted this delivery!", Toast.LENGTH_SHORT)
                    .show()
            } else {
                completeDelivery()
            }
        }

        val extra = intent.extras

        pickupAddress = extra?.getString("PICKUP_ADDRESS")
        deliveryAddress = extra?.getString("DELIVERY_ADDRESS")
        deliveryNotes = extra?.getString("NOTES")
        pickupLat = extra?.getDouble("PICKUP_LAT")
        pickupLng = extra?.getDouble("PICKUP_LNG")
        deliveryLat = extra?.getDouble("DELIVERY_LAT")
        deliveryLng = extra?.getDouble("DELIVERY_LNG")
        documentId = extra?.getString("DOCUMENT_ID")
        courierId = extra?.getString("COURIER_ID")
        deliveryPin = extra?.getString("DELIVERY_PIN")

        if (courierId == null) {
            canAcceptDelivery = true
            binding.orderActionButton.backgroundTintList = ColorStateList.valueOf(Color.GREEN)
        } else {
            this.canAcceptDelivery = false
            binding.orderActionButton.backgroundTintList = ColorStateList.valueOf(Color.RED)
        }

        binding.pickupAddress.text = pickupAddress
        binding.deliveryAddress.text = deliveryAddress
        binding.deliveryNote.text = deliveryNotes

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as? SupportMapFragment
        mapFragment?.getMapAsync(this)
    }

    private fun completeDelivery() {
        val dialogBuilder: AlertDialog.Builder = AlertDialog.Builder(this)

        val textInput = EditText(this)
        textInput.inputType = InputType.TYPE_CLASS_TEXT

        dialogBuilder.setTitle("Complete delivery:")
            .setMessage("Please enter the pin provided by the package sender")
            .setView(textInput)
            .setPositiveButton("OK") { dialog, which ->
                val enteredPin = textInput.text.toString()

                checkDeliveryPin(enteredPin)

                dialog.cancel()
            }
            .setNegativeButton("Cancel") { dialog, which ->
                dialog.cancel()
            }
            .show()
    }

    private fun checkDeliveryPin(enteredPin: String) {
        Log.d("CHECKING_DELIVERY_PIN", "Hello $enteredPin")

        if (enteredPin == deliveryPin) {
            documentId?.let {
                db.collection("deliveries")
                    .document(it)
                    .update("isComplete", true)
                    .addOnFailureListener { exception ->
                        Log.d("UPDATING_DELIVERY", "failed", exception)

                        Toast.makeText(this, "Something went wrong!", Toast.LENGTH_SHORT)
                            .show()
                    }
                    .addOnCompleteListener {
                        Toast.makeText(this, "Delivery successfully completed", Toast.LENGTH_SHORT)
                            .show()
                        
                        goToDeliveryListScreen()
                    }
            }
        } else {
            Toast.makeText(this, "Invalid pin entered!", Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun goToDeliveryListScreen() {
        val intent = Intent(this, PackageCourierActivity()::class.java)
        startActivity(intent)
    }

    private fun deliveryAction() {
        val currentUser = auth.currentUser

        Log.d("BUTTON_ACTION", "Hello")

        val updatePayload: String? = if (canAcceptDelivery) {
            currentUser.uid
        } else {
            null
        }

        documentId?.let {
            db
                .collection("deliveries")
                .document(it)
                .update("courierId", updatePayload)
                .addOnFailureListener { exception ->
                    Log.d("UPDATE_DELIVERY", "Delivery update failed: ", exception)
                }
                .addOnCompleteListener(this) {
                    canAcceptDelivery = !canAcceptDelivery

                    val color: Int = if (canAcceptDelivery) {
                        Color.GREEN
                    } else {
                        Color.RED
                    }

                    val icon: Int = if (canAcceptDelivery) {
                        R.drawable.ic_baseline_done_24
                    } else {
                        R.drawable.ic_baseline_close_24
                    }

                    binding.orderActionButton.backgroundTintList = ColorStateList.valueOf(color)
                    binding.orderActionButton.setImageResource(icon)
                }
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        if (
            pickupLat != null
            && pickupLng != null
            && deliveryLat != null
            && deliveryLng != null
        ) {
            val pickupGeoPoint = LatLng(pickupLat!!, pickupLng!!)
            val deliveryGeoPoint = LatLng(deliveryLat!!, deliveryLng!!)

            val zoomLat: Double = (pickupLat!! + deliveryLat!!) / 2.00
            val zoomLng: Double = (pickupLng!! + deliveryLng!!) / 2.00

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
        }
    }
}
