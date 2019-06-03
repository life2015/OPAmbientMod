package com.retrox.aodmod.remote.lyric;


import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.ParcelFileDescriptor;
import android.support.v4.provider.DocumentFile;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.util.Objects;
import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import okhttp3.ResponseBody;


public class Utils {

    private static final String TAG = "Utils";

    private static final MediaType mMediaType = MediaType.parse("application/x-www-form-urlencoded; charset=utf-8");
    private static SharedPreferences mSharedPreferences;
    private static OkHttpClient mClient;

    static void setSharedPreferences(SharedPreferences sp) {
        mSharedPreferences = sp;
    }

    static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = null;
        if (connectivityManager != null) {
            activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        }
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }


    static String readFile(String pathname) throws IOException {
        File file = new File(pathname);
        StringBuilder fileContents = new StringBuilder((int) file.length());
        String lineSeparator = System.getProperty("line.separator");

        try (Scanner scanner = new Scanner(file)) {
            while (scanner.hasNextLine()) {
                fileContents.append(scanner.nextLine()).append(lineSeparator);
            }
            return fileContents.toString();
        }
    }

    static String getFolder(String path) {
        int lastSlash = path.lastIndexOf("/");
        if (lastSlash > 0) {
            return path.substring(0, lastSlash);
        }
        return null;
    }

    static String getFileNameWithoutExtension(String path) {
        int lastSlash = path.lastIndexOf("/");
        if (lastSlash <= 0) {
            return "";
        }
        int lastDot = path.lastIndexOf(".");
        lastDot = lastDot > lastSlash ? lastDot : path.length();
        return path.substring(lastSlash + 1, lastDot);
    }

    static boolean writeFile(String content, String path, DocumentFile documentFile, ContentResolver contentResolver) {
        try {
            FileWriter fstream = new FileWriter(path, false);
            BufferedWriter out = new BufferedWriter(fstream);
            out.write(content);
            out.close();
        } catch (FileNotFoundException e) {
            if (documentFile != null && contentResolver != null) {
                Uri uri;
                String paths[] = path.split("/");
                int size = paths.length;
                //DocumentFile documentFile = DocumentFile.fromTreeUri(context, uri);
                for (int i = 0; i < size; i++) {
                    if (i > 2) {
                        DocumentFile file;
                        if (i != size - 1) {
                            file = documentFile.findFile(paths[i]);
                        } else {
                            if (documentFile == null) {
                                return false;
                            }
                            File f = new File(path);
                            if (f.exists() && !f.isDirectory()) {
                                file = documentFile.findFile(paths[i]);
                            } else {
                                file = documentFile.createFile(null, paths[i]);
                            }

                        }
                        documentFile = file;
                    }

                }
                uri = documentFile.getUri();
                ParcelFileDescriptor pfd;
                if (!Objects.equals(uri.toString(), "")) {
                    try {
                        pfd = contentResolver.openFileDescriptor(uri, "w");
                    } catch (FileNotFoundException ee) {
                        ee.printStackTrace();
                        return false;
                    }
                    if (pfd == null) {
                        return false;
                    }
                    try {
                        FileOutputStream fileOutputStream = new FileOutputStream(pfd.getFileDescriptor());
                        fileOutputStream.write(content.getBytes());
                        fileOutputStream.close();
                    } catch (NullPointerException | IOException ee) {
                        ee.printStackTrace();
                        return false;
                    }
                }
            } else {
                e.printStackTrace();
                return false;
            }
        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    static void deleteFile(String path) {
        File f = new File(path);
        //if (!f.exists()) {
        //return false;
        //}
        Boolean ok = f.delete();
        if (!ok) {
            Log.v(TAG, "delete failed");
        }
        //return true;
    }

    static int checkDocumentUI(PackageManager pm) {
        ApplicationInfo ai;
        try {
            ai = pm.getApplicationInfo("com.android.documentsui", 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return -2;
        }
        if (!ai.enabled) {
            return -1;
        }
        return 0;
    }

    static String changeCharset(String str, String newCharset) throws UnsupportedEncodingException {
        if (str != null) {
            //用默认字符编码解码字符串。与系统相关，中文windows默认为GB2312
            byte[] bs = str.getBytes();
            return new String(bs, newCharset);    //用新的字符编码生成字符串
        }
        return null;
    }

    static String changeCharseToUTF8(String str, String newCharset) throws UnsupportedEncodingException {
        if (str != null) {
            //用默认字符编码解码字符串。与系统相关，中文windows默认为GB2312
            byte[] bs = str.getBytes(newCharset);
            return new String(bs, "UTF-8");    //用新的字符编码生成字符串
        }
        return null;
    }

    static String removeLastZero(String str) {
        try {
            StringBuilder sb = new StringBuilder();
            BufferedReader reader = new BufferedReader(new StringReader(str));
            Pattern p = Pattern.compile("^(\\[.*])+(.*)");
            Pattern tp = Pattern.compile("^(\\[.{1,2}:.{1,2}\\..{3}])+(.*)");
            String line;
            while ((line = reader.readLine()) != null) {
                Matcher m = p.matcher(line);
                if (m.find()) {
                    Matcher tm = tp.matcher(line);
                    if (tm.find()) {
                        String formatted = m.group(1).substring(0, m.group(1).length() - 2) + "]";
                        sb.append(formatted);
                        Log.v(TAG, formatted);
                    } else {
                        sb.append(m.group(1));
                    }
                    sb.append(m.group(2));
                    sb.append(Constants.LINE_SEPARATOR);
                }
            }
            reader.close();


            return sb.toString();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return null;
    }

    static void copyToClipboard(String content, Context context) {
        ClipboardManager cmb = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData mClipData = ClipData.newPlainText("LrcJaeger", content);
        if (cmb != null) {
            cmb.setPrimaryClip(mClipData);
        }
    }

    static String post(String url, String params) throws IOException {
        if (mClient == null) {
            mClient = new OkHttpClient();
        }
        RequestBody body = RequestBody.create(mMediaType, params);
        Request.Builder builder = new Request.Builder();
        builder.url(url);
        builder.post(body);
        builder.addHeader("User-Agent", "NeteaseMusic/4.3.2.1514468990(111);Dalvik/2.1.0 (Linux; U; Android 7.1.2; Redmi Note 4 Build/NJH47F)");
        builder.addHeader("Cookie", "buildver=1514468990; resolution=1920x1080; osver=7.1.2; appver=4.3.2; mobilename=RedmiNote4; os=android; channel=google");
        String fakeip = "";
        if (!Objects.equals(fakeip, "")) {
            builder.addHeader("X-Real-IP", fakeip);
        }
        Request request = builder.build();
        try (Response response = mClient.newCall(request).execute()) {
            ResponseBody responseBody = response.body();
            if (responseBody != null) {
                String res = responseBody.string();
                response.close();
                responseBody.close();
                return res;
            } else {
                response.close();
                return "";
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";

    }

    public static String mergeLyrics(String basic, String trans) {
        try {
            StringBuilder sb = new StringBuilder();
            BufferedReader reader = new BufferedReader(new StringReader(basic));
            Pattern p = Pattern.compile("^(\\[.*])+(.*)");
            String line;
            String tline;
            while ((line = reader.readLine()) != null) {
                Matcher m = p.matcher(line);
                if (m.find()) {
                    BufferedReader treader = new BufferedReader(new StringReader(trans));
                    while ((tline = treader.readLine()) != null) {
                        Matcher tm = p.matcher(tline);
                        if (tm.find()) {
                            if (Objects.equals(tm.group(1), m.group(1))) {
                                sb.append(m.group(1));
                                sb.append(m.group(2));
                                sb.append(" ");
                                sb.append(tm.group(2));
                                sb.append(Constants.LINE_SEPARATOR);
                            }
                        }
                    }
                    treader.close();
                }
            }
            reader.close();
            return sb.toString();

        } catch (IOException ex) {
            ex.printStackTrace();
            return basic;
        }
    }
}
