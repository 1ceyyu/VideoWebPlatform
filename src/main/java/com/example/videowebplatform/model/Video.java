package com.example.videowebplatform.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Video {
    private int id;
    private String title;
    private String fileName; // 存储在服务器上的文件名
    private String coverImage;
    private long durationSeconds; // 视频时长，单位为秒
    private long fileLengthBytes; // 视频文件大小，单位为字节
    private int categoryId;     // 分类ID
    private String categoryName; // 分类名称
    public Video(int id, String title, String fileName, String coverImage, long durationSeconds, long fileLengthBytes) {
        this.id = id;
        this.title = title;
        this.fileName = fileName;
        this.coverImage = coverImage;
        this.durationSeconds = durationSeconds;
        this.fileLengthBytes = fileLengthBytes;
    }
}
