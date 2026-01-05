package com.example.videowebplatform.controller;

import jakarta.servlet.ServletException;
import java.util.List;
import com.example.videowebplatform.dao.VideoDAO;;
import com.example.videowebplatform.dao.VideoDAOImpl;
import com.example.videowebplatform.model.Category;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 该 Servlet 负责跳转到添加视频的表单页面
 */
@WebServlet("/addVideoPage")
public class AddVideoPageServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    /**
     * 处理 GET 请求，展示上传表单
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        VideoDAO videoDAO = new VideoDAOImpl();
        List<Category> categories = videoDAO.getAllCategories();

        // 将分类列表存入 request 作用域
        request.setAttribute("categories", categories);
        // 设置响应编码（可选，但推荐）
        response.setContentType("text/html;charset=UTF-8");

        try {
            // 转发到 WEB-INF 下的 JSP，确保安全性（外部无法直接通过浏览器访问该 JSP）
            request.getRequestDispatcher("/WEB-INF/views/addVideoPage.jsp").forward(request, response);

            System.out.println("用户进入了添加视频页面");
        } catch (ServletException | IOException e) {
            System.err.println("跳转添加视频页面失败: " + e.getMessage());
            // 如果跳转失败，发送 500 错误
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "页面跳转异常");
        }
    }

    /**
     * 明确禁用 doPost，防止误调用
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // 如果用户错误地通过 POST 访问，直接返回 405 不允许的方法
        response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "该路径仅支持 GET 请求");
    }
}