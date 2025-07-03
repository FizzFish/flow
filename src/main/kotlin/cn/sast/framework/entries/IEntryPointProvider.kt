package cn.sast.framework.entries

import cn.sast.framework.SootCtx
import java.util.ArrayList
import java.util.LinkedHashSet
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.coroutines.flow.Flow
import soot.Scene
import soot.SootClass
import soot.SootMethod
import soot.util.Chain

public interface IEntryPointProvider {
    public val iterator: Flow<AnalyzeTask>

    public open fun startAnalyze() {
    }

    public open fun endAnalyze() {
    }

    public interface AnalyzeTask {
        public val name: String
        public val entries: Set<SootMethod>

        public open val methodsMustAnalyze: Set<SootMethod>
            get() = emptySet()

        public open val additionalEntries: Set<SootMethod>?
            get() = null

        public open val components: Set<SootClass>?
            get() = null

        public abstract fun needConstructCallGraph(sootCtx: SootCtx)

        @SourceDebugExtension([
            "SMAP\nIEntryPointProvider.kt\nKotlin\n*S Kotlin\n*F\n+ 1 IEntryPointProvider.kt\ncn/sast/framework/entries/IEntryPointProvider\$AnalyzeTask\$DefaultImpls\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,25:1\n774#2:26\n865#2,2:27\n1454#2,5:29\n*S KotlinDebug\n*F\n+ 1 IEntryPointProvider.kt\ncn/sast/framework/entries/IEntryPointProvider\$AnalyzeTask\$DefaultImpls\n*L\n15#1:26\n15#1:27,2\n15#1:29,5\n*E\n"
        ])
        internal object DefaultImpls {
            @JvmStatic
            fun getMethodsMustAnalyze(`$this`: AnalyzeTask): MutableSet<SootMethod> {
                val classes = Scene.v().applicationClasses as Iterable<SootClass>
                val filteredClasses = ArrayList<SootClass>()
                
                for (clazz in classes) {
                    if (clazz.isInScene) {
                        filteredClasses.add(clazz)
                    }
                }

                val result = LinkedHashSet<SootMethod>()
                for (clazz in filteredClasses) {
                    result.addAll(clazz.methods)
                }

                return result
            }

            @JvmStatic
            fun getAdditionalEntries(`$this`: AnalyzeTask): MutableSet<SootMethod>? {
                return null
            }

            @JvmStatic
            fun getComponents(`$this`: AnalyzeTask): MutableSet<SootClass>? {
                return null
            }
        }
    }

    internal object DefaultImpls {
        @JvmStatic
        fun startAnalyze(`$this`: IEntryPointProvider) {
        }

        @JvmStatic
        fun endAnalyze(`$this`: IEntryPointProvider) {
        }
    }
}