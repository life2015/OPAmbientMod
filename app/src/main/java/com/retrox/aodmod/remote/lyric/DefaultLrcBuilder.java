package com.retrox.aodmod.remote.lyric;

import android.util.Log;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * 解析歌词，得到LrcRow的集合
 */
public class DefaultLrcBuilder {
    static final String TAG = "DefaultLrcBuilder";

    public static List<LrcRow> getLrcRows(String rawLrc) {
        Log.d(TAG, "getLrcRows by rawString");
        if (rawLrc == null || rawLrc.length() == 0) {
            Log.e(TAG, "getLrcRows rawLrc null or empty");
            return null;
        }
        StringReader reader = new StringReader(rawLrc);
        BufferedReader br = new BufferedReader(reader);
        String line = null;
        List<LrcRow> rows = new ArrayList<LrcRow>();
        try {
            //循环地读取歌词的每一行
            do {
                line = br.readLine();
                /**
                 一行歌词只有一个时间的  例如：徐佳莹   《我好想你》
                 [01:15.33]我好想你 好想你

                 一行歌词有多个时间的  例如：草蜢 《失恋战线联盟》
                 [02:34.14][01:07.00]当你我不小心又想起她
                 [02:45.69][02:42.20][02:37.69][01:10.60]就在记忆里画一个叉
                 **/
                Log.d(TAG, "lrc raw line: " + line);
                if (line != null && line.length() > 0) {
                    //解析每一行歌词 得到每行歌词的集合，因为有些歌词重复有多个时间，就可以解析出多个歌词行来
                    List<LrcRow> lrcRows = LrcRowUtil.createRows(line);
                    if (lrcRows != null && lrcRows.size() > 0) {
                        for (LrcRow row : lrcRows) {
                            Log.d(TAG, "row = " + row);
                            rows.add(row);
                        }
                    }
                }
            } while (line != null);

            if (rows.size() > 0) {
                // 根据歌词行的时间排序
                Collections.sort(rows);
                if (rows != null && rows.size() > 0) {
                    int size = rows.size();
                    for (int i = 0; i < size; i++) {
                        LrcRow lrcRow = rows.get(i);

                        /**
                         * 这里设置下 每一行歌词 结尾的时间
                         * 设置为 下一行歌词的开始时间
                         *
                         * 这样还是有点不准，只是粗略的这么计算，仅做demo使用。
                         * 如果要做为商用使用的话，自己重新计算每一行的结束时间
                         */
                        if (i < size - 1) {
                            lrcRow.setEndTime(rows.get(i + 1).getStartTime());
                        } else {
                            lrcRow.setEndTime(lrcRow.getStartTime() + 10000);
                        }
                        Log.d(TAG, "lrcRow:" + lrcRow.toString());
                    }
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "parse exceptioned:" + Log.getStackTraceString(e));
            return null;
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            reader.close();
        }
        return rows;
    }
}
