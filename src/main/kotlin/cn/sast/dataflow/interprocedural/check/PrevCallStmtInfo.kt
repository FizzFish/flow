package cn.sast.dataflow.interprocedural.check

import cn.sast.api.util.SootUtilsKt
import com.feysh.corax.config.api.IStmt
import com.feysh.corax.config.api.MLocal
import com.feysh.corax.config.api.MParameter
import java.util.ArrayList
import kotlin.jvm.internal.SourceDebugExtension
import soot.Body
import soot.Local
import soot.SootMethod
import soot.tagkit.ParamNamesTag
import kotlin.collections.CollectionsKt

@SourceDebugExtension(["SMAP\nPathCompanion.kt\nKotlin\n*S Kotlin\n*F\n+ 1 PathCompanion.kt\ncn/sast/dataflow/interprocedural/check/PrevCallStmtInfo\n+ 2 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 3 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,994:1\n808#2,11:995\n1#3:1006\n*S KotlinDebug\n*F\n+ 1 PathCompanion.kt\ncn/sast/dataflow/interprocedural/check/PrevCallStmtInfo\n*L\n472#1:995,11\n*E\n"])
class PrevCallStmtInfo(stmt: IStmt, method: SootMethod) : ModelingStmtInfo(stmt) {
    val method: SootMethod

    init {
        this.method = method
    }

    override fun getParameterNameByIndex(index: MLocal, filter: (Any) -> Boolean): Any? {
        return if (index is MParameter) {
            val name = getParameterNameByIndex(index.index)
            if (name != null) {
                if (getFirstParamIndex() == null) {
                    setFirstParamIndex(index.index)
                }
                if (filter(name)) name else null
            } else {
                null
            }
        } else {
            String.valueOf(if (filter(index)) index else null)
        }
    }

    fun getParameterNameByIndex(index: Int): String? {
        if (index == -1) {
            return "${method.declaringClass.shortName}.this"
        } else if (index in 0 until method.parameterCount) {
            val tags = method.tags
            val filteredTags = ArrayList<ParamNamesTag>()
            
            for (element in tags) {
                if (element is ParamNamesTag) {
                    filteredTags.add(element)
                }
            }

            val names = (filteredTags.firstOrNull()?.names) ?: emptyList()
            var name = names.getOrNull(index) as String?
            
            if (name == null) {
                name = try {
                    val body = SootUtilsKt.getActiveBodyOrNull(method)
                    body?.getParameterLocal(index)?.let { "local var $it" }
                } catch (e: RuntimeException) {
                    null
                }
            }

            return name
        } else {
            return null
        }
    }
}