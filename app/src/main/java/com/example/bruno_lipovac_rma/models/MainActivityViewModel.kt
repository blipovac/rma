package com.example.bruno_lipovac_rma.models

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class MainActivityViewModel: ViewModel() {
    val authUser: MutableLiveData<FirebaseUser> by lazy {
        MutableLiveData<FirebaseUser>()
    }

    val user: MutableLiveData<User> by lazy {
        MutableLiveData<User>()
    }
}