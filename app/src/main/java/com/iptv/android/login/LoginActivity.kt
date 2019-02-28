package com.iptv.android.login

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import com.iptv.android.m3u.M3UParser
import com.iptv.android.R
import com.iptv.android.categories.CategoriesActivity
import com.iptv.android.m3u.M3UPlaylist
import kotlinx.android.synthetic.main.activity_login.*
import java.io.BufferedInputStream
import java.net.URL


class LoginActivity : AppCompatActivity() {

    // Progress Dialog
    private var pDialog: ProgressDialog? = null
    val progress_bar_type = 0

    // File url to download
    private var file_url = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        btnLogin.setOnClickListener {
            file_url =
                    "http://goldiptv24.com:80/get.php?username=${etUserName.text}&password=${etPassword.text}&type=m3u_plus&output=ts"
            DownloadFileFromURL().execute(file_url)
        }

        val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this@LoginActivity)
        val username: String? = sharedPreferences.getString("USERNAME", null)
        val password: String? = sharedPreferences.getString("PASSWORD", null)
        username?.let { etUserName.setText(it) }
        password?.let { etPassword.setText(it) }
        if (username != null && password != null) {
            btnLogin.performClick()
        }

    }

    /**
     * Showing Dialog
     */

    override fun onCreateDialog(id: Int): Dialog? {
        when (id) {
            progress_bar_type // we set this to 0
            -> {
                pDialog = ProgressDialog(this)
                pDialog?.let { pDialog ->
                    pDialog.setMessage("Giriş yapılıyor. Lütfen bekleyiniz...")
                    pDialog.isIndeterminate = true
                    pDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER)
                    pDialog.setCancelable(false)
                    pDialog.show()
                }

                return pDialog
            }
            else -> return null
        }
    }


    internal inner class DownloadFileFromURL : AsyncTask<String, String, List<M3UPlaylist>>() {

        /**
         * Before starting background thread Show Progress Bar Dialog
         */
        override fun onPreExecute() {
            super.onPreExecute()
            showDialog(progress_bar_type)
        }

        /**
         * Downloading file in background thread
         */
        override fun doInBackground(vararg f_url: String): List<M3UPlaylist>? {
            var count: Int
            try {
                val url = URL(f_url[0])
                val conection = url.openConnection()
                conection.connect()

                // this will be useful so that you can show a tipical 0-100%
                // progress bar
                val lenghtOfFile = conection.contentLength

                // download the file
                val input = BufferedInputStream(
                    url.openStream(),
                    8192
                )

                val playlist = M3UParser().parseM3UFile(input)
                input.close()
                return playlist

            } catch (e: Exception) {
                Log.e("Error: ", e.message)
            }

            return null
        }

        /**
         * Updating progress bar
         */
        override fun onProgressUpdate(vararg progress: String) {
            // setting progress percentage
            pDialog?.progress = Integer.parseInt(progress[0])
        }

        /**
         * After completing background task Dismiss the progress dialog
         */
        override fun onPostExecute(categories: List<M3UPlaylist>?) {
            // dismiss the dialog after the file was downloaded
            try {
                dismissDialog(progress_bar_type)
            } catch (e: Exception) {
            }
            if (!categories.isNullOrEmpty()) {

                val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this@LoginActivity)
                sharedPreferences.edit().putString("USERNAME", etUserName.text.toString()).apply()
                sharedPreferences.edit().putString("PASSWORD", etPassword.text.toString()).apply()

                CategoriesActivity.categories = categories
                startActivity(Intent(this@LoginActivity, CategoriesActivity::class.java))
                finish()
            } else {
                Toast.makeText(this@LoginActivity, "Kullanıcı Adı ya da şifrenizi hatalı girdiniz.", Toast.LENGTH_LONG)
                    .show()
            }

        }

    }
}
