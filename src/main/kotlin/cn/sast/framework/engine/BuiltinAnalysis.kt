/*
$VF: Unable to decompile class
Please report this to the Vineflower issue tracker, at https://github.com/Vineflower/vineflower/issues with a copy of the class file (if you have the rights to distribute it!)
java.lang.NullPointerException: Cannot invoke "java.util.List.get(int)" because the return value of "org.jetbrains.java.decompiler.modules.decompiler.stats.Statement.getExprents()" is null
  at org.vineflower.kotlin.struct.DefaultArgsMap.from(DefaultArgsMap.java:111)
  at org.vineflower.kotlin.struct.KConstructor.parse(KConstructor.java:103)
  at org.vineflower.kotlin.KotlinWriter.writeClass(KotlinWriter.java:222)
  at org.vineflower.kotlin.KotlinWriter.writeClass(KotlinWriter.java:482)
  at org.jetbrains.java.decompiler.main.ClassesProcessor.writeClass(ClassesProcessor.java:500)
  at org.jetbrains.java.decompiler.main.Fernflower.getClassContent(Fernflower.java:196)
  at org.jetbrains.java.decompiler.struct.ContextUnit.lambda$save$3(ContextUnit.java:195)
*/package cn.sast.framework.engine

import kotlin.jvm.functions.Function0

// $VF: Class flags could not be determined
internal class `BuiltinAnalysis$allMethodsAnalyzeInScene$$inlined$bracket$default$1` : Function0<Object> {
   fun `BuiltinAnalysis$allMethodsAnalyzeInScene$$inlined$bracket$default$1`(`$msg`: java.lang.String) {
      this.$msg = `$msg`;
   }

   fun invoke(): Any {
      return "Started: ${this.$msg}";
   }
}
package cn.sast.framework.engine

import java.time.LocalDateTime
import kotlin.jvm.functions.Function0
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.jvm.internal.Ref.ObjectRef
import org.utbot.common.LoggingKt
import org.utbot.common.Maybe

// $VF: Class flags could not be determined
@SourceDebugExtension(["SMAP\nLogging.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Logging.kt\norg/utbot/common/LoggingKt$bracket$4\n+ 2 Logging.kt\norg/utbot/common/LoggingKt$bracket$1\n*L\n1#1,70:1\n51#2:71\n*E\n"])
internal class `BuiltinAnalysis$allMethodsAnalyzeInScene$$inlined$bracket$default$2` : Function0<Object> {
   fun `BuiltinAnalysis$allMethodsAnalyzeInScene$$inlined$bracket$default$2`(`$startTime`: LocalDateTime, `$msg`: java.lang.String, `$res`: ObjectRef) {
      this.$startTime = `$startTime`;
      this.$msg = `$msg`;
      this.$res = `$res`;
   }

   fun invoke(): Any {
      val var1: LocalDateTime = this.$startTime;
      val var10000: java.lang.String = LoggingKt.elapsedSecFrom(var1);
      val var10001: java.lang.String = this.$msg;
      val it: Any = Result.constructor-impl((this.$res.element as Maybe).getOrThrow());
      return "Finished (in $var10000): $var10001 ";
   }
}
package cn.sast.framework.engine

import java.time.LocalDateTime
import kotlin.jvm.functions.Function0
import org.utbot.common.LoggingKt

// $VF: Class flags could not be determined
internal class `BuiltinAnalysis$allMethodsAnalyzeInScene$$inlined$bracket$default$3` : Function0<Object> {
   fun `BuiltinAnalysis$allMethodsAnalyzeInScene$$inlined$bracket$default$3`(`$startTime`: LocalDateTime, `$msg`: java.lang.String) {
      this.$startTime = `$startTime`;
      this.$msg = `$msg`;
   }

   fun invoke(): Any {
      val var1: LocalDateTime = this.$startTime;
      return "Finished (in ${LoggingKt.elapsedSecFrom(var1)}): ${this.$msg} <Nothing>";
   }
}
package cn.sast.framework.engine

import java.time.LocalDateTime
import kotlin.jvm.functions.Function0
import kotlin.jvm.internal.SourceDebugExtension
import org.utbot.common.LoggingKt

// $VF: Class flags could not be determined
@SourceDebugExtension(["SMAP\nLogging.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Logging.kt\norg/utbot/common/LoggingKt$bracket$3\n+ 2 Logging.kt\norg/utbot/common/LoggingKt$bracket$1\n*L\n1#1,64:1\n51#2:65\n*E\n"])
internal class `BuiltinAnalysis$allMethodsAnalyzeInScene$$inlined$bracket$default$4` : Function0<Object> {
   fun `BuiltinAnalysis$allMethodsAnalyzeInScene$$inlined$bracket$default$4`(`$startTime`: LocalDateTime, `$msg`: java.lang.String, `$t`: java.lang.Throwable) {
      this.$startTime = `$startTime`;
      this.$msg = `$msg`;
      this.$t = `$t`;
   }

   fun invoke(): Any {
      val var1: LocalDateTime = this.$startTime;
      val var10000: java.lang.String = LoggingKt.elapsedSecFrom(var1);
      val var10001: java.lang.String = this.$msg;
      val it: Any = Result.constructor-impl(ResultKt.createFailure(this.$t));
      return "Finished (in $var10000): $var10001 :: EXCEPTION :: ";
   }
}
package cn.sast.framework.engine

import java.time.LocalDateTime
import kotlin.jvm.functions.Function0
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.jvm.internal.Ref.ObjectRef
import org.utbot.common.LoggingKt
import org.utbot.common.Maybe

// $VF: Class flags could not be determined
@SourceDebugExtension(["SMAP\nLogging.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Logging.kt\norg/utbot/common/LoggingKt$bracket$4\n+ 2 Logging.kt\norg/utbot/common/LoggingKt$bracket$1\n*L\n1#1,70:1\n51#2:71\n*E\n"])
internal class `BuiltinAnalysis$allMethodsAnalyzeInScene$$inlined$bracket$default$5` : Function0<Object> {
   fun `BuiltinAnalysis$allMethodsAnalyzeInScene$$inlined$bracket$default$5`(`$startTime`: LocalDateTime, `$msg`: java.lang.String, `$res`: ObjectRef) {
      this.$startTime = `$startTime`;
      this.$msg = `$msg`;
      this.$res = `$res`;
   }

   fun invoke(): Any {
      val var1: LocalDateTime = this.$startTime;
      val var10000: java.lang.String = LoggingKt.elapsedSecFrom(var1);
      val var10001: java.lang.String = this.$msg;
      val it: Any = Result.constructor-impl((this.$res.element as Maybe).getOrThrow());
      return "Finished (in $var10000): $var10001 ";
   }
}
package cn.sast.framework.engine

import java.time.LocalDateTime
import kotlin.jvm.functions.Function0
import org.utbot.common.LoggingKt

// $VF: Class flags could not be determined
internal class `BuiltinAnalysis$allMethodsAnalyzeInScene$$inlined$bracket$default$6` : Function0<Object> {
   fun `BuiltinAnalysis$allMethodsAnalyzeInScene$$inlined$bracket$default$6`(`$startTime`: LocalDateTime, `$msg`: java.lang.String) {
      this.$startTime = `$startTime`;
      this.$msg = `$msg`;
   }

   fun invoke(): Any {
      val var1: LocalDateTime = this.$startTime;
      return "Finished (in ${LoggingKt.elapsedSecFrom(var1)}): ${this.$msg} <Nothing>";
   }
}
package cn.sast.framework.engine

import kotlin.jvm.functions.Function0

// $VF: Class flags could not be determined
internal class `BuiltinAnalysis$analyzeInScene$$inlined$bracket$default$1` : Function0<Object> {
   fun `BuiltinAnalysis$analyzeInScene$$inlined$bracket$default$1`(`$msg`: java.lang.String) {
      this.$msg = `$msg`;
   }

   fun invoke(): Any {
      return "Started: ${this.$msg}";
   }
}
package cn.sast.framework.engine

import java.time.LocalDateTime
import kotlin.jvm.functions.Function0
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.jvm.internal.Ref.ObjectRef
import org.utbot.common.LoggingKt
import org.utbot.common.Maybe

// $VF: Class flags could not be determined
@SourceDebugExtension(["SMAP\nLogging.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Logging.kt\norg/utbot/common/LoggingKt$bracket$4\n+ 2 Logging.kt\norg/utbot/common/LoggingKt$bracket$1\n*L\n1#1,70:1\n51#2:71\n*E\n"])
internal class `BuiltinAnalysis$analyzeInScene$$inlined$bracket$default$2` : Function0<Object> {
   fun `BuiltinAnalysis$analyzeInScene$$inlined$bracket$default$2`(`$startTime`: LocalDateTime, `$msg`: java.lang.String, `$res`: ObjectRef) {
      this.$startTime = `$startTime`;
      this.$msg = `$msg`;
      this.$res = `$res`;
   }

   fun invoke(): Any {
      val var1: LocalDateTime = this.$startTime;
      val var10000: java.lang.String = LoggingKt.elapsedSecFrom(var1);
      val var10001: java.lang.String = this.$msg;
      val it: Any = Result.constructor-impl((this.$res.element as Maybe).getOrThrow());
      return "Finished (in $var10000): $var10001 ";
   }
}
package cn.sast.framework.engine

import java.time.LocalDateTime
import kotlin.jvm.functions.Function0
import org.utbot.common.LoggingKt

// $VF: Class flags could not be determined
internal class `BuiltinAnalysis$analyzeInScene$$inlined$bracket$default$3` : Function0<Object> {
   fun `BuiltinAnalysis$analyzeInScene$$inlined$bracket$default$3`(`$startTime`: LocalDateTime, `$msg`: java.lang.String) {
      this.$startTime = `$startTime`;
      this.$msg = `$msg`;
   }

   fun invoke(): Any {
      val var1: LocalDateTime = this.$startTime;
      return "Finished (in ${LoggingKt.elapsedSecFrom(var1)}): ${this.$msg} <Nothing>";
   }
}
package cn.sast.framework.engine

import java.time.LocalDateTime
import kotlin.jvm.functions.Function0
import kotlin.jvm.internal.SourceDebugExtension
import org.utbot.common.LoggingKt

// $VF: Class flags could not be determined
@SourceDebugExtension(["SMAP\nLogging.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Logging.kt\norg/utbot/common/LoggingKt$bracket$3\n+ 2 Logging.kt\norg/utbot/common/LoggingKt$bracket$1\n*L\n1#1,64:1\n51#2:65\n*E\n"])
internal class `BuiltinAnalysis$analyzeInScene$$inlined$bracket$default$4` : Function0<Object> {
   fun `BuiltinAnalysis$analyzeInScene$$inlined$bracket$default$4`(`$startTime`: LocalDateTime, `$msg`: java.lang.String, `$t`: java.lang.Throwable) {
      this.$startTime = `$startTime`;
      this.$msg = `$msg`;
      this.$t = `$t`;
   }

   fun invoke(): Any {
      val var1: LocalDateTime = this.$startTime;
      val var10000: java.lang.String = LoggingKt.elapsedSecFrom(var1);
      val var10001: java.lang.String = this.$msg;
      val it: Any = Result.constructor-impl(ResultKt.createFailure(this.$t));
      return "Finished (in $var10000): $var10001 :: EXCEPTION :: ";
   }
}
package cn.sast.framework.engine

import java.time.LocalDateTime
import kotlin.jvm.functions.Function0
import kotlin.jvm.internal.SourceDebugExtension
import kotlin.jvm.internal.Ref.ObjectRef
import org.utbot.common.LoggingKt
import org.utbot.common.Maybe

// $VF: Class flags could not be determined
@SourceDebugExtension(["SMAP\nLogging.kt\nKotlin\n*S Kotlin\n*F\n+ 1 Logging.kt\norg/utbot/common/LoggingKt$bracket$4\n+ 2 Logging.kt\norg/utbot/common/LoggingKt$bracket$1\n*L\n1#1,70:1\n51#2:71\n*E\n"])
internal class `BuiltinAnalysis$analyzeInScene$$inlined$bracket$default$5` : Function0<Object> {
   fun `BuiltinAnalysis$analyzeInScene$$inlined$bracket$default$5`(`$startTime`: LocalDateTime, `$msg`: java.lang.String, `$res`: ObjectRef) {
      this.$startTime = `$startTime`;
      this.$msg = `$msg`;
      this.$res = `$res`;
   }

   fun invoke(): Any {
      val var1: LocalDateTime = this.$startTime;
      val var10000: java.lang.String = LoggingKt.elapsedSecFrom(var1);
      val var10001: java.lang.String = this.$msg;
      val it: Any = Result.constructor-impl((this.$res.element as Maybe).getOrThrow());
      return "Finished (in $var10000): $var10001 ";
   }
}
package cn.sast.framework.engine

import java.time.LocalDateTime
import kotlin.jvm.functions.Function0
import org.utbot.common.LoggingKt

// $VF: Class flags could not be determined
internal class `BuiltinAnalysis$analyzeInScene$$inlined$bracket$default$6` : Function0<Object> {
   fun `BuiltinAnalysis$analyzeInScene$$inlined$bracket$default$6`(`$startTime`: LocalDateTime, `$msg`: java.lang.String) {
      this.$startTime = `$startTime`;
      this.$msg = `$msg`;
   }

   fun invoke(): Any {
      val var1: LocalDateTime = this.$startTime;
      return "Finished (in ${LoggingKt.elapsedSecFrom(var1)}): ${this.$msg} <Nothing>";
   }
}
