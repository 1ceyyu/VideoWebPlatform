package com.example.videowebplatform.listener;

import com.example.videowebplatform.util.AdSyncService;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.annotation.WebListener;

@WebListener
public class AppInitListener implements ServletContextListener {
    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // 当 Tomcat 启动时，开启一个后台线程去同步
        new Thread(() -> {
            AdSyncService syncService = new AdSyncService();
            syncService.fetchAndSaveAd();
        }).start();
    }
}