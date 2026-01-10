# Android 脚手架项目指南

## 构建前置
- JDK：推荐 21（当前配置）；如遇兼容性问题可切回 17 并开启 coreLibraryDesugaring。
- Android SDK：安装与 `compileSdk`/`targetSdk`（36）匹配的 SDK 和构建工具；准备可用的模拟器/真机以运行 `connectedAndroidTest`、Baseline Profile、宏基准。
- Gradle：使用项目自带 wrapper `./gradlew`。

## Android SDK 配置（本地）
本项目不会提交 `local.properties`（Android Studio 会自动生成）。如果你在命令行运行 `lintRelease` / `assembleRelease` 等任务，请确保能找到 SDK：

- 方式 A：使用 `local.properties`（推荐，Android Studio 会自动写入 `sdk.dir=...`）。
- 方式 B：设置环境变量（适合纯命令行）：
  - Windows PowerShell：
    - `setx ANDROID_HOME "D:\path\to\sdk"`
    - `setx ANDROID_SDK_ROOT "D:\path\to\sdk"`
  - macOS/Linux（bash/zsh）：
    - `export ANDROID_HOME="$HOME/Library/Android/sdk"`（路径按实际调整）
    - `export ANDROID_SDK_ROOT="$ANDROID_HOME"`

## 常用命令
- 代码规范与静态检查：`./gradlew ktlintCheck detekt lintDebug`
- detekt 自动修复（可选，默认关闭）：`./gradlew detekt -Pdetekt.autoCorrect=true`
- 单元测试：`./gradlew testDebugUnitTest`
- 全部检查（CI 同步）：`./gradlew ktlintCheck detekt lintDebug lintRelease testDebugUnitTest`
- 组装调试包：`./gradlew :app:assembleDebug`
- 组装 Release 包：`./gradlew :app:assembleRelease`
- 构建 Release 版本的 .aab 安装包：`./gradlew bundleRelease`

## 签名
- `build.gradle.kts` 支持 `keystore.properties`（从 `keystore.properties.example` 复制一份再填写）：
  ```properties
  storeFile=your.keystore
  storePassword=****
  keyAlias=****
  keyPassword=****
  ```
- 未提供/配置不完整/`storeFile` 不存在时会回退使用 debug 签名，适合模板/CI；`.gitignore` 已忽略 keystore 相关文件。

## 基线配置与性能
- Baseline Profile 生成：`./gradlew :app:generateBaselineProfile`（需可用设备/AVD，默认使用 managed device `pixel9Api36`，可按需调整）。
- 宏基准测试：`./gradlew :benchmark:connectedBenchmarkAndroidTest`（需真机/模拟器；CI 默认关闭，可在 GitHub Actions 中将环境变量 `RUN_DEVICE_TESTS` 设为 `true` 开启）。

## CI
- GitHub Actions 工作流：`.github/workflows/ci.yml`
  - 默认执行 ktlint、detekt、`lintDebug`/`lintRelease`、单元测试、`assembleDebug`/`assembleRelease`。
  - 如需设备测试，设置环境变量 `RUN_DEVICE_TESTS=true`。
  - 构建/报告会作为 artifact 上传。

## 其他
- Gradle 性能：`org.gradle.jvmargs` 设为 4096m，已开启并行；如在 CI 受限可按需调低。
