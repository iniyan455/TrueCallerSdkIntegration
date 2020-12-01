# Truecaller-Login
An android sample application that uses the Truecaller SDK for Phone number authentication.


KB-6646: Truecaller SDK Integration AndroidIN PROGRESSOffical TrueCaller Sdk Integration Document for Reference → https://docs.truecaller.com/truecaller-sdk/android/integrating-with-your-app

##Why Truecaller SDK

##Advantages :-
Increase successful verification/ signup/ login attempts

Avoid user drop-off and app abandonment with1-tap, instant verification - without any OTP SMS

Simple, ZERO effort flow

Auto-fill user registration form bycapturing mapped user profile (user name, email ID, city, etc.)

Achieve easy user activation and quick checkout

Implementing user flow for your app


Truecaller SDK is a mobile number verification service, without the need for any OTP whatsoever.
The right way to implement Truecaller SDK in your mobile app, is to invoke mobile number verification via Truecaller at touch points, where you have your users to sign-up/ login/ checkout by verifying their mobile numbers.

STEP 1: Signup for the developer account on their portal and add your application. Below are a few pictures to explain to you how after signup it looks.

Click on ADD APPLICATION

Image for post
Truecaller developer portal

Enter your app name, package name and Fingerprint SHA1 Key,

NOTE: You have to add two applications with the same package name for debug and release apk of your app with different SHA1 Key.


Copy your partner key generated after registering your app. We will need this while coding

STEP 2: Let’s code.

The minimum SDK requirement is Android API Level 16, So if your app has an API level below 16, then to avoid compilation errors, add this line in you Android Manifest.xml file


<uses-sdk tools:overrideLibrary="com.truecaller.android.sdk"/>
2. Then add the dependency in build.gradle file.


implementation "com.truecaller.android.sdk:truecaller-sdk:2.2.0"
3. Add the following lines also in build.gradle file.


compileOptions {
         sourceCompatibility JavaVersion.VERSION_1_8
         targetCompatibility JavaVersion.VERSION_1_8
}
4. Add this meta-data in your manifest file and add partner key generated above in strings.xml file.


<meta-data android:name="com.truecaller.android.sdk.PartnerKey" android:value="@string/partnerKey"/>
5. Truecaller Initialization


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
Truecaller SDK needs to be initialized only once and the same instance can be accessed at any place within your app, without the need to initialize it again, via TruecallerSDK.getInstance()

6. Now, after the initialization, we will check whether or not Truecaller is installed on your phone or if it is usable.


TruecallerSDK.getInstance().isUsable();
7. Now, to call Truecaller profile dialog anywhere in your app flow, call by using the below line.

Note :-  Please ensure that you have logged in your Truecaller once. This is important otherwise Truecaller dialog won’t show up irrespective where and how many times you call the below line mentioned. So open your Truecaller app and login with it if not log in/signup.


TruecallerSDK.getInstance().getUserProfile();
Add the following condition in the onActivityResult method.

NOTE: Please add a check for isUsable() everywhere before using Truecaller instance otherwise it will result in a crash in your app.


@Override
public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    if (TruecallerSDK.getInstance().isUsable()){
      TruecallerSDK.getInstance().onActivityResultObtained(this, resultCode, data);
    }
}
If you are implementing the Truecaller in Fragment, then add this line in Activity onActivityResult method also, which is holding the fragment, This is an important step to do so that you can get Truecaller callbacks in Fragment. The below-mentioned line is only for a fragment.


@Override
protected void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
    super.onActivityResult(requestCode, resultCode, data);

    Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.frameLayout);
    if (fragment != null)
        fragment.onActivityResult(requestCode, resultCode, data);
}
8. In your selected Activity/Fragment, either make the component implement ITrueCallback or create an instance of it :


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
9. Implement Callbacks

@Override
protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
    super.onActivityResult(requestCode, resultCode, data);
    
    TruecallerSDK.getInstance().onActivityResultObtained(this, resultCode, data);
    
}
onSuccessProfileShared() method

a.) When the user has agreed to share his profile information with your app by clicking on the "Continue" button on the Truecaller dialog 

b.) When a non Truecaller user is already verified previously on the same device. This would only happen when the TruecallerSdkScope#SDK_OPTION_WITH_OTP

onFailureProfileShared() method will be called when some error occurs or if an invalid request for verification is made.

onVerificationRequired() method will only be called whenTruecallerSdkScope#SDK_OPTION_WITH_OTP is selected. This will be called when the user is not a Truecaller app user. Also, you'll get a Nullable TrueError only when TC app is installed and user is logged in. For other cases, it would be null.

10. Clearing SDK instance
You can call this method when the activity/fragment in which you have initialised the SDK is getting killed/destroyed.


@Override
protected void onDestroy() {
   super.onDestroy();
   TruecallerSDK.clear();
}
onFailureProfileShared :-

##Error Code

What it means

1  Network Failure

2  User pressed back

3 Incorrect Partner Key

4 & 10 User not Verified on Truecaller*

5 Truecaller App Internal Error

13 User pressed back while verification in process

14 User pressed "SKIP / USE ANOTHER NUMBER"

15 To handle ActivityNotFound Exception, in case Truecaller app fails to initiate

## Verifying non Truecaller users

## For Android 8 and above :

<uses-permission android:name="android.permission.READ_PHONE_STATE"/>
<uses-permission android:name="android.permission.READ_CALL_LOG"/>
<uses-permission android:name="android.permission.ANSWER_PHONE_CALLS"/>


For Android 7 and below :

<uses-permission android:name="android.permission.READ_PHONE_STATE"/>
<uses-permission android:name="android.permission.READ_CALL_LOG"/>
<uses-permission android:name="android.permission.CALL_PHONE"/>

Depending on whether the verification medium is drop call or OTP, you need to call one of the following methods respectively:

Drop Call :-


TruecallerSDK.getInstance().verifyMissedCall(profile, apiCallback)

OTP 

TruecallerSDK.getInstance().verifyOtp(profile, OTP, apiCallback)

You need to call this method once you have received the callback in your VerificationCallback instance with requestCode as TYPE_OTP_RECEIVED and the OTP as a string in VerificationDataBundle

After you call the above method, you will receive a callback in your VerificationCallback instance with requestCode as TYPE_VERIFICATION_COMPLETE, which completes your verification process.
