package com.iptv.android.login

import android.app.Dialog
import android.app.ProgressDialog
import android.content.Intent
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.iptv.android.m3u.M3UParser
import com.iptv.android.R
import com.iptv.android.list.MainActivity
import com.iptv.android.m3u.M3UPlaylist
import com.muparse.M3UItem
import java.io.BufferedInputStream
import java.net.URL


class LoginActivity : AppCompatActivity() {

    // Progress Dialog
    private var pDialog: ProgressDialog? = null
    val progress_bar_type = 0

    // File url to download
    private val file_url =
        "http://goldiptv24.com/get.php?username=09e4OjOH4N&password=2X93EHak91&type=m3u_plus&output=ts"


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        DownloadFileFromURL().execute(file_url)
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
                    pDialog.setMessage("Downloading file. Please wait...")
                    pDialog.isIndeterminate = false
                    pDialog.max = 100
                    pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
                    pDialog.setCancelable(false)
                    pDialog.show()
                }

                return pDialog
            }
            else -> return null
        }
    }


    internal inner class DownloadFileFromURL : AsyncTask<String, String, M3UPlaylist>() {

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
        override fun doInBackground(vararg f_url: String): M3UPlaylist? {
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

                val playlist = M3UParser().parseFile(input)
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
        override fun onPostExecute(m3UPlaylist: M3UPlaylist?) {
            // dismiss the dialog after the file was downloaded
            try {
                dismissDialog(progress_bar_type)
            } catch (e: Exception) {
            }
            if (m3UPlaylist != null && m3UPlaylist.playlistItems != null && m3UPlaylist.playlistItems.size > 0) {
                MainActivity.m3UPlaylist = m3UPlaylist.playlistItems
                startActivity(Intent(this@LoginActivity, MainActivity::class.java))

            }

        }

    }
}
