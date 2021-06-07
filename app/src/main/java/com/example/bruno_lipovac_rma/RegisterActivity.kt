package com.example.bruno_lipovac_rma

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import android.widget.Toast
import com.example.bruno_lipovac_rma.databinding.ActivityRegisterBinding
import com.example.bruno_lipovac_rma.models.User
import com.example.bruno_lipovac_rma.models.enums.UserType
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {

    lateinit var binding: ActivityRegisterBinding

    lateinit var db: FirebaseFirestore

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)

        setContentView(binding.root)

        db = FirebaseFirestore.getInstance()

        binding.registerButton.setOnClickListener {
            register()
        }

        auth = FirebaseAuth.getInstance()
    }

    public override fun onStart() {
        super.onStart()

        val currentUser = auth.currentUser

        if(currentUser != null) {
            this.goToSomeScreen()
        }
    }

    private fun goToSomeScreen() {
        // send normal user to create deliver screen
        // send courier to pickup delivery screen
        // on failed registration stay on the same screen or maybe go to the first one
        TODO("Not yet implemented")
    }

    private fun register() {
        if (this.validateEmail() && this.validatePassword()) {
            this.sendToFirestore()
            this.createUserAuth()
        }
    }

    private fun createUserAuth() {
        auth.createUserWithEmailAndPassword(
            binding.email.editText?.text.toString(),
            binding.password.editText?.text.toString()
        ).addOnCompleteListener(this) {
            task ->
                if (task.isSuccessful) {
                    Log.d("AUTH", "Registration auth successful")
                    val user = auth.currentUser
                    this.goToSomeScreen()
                } else {
                    Log.d("AUTH", "Registration auth unsuccessful")
                    Toast.makeText(baseContext, "Authentication failed", Toast.LENGTH_SHORT)
                        .show()
                    this.goToSomeScreen()
                }
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
