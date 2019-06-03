package com.retrox.aodmod.remote.lyric;

public class QueryResult {
    final int mId;
    private final String mArtist;
    private final String mTitle;

    QueryResult(int id, String artist, String title) {
        mId = id;
        mArtist = artist;
        mTitle = title;
    }

    @Override
    public String toString() {
        return mTitle + " - " + mArtist;
    }

    public String getArtist() {
        return mArtist;
    }

    public String getTitle() {
        return mTitle;
    }
}
