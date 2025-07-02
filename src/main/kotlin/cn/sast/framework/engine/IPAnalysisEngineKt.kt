@file:SourceDebugExtension(["SMAP\nIPAnalysisEngine.kt\nKotlin\n*S Kotlin\n*F\n+ 1 IPAnalysisEngine.kt\ncn/sast/framework/engine/IPAnalysisEngineKt\n+ 2 SerializersModuleBuilders.kt\nkotlinx/serialization/modules/SerializersModuleBuildersKt\n*L\n1#1,344:1\n31#2,3:345\n*S KotlinDebug\n*F\n+ 1 IPAnalysisEngine.kt\ncn/sast/framework/engine/IPAnalysisEngineKt\n*L\n74#1:345,3\n*E\n"])

package cn.sast.framework.engine

import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonBuilder
import kotlinx.serialization.json.JsonKt
import kotlinx.serialization.modules.SerializersModule

public final val graphSerializersModule: SerializersModule
public final val graphJson: Json = JsonKt.Json$default(null, IPAnalysisEngineKt::graphJson$lambda$1, 1, null)

fun JsonBuilder.`graphJson$lambda$1`(): Unit {
   `$this$Json`.setEncodeDefaults(true);
   `$this$Json`.setUseArrayPolymorphism(true);
   `$this$Json`.setLenient(true);
   `$this$Json`.setPrettyPrint(true);
   `$this$Json`.setSerializersModule(graphSerializersModule);
   return Unit.INSTANCE;
}
