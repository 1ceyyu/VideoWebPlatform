<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>视频播放平台 - 首页</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css">
    <style>
        /* 分类导航栏样式 */
        .header-container {
            display: flex;
            justify-content: space-between;
            align-items: center;
            background: #f8f9fa;
            padding: 15px 20px;
            margin-bottom: 20px;
            border-bottom: 2px solid #eee;
            border-radius: 8px;
        }

        .category-nav {
            display: flex;
            gap: 12px;
            align-items: center;
        }

        .nav-item {
            text-decoration: none;
            padding: 8px 16px;
            border-radius: 20px;
            color: #555;
            background: #fff;
            border: 1px solid #ddd;
            font-size: 14px;
            transition: all 0.3s;
        }

        .nav-item:hover {
            background: #e9ecef;
        }

        .nav-item.active {
            background: #007bff;
            color: white;
            border-color: #007bff;
            font-weight: bold;
        }

        .btn-publish {
            background: #28a745;
            color: white;
            padding: 10px 20px;
            border-radius: 5px;
            text-decoration: none;
            font-weight: bold;
            transition: background 0.3s;
        }

        .btn-publish:hover {
            background: #218838;
        }

        /* 视频卡片分类标签样式 */
        .category-tag {
            font-size: 12px;
            color: #007bff;
            background: #e7f3ff;
            padding: 2px 6px;
            border-radius: 4px;
            margin-bottom: 5px;
            display: inline-block;
        }
    </style>
</head>
<body>

<div class="header-container">
    <div class="category-nav">
        <span style="font-weight: bold; color: #333; margin-right: 10px;">分类浏览:</span>

        <a href="${pageContext.request.contextPath}/home?catId=0"
           class="nav-item ${activeCatId == 0 || activeCatId == null ? 'active' : ''}">
            全部视频
        </a>

        <c:forEach var="cat" items="${categories}">
            <a href="${pageContext.request.contextPath}/home?catId=${cat.id}"
               class="nav-item ${activeCatId == cat.id ? 'active' : ''}">
                    ${cat.name}
            </a>
        </c:forEach>
    </div>

    <a href="${pageContext.request.contextPath}/addVideoPage" class="btn-publish">
        + 发布新视频
    </a>
</div>

<h2>🎥 视频列表</h2>

<div class="video-grid">
    <c:forEach var="video" items="${videoList}">
        <div class="video-card">
            <img src="${pageContext.request.contextPath}/cover?name=${video.coverImage}"
                 alt="Cover"
                 onerror="this.src='${pageContext.request.contextPath}/resources/covers/default.jpg';"
                 style="width: 100%; height: 180px; object-fit: cover; display: block; border-radius: 4px 4px 0 0;">

            <div style="padding: 10px;">
                <span class="category-tag">${video.categoryName}</span>

                <h3 style="margin: 5px 0;">
                    <a href="${pageContext.request.contextPath}/play?id=${video.id}" style="text-decoration: none; color: #333;">
                            ${video.title}
                    </a>
                </h3>
            </div>
        </div>
    </c:forEach>
</div>

<script src="${pageContext.request.contextPath}/resources/js/script.js"></script>
</body>
</html>