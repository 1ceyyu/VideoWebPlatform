<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>${video.title} - 播放</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/resources/css/style.css">
    <style>
        /* 保持原有样式不变，此处省略 CSS 代码以节省篇幅，请保留你原有的 CSS */
        #adOverlay { position: fixed; top: 0; left: 0; width: 100%; height: 100%; background: rgba(0, 0, 0, 0.95); z-index: 1000; display: none; justify-content: center; align-items: center; }
        #adPlayer { width: 80%; max-width: 900px; background: #000; border-radius: 10px; overflow: hidden; box-shadow: 0 5px 30px rgba(0,0,0,0.5); }
        #adVideo { width: 100%; height: auto; display: block; }
        .ad-controls { padding: 15px; background: #222; color: white; display: flex; justify-content: space-between; align-items: center; }
        #skipAdBtn { background: #ff4444; color: white; border: none; padding: 8px 20px; border-radius: 4px; cursor: pointer; font-weight: bold; }
        #skipAdBtn:disabled { background: #666; cursor: not-allowed; }
        .ad-info { font-size: 14px; color: #aaa; }
        .player-container { max-width: 1000px; margin: 20px auto; padding: 0 20px; }
        .video-title { margin-bottom: 20px; color: #333; }
        #mainVideo { width: 100%; height: auto; background: #000; border-radius: 8px; }
        .ad-notice { background: #fff3cd; border: 1px solid #ffeaa7; color: #856404; padding: 10px 15px; border-radius: 5px; margin: 15px 0; display: none; }
        .ad-notice.show { display: block; }
    </style>
</head>
<body>
<div class="player-container">
    <h1 class="video-title">${video.title}</h1>

    <div id="adNotice" class="ad-notice">
        <strong>📺 广告提示：</strong>
        <span id="adMessage">即将播放广告</span>
        <span id="countdown" style="margin-left: 10px;"></span>
    </div>

    <video id="mainVideo" controls preload="metadata">
        <source src="${pageContext.request.contextPath}/stream?id=${video.id}" type="video/mp4">
        您的浏览器不支持 HTML5 video 标签。
    </video>

    <c:if test="${showAd}">
        <div id="adOverlay">
            <div id="adPlayer">
                <video id="adVideo" preload="auto">
                    <c:choose>
                        <c:when test="${adVideo.fileName.startsWith('http')}">
                            <source src="${adVideo.fileName}" type="video/mp4">
                        </c:when>
                        <c:otherwise>
                            <source src="${pageContext.request.contextPath}/adstream?id=${adVideo.id}" type="video/mp4">
                        </c:otherwise>
                    </c:choose>
                </video>

                    <%-- 广告控制面板，没有这些 JS 会报错 --%>
                <div class="ad-controls">
                    <div class="ad-info">
                        广告剩余: <span id="adTime">--</span> 秒
                    </div>
                    <div>
                        <span id="skipSeconds">15</span>秒后可跳过
                        <button id="skipAdBtn" disabled>请稍候</button>
                    </div>
                </div>
            </div>
        </div>
    </c:if>

    <div style="margin-top: 30px; text-align: center;">
        <a href="${pageContext.request.contextPath}/home" class="back-btn">← 返回视频列表</a>
    </div>
</div>

<script>
    // 1. 声明全局变量
    var adType, midAdTime, adDuration, skipDelay, adPlayed, adTimer;
    var adOverlay, adVideo, skipAdBtn, skipSecondsSpan, adTimeSpan, adNotice, adMessage, countdownSpan, mainVideo;

    <c:if test="${showAd}">
    // 2. 初始化数据
    adType = ${adType};
    midAdTime = ${midAdTime};
    adDuration = ${adVideo.durationSeconds} || 15; // 默认15秒
    skipDelay = 15;
    adPlayed = false;
    adTimer = null;

    // 3. 核心初始化函数
    function initPlayer() {
        // 获取所有 DOM 元素
        adOverlay = document.getElementById('adOverlay');
        adVideo = document.getElementById('adVideo');
        skipAdBtn = document.getElementById('skipAdBtn');
        skipSecondsSpan = document.getElementById('skipSeconds');
        adTimeSpan = document.getElementById('adTime');
        adNotice = document.getElementById('adNotice');
        adMessage = document.getElementById('adMessage');
        countdownSpan = document.getElementById('countdown');
        mainVideo = document.getElementById('mainVideo');

        if (adVideo) {
            // 监听远程视频元数据加载，获取准确时长
            adVideo.addEventListener('loadedmetadata', function() {
                adDuration = Math.floor(adVideo.duration);
                if (adTimeSpan) adTimeSpan.textContent = adDuration;
            });

            // 【核心修复】：监听加载错误。如果API视频地址失效，立即关闭广告显示主视频
            adVideo.onerror = function() {
                console.error("广告视频加载失败 (可能是URL失效或跨域)，自动跳过");
                hideAdOverlay();
            };

            // 监听视频自然结束
            adVideo.onended = function() {
                hideAdOverlay();
            };
        }

        // 根据广告类型执行初始化
        switch(adType) {
            case 0: // 前贴片
                showAdNotice('视频开始前有广告，请稍候...');
                setTimeout(playPreRollAd, 1000);
                break;
            case 1: // 中插
                setupMidRollAd();
                break;
            case 2: // 后贴片
                setupPostRollAd();
                break;
        }
    }

    function playPreRollAd() {
        if(mainVideo) mainVideo.pause();
        showAdOverlay();
        startAdTimer();
    }

    function setupMidRollAd() {
        if(mainVideo) {
            mainVideo.addEventListener('timeupdate', function() {
                if (!adPlayed && this.currentTime >= midAdTime) {
                    this.pause();
                    showAdNotice('广告即将播放...');
                    setTimeout(function() {
                        showAdOverlay();
                        startAdTimer();
                    }, 1000);
                }
            });
        }
    }

    function setupPostRollAd() {
        if(mainVideo) {
            mainVideo.addEventListener('ended', function() {
                showAdNotice('精彩内容后有广告...');
                setTimeout(function() {
                    showAdOverlay();
                    startAdTimer();
                }, 1000);
            });
        }
    }

    function showAdOverlay() {
        if(!adOverlay || !adVideo) return;
        adOverlay.style.display = 'flex';
        adVideo.currentTime = 0;

        // 【关键】：尝试播放并处理浏览器自动播放拦截
        var playPromise = adVideo.play();
        if (playPromise !== undefined) {
            playPromise.catch(function(error) {
                console.warn("自动播放被拦截，尝试静音播放");
                adVideo.muted = true; // 静音后通常可以自动播放
                adVideo.play();
            });
        }
        adPlayed = true;
        if(adNotice) adNotice.classList.remove('show');
    }

    function hideAdOverlay() {
        if(adTimer) clearInterval(adTimer);
        if(adOverlay) adOverlay.style.display = 'none';
        if(adVideo) adVideo.pause();
        // 恢复主视频
        if ((adType === 0 || adType === 1) && mainVideo) {
            mainVideo.play().catch(e => console.log("主视频自动播放需点击"));
        }
    }

    function showAdNotice(message) {
        if(!adNotice) return;
        adMessage.textContent = message;
        adNotice.classList.add('show');
        var countdown = 3;
        if(countdownSpan) countdownSpan.textContent = countdown + '秒后播放';
        var nTimer = setInterval(function() {
            countdown--;
            if (countdown > 0) {
                if(countdownSpan) countdownSpan.textContent = countdown + '秒后播放';
            } else {
                clearInterval(nTimer);
                if(countdownSpan) countdownSpan.textContent = '';
            }
        }, 1000);
    }

    function startAdTimer() {
        if(adTimer) clearInterval(adTimer);
        var skipSeconds = skipDelay;
        var adRemaining = adDuration;

        adTimer = setInterval(function() {
            // 跳过按钮逻辑
            if (skipSeconds > 0) {
                skipSeconds--;
                if(skipSecondsSpan) skipSecondsSpan.textContent = skipSeconds;
            } else {
                if(skipAdBtn && skipAdBtn.disabled) {
                    skipAdBtn.disabled = false;
                    skipAdBtn.textContent = '跳过广告';
                }
            }

            // 倒计时逻辑
            if (adRemaining > 0) {
                adRemaining--;
                if(adTimeSpan) adTimeSpan.textContent = adRemaining;
            }

            // 自动结束逻辑
            if (adRemaining <= 0 || (adVideo && adVideo.ended)) {
                hideAdOverlay();
            }
        }, 1000);

        if(skipAdBtn) {
            skipAdBtn.onclick = function() {
                if (!this.disabled) hideAdOverlay();
            };
        }
    }
    </c:if>

    // 4. 统一入口
    document.addEventListener('DOMContentLoaded', function() {
        <c:if test="${showAd}">
        initPlayer();
        </c:if>

        <c:if test="${not showAd}">
        var mv = document.getElementById('mainVideo');
        if(mv) mv.play().catch(e => console.log("等待交互播放"));
        </c:if>
    });
</script>
</body>
</html>