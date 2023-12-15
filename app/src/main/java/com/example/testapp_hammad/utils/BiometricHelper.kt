package com.example.testapp_hammad.utils

import android.annotation.SuppressLint
import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import com.example.testapp_hammad.MainActivity
import com.example.testapp_hammad.R
import java.util.concurrent.Executor

@SuppressLint("StaticFieldLeak")
object BiometricHelper {
    private lateinit var executor: Executor
    private lateinit var callBack: BiometricPrompt.AuthenticationCallback
    private lateinit var biometricPrompt: BiometricPrompt
    lateinit var context: Context
    private lateinit var activity: AppCompatActivity
    private var startActivityForResult: ((Intent, Int) -> Unit)? = null
    var targetActivity: Class<*>? = null
    var keyguardManager: KeyguardManager? = null
        const val RC_BIOMETRICS_ENROLL = 10
        const val RC_DEVICE_CREDENTIAL_ENROLL = 18


    fun init(
        context: Context,
        activity: AppCompatActivity,
        targetActivity: Class<*>,
        startActivityForResult: (Intent, Int) -> Unit
    ) {
        this.context = context
        this.activity = activity
        this.targetActivity = targetActivity
        this.startActivityForResult = startActivityForResult
        executor = ContextCompat.getMainExecutor(this.context)
        callBack = object : BiometricPrompt.AuthenticationCallback() {
            override fun onAuthenticationFailed() {
                super.onAuthenticationFailed()
                Toast.makeText(
                    context,
                    context.getString(R.string.error_unknown),
                    Toast.LENGTH_LONG
                ).show()
            }

            override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                super.onAuthenticationSucceeded(result)
                Toast.makeText(
                    context,
                    context.getString(R.string.message_success),
                    Toast.LENGTH_LONG
                ).show()
                val intent = Intent(context, targetActivity)
                context.startActivity(intent)
            }

            override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                super.onAuthenticationError(errorCode, errString)
                // Handle authentication error
                if (errorCode == BiometricPrompt.ERROR_NEGATIVE_BUTTON) {
                    biometricPrompt.cancelAuthentication()

                }


                Toast.makeText(context, getErrorMessage(errorCode), Toast.LENGTH_LONG).show()
            }
        }
        if (isBiometricAvailable()) {
            biometricPrompt = BiometricPrompt(this.activity, executor, callBack)
        }

        //biometricPrompt = BiometricPrompt(this.activity, executor, callBack)
    }

    fun isBiometricAvailable(): Boolean {
        val biometricManager = BiometricManager.from(context)
        return when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK or BiometricManager.Authenticators.DEVICE_CREDENTIAL)) {
            BiometricManager.BIOMETRIC_SUCCESS -> true
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> false
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> false
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> false
            BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED -> false
            BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED -> false
            BiometricManager.BIOMETRIC_STATUS_UNKNOWN -> false
            else -> false
        }
    }
    fun checkDeviceCanAuthenticateWithBiometrics() {
        val biometricManager = BiometricManager.from(context)
        when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_WEAK or BiometricManager.Authenticators.DEVICE_CREDENTIAL)) {
            BiometricManager.BIOMETRIC_SUCCESS -> {

            }
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE -> {
                Toast.makeText(
                    context,
                    context.getString(R.string.message_no_support_biometrics),
                    Toast.LENGTH_LONG
                ).show()
            }
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE -> {
                Toast.makeText(
                    context,
                    context.getString(R.string.message_no_hardware_available),
                    Toast.LENGTH_LONG
                ).show()
            }
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED -> {
                checkAPILevelAndProceed()
            }
            BiometricManager.BIOMETRIC_ERROR_SECURITY_UPDATE_REQUIRED -> {
                Toast.makeText(
                    context,
                    context.getString(R.string.error_security_update_required),
                    Toast.LENGTH_LONG
                ).show()
            }
            BiometricManager.BIOMETRIC_ERROR_UNSUPPORTED -> {
                Toast.makeText(
                    context,
                    context.getString(R.string.error_unknown),
                    Toast.LENGTH_LONG
                ).show()
            }
            BiometricManager.BIOMETRIC_STATUS_UNKNOWN -> {
                Toast.makeText(
                    context,
                    context.getString(R.string.error_unknown),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    fun biometricsEnrollIntent(): Intent {
        return Intent(Settings.ACTION_BIOMETRIC_ENROLL).apply {
            putExtra(
                Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                BiometricManager.Authenticators.BIOMETRIC_WEAK or BiometricManager.Authenticators.DEVICE_CREDENTIAL
            )
        }
    }

    fun setUpDeviceLockInAPIBelow23Intent(): Intent {
        return Intent(Settings.ACTION_SECURITY_SETTINGS)
    }

    fun checkAPILevelAndProceed() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            startActivityForResult?.invoke(
                setUpDeviceLockInAPIBelow23Intent(),
                RC_DEVICE_CREDENTIAL_ENROLL
            )
        } else {
            startActivityForResult?.invoke(
                biometricsEnrollIntent(),
                RC_BIOMETRICS_ENROLL
            )
        }
    }

    fun getErrorMessage(errorCode: Int): String {
        return when (errorCode) {
            BiometricPrompt.ERROR_NEGATIVE_BUTTON -> {
                context.getString(R.string.message_user_app_authentication)
            }
            BiometricPrompt.ERROR_HW_UNAVAILABLE -> {
                context.getString(R.string.error_hw_unavailable)
            }
            BiometricPrompt.ERROR_UNABLE_TO_PROCESS -> {
                context.getString(R.string.error_unable_to_process)
            }
            BiometricPrompt.ERROR_TIMEOUT -> {
                context.getString(R.string.error_time_out)
            }
            BiometricPrompt.ERROR_NO_SPACE -> {
                context.getString(R.string.error_no_space)
            }
            BiometricPrompt.ERROR_CANCELED -> {
                context.getString(R.string.error_canceled)
            }
            BiometricPrompt.ERROR_LOCKOUT -> {
                context.getString(R.string.error_lockout)
            }
            BiometricPrompt.ERROR_VENDOR -> {
                context.getString(R.string.error_vendor)
            }
            BiometricPrompt.ERROR_LOCKOUT_PERMANENT -> {
                context.getString(R.string.error_lockout_permanent)
            }
            BiometricPrompt.ERROR_USER_CANCELED -> {
                context.getString(R.string.error_user_canceled)
            }
            BiometricPrompt.ERROR_NO_BIOMETRICS -> {
                checkAPILevelAndProceed()
                context.getString(R.string.error_no_biometrics)
            }
            BiometricPrompt.ERROR_HW_NOT_PRESENT -> {
                context.getString(R.string.error_hw_not_present)
            }
            BiometricPrompt.ERROR_NO_DEVICE_CREDENTIAL -> {
                startActivityForResult?.invoke(
                    biometricsEnrollIntent(),
                    RC_BIOMETRICS_ENROLL
                )
                context.getString(R.string.error_no_device_credentials)
            }
            BiometricPrompt.ERROR_SECURITY_UPDATE_REQUIRED -> {
                context.getString(R.string.error_security_update_required)
            }
            else -> {
                context.getString(R.string.error_unknown)
            }
        }
    }

    fun authenticateWithBiometrics() {


        if (biometricPrompt == null) {
            // Handle the case where biometrics are not supported
            // For example, show a message to the user or start an alternative authentication method.
            val intent = Intent(context, MainActivity::class.java)
            context.startActivity(intent)
            return
        }
        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(context.getString(R.string.title_biometric_dialog))
            //.setDescription(context.getString(R.string.text_description_biometrics_dialog))
            .setNegativeButtonText("Try another way")
            .build()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
            keyguardManager =
                context.getSystemService(AppCompatActivity.KEYGUARD_SERVICE) as KeyguardManager
            keyguardManager?.let { manager ->
                if (manager.isKeyguardSecure) {
                    biometricPrompt.authenticate(promptInfo)
                } else {
                    startActivityForResult?.invoke(
                        setUpDeviceLockInAPIBelow23Intent(),
                        RC_DEVICE_CREDENTIAL_ENROLL
                    )
                }
            }
        } else {
            biometricPrompt.authenticate(promptInfo)
        }
    }
}
