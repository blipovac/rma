package com.example.bruno_lipovac_rma

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.bruno_lipovac_rma.databinding.ActivityMainBinding
import com.example.bruno_lipovac_rma.models.MainActivityViewModel
import com.example.bruno_lipovac_rma.models.User
import com.example.bruno_lipovac_rma.models.enums.UserType
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var db: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    private val model: MainActivityViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        db = FirebaseFirestore.getInstance()

        auth = FirebaseAuth.getInstance()

        setContentView(binding.root)

        val authUserObserver = Observer<FirebaseUser> { authUser ->
            this.getUser(authUser.uid)
        }

        val userObserver = Observer<User> { user ->
            this.goToSomeScreen(user)
        }

        model.authUser.observe(this, authUserObserver)

        model.user.observe(this, userObserver)

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

                this.model.authUser.value= auth.currentUser
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

                intent.putExtra("USER_UID", this.model.user.value?.uid)

                startActivity(intent)
            }
            else -> {
                val intent = Intent(this, MainActivity::class.java)

                Toast.makeText(baseContext, "Some error occurred", Toast.LENGTH_SHORT).show()

                startActivity(intent)
            }
        }
    }

    private fun getUser(uid: String?) {
        db.collection("users")
            .whereEqualTo("uid", uid)
            .get()
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d("TASK", task.result.toString())

                    val users: MutableList<User> = mutableListOf<User>()

                    for (document in task.result) {
                        users.add(User(
                            document.data["email"].toString(),
                            document.data["password"].toString(),
                            UserType.valueOf(document.data["userType"].toString()),
                            document.data["uid"].toString()
                        ))
                    }

                    // There should only be one user present in the list, but the data is formed as
                    // a list because firebase documents are always a list.
                    this.model.user.value = users[0]
                } else {
                    Log.w("TAG", "Error getting documents.", task.exception)
                }


            }
            .addOnFailureListener { exception ->
                Log.d("USER_FETCH", "user fetch failed: ", exception)
            }
    }
}