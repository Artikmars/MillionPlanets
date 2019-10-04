package com.artamonov.millionplanets.data

import android.content.SharedPreferences
import android.os.Build
import androidx.annotation.RequiresApi
import com.crashlytics.android.Crashlytics
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.securepreferences.SecurePreferences
import javax.inject.Inject

class AppPreferenceHelper @Inject constructor(
    private val prefs: SecurePreferences,
    private val sharedPrefs: SharedPreferences,
    private val gson: Gson
) :
        PreferenceHelper {

    companion object {
        private val USERNAME = "USERNAME"
        private val PASSWORD = "PASSWORD"
        private val TOKEN = "TOKEN"
        private val TOKEN_EXPIRETIMESTAMP_MS = "TOKEN_EXPIRETIMESTAMP_MS"
        private val USER_PROFILE = "USER_PROFILE"
        private val TOKEN_REFRESH_HOURS = 3
        private val PUSH_NOTIFICATIONS_ENABLED = "PUSH_NOTIFICATIONS_ENABLED"
        private val SHOW_APP_TUTORIAL = "SHOW_APP_TUTORIAL"
        private val DEVICE_ID = "DEVICE_ID"
        private val SESSION = "SESSION"
        private val WINDOW_SESSION = "WINDOW_SESSION"
        private val USER_COMPANY_EXTERNAL_ID = "USER_COMPANY_EXTERNAL_ID"
        private val EXTERNAL_IP = "EXTERNAL_IP"
    }

    private fun getEditor(): SecurePreferences.Editor {
        return prefs.edit()
    }

    private fun getSharedEditor(): SharedPreferences.Editor {
        return sharedPrefs.edit()
    }

    @RequiresApi(Build.VERSION_CODES.GINGERBREAD)
    override fun saveUserProfile(
        userProfile: GoogleSignInAccount?
    ) {
        getEditor().putString(USER_PROFILE, gson.toJson(userProfile)).apply()
    }

    override fun privateUserAvailable(): Boolean {
        return prefs.getString(TOKEN, null) != null
    }

    override fun getUserProfile(): GoogleSignInAccount? {
        val userProfileJson = prefs.getString(USER_PROFILE, null) ?: return null

        return try {
            gson.fromJson(userProfileJson, GoogleSignInAccount::class.java)
        } catch (jse: JsonSyntaxException) {
            jse.printStackTrace()
            Crashlytics.logException(jse)
            null
        }
    }

    override fun userAvailable(): Boolean {
        return privateUserAvailable()
    }
}
