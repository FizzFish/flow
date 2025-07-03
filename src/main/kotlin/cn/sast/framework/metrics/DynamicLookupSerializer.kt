package cn.sast.framework.metrics

import kotlinx.serialization.ContextualSerializer
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.SerializersKt
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.modules.SerializersModule

@ExperimentalSerializationApi
public class DynamicLookupSerializer : KSerializer<Any> {
    override val descriptor: SerialDescriptor = ContextualSerializer(Any::class, null, emptyArray()).descriptor

    override fun serialize(encoder: Encoder, value: Any) {
        var serializer: KSerializer<Any> = SerializersModule.getContextual(encoder.serializersModule, value::class, null) as? KSerializer<Any>
            ?: SerializersKt.serializer(value::class) as KSerializer<Any>
        encoder.encodeSerializableValue(serializer as SerializationStrategy<Any>, value)
    }

    override fun deserialize(decoder: Decoder): Any {
        throw IllegalStateException("not support yet")
    }
}