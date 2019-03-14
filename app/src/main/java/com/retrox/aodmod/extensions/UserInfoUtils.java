package com.retrox.aodmod.extensions;

import android.os.Environment;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class UserInfoUtils {
    // 保存用户名和密码的业务方法
    public static boolean saveInfo(String username, String pwd) {

        try {
            String result = username + "##" + pwd;

            //创建file类指定我们要存储数据的位置 把数据保存到sd卡

            String sdPath = Environment.getExternalStorageDirectory().getPath() + "/data/aod";

            File file = new File(sdPath, "haha.txt");

            //2 创建一个文件输出流
            FileOutputStream fos = new FileOutputStream(file);

            fos.write(result.getBytes());
            fos.close();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    // 读取用户的信息
    public static Map<String ,String> readInfo() {
        try {
            //1 定义Map
            Map<String, String> maps = new HashMap<String, String>();

            String sdPath = Environment.getExternalStorageDirectory().getPath();

            File file = new File(sdPath, "haha.txt");

            FileInputStream fis = new FileInputStream(file);

            BufferedReader bufr = new BufferedReader(new InputStreamReader(fis));
            String content = bufr.readLine(); // 读取数据

            // 切割字符串 封装到map集合中
            String[] splits = content.split("##");
            String name = splits[0];
            String pwd = splits[1];
            //　把name 和 pwd 放入map中
            maps.put("name", name);
            maps.put("pwd", pwd);
            fis.close();
            return maps;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}