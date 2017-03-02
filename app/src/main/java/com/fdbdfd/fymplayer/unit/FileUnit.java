package com.fdbdfd.fymplayer.unit;


import java.io.File;
import java.util.Arrays;
import java.util.HashSet;


public class FileUnit {

    private static final HashSet<String> mHashVideo;

    private static final String[] VIDEO_EXTENSIONS = { "3gp", "avi", "flv", "mkv", "mp4", "wmv"};//文件后缀名

    static {
        mHashVideo = new HashSet<String>(Arrays.asList(VIDEO_EXTENSIONS));
    }

    public static boolean isVideo(File f) {
        final String ext = getFileExtension(f);
        return mHashVideo.contains(ext);
    }

    /** 获取文件后缀 */
    private static String getFileExtension(File f) {
        if (f != null) {
            String filename = f.getName();
            int i = filename.lastIndexOf('.');
            if (i > 0 && i < filename.length() - 1) {
                return filename.substring(i + 1).toLowerCase(); //获取到的后缀名转成小写
            }
        }
        return null;
    }
}
