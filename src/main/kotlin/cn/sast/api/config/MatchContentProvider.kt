package cn.sast.api.config

import com.feysh.corax.config.api.rules.ProcessRule.FileMatch
import com.feysh.corax.config.api.rules.ProcessRule.ClassMemberMatch
import com.feysh.corax.config.api.rules.ProcessRule.ClassPathMatch
import java.nio.file.Path
import soot.SootClass
import soot.SootField
import soot.SootMethod

interface MatchContentProvider {
    fun get(file: Path): FileMatch.MatchTarget

    fun get(sf: SootField): ClassMemberMatch.MatchTarget

    fun get(sm: SootMethod): ClassMemberMatch.MatchTarget

    fun get(sc: SootClass): ClassMemberMatch.MatchTarget

    fun getClassPath(classpath: Path): ClassPathMatch.MatchTarget
}