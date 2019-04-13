package com.iptv.android.m3u;

import android.util.Log;
import com.muparse.M3UItem;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.*;

public class M3UParser {

    private static final String TAG = "M3UParser";
    private static final String EXT_M3U = "#EXTM3U";
    private static final String EXT_INF = "#EXTINF:";
    private static final String EXT_PLAYLIST_NAME = "#PLAYLIST";
    private static final String EXT_LOGO = "tvg-logo";
    private static final String EXT_GROUP_TITLE = "group-title";
    private static final String EXT_URL = "http://";

    public String convertStreamToString(InputStream is) {
        try {
            return new Scanner(is).useDelimiter("\\A").next();
        } catch (NoSuchElementException e) {
            return "";
        }
    }

    private M3UPlaylist parseFile(InputStream inputStream) throws FileNotFoundException {
        Log.d(TAG, "parseFile() called with: inputStream = [" + inputStream + "]");
        M3UPlaylist m3UPlaylist = new M3UPlaylist();
        List<M3UItem> playlistItems = new ArrayList<>();
        String stream = convertStreamToString(inputStream);
        String linesArray[] = stream.split(EXT_INF);
        for (int i = 0; i < linesArray.length; i++) {
            String currLine = linesArray[i];
            if (currLine.contains(EXT_M3U)) {
                //header of file
                if (currLine.contains(EXT_PLAYLIST_NAME)) {
                    String fileParams = currLine.substring(EXT_M3U.length(), currLine.indexOf(EXT_PLAYLIST_NAME));
                    String playListName = currLine.substring(currLine.indexOf(EXT_PLAYLIST_NAME) + EXT_PLAYLIST_NAME.length()).replace(":", "");
                    m3UPlaylist.playlistName = playListName;
                } else {
                    m3UPlaylist.playlistName = "Noname Playlist";
                }
            } else {
                M3UItem playlistItem = new M3UItem();
                String[] dataArray = currLine.split(",");
                if (dataArray[0].contains(EXT_LOGO)) {
                    String duration = dataArray[0].substring(0, dataArray[0].indexOf(EXT_LOGO)).replace(":", "").replace("\n", "");
                    String icon = dataArray[0].substring(dataArray[0].indexOf(EXT_LOGO) + EXT_LOGO.length()).replace("=", "").replace("\"", "").replace("\n", "");
                    if (icon.contains(EXT_GROUP_TITLE)){
                        icon = icon.substring(0, icon.indexOf(EXT_GROUP_TITLE)).trim();
                    }
                    playlistItem.setItemDuration(duration);
                    playlistItem.setItemIcon(icon);
                } else {
                    String duration = dataArray[0].replace(":", "").replace("\n", "");
                    playlistItem.setItemDuration(duration);
                    playlistItem.setItemIcon("");
                }
                if (dataArray[0].contains(EXT_GROUP_TITLE)) {
                    String groupTitle = dataArray[0].substring(dataArray[0].indexOf(EXT_GROUP_TITLE) + EXT_GROUP_TITLE.length()).replace("=", "").replace("\"", "").replace("\n", "");
                    playlistItem.setGroupTitle(groupTitle);
                }
                try {
                    String url = dataArray[1].substring(dataArray[1].indexOf(EXT_URL)).replace("\n", "").replace("\r", "");
                    String name = dataArray[1].substring(0, dataArray[1].indexOf(EXT_URL)).replace("\n", "");
                    playlistItem.setItemName(name);
                    playlistItem.setItemUrl(url);
                } catch (Exception fdfd) {
                    Log.e("Google", "Error: " + fdfd.fillInStackTrace());
                }
                playlistItems.add(playlistItem);
            }
        }
        m3UPlaylist.playlistItems = playlistItems;
        return m3UPlaylist;
    }

    public List<M3UPlaylist> parseM3UFile(InputStream inputStream) {
        Map<String, M3UPlaylist> data = new TreeMap<String, M3UPlaylist>();
        try {
            M3UPlaylist m3UPlaylist = parseFile(inputStream);
            if (m3UPlaylist.playlistItems != null) {
                for (M3UItem m3u : m3UPlaylist.playlistItems) {
                    if (m3u.getGroupTitle() == null || m3u.getGroupTitle().equals("") || m3u.getGroupTitle().startsWith("***")) {
                        continue;
                    }
                    if (!data.containsKey(m3u.getGroupTitle())) {
                        M3UPlaylist playlist = new M3UPlaylist();
                        playlist.playlistName = m3u.getGroupTitle();
                        playlist.playlistItems.add(m3u);
                        data.put(m3u.getGroupTitle(), playlist);
                    } else {
                        if (m3u.getGroupTitle() != null && data.get(m3u.getGroupTitle()) != null) {
                            data.get(m3u.getGroupTitle()).playlistItems.add(m3u);
                        }

                    }
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        List<M3UPlaylist> retList = new ArrayList<>();
        for (String key : data.keySet()) {
            retList.add(data.get(key));

            for (M3UItem item : data.get(key).playlistItems){
                Log.d("BURHAN", item.getItemIcon());
            }
        }

        return retList;
    }
}
