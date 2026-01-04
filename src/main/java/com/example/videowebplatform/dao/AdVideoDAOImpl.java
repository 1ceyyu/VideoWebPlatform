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
        // 1. 检查是否存在该标题的广告
        String checkSql = "SELECT id FROM ad_video WHERE title = ?";
        // 2. 更新语句
        String updateSql = "UPDATE ad_video SET file_name = ? WHERE title = ?";
        // 3. 插入语句 (包含默认时长)
        String insertSql = "INSERT INTO ad_video (title, file_name, duration_seconds, file_length_bytes) VALUES (?, ?, 15, 0)";

        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            // 建议显式设置，确保立即生效
            conn.setAutoCommit(true);

            try (PreparedStatement psCheck = conn.prepareStatement(checkSql)) {
                psCheck.setString(1, title);
                ResultSet rs = psCheck.executeQuery();

                if (rs.next()) {
                    // 已存在，执行更新
                    try (PreparedStatement psUpdate = conn.prepareStatement(updateSql)) {
                        psUpdate.setString(1, mediaUrl);
                        psUpdate.setString(2, title);
                        int rows = psUpdate.executeUpdate();
                        System.out.println("数据库更新行数: " + rows + " [标题: " + title + "]");
                    }
                } else {
                    // 不存在，执行插入
                    try (PreparedStatement psInsert = conn.prepareStatement(insertSql)) {
                        psInsert.setString(1, title);
                        psInsert.setString(2, mediaUrl);
                        int rows = psInsert.executeUpdate();
                        System.out.println("数据库插入行数: " + rows + " [标题: " + title + "]");
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("数据库操作失败！错误代码: " + e.getErrorCode());
            e.printStackTrace();
        } finally {
            DBUtil.close(conn); // 确保连接关闭 [cite: 111, 133]
        }
    }
}