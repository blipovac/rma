package com.example.bruno_lipovac_rma

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Patterns
import com.example.bruno_lipovac_rma.databinding.ActivityRegisterBinding
import com.example.bruno_lipovac_rma.models.User
import com.example.bruno_lipovac_rma.models.enums.UserType
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {

    lateinit var binding: ActivityRegisterBinding

    lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)

        setContentView(binding.root)

        db = FirebaseFirestore.getInstance()

        binding.registerButton.setOnClickListener {
            register()
        }
    }

    private fun register() {
        if (this.validateEmail() && this.validatePassword()) {
         this.sendToFirestore()
        }
    }

    private fun sendToFirestore() {
        val userType: UserType = if (binding.packageCourierButton.isChecked) {
            UserType.COURIER
        } else {
            UserType.SENDER
        }

        val user = User(
            binding.email.editText?.text.toString(),
            binding.password.editText?.text.toString(),
            userType
        )

        db.collection("users")
            .add(user)
    }

    private fun validatePassword(): Boolean {
        if (binding.password.editText?.text.toString().isEmpty()) {
            binding.password.editText?.error = "Password should not be empty"
            return false
        }

        if (TextUtils.isEmpty(binding.passwordConfirm.editText?.text.toString())) {
            binding.passwordConfirm.editText?.error = "Password confirmation should not be empty"
            return false
        }

        if (binding.password.editText?.text.toString() != binding.passwordConfirm.editText?.text.toString()) {
            binding.passwordConfirm.editText?.error = "Passwords must match"
            return false
        }

        return true
    }

    private fun validateEmail(): Boolean {
        if (TextUtils.isEmpty(binding.email.editText?.text) ||
            !Patterns.EMAIL_ADDRESS.matcher(binding.email.editText?.text.toString()).matches()
        ) {
            binding.email.editText?.error = "Invalid email address"

            return false
        }

        return true
    }
}
