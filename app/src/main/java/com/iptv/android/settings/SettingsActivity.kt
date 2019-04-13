package com.iptv.android.settings

import android.content.Intent
import android.net.Uri
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import com.iptv.android.R
import com.iptv.android.login.LoginActivity
import kotlinx.android.synthetic.main.activity_settings.*
import java.net.URLEncoder

class SettingsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

//        ivSet1.setOnClickListener {  }
//        ivSet2.setOnClickListener {  }
//        ivSet3.setOnClickListener {  }
//        ivSet4.setOnClickListener {  }
//        ivSet5.setOnClickListener {  }
        ivSet6.setOnClickListener {  }
        ivSet7.setOnClickListener {  }
        ivSet8.setOnClickListener {
            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse("http://www.fast.com"))
            startActivity(browserIntent)
        }
        ivSet9.setOnClickListener {
            openWhatsapp()
        }
        ivSet10.setOnClickListener {
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
            sharedPreferences.edit().putString("USERNAME", null).apply()
            sharedPreferences.edit().putString("PASSWORD", null).apply()

            startActivity(Intent(this@SettingsActivity, LoginActivity::class.java))
            finish()
        }
    }

    private fun openWhatsapp() {
        try {
            val gsm = "+32460242927"
            val intent = Intent(Intent.ACTION_VIEW)
            val url =
                "https://api.whatsapp.com/send?phone=$gsm&text=" + URLEncoder.encode(
                    "Merhaba IPTV Medya,",
                    "UTF-8"
                )
            intent.setPackage("com.whatsapp")
            intent.data = Uri.parse(url)
            if (intent.resolveActivity(packageManager) != null) {
                startActivity(intent)
            } else {

            }
        } catch (e: Exception) {
            Log.e("ERROR WHATSAPP", e.toString())

        }
    }
}
