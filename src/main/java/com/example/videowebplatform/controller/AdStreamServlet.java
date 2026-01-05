package com.example.videowebplatform.controller;

import com.example.videowebplatform.dao.AdVideoDAO;
import com.example.videowebplatform.dao.AdVideoDAOImpl;
import com.example.videowebplatform.model.AdVideo;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.*;
import java.util.List;

@WebServlet("/adstream")
public class AdStreamServlet extends HttpServlet {

    private static final String BASE_VIDEO_PATH = "/var/www/videodata/ads/";
    private final AdVideoDAO adVideoDAO = new AdVideoDAOImpl();
    private static final int BUFFER_SIZE = 16384;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String adIdParam = request.getParameter("id");
        if (adIdParam == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        // 简单粗暴地遍历获取广告文件名 (由于没有 getAdById 方法，这里暂时遍历)
        int adId = Integer.parseInt(adIdParam);
        List<AdVideo> ads = adVideoDAO.getAllAds();
        AdVideo targetAd = null;
        for (AdVideo ad : ads) {
            if (ad.getId() == adId) {
                targetAd = ad;
                break;
            }
        }

        if (targetAd == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        File videoFile = new File(BASE_VIDEO_PATH + targetAd.getFileName());
        if (!videoFile.exists()) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND);
            return;
        }

        // --- 标准流处理逻辑 (与 StreamServlet 相同) ---
        long fileLength = videoFile.length();
        long start = 0;
        long end = fileLength - 1;

        String rangeHeader = request.getHeader("Range");
        if (rangeHeader != null) {
            response.setStatus(HttpServletResponse.SC_PARTIAL_CONTENT);
            try {
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

        if (start > end || start >= fileLength) {
            response.setHeader("Content-Range", "bytes */" + fileLength);
            response.sendError(HttpServletResponse.SC_REQUESTED_RANGE_NOT_SATISFIABLE);
            return;
        }

        long contentLength = end - start + 1;
        response.setContentType("video/mp4");
        response.setHeader("Accept-Ranges", "bytes");
        response.setHeader("Content-Length", String.valueOf(contentLength));
        response.setHeader("Content-Range", String.format("bytes %d-%d/%d", start, end, fileLength));
        response.setHeader("Content-Disposition", "inline; filename=\"" + targetAd.getFileName() + "\"");

        try (RandomAccessFile raf = new RandomAccessFile(videoFile, "r");
             OutputStream out = response.getOutputStream()) {
            raf.seek(start);
            byte[] buffer = new byte[BUFFER_SIZE];
            long bytesNeeded = contentLength;
            while (bytesNeeded > 0) {
                int bytesToRead = (int) Math.min(BUFFER_SIZE, bytesNeeded);
                int bytesRead = raf.read(buffer, 0, bytesToRead);
                if (bytesRead == -1) break;
                out.write(buffer, 0, bytesRead);
                bytesNeeded -= bytesRead;
            }
        } catch (IOException e) {
            // Ignore client abort
        }
    }
}