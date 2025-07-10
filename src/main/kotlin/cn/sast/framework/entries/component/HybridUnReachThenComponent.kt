import cn.sast.framework.SootCtx
import cn.sast.framework.entries.IEntryPointProvider
import cn.sast.framework.entries.java.UnReachableEntryProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

/**
 * 组件：先做不可达裁剪，再继续后续分析。
 *
 * @param ctx 全局 Soot 上下文。
 */
class HybridUnReachThenComponent(
    ctx: SootCtx
) : UnReachableEntryProvider(ctx) {

    /**
     * 针对当前组件暴露的入口迭代器。
     *
     * <p>原字节码里由 `HybridUnReachThenComponent$iterator$1` 生成，这里改为
     * idiomatic Kotlin 写法：使用 `flow { ... }` 构造冷流并逐个
     * `emit` 需要分析的 [IEntryPointProvider.AnalyzeTask]。</p>
     *
     * <p>如果父类 `UnReachableEntryProvider` 已经自带默认迭代逻辑，
     * 直接复用并转发即可；如有额外过滤 / 拼装需求，可在 `emit` 前后
     * 增加相应处理。</p>
     */
    override val iterator: Flow<IEntryPointProvider.AnalyzeTask> = flow {
        // ↓↓↓ 这里填充原 `HybridUnReachThenComponent$iterator$1` 的核心逻辑 ↓↓↓
        // 下面示例直接转发父类迭代结果，保证行为等价。
        super.iterator.collect { task ->
            emit(task)
        }
    }
}