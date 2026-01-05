package com.example.videowebplatform.util;

import com.example.videowebplatform.dao.AdVideoDAO;
import com.example.videowebplatform.dao.AdVideoDAOImpl;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class AdSyncService {
    private final AdVideoDAO adVideoDAO = new AdVideoDAOImpl();

    public void fetchAndSaveAd() {
        String apiUrl = "http://175.24.232.219:8080/api/ads";
        try {
            URL url = new URL(apiUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setConnectTimeout(5000);

            if (conn.getResponseCode() == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                // 1. 将响应解析为 JsonArray (因为你的 API 返回的是 [{},{}])
                JsonElement element = JsonParser.parseString(response.toString());
                if (element.isJsonArray()) {
                    JsonArray adsArray = element.getAsJsonArray();

                    int videoCount = 0;
                    // 2. 遍历数组中的每一个对象
                    for (JsonElement item : adsArray) {
                        JsonObject adObj = item.getAsJsonObject();

                        // 3. 核心过滤：只捕捉 type 为 "video" 的数据
                        String type = adObj.get("type").getAsString();
                        if ("video".equalsIgnoreCase(type)) {
                            String title = adObj.get("title").getAsString();
                            String mediaUrl = adObj.get("media_url").getAsString();
                            if (mediaUrl.contains("175.24.232.219") && !mediaUrl.contains(":8080")) {
                                // 将 "175.24.232.219/uploads" 替换为 "175.24.232.219:8080/uploads"
                                mediaUrl = mediaUrl.replace("175.24.232.219", "175.24.232.219:8080");
                                System.out.println("检测到 URL 缺失端口，已自动修正: " + mediaUrl);
                            }

                            // 4. 调用 DAO 存入数据库（查重插入逻辑）
                            adVideoDAO.saveOrUpdateExternalAd(title, mediaUrl);
                            videoCount++;
                        }
                    }
                    System.out.println("API 同步完成，共捕捉到 " + videoCount + " 条视频广告。");
                }
            }
        } catch (Exception e) {
            System.err.println("解析 API 失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}