package com.iptv.android.list

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import com.iptv.android.R
import com.iptv.android.player.PlayerExo
import com.muparse.M3UItem
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        rvPlayList.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rvPlayList.layoutManager = layoutManager
        rvPlayList.adapter = PlaylistAdapter().apply {
            data = m3UPlaylist
            listener = object : PlaylistAdapter.PlayItemSelectListener {
                override fun onPlayItemSelected(m3UItem: M3UItem) {
                    m3UItem.itemUrl?.let { url ->
                        val intent = Intent(this@MainActivity, PlayerExo::class.java)
                        intent.putExtra("Name", m3UItem.itemName)
                        intent.putExtra("Url", url)
                        Log.d(TAG, "$m3UItem selected")
                        startActivity(intent)
                    }

                }
            }
        }


    }

    companion object {
        val TAG = MainActivity::class.java.simpleName
        var m3UPlaylist: List<M3UItem> = listOf()
    }
}
