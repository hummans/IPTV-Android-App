package com.iptv.android.categories

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.SearchView
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import com.iptv.android.R
import com.iptv.android.list.ListActivity
import com.iptv.android.login.LoginActivity
import com.iptv.android.m3u.M3UPlaylist
import kotlinx.android.synthetic.main.activity_categories.*
import kotlinx.android.synthetic.main.activity_login.*

class CategoriesActivity : AppCompatActivity() {

    private val adapter = CategoriesAdapter()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_categories)

        rvCategories.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rvCategories.layoutManager = layoutManager
        rvCategories.adapter = adapter

        adapter.apply {
            data = categories
            listener = object : CategoriesAdapter.CategorySelectListener {
                override fun onCategorySelected(category: M3UPlaylist) {
                    category.playlistName?.let { name ->
                        Log.d(TAG, "$name category selected")
                        val intent = Intent(this@CategoriesActivity, ListActivity::class.java)
                        ListActivity.m3UPlaylist = category.playlistItems
                        startActivity(intent)
                    }
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu_with_exit, menu)

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
