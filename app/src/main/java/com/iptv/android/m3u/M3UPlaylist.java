package com.iptv.android.m3u;

import com.muparse.M3UItem;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class M3UPlaylist implements Serializable {

    public String playlistName;
    public List<M3UItem> playlistItems = new ArrayList<>();

    @Override
    public String toString() {
        return "M3UPlaylist{" +
                "playlistName='" + playlistName + '\'' +
                ", playlistItems=" + playlistItems +
                '}';
    }
}
