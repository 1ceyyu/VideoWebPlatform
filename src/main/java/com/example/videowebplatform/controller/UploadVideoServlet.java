package com.example.videowebplatform.controller;

import com.example.videowebplatform.dao.VideoDAO;
import com.example.videowebplatform.dao.VideoDAOImpl;
import com.example.videowebplatform.model.Video;
import com.example.videowebplatform.util.VideoUtil;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

@WebServlet("/uploadVideo")
@MultipartConfig(
        fileSizeThreshold = 1024 * 1024 * 2, // 2MB
        maxFileSize = 1024 * 1024 * 100,      // 100MB
        maxRequestSize = 1024 * 1024 * 110    // 110MB
)
public class UploadVideoServlet extends HttpServlet {

    private final VideoDAO videoDAO = new VideoDAOImpl();
    private static final String VIDEO_PATH = "/var/www/videodata/movies/"; // [cite: 58]
    private static final String COVER_PATH = "/var/www/videodata/covers/"; //

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        String title = request.getParameter("title");
        String catIdStr = request.getParameter("categoryId");
        int categoryId = Integer.parseInt(catIdStr);
        // 获取视频和封面 Part
        Part videoPart = request.getPart("videoFile");
        Part coverPart = request.getPart("coverFile");

        String videoFileName = "";
        String coverFileName = "default.jpg"; // 默认封面

        // 1. 处理视频上传
        if (videoPart != null && videoPart.getSize() > 0) {
            videoFileName = videoPart.getSubmittedFileName();
            videoPart.write(VIDEO_PATH + videoFileName);
        }

        // 2. 处理封面图片上传 (jpg/png)
        if (coverPart != null && coverPart.getSize() > 0) {
            String originalCoverName = coverPart.getSubmittedFileName();
            // 建议使用 UUID 重命名封面，防止文件名冲突
            coverFileName = UUID.randomUUID().toString() + "_" + originalCoverName;
            coverPart.write(COVER_PATH + coverFileName);
        }

        // 3. 自动获取视频时长
        File savedVideo = new File(VIDEO_PATH + videoFileName);
        long duration = VideoUtil.getVideoDuration(savedVideo);

        // 4. 构建并保存到数据库
        Video video = new Video();
        video.setTitle(title);
        video.setCategoryId(categoryId);
        video.setFileName(videoFileName);
        video.setCoverImage(coverFileName); // 存储实际文件名或默认值 [cite: 144]
        video.setDurationSeconds(duration);
        video.setFileLengthBytes(0); // 默认为0 [cite: 145]

        videoDAO.saveVideo(video);

        response.sendRedirect(request.getContextPath() + "/home");
    }
}