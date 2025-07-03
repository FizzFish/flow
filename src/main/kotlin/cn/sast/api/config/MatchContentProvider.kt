package cn.sast.api.config

import com.feysh.corax.config.api.rules.ProcessRule.FileMatch.MatchTarget
import java.nio.file.Path
import soot.SootClass
import soot.SootField
import soot.SootMethod

public interface MatchContentProvider {
    public abstract fun get(file: Path): MatchTarget

    public abstract fun get(sf: SootField): com.feysh.corax.config.api.rules.ProcessRule.ClassMemberMatch.MatchTarget

    public abstract fun get(sm: SootMethod): com.feysh.corax.config.api.rules.ProcessRule.ClassMemberMatch.MatchTarget

    public abstract fun get(sc: SootClass): com.feysh.corax.config.api.rules.ProcessRule.ClassMemberMatch.MatchTarget

    public abstract fun getClassPath(classpath: Path): com.feysh.corax.config.api.rules.ProcessRule.ClassPathMatch.MatchTarget
}