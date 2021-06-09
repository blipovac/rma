package com.example.bruno_lipovac_rma.models

import com.example.bruno_lipovac_rma.models.enums.UserType

data class User(val email: String, val password: String, val userType: UserType, val uid: String?)
