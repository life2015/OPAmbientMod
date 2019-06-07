package com.retrox.aodmod.remote.lyric;

import android.content.ContentResolver;
import android.content.SharedPreferences;
import android.support.v4.provider.DocumentFile;
import android.util.Log;

import com.retrox.aodmod.MainHook;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Objects;


/**
 * Utils for quering and getting lyrics from server.
 */
public class NEMDownloader {
    private static final String TAG = "AODMOD";
    private static final String SEARCH_URL = "https://music.163.com/api/search/get/web?csrf_token";
    private static final String LYRIC_URL = "https://music.163.com/api/song/lyric";
    private static SharedPreferences mSharedPreferences;

    static void setSharedPreferences(SharedPreferences sp) {
        mSharedPreferences = sp;
    }

    private static String download(QueryResult item) {
        return download(item, false);
    }

    public static String download(QueryResult item, boolean needTranslation) {
        String params = "os=pc&id=" + item.mId + "&lv=-1&kv=-1&tv=-1";
        String response;
        try {
            response = Utils.post(LYRIC_URL, params);
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        }
        if (response == null || Objects.equals(response, "")) {
            Log.e(TAG, "Error: cannot get response from server");
            System.gc();
            return "";
        }
        JSONObject JSONObject = null;
        try {
            JSONObject = new JSONObject(response);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String lyric = "";
        String tlyric = "";
        if (JSONObject == null) {
            System.gc();
            return "";
        }
        try {
            lyric = JSONObject.getJSONObject("lrc").getString("lyric");
            tlyric = JSONObject.getJSONObject("tlyric").getString("lyric");
        } catch (JSONException | NullPointerException e) {
            e.printStackTrace();
        }
        //Log.v(TAG, tlyric);
        if ((!tlyric.equals("null") && !tlyric.equals("")) && needTranslation) {
            //Log.v(TAG, "1");
            lyric = Utils.mergeLyrics(lyric, tlyric);
        } else {
            try {
                lyric = JSONObject.getJSONObject("lrc").getString("lyric");
            } catch (JSONException | NullPointerException e) {
                e.printStackTrace();
            }
        }
//        Log.v(TAG, lyric);
//        if (mSharedPreferences.getBoolean("format_lyrics", false)) {
//            lyric = Utils.removeLastZero(lyric);
//        }
        return lyric;
    }

    /**
     * Download a single lyric for a song
     *
     * @param item    the song info
     * @param lrcPath file path to save this lyric
     */
    static boolean download(QueryResult item, String lrcPath, DocumentFile documentFile, ContentResolver contentResolver) {
        String result;
        try {
            result = download(item);
        } catch (NullPointerException e) {
            e.printStackTrace();
            return false;
        }
        if (Objects.equals(result, "")) {
            Log.e(TAG, "nothing");
            return false;
        }
        //return true;
        String charset = mSharedPreferences.getString("charset", "UTF-8");
        if (!Objects.equals(charset, "UTF-8")) {
            try {
                result = Utils.changeCharset(result, charset);
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
        return Utils.writeFile(result, lrcPath, documentFile, contentResolver);
    }

    /**
     * Batch download a bunch of lyrics
     *
     * @param items   the list of song infos
     * @param lrcPath file path to save this lyric
     */
    static boolean download(ArrayList<QueryResult> items, String lrcPath, DocumentFile documentFile, ContentResolver contentResolver) {
        return download(items.get(0), lrcPath, documentFile, contentResolver);

    }

    /**
     * Query server for lyrics info, parameters MUST be encoded in utf8.
     *
     * @param artist can be null
     * @param title  title of the song
     */
    public static ArrayList<QueryResult> query(String artist, String title) {
        ArrayList<QueryResult> result = new ArrayList<>();
        String params = "";
        try {
            String s;
            if (Objects.equals(artist, "未知") || Objects.equals(artist, "Unknown")) {
                s = URLEncoder.encode(title, "UTF-8");
            } else {
                s = URLEncoder.encode(title + " - " + artist, "UTF-8");
            }
            params = "s=" + s + "&type=1&offset=0&total=true&limit=20";
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String response;
        try {
            response = Utils.post(SEARCH_URL, params);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        if (response == null) {
            Log.e(TAG, "Error: cannot get response from server");
            return null;
        }
        JSONObject JSONObject = null;
        try {
            JSONObject = new JSONObject(response);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (JSONObject == null) {
            return null;
        }
        try {

            for (int i = 0; i < JSONObject.getJSONObject("result").getJSONArray("songs").length(); i++) {
                JSONArray songs = JSONObject.getJSONObject("result").getJSONArray("songs");
                String id = songs.getJSONObject(i).getString("id");
                String art = formatArtists(songs.getJSONObject(i).getJSONArray("artists"));
                String tit = songs.getJSONObject(i).getString("name");
                result.add(new QueryResult(Integer.parseInt(id), art, tit));
            }
        } catch (JSONException | NullPointerException e) {
            e.printStackTrace();
        }
        return result;
    }

    private static String formatArtists(JSONArray artists) {
        StringBuilder artistString = new StringBuilder();
        for (int i = 0; i < artists.length(); i++) {
            try {
                if (i != artists.length() - 1) {
                    artistString.append(artists.getJSONObject(i).getString("name")).append("/");
                } else {
                    artistString.append(artists.getJSONObject(i).getString("name"));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return artistString.toString();
    }


}
