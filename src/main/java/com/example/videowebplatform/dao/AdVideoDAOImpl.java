// File: com/example/videowebplatform/dao/AdVideoDAOImpl.java
package com.example.videowebplatform.dao;

import com.example.videowebplatform.model.AdVideo;
import com.example.videowebplatform.util.DBUtil;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class AdVideoDAOImpl implements AdVideoDAO {

    // 假设广告表名为 ad_video
    private static final String SELECT_ALL_ADS =
            "SELECT id, title, file_name, duration_seconds, file_length_bytes FROM ad_video";

    @Override
    public List<AdVideo> getAllAds() {
        List<AdVideo> ads = new ArrayList<>();
        Connection conn = null;
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            conn = DBUtil.getConnection();// 获取数据库连接
            ps = conn.prepareStatement(SELECT_ALL_ADS);
            rs = ps.executeQuery();

            while (rs.next()) {
                AdVideo ad = new AdVideo(
                        rs.getInt("id"),
                        rs.getString("title"),
                        rs.getString("file_name"),
                        rs.getLong("duration_seconds"),
                        rs.getLong("file_length_bytes")
                );
                ads.add(ad);
            }
        } catch (SQLException e) {
            e.printStackTrace(); // 打印错误
        } finally {
            // 确保资源被关闭
            try { if (rs != null) rs.close(); } catch (SQLException e) { e.printStackTrace(); }
            try { if (ps != null) ps.close(); } catch (SQLException e) { e.printStackTrace(); }
            DBUtil.close(conn);
        }
        return ads;
    }
    public void saveOrUpdateExternalAd(String title, String mediaUrl) {
        // 1. 检查 SQL 是否正确。注意：确保数据库中有 duration_seconds 字段
        String checkSql = "SELECT id FROM ad_video WHERE file_name = ?";
        String updateSql = "UPDATE ad_video SET title = ? WHERE file_name = ?";
        String insertSql = "INSERT INTO ad_video (title, file_name, duration_seconds,file_length_bytes) VALUES (?, ?, 15,3000)";

        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            // 【关键修复 1】：显式开启自动提交，防止事务挂起
            if (conn != null) {
                conn.setAutoCommit(true);
            } else {
                System.err.println("数据库连接失败，请检查 DBUtil 配置！");
                return;
            }

            try (PreparedStatement psCheck = conn.prepareStatement(checkSql)) {
                psCheck.setString(1, mediaUrl);
                ResultSet rs = psCheck.executeQuery();

                if (rs.next()) {
                    // 已存在，更新标题
                    try (PreparedStatement psUpdate = conn.prepareStatement(updateSql)) {
                        psUpdate.setString(1, title);
                        psUpdate.setString(2, mediaUrl);
                        int rows = psUpdate.executeUpdate();
                        System.out.println("SQL成功：更新广告 [" + title + "]，受影响行数: " + rows);
                    }
                } else {
                    // 不存在，插入新数据
                    try (PreparedStatement psInsert = conn.prepareStatement(insertSql)) {
                        psInsert.setString(1, title);
                        psInsert.setString(2, mediaUrl);
                        int rows = psInsert.executeUpdate();
                        System.out.println("SQL成功：插入新广告 [" + title + "]，受影响行数: " + rows);
                    }
                }
            }
        } catch (SQLException e) {
            // 【关键修复 2】：不要只打印 e.getMessage()，要看全堆栈，检查是否是字段名写错或长度超限
            System.err.println("数据库操作崩溃！错误代码: " + e.getErrorCode());
            e.printStackTrace();
        } finally {
            // 【关键修复 3】：确保连接关闭，防止连接池耗尽
            try { if (conn != null) conn.close(); } catch (SQLException e) { e.printStackTrace(); }
        }
    }
}