package com.iptv.android.list

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.util.Log
import android.view.Menu
import com.iptv.android.R
import com.iptv.android.player.PlayerExo
import com.muparse.M3UItem
import kotlinx.android.synthetic.main.activity_list.*


class ListActivity : AppCompatActivity() {

    private val adapter = PlaylistAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)

        rvPlayList.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rvPlayList.layoutManager = layoutManager
        rvPlayList.adapter = adapter

        adapter.apply {
            data = m3UPlaylist
            listener = object : PlaylistAdapter.PlayItemSelectListener {
                override fun onPlayItemSelected(m3UItem: M3UItem) {
                    m3UItem.itemUrl?.let { url ->
                        PlayerExo.m3UItem = m3UItem
                        val intent = Intent(this@ListActivity, PlayerExo::class.java)
                        intent.putExtra("Name", m3UItem.itemName)
                        intent.putExtra("Url", url)
                        Log.d(TAG, "$m3UItem selected")
                        startActivity(intent)
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)

        // Associate searchable configuration with the SearchView
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView = menu?.findItem(R.id.action_search)?.actionView as SearchView
        searchView.setSearchableInfo(
            searchManager
                .getSearchableInfo(componentName)
        )
        searchView.maxWidth = Integer.MAX_VALUE

        // listening to search query text change
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                // filter recycler view when query submitted
                adapter.filter.filter(query)
                return false
            }

            override fun onQueryTextChange(query: String): Boolean {
                // filter recycler view when text is changed
                adapter.filter.filter(query)
                return false
            }
        })
        return true
    }

    companion object {
        val TAG = ListActivity::class.java.simpleName
        var m3UPlaylist: List<M3UItem> = listOf()
    }
}
