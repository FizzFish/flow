package cn.sast.framework.engine

import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptorsKt
import kotlinx.serialization.descriptors.PrimitiveKind.STRING
import kotlinx.serialization.encoding.CompositeEncoder
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import soot.SootMethod

@Serializer(forClass = SootMethod::class)
@SourceDebugExtension(["SMAP\nIPAnalysisEngine.kt\nKotlin\n*S Kotlin\n*F\n+ 1 IPAnalysisEngine.kt\ncn/sast/framework/engine/SootMethodSerializer\n+ 2 Encoding.kt\nkotlinx/serialization/encoding/EncodingKt\n*L\n1#1,344:1\n475#2,4:345\n*S KotlinDebug\n*F\n+ 1 IPAnalysisEngine.kt\ncn/sast/framework/engine/SootMethodSerializer\n*L\n69#1:345,4\n*E\n"])
public object SootMethodSerializer : KSerializer<SootMethod> {
   public open val descriptor: SerialDescriptor = SerialDescriptorsKt.PrimitiveSerialDescriptor("SootMethod", STRING.INSTANCE as PrimitiveKind)

   public open fun deserialize(decoder: Decoder): SootMethod {
      throw new IllegalStateException("Not yet implemented".toString());
   }

   public open fun serialize(encoder: Encoder, value: SootMethod) {
      val `descriptor$iv`: SerialDescriptor = this.getDescriptor();
      val `composite$iv`: CompositeEncoder = encoder.beginStructure(`descriptor$iv`);
      val var10001: java.lang.String = value.toString();
      encoder.encodeString(var10001);
      `composite$iv`.endStructure(`descriptor$iv`);
   }
}
