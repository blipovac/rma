package com.example.bruno_lipovac_rma

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.bruno_lipovac_rma.databinding.ActivityMainBinding
import com.example.bruno_lipovac_rma.models.User
import com.example.bruno_lipovac_rma.models.enums.UserType
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore


class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        db = FirebaseFirestore.getInstance()

        auth = FirebaseAuth.getInstance()

        setContentView(binding.root)

        db.collection("users")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    for (document in task.result) {
                        Log.d("TAG", document.id + " => " + document.data)
                    }
                } else {
                    Log.w("TAG", "Error getting documents.", task.exception)
                }
            }

        binding.loginButton.setOnClickListener {
            loginUser()
        }

        binding.registerButton.setOnClickListener {
            goToRegister()
        }
    }

    private fun goToRegister() {
        val intent = Intent(this, RegisterActivity::class.java)
        startActivity(intent)
    }

    private fun loginUser() {
        auth.signInWithEmailAndPassword(
            binding.email.editText?.text.toString(),
            binding.password.editText?.text.toString()
        ).addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                Log.d("AUTH", "login is successful")


                val authUser = auth.currentUser

                this.getUser(authUser)
            } else {
                Log.d("AUTH", "login failed")

                Toast.makeText(baseContext, "Login failed", Toast.LENGTH_SHORT)
                    .show()
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

    private fun getUser(authUser: FirebaseUser?) {
        db.collection("users").whereEqualTo("uid", authUser).get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val user = User(
                        document.data["email"].toString(),
                        document.data["password"].toString(),
                        document.data["userType"] as UserType,
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