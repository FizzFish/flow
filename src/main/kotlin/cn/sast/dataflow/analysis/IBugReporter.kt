package cn.sast.dataflow.analysis

/* ───────────────────────── Imports ───────────────────────── */
import com.feysh.corax.config.api.BugMessage
import com.feysh.corax.config.api.CheckType
import com.feysh.corax.config.api.report.Region
import java.nio.file.Path
import soot.SootClass
import soot.Unit as SootUnit
import soot.SootField
import soot.SootMethod    // 避免 kotlin.Unit 名称冲突

/**
 * 统一的“报告生成”接口。
 * 每个重载都带一个可选的 [env] lambda，用于补充
 * [BugMessage.Env.args] 等额外字段 —— 默认什么也不做。
 */
interface IBugReporter {

   /* ============= 按 Class + Region ============= */

   fun report(
      checkType: CheckType,
      atClass: SootClass,
      region: Region,
      env: BugMessage.Env.() -> Unit = {}            // ← 默认空实现
   )

   /* ============= 按文件 Path + Region ============= */

   fun report(
      checkType: CheckType,
      file: Path,
      region: Region,
      env: BugMessage.Env.() -> Unit = {}
   )

   /* ============= 按 Field ============= */

   fun report(
      checkType: CheckType,
      field: SootField,
      env: BugMessage.Env.() -> Unit = {}
   )

   /* ============= 按 Method ============= */

   fun report(
      checkType: CheckType,
      method: SootMethod,
      env: BugMessage.Env.() -> Unit = {}
   )

   /* ============= 指定到某条 Soot 指令 ============= */

   fun report(
      checkType: CheckType,
      clazz: SootClass,
      unit: SootUnit,
      env: BugMessage.Env.() -> Unit = {}
   )
}
