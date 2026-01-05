package com.example.videowebplatform.controller;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@WebServlet("/cover")
public class ImageStreamServlet extends HttpServlet {

    private static final String COVER_BASE_PATH = "/var/www/videodata/covers/";

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String imageName = request.getParameter("name");
        if (imageName == null || imageName.isEmpty()) return;

        File imageFile = new File(COVER_BASE_PATH + imageName);
        if (!imageFile.exists()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // 自动识别图片类型 (jpg, png, gif等)
        String contentType = getServletContext().getMimeType(imageFile.getName());
        response.setContentType(contentType != null ? contentType : "image/jpeg");
        response.setContentLength((int) imageFile.length());

        // 高效输出图片流
        Files.copy(imageFile.toPath(), response.getOutputStream());
    }
}