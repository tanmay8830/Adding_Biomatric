package com.example.adding_biomatric

import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.example.adding_biomatric.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var biometricPrompt: BiometricPrompt
    private lateinit var promptInfo: BiometricPrompt.PromptInfo

    override fun onCreate(savedInstanceState: Bundle?)   {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
//call
        binding.btnAuthenticate.setOnClickListener   {

            openAppDetailsSettings()

        }
        showBiometricPrompt()


    }

    private fun showBiometricPrompt() {
        val biometricManager = BiometricManager.from(this)
        when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {
                val executor = ContextCompat.getMainExecutor(this)
                biometricPrompt = BiometricPrompt(this, executor, object : BiometricPrompt.AuthenticationCallback() {
                    override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                        super.onAuthenticationError(errorCode, errString)
                        Toast.makeText(applicationContext, "Authentication error: $errString", Toast.LENGTH_SHORT).show()
                    }

                    override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                        super.onAuthenticationSucceeded(result)
                        Toast.makeText(applicationContext, "Authentication succeeded!", Toast.LENGTH_SHORT).show()
                        goToDashboard()
                    }

                    override fun onAuthenticationFailed() {
                        super.onAuthenticationFailed()
                        Toast.makeText(applicationContext, "Authentication failed", Toast.LENGTH_SHORT).show()
                    }
                })

                // Build the prompt info, allowing both face and device credentials
                promptInfo = BiometricPrompt.PromptInfo.Builder()
                    .setTitle("Biometric Authentication")
                    .setSubtitle("Log in using your face or other credential")
                    // Don't set negative button text for flexibility
                    .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG or BiometricManager.Authenticators.DEVICE_CREDENTIAL)
                    .build()

                biometricPrompt.authenticate(promptInfo)
            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                Toast.makeText(this, "No biometric features available on this device.", Toast.LENGTH_LONG).show()
                // Handle fallback to device credentials (e.g., show login screen with pattern/PIN/password)
            }
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                Toast.makeText(this, "Biometric features are currently unavailable.", Toast.LENGTH_LONG).show()
                // Handle fallback to device credentials

            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                Toast.makeText(this, "No biometric credentials are enrolled on this device, please set a password or fingerprint first.", Toast.LENGTH_LONG).show()
                // Handle fallback to device credentials or prompt user to enroll
                openAppDetailsSettings()
               // openSecuritySettings()
            }
        }
    }

    private fun openAppDetailsSettings() {
        try {
            // Check if biometric enrollment is available
            val biometricIntent = Intent(Settings.ACTION_BIOMETRIC_ENROLL)
            if (biometricIntent.resolveActivity(packageManager) != null) {
                startActivity(biometricIntent)
            } else {
                // If biometric enrollment is not available, open the security settings
                openSecuritySettings()
            }
        } catch (e: ActivityNotFoundException) {
            // Handle the exception, e.g., show a Toast or log the error
            Log.d("MainActivityx", e.printStackTrace().toString())
        }
    }

    private fun openSecuritySettings() {
        val securityIntent = Intent(Settings.ACTION_SETTINGS)
        startActivity(securityIntent)
    }


    private fun goToDashboard() {
        val mainIntent = Intent(this, MainActivity2::class.java)
        startActivity(mainIntent)
        finish()
    }
}
