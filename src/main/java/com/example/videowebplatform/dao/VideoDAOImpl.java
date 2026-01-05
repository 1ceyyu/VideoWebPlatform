package com.example.videowebplatform.dao;

import com.example.videowebplatform.model.Category;
import com.example.videowebplatform.model.Video;
import com.example.videowebplatform.util.DBUtil;
import java.util.List;
import java.util.ArrayList;
import java.sql.*;

public class VideoDAOImpl implements VideoDAO {

    // --- 1. 获取全部分类 (用于生成首页导航栏) ---
    @Override
    public List<Category> getAllCategories() {
        List<Category> list = new ArrayList<>();
        String sql = "SELECT * FROM category ORDER BY id ASC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                Category c = new Category();
                c.setId(rs.getInt("id"));
                c.setName(rs.getString("name"));
                list.add(c);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // --- 2. 按分类ID获取视频列表 (核心筛选逻辑) ---
    @Override
    public List<Video> getVideosByCategoryId(int catId) {
        List<Video> videos = new ArrayList<>();
        // 使用 LEFT JOIN 关联查询，确保能拿到分类名称显示在前端
        String sql = "SELECT v.*, c.name as catName FROM video v " +
                "LEFT JOIN category c ON v.category_id = c.id";

        // 如果 catId 为 0，则查询全部；否则拼接 WHERE 子句
        if (catId > 0) {
            sql += " WHERE v.category_id = ?";
        }

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            if (catId > 0) {
                ps.setInt(1, catId);
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    // 使用你的 Video 构造函数封装对象
                    Video video = new Video(
                            rs.getInt("id"),
                            rs.getString("title"),
                            rs.getString("file_name"),
                            rs.getString("cover_image"),
                            rs.getLong("duration_seconds"),
                            rs.getLong("file_length_bytes")
                    );
                    // 设置关联的分类名称
                    video.setCategoryName(rs.getString("catName"));
                    // 设置分类ID（可选，方便后续操作）
                    video.setCategoryId(rs.getInt("category_id"));

                    videos.add(video);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return videos;
    }

    // --- 3. 获取全部视频 (重用筛选逻辑) ---
    @Override
    public List<Video> getAllVideos() {
        return getVideosByCategoryId(0); // 传入0表示不筛选分类
    }

    // --- 4. 根据ID获取单个视频 ---
    @Override
    public Video getVideoById(int id) {
        String sql = "SELECT v.*, c.name as catName FROM video v " +
                "LEFT JOIN category c ON v.category_id = c.id WHERE v.id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Video video = new Video(
                            rs.getInt("id"), rs.getString("title"),
                            rs.getString("file_name"),
                            rs.getString("cover_image"),
                            rs.getLong("duration_seconds"),
                            rs.getLong("file_length_bytes")
                    );
                    video.setCategoryName(rs.getString("catName"));
                    return video;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // --- 5. 保存视频 (包含分类ID) ---
    @Override
    public void saveVideo(Video video) {
        String sql = "INSERT INTO video (title, file_name, cover_image, duration_seconds, file_length_bytes, category_id) VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, video.getTitle());
            ps.setString(2, video.getFileName());
            ps.setString(3, video.getCoverImage());
            ps.setLong(4, video.getDurationSeconds());
            ps.setLong(5, 0); // 默认设置为0
            ps.setInt(6, video.getCategoryId()); // 保存关联的分类ID
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}