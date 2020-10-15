package com.artamonov.millionplanets.modules

import android.app.ActivityOptions
import android.content.Intent
import android.os.Build
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.artamonov.millionplanets.R

class ModulesListActivity : AppCompatActivity(R.layout.modules_list) {

    fun onWeapons(view: View?) {
        val intent = Intent(this, ModulesActivity::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            startActivity(intent, ActivityOptions.makeSceneTransitionAnimation(this).toBundle())
        } else {
            startActivity(intent)
        }
    }
}