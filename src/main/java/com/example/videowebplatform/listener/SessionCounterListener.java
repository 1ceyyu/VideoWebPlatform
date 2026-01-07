package com.example.videowebplatform.listener;

import com.example.videowebplatform.dao.VideoDAO;
import com.example.videowebplatform.dao.VideoDAOImpl;
import jakarta.servlet.annotation.WebListener;
import jakarta.servlet.http.HttpSessionEvent;
import jakarta.servlet.http.HttpSessionListener;
@WebListener
public class SessionCounterListener implements HttpSessionListener {
    private static int activeSessions = 0;
    private final VideoDAO videoDAO = new VideoDAOImpl();

    @Override
    public void sessionCreated(HttpSessionEvent se) {
        activeSessions++;
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        activeSessions--;
        // 当没有任何活跃会话时（即所有人都关闭了浏览器或超时）
        if (activeSessions <= 0) {
            videoDAO.resetAllClicks();
            System.out.println("所有用户已离开，点击量已重置。");
        }
    }
}
