<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>添加新视频 - 视频管理系统</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css">
    <style>
        /* ... 你原有的样式 ... */
        .upload-card { max-width: 600px; margin: 50px auto; padding: 30px; background: #fff; border-radius: 12px; box-shadow: 0 4px 20px rgba(0,0,0,0.08); }
        .form-group { margin-bottom: 20px; }
        .form-group label { display: block; margin-bottom: 8px; font-weight: bold; color: #444; }
        .form-group input[type="text"], .form-group select {
            width: 100%; padding: 10px; border: 1px solid #ddd; border-radius: 6px; box-sizing: border-box;
        }
        .file-input-wrapper { padding: 15px; border: 2px dashed #eee; border-radius: 8px; text-align: center; background: #fafafa; }
        .btn-submit { width: 100%; padding: 12px; background: #007bff; color: white; border: none; border-radius: 6px; font-size: 16px; cursor: pointer; transition: background 0.3s; }
        .btn-submit:hover { background: #0056b3; }
        .hint { font-size: 12px; color: #888; margin-top: 5px; }
    </style>
</head>
<body>

<div class="upload-card">
    <h2 style="text-align: center; margin-bottom: 30px;">🎬 上传并发布新视频</h2>

    <form action="${pageContext.request.contextPath}/uploadVideo" method="post" enctype="multipart/form-data">
        <div class="form-group">
            <label>视频标题</label>
            <input type="text" name="title" placeholder="请输入吸引人的标题..." required>
        </div>

        <div class="form-group">
            <label>选择分类</label>
            <select name="categoryId" required>
                <option value="">-- 请选择视频分类 --</option>
                <c:forEach var="cat" items="${categories}">
                    <option value="${cat.id}">${cat.name}</option>
                </c:forEach>
            </select>
        </div>

        <div class="form-group">
            <label>视频文件 (MP4)</label>
            <div class="file-input-wrapper">
                <input type="file" name="videoFile" accept="video/mp4" required>
                <div class="hint">最大支持 100MB 的 MP4 视频</div>
            </div>
        </div>

        <div class="form-group">
            <label>自定义封面 (JPG/PNG)</label>
            <div class="file-input-wrapper">
                <input type="file" name="coverFile" accept="image/jpeg,image/png">
                <div class="hint">如果不上传，将自动使用默认封面</div>
            </div>
        </div>

        <button type="submit" class="btn-submit">开始上传视频</button>

        <div style="text-align: center; margin-top: 15px;">
            <a href="${pageContext.request.contextPath}/home" style="color: #666; text-decoration: none; font-size: 14px;">← 返回视频列表</a>
        </div>
    </form>
</div>

</body>
</html>