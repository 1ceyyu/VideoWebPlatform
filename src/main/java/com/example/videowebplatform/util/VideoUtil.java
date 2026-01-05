package com.example.videowebplatform.util;

import ws.schild.jave.MultimediaObject;
import ws.schild.jave.info.MultimediaInfo;
import java.io.File;

public class VideoUtil {
    /**
     * 获取视频时长（秒）
     */
    public static long getVideoDuration(File file) {
        try {
            MultimediaObject instance = new MultimediaObject(file);
            MultimediaInfo info = instance.getInfo();
            return info.getDuration() / 1000; // 毫秒转秒
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }
}