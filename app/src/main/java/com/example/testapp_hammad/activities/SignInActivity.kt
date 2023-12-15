package com.example.testapp_hammad.activities

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.testapp_hammad.MainActivity
import com.example.testapp_hammad.databinding.ActivitySignInBinding
import com.example.testapp_hammad.utils.BiometricHelper
import com.google.firebase.auth.FirebaseAuth

class SignInActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivitySignInBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initilaizeandCheckBiometric()
        auth = FirebaseAuth.getInstance()

        binding.apply {
            signUpButton.setOnClickListener {
                val email = emailEditText.text.toString()
                val password = passwordEditText.text.toString()

                if (email.isNotEmpty() && password.isNotEmpty()) {
                    signUp(email, password)
                }
            }
            signInButton.setOnClickListener {
                val email = emailEditText.text.toString()
                val password = passwordEditText.text.toString()

                if (email.isNotEmpty() && password.isNotEmpty()) {
                    signIn(email, password)
                }
            }
        }

    }

    override fun onResume() {
        super.onResume()
        initilaizeandCheckBiometric()
    }

    private fun initilaizeandCheckBiometric() {
        BiometricHelper.init(this,
            this,
            MainActivity::class.java,
            { intent, requestCode -> startActivityForResult(intent, requestCode) })

        if (BiometricHelper.isBiometricAvailable()) {
            BiometricHelper.checkDeviceCanAuthenticateWithBiometrics()
            BiometricHelper.authenticateWithBiometrics()
        } else {
            Toast.makeText(this, "biometric not avaliable", Toast.LENGTH_SHORT).show()
            val intent = Intent(Settings.ACTION_SECURITY_SETTINGS)
            startActivity(intent)
        }
    }

    private fun signUp(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    showToast("Sign-in failed. Please check your credentials and try again.")
                }
            }

    }

    private fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    val exception = task.exception
                    showToast("Sign-in failed. Please check your credentials and try again2.")
                }
            }

    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}