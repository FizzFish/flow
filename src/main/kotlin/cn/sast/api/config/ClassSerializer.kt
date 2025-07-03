package cn.sast.api.config

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptorsKt
import kotlinx.serialization.descriptors.PrimitiveKind.STRING
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

public object ClassSerializer : KSerializer<Class<*>> {
    override val descriptor: SerialDescriptor = SerialDescriptorsKt.PrimitiveSerialDescriptor("ClassSerializer", STRING)

    override fun serialize(encoder: Encoder, value: Class<*>) {
        encoder.encodeString(value.name)
    }

    override fun deserialize(decoder: Decoder): Class<*> {
        return Class.forName(decoder.decodeString())
    }
}