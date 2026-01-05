package com.example.videowebplatform.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBUtil {
    // 数据库连接配置
    private static final String URL = "jdbc:mysql://10.100.164.26:3306/videowebsite?serverTimezone=UTC";
    private static final String USER = "remote_admin";
    private static final String PASSWORD = "AdminPassword123!";

    // 静态代码块，用于加载 JDBC 驱动
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            throw new RuntimeException("无法加载 MySQL JDBC 驱动", e);
        }
    }
    // 获取数据库连接
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
    // 关闭资源（重载方法方便调用）
    public static void close(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}