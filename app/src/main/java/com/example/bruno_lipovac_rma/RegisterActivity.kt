package com.example.bruno_lipovac_rma

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.util.Patterns
import com.example.bruno_lipovac_rma.databinding.ActivityRegisterBinding

class RegisterActivity : AppCompatActivity() {

    lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegisterBinding.inflate(layoutInflater)

        setContentView(binding.root)

        setContentView(R.layout.activity_register)

        binding.registerButton.setOnClickListener {
            register()
        }
    }

    private fun register() {
        this.validateEmail()
        this.validatePassword()
        this.sendToFirestore()
    }

    private fun sendToFirestore() {
        TODO("Not yet implemented")
    }

    private fun validatePassword(): Boolean {
        if (TextUtils.isEmpty(binding.password.editText.toString())) {
            return false
        }

        if (TextUtils.isEmpty(binding.passwordConfirm.editText.toString())) {
            return false
        }

        if (binding.password.editText.toString() != binding.passwordConfirm.editText.toString()) {
            return false
        }

        return true
    }

    private fun validateEmail(): Boolean {
        return !TextUtils.isEmpty(binding.email.editText?.text) &&
                Patterns.EMAIL_ADDRESS.matcher(binding.email.editText?.text.toString()).matches()
    }
}