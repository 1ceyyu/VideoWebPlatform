package com.example.videowebplatform.controller;
import com.example.videowebplatform.dao.VideoDAO;
import com.example.videowebplatform.dao.VideoDAOImpl;
import com.example.videowebplatform.model.CategoryStat;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import com.google.gson.Gson;
@WebServlet("/api/video-stats")
public class VideoStatApiServlet extends HttpServlet {
    private final VideoDAO videoDAO = new VideoDAOImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // 设置跨域（CORS），允许其他网站 fetch 你的 API
        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setContentType("application/json;charset=UTF-8");

        List<CategoryStat> stats = videoDAO.getCategoryStatistics();

        // 使用 Gson 将对象列表转为 JSON 字符串
        String json = new Gson().toJson(stats);
        response.getWriter().write(json);
    }
}
