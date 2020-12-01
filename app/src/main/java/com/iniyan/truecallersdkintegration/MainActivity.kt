package com.iniyan.truecallersdkintegration

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.Nullable
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.truecaller.android.sdk.*
import com.truecaller.android.sdk.clients.VerificationCallback
import com.truecaller.android.sdk.clients.VerificationDataBundle
import java.util.*


class MainActivity : AppCompatActivity() ,ITrueCallback , VerificationCallback {

    private val TAG = this::class.java.simpleName

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        // Truecaller Dialog Verification
        /** val trueScope = TruecallerSdkScope.Builder(this, this)
        .consentMode(TruecallerSdkScope.CONSENT_MODE_POPUP)
        .consentTitleOption(TruecallerSdkScope.SDK_CONSENT_TITLE_VERIFY)
        .footerType(TruecallerSdkScope.FOOTER_TYPE_SKIP)
        .sdkOptions(TruecallerSdkScope.SDK_OPTION_WITH_OTP)
        .build()
         **/

        // Bottom View Login Verification
        val trueScope = TruecallerSdkScope.Builder(this, this)
                .consentMode(TruecallerSdkScope.CONSENT_MODE_BOTTOMSHEET)
                .buttonColor(ContextCompat.getColor(this, R.color.common_google_signin_btn_text_light_focused))
                .buttonTextColor(ContextCompat.getColor(this, R.color.common_google_signin_btn_text_dark_pressed))
                .loginTextPrefix(TruecallerSdkScope.LOGIN_TEXT_PREFIX_TO_GET_STARTED)
                .loginTextSuffix(TruecallerSdkScope.LOGIN_TEXT_SUFFIX_PLEASE_VERIFY_MOBILE_NO)
                .ctaTextPrefix(TruecallerSdkScope.CTA_TEXT_PREFIX_USE)
                .buttonShapeOptions(TruecallerSdkScope.BUTTON_SHAPE_ROUNDED)
                .privacyPolicyUrl("<<YOUR_PRIVACY_POLICY_LINK>>")
                .termsOfServiceUrl("<<YOUR_PRIVACY_POLICY_LINK>>")
                .footerType(TruecallerSdkScope.FOOTER_TYPE_NONE)
                .consentTitleOption(TruecallerSdkScope.SDK_CONSENT_TITLE_LOG_IN)
                .sdkOptions(TruecallerSdkScope.SDK_OPTION_WITH_OTP)
                .build()

        // Init Call TrueCallerSdk
        // we will check whether or not Truecaller is installed on your phone or if it is usable
        TruecallerSDK.init(trueScope)


        if (TruecallerSDK.getInstance().isUsable) {
            val locale = Locale("en")
            TruecallerSDK.getInstance().setLocale(locale)
            TruecallerSDK.getInstance().getUserProfile(this@MainActivity)
        } else {
            val dialogBuilder = AlertDialog.Builder(this)
            dialogBuilder.setMessage("Truecaller App not installed.")
            dialogBuilder.setPositiveButton("OK"
            ) { dialog: DialogInterface, which: Int ->
                Log.e(TAG, "onClick: Closing dialog")
                dialog.dismiss()
            }

            dialogBuilder.setIcon(R.drawable.com_truecaller_icon)
            dialogBuilder.setTitle(" ")

            val alertDialog = dialogBuilder.create()
            alertDialog.show()

        }


    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, @Nullable data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (TruecallerSDK.getInstance().isUsable) {
            TruecallerSDK.getInstance().onActivityResultObtained(this, resultCode, data)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onSuccessProfileShared(p0: TrueProfile) {
        if (TruecallerSDK.getInstance().isUsable) {
            Log.e(TAG, "onSuccessProfileShared: ${p0.phoneNumber}")
            Log.e(TAG, "onSuccessProfileShared: ${p0.email}")
            Log.e(TAG, "onSuccessProfileShared ${p0.firstName} -- ${p0.lastName}")
            Toast.makeText(this, "Success Profile details Obtained", Toast.LENGTH_SHORT).show()

            Log.e(TAG, "onSuccessProfileShared gender: ${p0.gender}")
            Log.e(TAG, "onSuccessProfileShared accesstoken: ${p0.accessToken}")
            Log.e(TAG, "onSuccessProfileShared fb: ${p0.facebookId}")
            findViewById<TextView>(R.id.userinfo).text = "Phone Number ${p0.phoneNumber} \n " +
                    "EmailId ${p0.email} \n " +
                    "Firstname ${p0.firstName} \n " +
                    "Lastname ${p0.lastName}"
        }
    }

    override fun onFailureProfileShared(p0: TrueError) {
        Log.e(TAG, "onSuccessProfileShared: ${p0.errorType}")
        Toast.makeText(this, "Failure Causes ${p0.errorType}", Toast.LENGTH_SHORT).show()
    }

    override fun onVerificationRequired(p0: TrueError?) {
        Log.e(TAG, "onSuccessProfileShared: ${p0?.errorType}")
        Toast.makeText(this, "onVerificationRequired Causes ${p0?.errorType}", Toast.LENGTH_SHORT).show()
        TruecallerSDK.getInstance().requestVerification("IN", "+919894591650", this, this);
    }

    override fun onDestroy() {
        super.onDestroy()
        TruecallerSDK.clear()
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>,
                                            grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

    }

    override fun onRequestSuccess(requestCode: Int, extras: VerificationDataBundle?) {


        Log.e(TAG, "onRequestSuccess: $requestCode")
        Log.e(TAG, "onRequestSuccess: ${extras?.getString(VerificationDataBundle.KEY_ACCESS_TOKEN)}")
        var profile = TrueProfile.Builder("iniyan", "Arul").build()

        if (requestCode == VerificationCallback.TYPE_MISSED_CALL_INITIATED) {
            extras?.getString(VerificationDataBundle.KEY_TTL)
            profile = TrueProfile.Builder("iniyan", "Arul").build()
        }

        if (requestCode == VerificationCallback.TYPE_MISSED_CALL_RECEIVED) {
            TruecallerSDK.getInstance().verifyMissedCall(profile, this)
        }

        if (requestCode == VerificationCallback.TYPE_OTP_INITIATED) {
            extras?.getString(VerificationDataBundle.KEY_TTL)
        }

        if (requestCode == VerificationCallback.TYPE_OTP_RECEIVED) {
            extras?.getString(VerificationDataBundle.KEY_OTP)?.let { TruecallerSDK.getInstance().verifyOtp(profile, it, this) }

        }
        if (requestCode == VerificationCallback.TYPE_VERIFICATION_COMPLETE) {
        }

        if (requestCode == VerificationCallback.TYPE_PROFILE_VERIFIED_BEFORE) {
        }
    }

    override fun onRequestFailure(p0: Int, p1: TrueException) {
        Toast.makeText(this, p1?.exceptionMessage, Toast.LENGTH_SHORT).show()
        Log.e(TAG, "onRequestFailure: ${p1?.exceptionMessage}")
    }
}
