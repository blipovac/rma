package com.example.bruno_lipovac_rma.models

import com.google.android.gms.maps.model.LatLng

data class Delivery(
    val pickupAddress: String,
    val deliverAddress: String,
    val notes: String,
    val isComplete: Boolean,
    val deliveryPin: String,
    val userUid: String,
    val deliveryLatLng: LatLng?,
    val pickupLatLng: LatLng?,
)
