package com.iptv.android.player;

import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.MediaRouteButton;
import android.util.Log;
import android.view.WindowManager;
import com.devbrackets.android.exomedia.ui.widget.VideoView;
import com.google.android.gms.cast.MediaInfo;
import com.google.android.gms.cast.framework.*;
import com.google.android.gms.cast.framework.media.RemoteMediaClient;
import com.iptv.android.R;

public class PlayerExo extends AppCompatActivity implements com.devbrackets.android.exomedia.listener.OnPreparedListener {

    private static final String TAG = "BURHAN";
    VideoView videoView;
    private String url = "";

    private CastSession mCastSession;
    private SessionManager mSessionManager;
    private final SessionManagerListener mSessionManagerListener = new SessionManagerListenerImpl();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_exo);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        Bundle bundle = getIntent().getExtras();

        mSessionManager = CastContext.getSharedInstance(this).getSessionManager();
        MediaRouteButton mMediaRouteButton = findViewById(R.id.media_route_button);
        CastButtonFactory.setUpMediaRouteButton(getApplicationContext(), mMediaRouteButton);
        CastContext castContext = CastContext.getSharedInstance(this);

        videoView = findViewById(R.id.video_view);
        videoView.setOnPreparedListener(this);
        try {
            url = bundle.getString("Url");
            videoView.setVideoURI(Uri.parse(bundle.getString("Url")));
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    public void onPrepared() {
        videoView.start();
    }

    @Override
    protected void onResume() {
        mCastSession = mSessionManager.getCurrentCastSession();
        mSessionManager.addSessionManagerListener(mSessionManagerListener);
        super.onResume();


        MediaInfo mediaInfo = new MediaInfo.Builder(url)
                .setStreamType(MediaInfo.STREAM_TYPE_BUFFERED)
                .setContentType("videos/mp4")
//                .setMetadata(movieMetadata)
//                .setStreamDuration(mSelectedMedia.getDuration() * 1000)
                .build();
        if (mCastSession != null) {
            RemoteMediaClient remoteMediaClient = mCastSession.getRemoteMediaClient();
            remoteMediaClient.load(mediaInfo);
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        mSessionManager.removeSessionManagerListener(mSessionManagerListener);
        mCastSession = null;
    }


    private class SessionManagerListenerImpl implements SessionManagerListener {
        @Override
        public void onSessionStarting(Session session) {

        }

        @Override
        public void onSessionStarted(Session session, String sessionId) {
            Log.d(TAG, "onSessionStarted() called with: session = [" + session + "], sessionId = [" + sessionId + "]");
            invalidateOptionsMenu();
        }

        @Override
        public void onSessionStartFailed(Session session, int i) {
            Log.d(TAG, "onSessionStartFailed() called with: session = [" + session + "], i = [" + i + "]");
        }

        @Override
        public void onSessionEnding(Session session) {
            Log.d(TAG, "onSessionEnding() called with: session = [" + session + "]");
        }

        @Override
        public void onSessionResumed(Session session, boolean wasSuspended) {
            Log.d(TAG, "onSessionResumed() called with: session = [" + session + "], wasSuspended = [" + wasSuspended + "]");
            invalidateOptionsMenu();
        }

        @Override
        public void onSessionResumeFailed(Session session, int i) {
            Log.d(TAG, "onSessionResumeFailed() called with: session = [" + session + "], i = [" + i + "]");
        }

        @Override
        public void onSessionSuspended(Session session, int i) {
            Log.d(TAG, "onSessionSuspended() called with: session = [" + session + "], i = [" + i + "]");
        }

        @Override
        public void onSessionEnded(Session session, int error) {
            Log.d(TAG, "onSessionEnded() called with: session = [" + session + "], error = [" + error + "]");
            finish();
        }

        @Override
        public void onSessionResuming(Session session, String s) {
            Log.d(TAG, "onSessionResuming() called with: session = [" + session + "], s = [" + s + "]");
        }
    }
}
