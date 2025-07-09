package cn.sast.idfa.progressbar

import cn.sast.idfa.analysis.ProcessInfoView
import java.lang.reflect.Field
import java.text.DecimalFormat
import java.time.temporal.ChronoUnit
import java.util.concurrent.atomic.AtomicLong
import me.tongfei.progressbar.*

/**
 * 统一封装 [me.tongfei.progressbar.ProgressBar]，
 * 提供更新间隔 / 最大渲染宽度等可配置能力。
 *
 * @param updateIntervalMillis 进度条刷新频率（毫秒）
 * @param maxProgressLength    单行最大渲染宽度
 */
class ProgressBarExt(
   var updateIntervalMillis: Int = 500,
   var maxProgressLength: Int = 120,
) {

   /**
    * 创建并返回一个新的 [ProgressBar]。
    *
    * @param unitName   计量单位名称（如 "MB"、"items"）
    * @param initialMax 总进度（<=0 表示未知）
    * @param style      进度条样式
    * @param builder    额外自定义构建逻辑（可选）
    */
   @JvmOverloads
   fun getProgressBar(
      unitName: String,
      initialMax: Long,
      style: ProgressBarStyle = ProgressBarStyle.COLORFUL_UNICODE_BLOCK,
      builder: ProgressBarBuilder.() -> Unit = {},
   ): ProgressBar =
      ProgressBarBuilder()
         .setTaskName(">")
         .showSpeed(DecimalFormat("#.##"))
         .setStyle(style)
         .setInitialMax(initialMax)
         .setUnit(unitName, 1L)
         .setSpeedUnit(ChronoUnit.SECONDS)
         .setMaxRenderedLength(maxProgressLength)
         .continuousUpdate()
         .setUpdateIntervalMillis(updateIntervalMillis)
         .apply(builder)
         .build()

   // ------------------------------------------------------------------ //
   // 内部扩展：在进度条行尾实时渲染系统资源占用信息
   // ------------------------------------------------------------------ //

   open class DefaultProcessInfoRenderer(
      private val progressBar: ProgressBar,
      private val delegate: ProgressBarRenderer,
   ) : ProcessInfoView(), ProgressBarRenderer {

      /** 当总量较大时，可将进度条拆分为多行显示 */
      var splitLines: Long = 0L

      private val counter = AtomicLong(-1L)

      /** 组合 JVM 内存 / 系统内存 / CPU 使用率文案 */
      open val extraMessage: String
         get() = " ${getJvmMemoryUsageText()} ${getFreeMemoryText()} ${getCpuLoadText()}"

      override fun render(progressState: ProgressState, maxLength: Int): String =
         synchronized(progressBar) {
            // 拼接额外信息 → 委托渲染 → 恢复
            progressBar.extraMessage = extraMessage + progressBar.extraMessage
            val rendered = delegate.render(progressState, maxLength)
            progressBar.extraMessage = ""
            rendered
         }

      /** 步进（支持分行刷新） */
      fun step() {
         if (splitLines <= 0) {
            progressBar.step()
            return
         }

         val rows = progressBar.max / splitLines
         val cur = counter.incrementAndGet()

         var needRefresh = false
         synchronized(progressBar) {
            progressBar.step()
            if ((rows != 0L && cur % rows == 0L) || cur == progressBar.max) {
               progressBar.extraMessage = "\n"
               needRefresh = true
            }
         }
         if (needRefresh) progressBar.refresh()
      }

      fun refresh() = progressBar.refresh()

      fun close() {
         progressBar.pause()
         progressBar.close()
      }
   }
}

// ----------------------------------------------------------------------
// ProgressBar 扩展：动态替换内部 renderer，实现行尾资源监控
// ----------------------------------------------------------------------

/**
 * 通过反射把 [ProgressBar] 的默认 renderer 换成自定义实现。
 *
 * @param newRenderWrapper 传入旧 renderer，返回新的 renderer
 * @return 新创建并已注入的 renderer
 */
@Suppress("UNCHECKED_CAST")
fun <Render : ProgressBarRenderer> ProgressBar.wrapper(
   newRenderWrapper: (ProgressBar, ProgressBarRenderer) -> Render,
): Render {
   // private final ProgressBarAction action
   val actionField: Field = ProgressBar::class.java
      .getDeclaredField("action").apply { isAccessible = true }
   val actionObj: Any = actionField[this]

   // private ProgressBarRenderer renderer
   val rendererField: Field = actionObj.javaClass
      .getDeclaredField("renderer").apply { isAccessible = true }
   val oldRenderer = rendererField[actionObj] as ProgressBarRenderer

   val newRenderer = newRenderWrapper(this, oldRenderer)
   rendererField[actionObj] = newRenderer
   return newRenderer
}

/**
 * 便捷版：直接注入 [ProgressBarExt.DefaultProcessInfoRenderer]。
 */
fun ProgressBar.wrapper(): ProgressBarExt.DefaultProcessInfoRenderer =
   wrapper { bar, renderer -> ProgressBarExt.DefaultProcessInfoRenderer(bar, renderer) }
