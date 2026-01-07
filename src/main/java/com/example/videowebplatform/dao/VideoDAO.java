package com.example.videowebplatform.dao;
import com.example.videowebplatform.model.Video;
import com.example.videowebplatform.model.Category;
import com.example.videowebplatform.model.CategoryStat;
import java.util.List;
public interface VideoDAO {
    List<Video> getAllVideos();

    // 定义根据 ID 获取单个视频的抽象方法
    Video getVideoById(int id);

    // 可以继续添加其他操作，如 save, update, delete
    void saveVideo(Video video);
    List<Category> getAllCategories();
    List<Video> getVideosByCategoryId(int catId); // catId为0时查全部
    List<CategoryStat> getCategoryStatistics();
    void incrementVideoClicks(int videoId);
    void resetAllClicks();
}
