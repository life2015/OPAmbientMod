package com.retrox.aodmod.remote.lyric;

import android.util.Log;

import java.util.Objects;

/**
 * 歌词行
 * 包括该行歌词的时间，歌词内容
 */
public class LrcRow implements Comparable<LrcRow>{
    public final static String TAG = "LrcRow";

    /** 该行歌词要开始播放的时间，格式如下：[02:34.14] */
    public String startTimeString;

    /** 该行歌词要开始播放的时间，由[02:34.14]格式转换为long型，
     * 即将2分34秒14毫秒都转为毫秒后 得到的long型值：startTime=02*60*1000+34*1000+14
     */
    public long startTime;

    /** 该行歌词要结束播放的时间，由[02:34.14]格式转换为long型，
     * 即将2分34秒14毫秒都转为毫秒后 得到的long型值：startTime=02*60*1000+34*1000+14
     */
    public long endTime;

    /** 该行歌词的内容 */
    public String content;

    
    public LrcRow(){}

    public String getStartTimeString() {
        return startTimeString;
    }

    public void setStartTimeString(String startTimeString) {
        this.startTimeString = startTimeString;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public String toString() {
        return "LrcRow{" +
                "startTimeString='" + startTimeString + '\'' +
                ", startTime=" + startTime +
                ", endTime=" + endTime +
                ", content='" + content + '\'' +
                '}';
    }

    /**
     * 排序的时候，根据歌词的时间来排序
     */
    public int compareTo(LrcRow another) {
        return (int)(this.startTime - another.startTime);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LrcRow lrcRow = (LrcRow) o;
        return startTime == lrcRow.startTime &&
                endTime == lrcRow.endTime &&
                Objects.equals(startTimeString, lrcRow.startTimeString) &&
                Objects.equals(content, lrcRow.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(startTimeString, startTime, endTime, content);
    }
}