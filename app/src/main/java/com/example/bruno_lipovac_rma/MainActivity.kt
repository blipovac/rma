package com.example.bruno_lipovac_rma

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.bruno_lipovac_rma.databinding.ActivityMainBinding
import com.google.firebase.firestore.FirebaseFirestore


class MainActivity : AppCompatActivity() {

    lateinit var binding: ActivityMainBinding
    lateinit var db: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)

        db = FirebaseFirestore.getInstance()

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
    }

    private fun loginUser() {
        Log.d("tagito", binding.email.editText?.text.toString())
        Log.d("tagito", binding.password.editText?.text.toString())
    }
}