# AGENTS.md
Refactor the Vineflower-decompiled Kotlin code base with OpenAI Codex  
© 2025-07-02

## 0 | 目标与原则
| 编号 | 说明 |
|-----:|------|
| G-1  | **保证 _单文件语法正确_** —— 每次提交只要求改动文件经 `kotlinc -Xscript` / PSI 解析无报错；不要求全项目能编译。 |
| G-2  | **可增量迭代** —— 任意时刻都能停下来，下轮 Agent 从“剩余文件清单”继续。 |
| G-3  | **提升可读性** —— 修复语法 → 语义化重命名 → 折回内部/匿名类。 |
| G-4  | **行为保持** —— 不改业务逻辑；仅重命名 & 文件组织调整。 |
| G-5  | **自动化验证可分块** —— CI 只检查 *本次改动文件*，而非全仓库。 |

---

## 1 | 总体流程

```mermaid
graph TD
  scan[FileDiscovery Agent] --> queue[open GitHub Issues]
  subgraph incremental loop
    queue --> syntax[SyntaxFix Agent] --> rename[Rename Agent] --> merge[MergeInner Agent]
    merge --> done[Mark file ✅] --> queue
  end
