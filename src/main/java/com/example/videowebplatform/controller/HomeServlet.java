// File: com/example/videowebplatform/controller/HomeServlet.java
package com.example.videowebplatform.controller;

import com.example.videowebplatform.dao.VideoDAO;
import com.example.videowebplatform.dao.VideoDAOImpl;
import com.example.videowebplatform.model.Video;
import com.example.videowebplatform.model.Category;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@WebServlet("/home")
public class HomeServlet extends HttpServlet {
    private final VideoDAO videoDAO = new VideoDAOImpl();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. 获取分类ID参数
        String catIdStr = request.getParameter("catId");
        int catId = (catIdStr != null && !catIdStr.isEmpty()) ? Integer.parseInt(catIdStr) : 0;

        // 2. 获取数据
        List<Category> categories = videoDAO.getAllCategories();
        List<Video> videoList = videoDAO.getVideosByCategoryId(catId);

        // 3. 存入请求域 [cite: 499]
        request.setAttribute("categories", categories);
        request.setAttribute("videoList", videoList);
        request.setAttribute("activeCatId", catId); // 用于前端高亮

        request.getRequestDispatcher("/WEB-INF/views/home.jsp").forward(request, response);
    }
}