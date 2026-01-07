// File: com/example/videowebplatform/controller/PlayServlet.java
package com.example.videowebplatform.controller;

import com.example.videowebplatform.dao.VideoDAO;
import com.example.videowebplatform.dao.VideoDAOImpl;
import com.example.videowebplatform.dao.AdVideoDAO;
import com.example.videowebplatform.dao.AdVideoDAOImpl;
import com.example.videowebplatform.model.Video;
import com.example.videowebplatform.model.AdVideo;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;
import java.util.Random;

@WebServlet("/play")
public class PlayServlet extends HttpServlet {

    private final VideoDAO videoDAO = new VideoDAOImpl();
    private final AdVideoDAO adVideoDAO = new AdVideoDAOImpl();
    private final Random random = new Random();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String videoIdParam = request.getParameter("id");

        try {
            int id = Integer.parseInt(videoIdParam);
            videoDAO.incrementVideoClicks(id);
            Video video = videoDAO.getVideoById(id);

            if (video != null) {
                // 1. 获取所有广告
                List<AdVideo> ads = adVideoDAO.getAllAds();

                // 2. 决定是否显示广告（70%概率显示）
                boolean showAd = !ads.isEmpty() && random.nextInt(100) < 70;

                if (showAd) {
                    // 随机选择一个广告
                    AdVideo ad = ads.get(random.nextInt(ads.size()));

                    // 随机选择广告类型：0=前贴片，1=中插，2=后贴片
                    int adType = random.nextInt(3);

                    // 计算中插时间点（如果是中插广告）
                    int midAdTime = -1;
                    if (adType == 1) {
                        // 在视频的30%-70%之间随机选择插入点
                        int videoDuration = (int) video.getDurationSeconds();
                        int start = (int) (videoDuration * 0.3);
                        int end = (int) (videoDuration * 0.7);
                        midAdTime = start + random.nextInt(end - start);
                    }

                    // 将广告信息设置到请求属性
                    request.setAttribute("adVideo", ad);
                    request.setAttribute("adType", adType);
                    request.setAttribute("midAdTime", midAdTime);
                    request.setAttribute("showAd", true);

                    System.out.println("为视频【" + video.getTitle() + "】添加广告【" + ad.getTitle() + "】，类型：" +
                            (adType == 0 ? "前贴片" : adType == 1 ? "中插" : "后贴片"));
                } else {
                    request.setAttribute("showAd", false);
                    System.out.println("视频【" + video.getTitle() + "】不显示广告");
                }

                // 3. 设置视频信息
                request.setAttribute("video", video);

                // 4. 转发到新的播放页面
                request.getRequestDispatcher("/WEB-INF/views/videoPlayerNew.jsp").forward(request, response);

            } else {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "视频未找到");
            }
        } catch (NumberFormatException | NullPointerException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "无效的视频ID");
        }

    }

}