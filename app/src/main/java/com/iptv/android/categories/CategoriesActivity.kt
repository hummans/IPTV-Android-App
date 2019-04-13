package com.iptv.android.categories

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.iptv.android.R
import com.iptv.android.list.ListActivity
import com.iptv.android.list.PlaylistAdapter
import com.iptv.android.login.LoginActivity
import com.iptv.android.m3u.M3UPlaylist
import com.iptv.android.player.PlayerExo
import com.muparse.M3UItem
import kotlinx.android.synthetic.main.activity_categories.*
import kotlinx.android.synthetic.main.activity_login.*

class CategoriesActivity : AppCompatActivity() {

    private val adapter = CategoriesAdapter()
    private val adapterItems = PlaylistAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categories)

        rvCategories.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rvCategories.layoutManager = layoutManager
        rvCategories.adapter = adapter

        rvCategoryItems.setHasFixedSize(true)
        val layoutManagerCategoryItems = GridLayoutManager(this, 4)
        rvCategoryItems.layoutManager = layoutManagerCategoryItems
        rvCategoryItems.adapter = adapterItems

        adapter.apply {
            data = categories
            listener = object : CategoriesAdapter.CategorySelectListener {
                override fun onCategorySelected(category: M3UPlaylist) {
                    category.playlistName?.let { name ->
                        Log.d(TAG, "$name category selected")
                        adapterItems.data = category.playlistItems
                        rvCategoryItems.scrollToPosition(0)
                    }
                }
            }

            if (data.isNotEmpty()){
                listener?.onCategorySelected(data[0])
            }

        }

        adapterItems.apply {
            listener = object : PlaylistAdapter.PlayItemSelectListener{
                override fun onPlayItemSelected(m3UItem: M3UItem) {
                    m3UItem.itemUrl?.let { url ->
                        PlayerExo.m3UItem = m3UItem
                        val intent = Intent(this@CategoriesActivity, PlayerExo::class.java)
                        intent.putExtra("Name", m3UItem.itemName)
                        intent.putExtra("Url", url)
                        Log.d(ListActivity.TAG, "$m3UItem selected")
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
                adapterItems.filter.filter(query)
                return false
            }

            override fun onQueryTextChange(query: String): Boolean {
                // filter recycler view when text is changed
                adapterItems.filter.filter(query)
                return false
            }
        })
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if(item?.itemId == R.id.action_logout){
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
            sharedPreferences.edit().putString("USERNAME", null).apply()
            sharedPreferences.edit().putString("PASSWORD", null).apply()

            startActivity(Intent(this@CategoriesActivity, LoginActivity::class.java))
            finish()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    companion object {
        val TAG = CategoriesActivity::class.java.simpleName
        var categories: List<M3UPlaylist> = listOf()
    }
}
