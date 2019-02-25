package com.iptv.android.m3u;

import com.muparse.M3UItem;

import java.io.Serializable;
import java.util.List;

public class M3UPlaylist implements Serializable {

    private String playlistName;

    private String playlistParams;

    private List<M3UItem> playlistItems;

    public List<M3UItem> getPlaylistItems() {
        return playlistItems;
    }

    void setPlaylistItems(List<M3UItem> playlistItems) {
        this.playlistItems = playlistItems;
    }

    public String getPlaylistName() {
        return playlistName;
    }

    public void setPlaylistName(String playlistName) {
        this.playlistName = playlistName;
    }

    public String getPlaylistParams() {
        return playlistParams;
    }

    public void setPlaylistParams(String playlistParams) {
        this.playlistParams = playlistParams;
    }

    public String getSingleParameter(String paramName) {
        String[] paramsArray = this.playlistParams.split(" ");
        for (String parameter : paramsArray) {
            if (parameter.contains(paramName)) {
                return parameter.substring(parameter.indexOf(paramName) + paramName.length()).replace("=", "");
            }
        }
        return "";
    }
}
