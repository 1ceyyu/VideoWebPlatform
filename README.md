
# Video Web Platform

这是一个基于 Java Servlet + JDBC 开发的轻量级视频管理与播放平台。系统不仅提供了流媒体播放和广告投放功能，还内置了受众喜好分析接口 (API)，支持第三方网站获取实时统计数据。

## 🌐 部署与访问信息
- **项目主页**: http://10.100.164.26:8080/VideoWebPlatform/home
- **统计 API**: http://10.100.164.26:8080/VideoWebPlatform/api/video-stats

## 🚀 核心功能
- **流媒体传输**: 使用 StreamServlet 处理视频流，支持 HTML5 播放器的 Range 请求（实现进度条随意拖动）。
- **广告系统**: 视频播放前自动加载贴片广告，支持 5 秒倒计时、手动跳过及自动结束切换。
- **点击量统计**: 用户每进入一次播放页，系统自动更新数据库中对应视频分类的点击总数。
- **自动重置机制**: 集成 HttpSessionListener。当服务器检测到全站无活跃用户（所有用户关闭浏览器或会话超时）时，自动清空数据库点击统计，实现数据的新鲜度管理。

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

### 1. 后端逻辑 (src/main/java)
```
com.example.videowebplatform/
├── controller/                 # 控制层 (Controller)
│   ├── AddVideoPageServlet.java   # 跳转至上传页面
│   ├── AddVideoServlet.java       # 处理视频上传逻辑
│   ├── CategoryServlet.java       # 处理分类过滤查询
│   ├── HomeServlet.java           # 首页数据装载与跳转
│   ├── PlayServlet.java           # 播放逻辑：处理广告判定与点击量累加
│   ├── StreamServlet.java         # 二进制流传输：支持视频断点续传
│   └── VideoStatApiServlet.java   # [API] 返回 JSON 格式的分类统计数据
├── dao/                        # 数据访问层 (DAO)
│   ├── VideoDAO.java              # 接口定义：包含统计与重置方法
│   └── VideoDAOImpl.java          # JDBC 实现：执行 SUM 聚合查询与 UPDATE
├── model/                      # 模型层 (Model)
│   ├── Video.java                 # 视频实体类
│   ├── Category.java              # 分类实体类
│   └── CategoryStat.java          # 统计专用 DTO (包含分类名与总点击数)
├── listener/                   # 监听器层
│   └── SessionCounterListener.java # 监听 Session 销毁，触发数据库清零逻辑
└── util/                       # 工具类
    └── DBUtil.java                # 封装数据库连接池 (JDBC)
```

### 2. 前端展示 (src/main/webapp)
```
webapp/
├── WEB-INF/
│   ├── views/                  # 视图层 (View): 受保护的 JSP 页面
│   │   ├── addVideoPage.jsp       # 视频上传表单页面
│   │   ├── home.jsp               # 视频展示主页
│   │   └── play.jsp               # 视频播放页（含广告倒计时与跳过逻辑）
│   ├── lib/                    # 项目依赖 (gson-x.x.jar, mysql-connector-j.jar)
│   └── web.xml                 # 全局配置：Servlet 映射及 Session 超时时间
├── index.jsp                   # 入口引导页
└── static/                     # 静态资源 (存放 CSS, JS 脚本等)
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

## 📝 开发者备注
- **CORS 跨域说明**: API 接口已配置 `Access-Control-Allow-Origin: *`，其他网站的 JS 可通过 `fetch()` 直接调用。
- **重置延迟**: 由于浏览器关闭无法实时通知服务器，Session 销毁存在延迟（默认为 30 分钟）。若需调整，请修改 web.xml 中的 `<session-timeout>`。
- **安全性**: 所有 JSP 均存放在 WEB-INF 目录下，禁止外部通过 URL 直接绕过控制器访问，保证了业务逻辑的完整性。
```
