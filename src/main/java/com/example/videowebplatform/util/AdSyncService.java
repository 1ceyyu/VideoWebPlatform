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
            conn.setConnectTimeout(5000); // 建议增加超时设置

            if (conn.getResponseCode() == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                // 1. 先解析为 JsonElement
                JsonElement jsonElement = JsonParser.parseString(response.toString());
                JsonObject targetObject = null;

                // 2. 判断是数组还是对象
                if (jsonElement.isJsonArray()) {
                    JsonArray jsonArray = jsonElement.getAsJsonArray();
                    if (jsonArray.size() > 0) {
                        // 【核心修改】：获取第一条数据（下标为0）
                        targetObject = jsonArray.get(0).getAsJsonObject();
                    }
                } else if (jsonElement.isJsonObject()) {
                    targetObject = jsonElement.getAsJsonObject();
                }

                if (targetObject != null) {
                    String title = targetObject.get("title").getAsString();
                    String mediaUrl = targetObject.get("media_url").getAsString();

                    // 存入数据库
                    adVideoDAO.saveOrUpdateExternalAd(title, mediaUrl);
                    System.out.println("API同步成功：捕捉到第一条广告 [" + title + "]");
                }
            }
        } catch (Exception e) {
            System.err.println("同步 API 失败: " + e.getMessage());
            e.printStackTrace();
        }
    }
}