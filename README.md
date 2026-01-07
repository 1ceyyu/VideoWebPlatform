
# Video Web Platform

一个功能完整的视频播放与管理系统，支持视频上传、分类浏览、智能广告插入和流媒体播放。采用Java Servlet + JSP + MySQL技术栈构建，系统不仅提供了流媒体播放和广告投放功能，还内置了受众喜好分析接口 (API)，支持第三方网站获取实时统计数据。

## 🌐 部署与访问信息
- **项目主页**: http://10.100.164.26:8080/VideoWebPlatform/home
- **统计 API**: http://10.100.164.26:8080/VideoWebPlatform/api/video-stats

## 🚀 核心功能
- **流媒体传输**: 使用 StreamServlet 处理视频流，支持 HTML5 播放器的 Range 请求（实现进度条随意拖动）。
- **广告系统**:  视频播放前自动加载贴片广告，支持 5 秒倒计时、手动跳过及自动结束切换。
- **点击量统计**:  用户每进入一次播放页，系统自动更新数据库中对应视频分类的点击总数。
- **自动重置机制**: 集成 HttpSessionListener。当服务器检测到全站无活跃用户（所有用户关闭浏览器或会话超时）时，自动清空数据库点击统计，实现数据的新鲜度管理。

## 🏗️技术栈
- **后端**: Java Servlet 4.0+, JDBC
- **前端**: JSP + JSTL + HTML5 + CSS3 + JavaScript
- **数据库**: MySQL 8.0+
- **构建工具**: Maven
- **服务器**: Apache Tomcat 10+

## 📊 开放 API 规范
本项目对外开放了 JSON 格式的统计接口，支持跨域访问（CORS），方便其他网站采集用户喜好数据。

- **Endpoint**: `/api/video-stats`
- **Method**: `GET`
- **Response Header**: `Access-Control-Allow-Origin: *`

**返回示例**:
```json
[
  {
    "categoryName": "电影",
    "totalClicks": 128
  },
  {
    "categoryName": "纪录片",
    "totalClicks": 45
  }
]
```

## 📂 详细项目结构
项目采用典型的 MVC (Model-View-Controller) 架构，确保了逻辑与展示的彻底分离。

### 项目结构
```
video-web-platform/                          # 项目根目录
├── pom.xml                                   # Maven 配置文件
├── README.md                                 # 项目说明文档
├── .gitignore                                # Git 忽略文件配置
│
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/example/videowebplatform/
│   │   │       ├── controller/               # 控制器层（Servlet）
│   │   │       │   ├── AddVideoPageServlet.java          # 添加视频页面控制器
│   │   │       │   ├── AdStreamServlet.java              # 广告视频流控制器
│   │   │       │   ├── HomeServlet.java                  # 首页控制器
│   │   │       │   ├── ImageStreamServlet.java           # 封面图片流控制器
│   │   │       │   ├── PlayServlet.java                  # 视频播放控制器
│   │   │       │   ├── StreamServlet.java                # 主视频流控制器
│   │   │       │   ├── UploadVideoServlet.java           # 视频上传控制器
│   │   │       │   └── VideoStatApiServlet.java          # 视频统计API控制器
│   │   │       │
│   │   │       ├── dao/                      # 数据访问层
│   │   │       │   ├── AdVideoDAO.java                   # 广告DAO接口
│   │   │       │   ├── AdVideoDAOImpl.java               # 广告DAO实现
│   │   │       │   ├── VideoDAO.java                     # 视频DAO接口
│   │   │       │   └── VideoDAOImpl.java                 # 视频DAO实现
│   │   │       │
│   │   │       ├── model/                    # 数据模型层
│   │   │       │   ├── AdVideo.java                      # 广告视频实体类
│   │   │       │   ├── Category.java                     # 分类实体类
│   │   │       │   ├── CategoryStat.java                 # 分类统计实体类
│   │   │       │   └── Video.java                        # 视频实体类
│   │   │       │
│   │   │       ├── util/                     # 工具类层
│   │   │       │   ├── AdSyncService.java                # 广告同步服务
│   │   │       │   ├── DBUtil.java                       # 数据库工具类
│   │   │       │   └── VideoUtil.java                    # 视频处理工具类
│   │   │       │
│   │   │       └── listener/                 # 监听器层
│   │   │           ├── AppInitListener.java              # 应用初始化监听器
│   │   │           └── SessionCounterListener.java       # 会话计数监听器
│   │   │
│   │   └── webapp/                           # Web 资源目录
│   │       ├── WEB-INF/                      # 受保护目录
│   │       │   ├── web.xml                   # Web 应用部署描述符（可选）
│   │       │   └── views/                    # JSP 视图文件
│   │       │       ├── addVideoPage.jsp      # 添加视频页面
│   │       │       ├── home.jsp              # 首页
│   │       │       └── videoPlayerNew.jsp    # 视频播放页面
│   │       │
│   │       ├── resources/                    # 静态资源
│   │       │   ├── css/
│   │       │   │   └── style.css             # 全局样式表
│   │       │   ├── js/
│   │       │   │   └── script.js             # 全局JavaScript文件
│   │       │   └── covers/                   # 默认封面图片
│   │       │       └── default.jpg           # 默认封面
│   │       │
│   │       └── index.jsp                     # 首页重定向页面
│   │
│   └── test/                                 # 测试目录
│       ├── java/                             # 单元测试
│       └── resources/                        # 测试资源
│
├── target/                                   # Maven 构建输出目录
├── lib/                                      # 第三方依赖库（如有）
└── config/                                   # 配置文件目录
    ├── database/                             # 数据库脚本
    │   ├── schema.sql                        # 数据库表结构
    │   ├── data.sql                          # 初始数据
    │   └── indexes.sql                       # 索引优化脚本
    └── deployment/                           # 部署配置
        ├── tomcat-context.xml                # Tomcat 上下文配置
        ├── nginx.conf                        # Nginx 反向代理配置
        └── log4j2.xml                        # 日志配置文件
```

## 🛠️ 数据库设计
请在 MySQL 中执行以下初始化语句以确保统计功能正常运行：

```sql
-- 1. 创建分类表
CREATE TABLE category (
    id INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) NOT NULL
);

-- 2. 创建视频表
CREATE TABLE video (
    id INT PRIMARY KEY AUTO_INCREMENT,
    title VARCHAR(100),
    url VARCHAR(255),
    category_id INT,
    clicks INT DEFAULT 0, -- 核心统计字段
    FOREIGN KEY (category_id) REFERENCES category(id)
);
```

## 核心配置
文件存储路径
```java
// 主视频存储路径
private static final String BASE_VIDEO_PATH = "/var/www/videodata/movies/";
// 封面图片存储路径  
private static final String COVER_BASE_PATH = "/var/www/videodata/covers/";
// 广告视频存储路径
private static final String BASE_VIDEO_PATH = "/var/www/videodata/ads/";
```
广告配置
```java
// 广告显示概率（70%）
boolean showAd = !ads.isEmpty() && random.nextInt(100) < 70;

// 广告类型：0=前贴片，1=中插，2=后贴片
int adType = random.nextInt(3);

// 广告跳过等待时间（秒）
int skipDelay = 15;
```
视频上传配置
```java
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024 * 2,  // 2MB
    maxFileSize = 1024 * 1024 * 100,      // 100MB
    maxRequestSize = 1024 * 1024 * 110    // 110MB
)
```
## 📝 开发者备注
- **CORS 跨域说明**: API 接口已配置 `Access-Control-Allow-Origin: *`，其他网站的 JS 可通过 `fetch()` 直接调用。
- **重置延迟**: 由于浏览器关闭无法实时通知服务器，Session 销毁存在延迟（默认为 30 分钟）。若需调整，请修改 web.xml 中的 `<session-timeout>`。
- **安全性**: 所有 JSP 均存放在 WEB-INF 目录下，禁止外部通过 URL 直接绕过控制器访问，保证了业务逻辑的完整性。
```
