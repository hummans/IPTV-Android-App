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
import com.google.android.gms.cast.framework.CastButtonFactory
import com.iptv.android.R
import com.iptv.android.player.PlayerExo
import com.muparse.M3UItem
import kotlinx.android.synthetic.main.activity_main.*
import com.google.android.gms.cast.framework.CastContext
import com.google.android.gms.cast.CastRemoteDisplayLocalService
import android.support.v7.media.MediaRouter.CALLBACK_FLAG_REQUEST_DISCOVERY
import android.support.v4.app.NotificationCompat.getExtras
import com.google.android.gms.cast.CastDevice
import com.google.android.gms.cast.CastMediaControlIntent
import android.support.v7.media.MediaRouteSelector
import android.support.v7.media.MediaRouter
import android.widget.Toast
import android.app.PendingIntent
import android.net.wifi.WifiConfiguration
import com.google.android.gms.common.api.Status


class ListActivity : AppCompatActivity() {

    private val mediaRouter: MediaRouter? = null
    private var castDevice: CastDevice? = null

    private val adapter = PlaylistAdapter()

    private var mCastContext: CastContext? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mCastContext = CastContext.getSharedInstance(this)

        rvPlayList.setHasFixedSize(true)
        val layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rvPlayList.layoutManager = layoutManager
        rvPlayList.adapter = adapter

        adapter.apply {
            data = m3UPlaylist
            listener = object : PlaylistAdapter.PlayItemSelectListener {
                override fun onPlayItemSelected(m3UItem: M3UItem) {
                    m3UItem.itemUrl?.let { url ->
                        val intent = Intent(this@ListActivity, PlayerExo::class.java)
                        intent.putExtra("Name", m3UItem.itemName)
                        intent.putExtra("Url", url)
                        Log.d(TAG, "$m3UItem selected")
                        startActivity(intent)
                    }
                }
            }
        }


        setupMediaRouter()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        super.onCreateOptionsMenu(menu)
        menuInflater.inflate(R.menu.main_menu, menu)

        CastButtonFactory.setUpMediaRouteButton(
            applicationContext,
            menu,
            R.id.media_route_menu_item
        )

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


    override fun onResume() {
        super.onResume()
        if (!isRemoteDisplaying()) {
            if (castDevice != null) {
                startCastService(castDevice)
            }
        }
    }

    public override fun onDestroy() {
        if (mediaRouter != null) {
            mediaRouter.removeCallback(mMediaRouterCallback)
        }
        super.onDestroy()
    }


    private fun setupMediaRouter() {
        mediaRouter = MediaRouter.getInstance(applicationContext)
        val mediaRouteSelector = MediaRouteSelector.Builder().addControlCategory(
            CastMediaControlIntent.categoryForCast(getString(R.string.app_cast_id))
        ).build()
        if (isRemoteDisplaying()) {
            this.castDevice = CastDevice.getFromBundle(mediaRouter.getSelectedRoute().extras)
        } else {
            val extras = intent.extras
            if (extras != null) {
                castDevice = extras.getParcelable(INTENT_EXTRA_CAST_DEVICE)
            }
        }

        mediaRouter.addCallback(
            mediaRouteSelector, mMediaRouterCallback,
            MediaRouter.CALLBACK_FLAG_REQUEST_DISCOVERY
        )
    }

    private fun isRemoteDisplaying(): Boolean {
        return CastRemoteDisplayLocalService.getInstance() != null
    }


    private val mMediaRouterCallback = object : MediaRouter.Callback() {
        override fun onRouteSelected(router: MediaRouter?, info: MediaRouter.RouteInfo) {
            castDevice = CastDevice.getFromBundle(info.extras)
            Toast.makeText(
                applicationContext,
                getString(R.string.app_id) + info.name, Toast.LENGTH_LONG
            ).show()
            startCastService(castDevice)
        }

        override fun onRouteUnselected(router: MediaRouter?, info: MediaRouter.RouteInfo?) {
            if (isRemoteDisplaying()) {
                CastRemoteDisplayLocalService.stopService()
            }
            castDevice = null
        }
    }

    private fun startCastService(castDevice: CastDevice) {
        val intent = Intent(this@ListActivity, MainActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        val notificationPendingIntent = PendingIntent.getActivity(this@ListActivity, 0, intent, 0)

        val settings = CastRemoteDisplayLocalService.NotificationSettings.Builder().setNotificationPendingIntent(
            notificationPendingIntent
        ).build()

        CastRemoteDisplayLocalService.startService(this@ListActivity, PresentationService::class.java!!,
            getString(R.string.app_id), castDevice, settings,
            object : CastRemoteDisplayLocalService.Callbacks {
                override fun onRemoteDisplaySessionError(p0: Status?) {
                    initError()

                    this@ListActivity.castDevice = null
                    this@ListActivity.finish()
                }

                override fun onServiceCreated(service: CastRemoteDisplayLocalService) {
                    (service as PresentationService).setAdViewModel(
                        fragmentStatePagerAdapter.getAdAt(currentPosition)
                    )
                }

                override fun onRemoteDisplaySessionStarted(service: CastRemoteDisplayLocalService) {}

                fun onRemoteDisplaySessionError(errorReason: WifiConfiguration.Status) {
                    initError()

                    this@ListActivity.castDevice = null
                    this@ListActivity.finish()
                }

                override fun onRemoteDisplaySessionEnded(castRemoteDisplayLocalService: CastRemoteDisplayLocalService) {}
            })
    }

    private fun initError() {
        val toast = Toast.makeText(
            applicationContext, R.string.toast_connection_error,
            Toast.LENGTH_SHORT
        )
        mediaRouter?.selectRoute(mediaRouter.defaultRoute)
        toast.show()
    }

    companion object {
        val TAG = ListActivity::class.java.simpleName
        var m3UPlaylist: List<M3UItem> = listOf()
    }
}
