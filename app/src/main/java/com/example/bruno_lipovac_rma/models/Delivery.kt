package com.example.bruno_lipovac_rma.models

data class Delivery(
    val pickupAddress: String,
    val deliverAddress: String,
    val notes: String,
    val isComplete: Boolean,
    val deliveryPin: String,
    val userUid: String,
)
