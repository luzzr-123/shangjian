# PowerShell 8009001d 修复说明

## 现象
- Codex 内所有 `shell_command` 在启动 PowerShell 阶段直接失败。
- 错误码为 `8009001d`。
- 连最小命令 `Write-Output hello` 也无法执行。

## 已确认结论
- 这不是仓库代码问题。
- 这发生在 PowerShell 进程启动前期，典型根因是 **以缺失用户环境变量的“新环境”启动 Windows PowerShell**。
- 在这种情况下，`USERPROFILE`、`APPDATA`、`LOCALAPPDATA`、`TEMP`、`TMP` 等关键变量缺失，会直接导致 Windows PowerShell 5.1 启动失败并返回 `8009001d`。

## 已提供修复文件
- 注册表修复文件：`D:\project\codex\MBK\tools\powershell-env-repair.reg`

## 执行步骤
1. 关闭 Codex。
2. 双击导入 `D:\project\codex\MBK\tools\powershell-env-repair.reg`。
3. 注销并重新登录 Windows，或至少完全重启一次桌面会话。
4. 重新打开 Codex。
5. 再执行构建命令：
   - `D:\project\codex\MBK\gradlew.bat --no-daemon :app:assembleRelease --console=plain`

## 如果仍失败
- 改为在系统“环境变量”里手动确认以下用户变量存在：
  - `USERPROFILE`
  - `HOMEDRIVE`
  - `HOMEPATH`
  - `APPDATA`
  - `LOCALAPPDATA`
  - `TEMP`
  - `TMP`
- 若这些变量存在但 Codex 内仍失败，则问题在 Codex 的 PowerShell 启动方式本身，不在项目仓库。

## 说明
- 该修复是最小侵入方案，只补齐当前用户级环境变量。
- 它不会修改项目代码或 Android 构建逻辑。
