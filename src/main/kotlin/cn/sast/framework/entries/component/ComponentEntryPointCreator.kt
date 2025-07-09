package cn.sast.framework.entries.component

import cn.sast.framework.entries.utils.PhantomValueForType
import soot.Body
import soot.Local
import soot.LocalGenerator
import soot.SootClass
import soot.Type
import soot.Value
import soot.jimple.infoflow.entryPointCreators.SequentialEntryPointCreator

/**
 * Same behaviour as the original Java implementation but expressed in idiomatic Kotlin.
 */
class ComponentEntryPointCreator(
    entry: Collection<String>
) : SequentialEntryPointCreator(entry) {

    private val phantom = PhantomValueForType()

    override fun getValueForType(
        tp: Type,
        constructionStack: MutableSet<SootClass>?,
        parentClasses: Set<SootClass>?,
        generatedLocals: MutableSet<Local>?,
        ignoreExcludes: Boolean
    ): Value? = phantom.getValueForType(body, generator, tp)
}