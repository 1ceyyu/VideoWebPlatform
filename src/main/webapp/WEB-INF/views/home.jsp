<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>视频播放平台 - 首页</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css">
</head>
<body>
<h1>🎥视频列表</h1>
<div class="video-grid">
    <c:forEach var="video" items="${videoList}">
        <div class="video-card">
            <img src="${pageContext.request.contextPath}/cover?name=${video.coverImage}"
                 alt="Cover"
                 onerror="this.src='${pageContext.request.contextPath}/resources/covers/default.jpg';"
                 style="width: 100%; height: 180px; object-fit: cover; display: block;">
            <h3><a href="${pageContext.request.contextPath}/play?id=${video.id}">${video.title}</a></h3>
        </div>
    </c:forEach>
</div>
<script src="${pageContext.request.contextPath}/resources/js/script.js"></script>
</body>
</html>