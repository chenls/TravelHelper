package com.cqupt.travelhelper.utils;

import android.content.Context;
import android.os.Environment;
import android.widget.Toast;

import java.io.File;

public class SDCardHelper {

    // 判断SD卡是否被挂载
    public static boolean isSDCardMounted() {
        // return Environment.getExternalStorageState().equals("mounted");
        return Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED);
    }

    // 获取SD卡私有Files目录的路径
    public static String getSDCardPrivateFilesDir(Context context, String type) {
        if (isSDCardMounted())
            return context.getExternalFilesDir(type).getAbsolutePath();
        else {
            Toast.makeText(context, "SD卡未挂载", Toast.LENGTH_SHORT).show();
            return null;
        }
    }

    /**
     * 删除单个文件
     *
     * @param sPath 被删除文件的文件名
     */
    public static void deleteFile(String sPath) {
        File file = new File(sPath);
        // 路径为文件且不为空则进行删除
        if (file.isFile() && file.exists()) {
            file.delete();
        }
    }

}
