package com.artamonov.millionplanets.data

import com.google.android.gms.auth.api.signin.GoogleSignInAccount

interface PreferenceHelper {

    fun getUserProfile(): GoogleSignInAccount?

    fun privateUserAvailable(): Boolean

    fun saveUserProfile(userProfile: GoogleSignInAccount?)

    fun userAvailable(): Boolean
}