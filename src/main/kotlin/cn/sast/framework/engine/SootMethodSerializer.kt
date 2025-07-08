package cn.sast.framework.engine

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.buildSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import soot.SootMethod

/**
 * Serialises a [SootMethod] to its fully‑qualified signature string.  We do **not**
 * implement deserialisation because reconstructing a `SootMethod` instance
 * without a populated Soot [soot.Scene] is error‑prone.  If you need a
 * deserialiser, look up the method in an initialised scene instead.
 */
object SootMethodSerializer : KSerializer<SootMethod> {

    override val descriptor: SerialDescriptor =
        buildSerialDescriptor("SootMethod", PrimitiveKind.STRING)

    override fun serialize(encoder: Encoder, value: SootMethod) {
        // Example: <com.example.Foo: void bar(int)>
        encoder.encodeString(value.signature)
    }

    override fun deserialize(decoder: Decoder): SootMethod {
        throw UnsupportedOperationException(
            "Deserialising SootMethod is not supported – obtain it from an active Soot Scene instead.")
    }
}