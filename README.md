# NoteFlow

一个使用 **Android 原生 Kotlin + Jetpack Compose** 开发的本地优先个人多功能记事应用，聚合了任务、习惯、笔记与今日概览能力。

## 项目定位

NoteFlow 面向“个人日常管理”场景，当前以本地数据为核心，优先保证：

- 本地可用，不依赖云同步
- 任务、习惯、笔记三类内容统一管理
- 提醒逻辑集中处理，不散落在页面层
- 删除默认软删除，支持回收站恢复
- Markdown 笔记与本地图片引用
- 备份与恢复能力

## 当前能力

目前仓库内已经包含以下模块与能力：

- 任务模块：列表、详情、编辑、子任务、提醒
- 习惯模块：列表、详情、编辑、步骤、提醒
- 笔记模块：列表、编辑、Markdown 预览、图片引用
- 今日页：聚合任务与习惯视图
- 设置：提醒相关偏好
- 回收站：软删除内容管理
- 备份恢复：本地导出与导入

## 技术栈

- Kotlin
- Jetpack Compose
- Room
- Hilt
- Navigation Compose
- WorkManager
- DataStore
- Kotlin Serialization

## 项目结构

```text
app/src/main/java/com/noteflow
├─ app/                 # 应用入口、导航
├─ core/                # UI、时间、Markdown、提醒等通用能力
├─ data/                # Room、DataStore、Repository 实现
├─ domain/              # 模型、仓储接口、UseCase
└─ feature/             # tasks / habits / notes / today / trash / backup / settings
```

## 环境要求

- Android Studio 最新稳定版
- JDK 17
- Android SDK 36
- 最低支持 Android 10（API 29）

## 本地运行

1. 创建 `local.properties`
2. 配置本机 Android SDK 路径
3. 使用 Gradle Wrapper 构建

示例：

```properties
sdk.dir=C\:\\Users\\<YourUser>\\AppData\\Local\\Android\\Sdk
```

Debug 构建：

```powershell
.\gradlew.bat assembleDebug
```

Release 构建：

```powershell
.\gradlew.bat assembleRelease bundleRelease
```

## 发布签名

如果需要生成签名的 release 包，请在项目根目录创建 `keystore.properties`。该文件已被 `.gitignore` 排除，不应提交到仓库。

示例：

```properties
storeFile=D:/path/to/your-keystore.jks
storePassword=your-store-password
keyAlias=your-key-alias
keyPassword=your-key-password
```

## 产物位置

- Debug APK：`app/build/outputs/apk/debug/app-debug.apk`
- Release APK：`app/build/outputs/apk/release/app-release.apk`
- Release AAB：`app/build/outputs/bundle/release/app-release.aab`
- 混淆映射：`app/build/outputs/mapping/release/mapping.txt`

## 设计与文档

仓库中保留了产品与设计文档，便于继续按里程碑推进：

- `PRD.md`
- `TECH_SPEC_安卓原生技术方案.md`
- `MILESTONES_分阶段计划.md`
- `DB_SCHEMA_数据模型与数据库设计.md`
- `IA_信息架构.md`
- `UI_STYLE_GUIDE.md`

## 当前注意点

- 项目以本地优先为原则，暂不包含云同步
- 提醒能力依赖 Android 后台限制与系统权限，真机行为需要持续验证
- `local.properties`、`keystore.properties`、构建缓存与产物均不应提交

