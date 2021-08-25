package com.example.bruno_lipovac_rma

import android.content.Intent
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
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding

    private lateinit var db: FirebaseFirestore

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

        val authUser = auth.currentUser

        if (authUser != null) {
            db.collection("users").whereEqualTo("uid", authUser.uid).get()
                .addOnSuccessListener { documents ->
                    for (document in documents) {
                        val user = User(
                            document.data["email"].toString(),
                            document.data["password"].toString(),
                            UserType.valueOf(document.data["userType"].toString()),
                            document.data["uid"].toString()

                        )

                        this.goToSomeScreen(user)
                    }
                }
                .addOnFailureListener { exception ->
                    Log.d("USER_FETCH", "user fetch failed: ", exception)
                }
        }
    }

    private fun goToSomeScreen(user: User?) {
        when (user?.userType) {
            UserType.COURIER -> {
                val intent = Intent(this, PackageCourierActivity::class.java)

                startActivity(intent)
            }
            UserType.SENDER -> {
                val intent = Intent(this, PackageSenderActivity::class.java)

                startActivity(intent)
            }
            else -> {
                val intent = Intent(this, MainActivity::class.java)

                Toast.makeText(baseContext, "Some error occurred", Toast.LENGTH_SHORT).show()

                startActivity(intent)
            }
        }
    }

    private fun register() {
        if (this.validateEmail() && this.validatePassword()) {
            this.createUserAuth()
        }
    }

    private fun createUserAuth() {
        auth.createUserWithEmailAndPassword(
            binding.email.editText?.text.toString(),
            binding.password.editText?.text.toString()
        ).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                Log.d("AUTH", "Registration auth successful")

                this.sendToFirestore(auth.currentUser)
            } else {
                Log.d("AUTH", "Registration auth unsuccessful")
                Toast.makeText(baseContext, "Authentication failed", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun sendToFirestore(authUser: FirebaseUser?) {
        val userType: UserType = if (binding.packageCourierButton.isChecked) {
            UserType.COURIER
        } else {
            UserType.SENDER
        }

        val user = User(
            binding.email.editText?.text.toString(),
            binding.password.editText?.text.toString(),
            userType,
            authUser?.uid
        )

        db.collection("users")
            .add(user)

        this.goToSomeScreen(user)
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
