package com.iptv.android.player;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.MediaRouteButton;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.widget.MediaController;
import com.devbrackets.android.exomedia.ui.widget.VideoView;
import com.google.android.gms.cast.framework.CastSession;
import com.iptv.android.R;
import com.muparse.M3UItem;
import pl.droidsonroids.casty.Casty;
import pl.droidsonroids.casty.MediaData;

public class PlayerExo extends AppCompatActivity implements com.devbrackets.android.exomedia.listener.OnPreparedListener {

    VideoView videoView;
    private Casty casty;

    String url = "";

    public static M3UItem m3UItem = null;
    MediaRouteButton mediaRouteButton = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_exo);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Bundle bundle = getIntent().getExtras();

        videoView = findViewById(R.id.video_view);
        videoView.setOnPreparedListener(this);
        if (m3UItem != null) {
            url = m3UItem.getItemUrl();
        }

        try {
            videoView.setVideoURI(Uri.parse(url));
        } catch (Exception e) {
            e.printStackTrace();
        }


        casty = Casty.create(this).withMiniController();
        mediaRouteButton = findViewById(R.id.media_route_button);
        casty.setUpMediaRouteButton(mediaRouteButton);
        casty.setOnCastSessionUpdatedListener(new Casty.OnCastSessionUpdatedListener() {
            @Override
            public void onCastSessionUpdated(CastSession castSession) {
                Log.d("BURHAN", "onCastSessionUpdated() called with: castSession = [" + castSession + "]");
            }
        });


        casty.setOnConnectChangeListener(new Casty.OnConnectChangeListener() {
            @Override
            public void onConnected() {
                videoView.pause(true);
                casty.getPlayer().loadMediaAndPlay(getMediaData(url));
            }

            @Override
            public void onDisconnected() {
                videoView.start();
                videoView.getDuration();
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        if (casty!= null) {
            Log.d("BURHAN", "connected: " + casty.isConnected());
            Log.d("BURHAN", "player: " + (casty.getPlayer() != null));
        }

        if (m3UItem != null) {
            url = m3UItem.getItemUrl();
        }

        if (casty != null && casty.isConnected() && (casty.getPlayer() != null)) {
            casty.getPlayer().pause();
            videoView.pause(true);
            casty.getPlayer().loadMediaAndPlay(getMediaData(url));
        }


        videoView.setTag(videoView.getVisibility());
        videoView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                if(videoView.getVideoControlsCore().isVisible()){
                    mediaRouteButton.setVisibility(View.VISIBLE);
                }else{
                    mediaRouteButton.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d("BURHAN", "onRestoreInstanceState: ");
    }

    private MediaData getMediaData(String url) {

        Log.d("BURHAN", "videoView.getCurrentPosition(): "+videoView.getCurrentPosition());
        final MediaData mediaData = new MediaData.Builder(url)
                .setStreamType(MediaData.STREAM_TYPE_BUFFERED) //required
                .setContentType("videos/mp4") //required
                .setMediaType(MediaData.MEDIA_TYPE_MOVIE)
                .setPosition(videoView.getCurrentPosition())
                .setTitle(m3UItem.getItemName())
                .setSubtitle(m3UItem.getGroupTitle())
                .addPhotoUrl("https://n11scdn.akamaized.net/a1/450/elektronik/medya-oynatici/ip-tv-1-yillik-uyelik-ip-tv-12-aylik-uyelik__1205202673201211.jpg")
                .build();

        return mediaData;
    }

    @Override
    public void onPrepared() {
        videoView.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d("BURHAN", "onPause() called");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d("BURHAN", "onSaveInstanceState: ");
    }
}
