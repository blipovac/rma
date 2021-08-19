package com.example.bruno_lipovac_rma.models

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class PackageCourierViewModel: ViewModel() {
    val deliveries: MutableLiveData<MutableList<Delivery>> by lazy {
        MutableLiveData<MutableList<Delivery>>()
    }
}