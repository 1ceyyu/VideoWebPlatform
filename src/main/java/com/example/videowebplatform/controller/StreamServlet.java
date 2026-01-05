package com.example.videowebplatform.controller;

import com.example.videowebplatform.dao.VideoDAO;
import com.example.videowebplatform.dao.VideoDAOImpl;
import com.example.videowebplatform.model.Video;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.*;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@WebServlet("/stream")
public class StreamServlet extends HttpServlet {

    // 视频存储的基础路径
    private static final String BASE_VIDEO_PATH = "/var/www/videodata/movies/";
    private final VideoDAO videoDAO = new VideoDAOImpl();
    private static final int BUFFER_SIZE = 16384; // 16KB 缓冲区

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 1. 获取视频ID
        String videoIdParam = request.getParameter("id");
        if (videoIdParam == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // 2. 查询视频信息
        Video video;
        try {
            int id = Integer.parseInt(videoIdParam);
            video = videoDAO.getVideoById(id);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        if (video == null || video.getFileName() == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // 3. 找到物理文件
        File videoFile = new File(BASE_VIDEO_PATH + video.getFileName());
        if (!videoFile.exists()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "磁盘上找不到视频文件");
            return;
        }

        // 4. 处理 Range 头 (支持拖动进度条和分段加载的关键)
        long fileLength = videoFile.length();
        long start = 0;
        long end = fileLength - 1;

        String rangeHeader = request.getHeader("Range");
        if (rangeHeader != null) {
            response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
            try {
                // 解析 "bytes=0-1024" 格式
                String rangeValue = rangeHeader.substring("bytes=".length());
                String[] ranges = rangeValue.split("-");
                start = Long.parseLong(ranges[0]);
                if (ranges.length > 1 && !ranges[1].isEmpty()) {
                    end = Long.parseLong(ranges[1]);
                }
            } catch (NumberFormatException e) {
                start = 0;
                end = fileLength - 1;
            }
        } else {
            response.setStatus(HttpServletResponse.SC_OK);
        }

        // 校验范围
        if (start > end || start >= fileLength) {
            response.setHeader("Content-Range", "bytes */" + fileLength);
            response.sendError(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
            return;
        }

        long contentLength = end - start + 1;

        // 5. 设置响应头
        response.setContentType("video/mp4");
        response.setHeader("Accept-Ranges", "bytes");
        response.setHeader("Content-Length", String.valueOf(contentLength));
        response.setHeader("Content-Range", String.format("bytes %d-%d/%d", start, end, fileLength));

        // 使用 URLEncoder 将中文转为 % 编码，并使用 RFC 6266 标准的 filename*
        String fileName = video.getFileName();
        String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString())
                .replaceAll("\\+", "%20"); // 处理空格转换问题

        // filename*=UTF-8'' 这种写法是现代浏览器处理 Header 中文的最佳实践
        response.setHeader("Content-Disposition", "inline; filename*=UTF-8''" + encodedFileName);

        // 6. 传输数据
        try (RandomAccessFile raf = new RandomAccessFile(videoFile, "r");
             OutputStream out = response.getOutputStream()) {

            raf.seek(start); // 跳转到请求的起始位置
            byte[] buffer = new byte[BUFFER_SIZE];
            long bytesNeeded = contentLength;

            while (bytesNeeded > 0) {
                int bytesToRead = (int) Math.min(BUFFER_SIZE, bytesNeeded);
                int bytesRead = raf.read(buffer, 0, bytesToRead);

                if (bytesRead == -1) {
                    break;
                }

                out.write(buffer, 0, bytesRead);
                bytesNeeded -= bytesRead;
            }
        } catch (IOException e) {
            // 客户端中断连接（如用户停止播放或跳转），属于正常情况
        }
    }
}