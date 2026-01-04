package com.example.videowebplatform.dao;

import com.example.videowebplatform.model.AdVideo;
import java.util.List;

public interface AdVideoDAO {
    /**
     * 获取所有可用的广告视频。
     * @return 广告视频列表。
     */
    List<AdVideo> getAllAds();
    void saveOrUpdateExternalAd(String title, String mediaUrl);
}