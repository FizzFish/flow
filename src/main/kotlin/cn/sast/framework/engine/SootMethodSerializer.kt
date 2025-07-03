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
object SootMethodSerializer : KSerializer<SootMethod> {
    override val descriptor: SerialDescriptor = SerialDescriptorsKt.PrimitiveSerialDescriptor("SootMethod", STRING)

    override fun deserialize(decoder: Decoder): SootMethod {
        throw IllegalStateException("Not yet implemented")
    }

    override fun serialize(encoder: Encoder, value: SootMethod) {
        val descriptor = this.descriptor
        val composite = encoder.beginStructure(descriptor)
        val stringValue = value.toString()
        encoder.encodeString(stringValue)
        composite.endStructure(descriptor)
    }
}