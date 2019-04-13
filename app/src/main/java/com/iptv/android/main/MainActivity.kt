package com.iptv.android.main

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.iptv.android.R
import com.iptv.android.categories.CategoriesActivity
import com.iptv.android.m3u.M3UPlaylist
import com.iptv.android.settings.SettingsActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val liveTvs = mutableListOf<M3UPlaylist>()
        for (category in categories){
            category.playlistItems?.let {
                it[0]?.let {
                    it.itemName?.let {
                        if (it.contains("[") && it.contains("]")){
                            liveTvs.add(category)
                        }
                    }
                }
            }
        }

        val series = mutableListOf<M3UPlaylist>()
        for (category in categories){
            category.playlistItems?.let {
                it[0]?.let {
                    it.itemName?.let {
                        if (it.contains("S01") ||  it.contains("S02")  ||  it.contains("S03")){
                            series.add(category)
                        }
                    }
                }
            }
        }



        val radios = categories.filter { s ->
            s.playlistName.contains("radio", ignoreCase = true) || s.playlistName.contains(
                "radyo",
                ignoreCase = true
            )
        }

        var vods: MutableList<M3UPlaylist> = mutableListOf()
        vods.addAll(categories)

        for (tv in liveTvs) {
            for (i in vods.size - 1 downTo 0) {
                if (vods[i].playlistName == tv.playlistName) {
                    vods.removeAt(i)
                }
            }
        }

        for (radio in radios) {
            for (i in vods.size - 1 downTo 0) {
                if (vods[i].playlistName == radio.playlistName) {
                    vods.removeAt(i)
                }
            }

        }

        for (sery in series) {
            for (i in vods.size - 1 downTo 0) {
                if (vods[i].playlistName == sery.playlistName) {
                    vods.removeAt(i)
                }
            }

        }

        for (i in vods.size - 1 downTo 0) {
            if (vods[i].playlistItems!= null && vods[i].playlistItems.size > 0 && vods[i].playlistItems[0].itemUrl != null && !vods[i].playlistItems[0].itemUrl?.endsWith("mp4")!!) {
                liveTvs.add(0,vods[i])
                vods.removeAt(i)
            }
        }


        iv_livetv.setOnClickListener {
            CategoriesActivity.categories = liveTvs
            startActivity(Intent(this@MainActivity, CategoriesActivity::class.java))
        }
        iv_series.setOnClickListener {
            CategoriesActivity.categories = series
            startActivity(Intent(this@MainActivity, CategoriesActivity::class.java))
        }
        iv_vod.setOnClickListener {
            CategoriesActivity.categories = vods
            startActivity(Intent(this@MainActivity, CategoriesActivity::class.java))
        }
        iv_radio.setOnClickListener {
            CategoriesActivity.categories = radios
            startActivity(Intent(this@MainActivity, CategoriesActivity::class.java))
        }
        iv_settings.setOnClickListener { startActivity(Intent(this@MainActivity, SettingsActivity::class.java)) }
    }

    companion object {
        var categories: List<M3UPlaylist> = listOf()
    }
}
