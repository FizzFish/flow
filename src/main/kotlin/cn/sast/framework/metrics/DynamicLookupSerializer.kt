package cn.sast.framework.metrics

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerializationStrategy
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.modules.SerializersModule
import kotlinx.serialization.serializer // Kotlin 1.9+

/**
 * 在运行时根据实际对象类型动态查找合适的 [KSerializer]。
 *
 * > **注意**：目前仅支持 *序列化*，反序列化会抛异常。
 */
@OptIn(ExperimentalSerializationApi::class)
class DynamicLookupSerializer : KSerializer<Any> {

    override val descriptor: SerialDescriptor =
        // ContextualSerializer(Any::class) 只是为了拿一个 “不会被真正使用” 的 descriptor
        kotlinx.serialization.ContextualSerializer(Any::class).descriptor

    override fun serialize(encoder: Encoder, value: Any) {
        // 1. 先尝试从当前 SerializersModule 获取 contextual serializer
        // 2. 若失败再回退到平台 `serializer()` 推断
        val serializer =
            encoder.serializersModule.getContextual(value::class)
                ?: encoder.serializersModule.serializer(value::class)

        @Suppress("UNCHECKED_CAST") // 已经确保 serializer 与 value::class 对应
        encoder.encodeSerializableValue(serializer as SerializationStrategy<Any>, value)
    }

    override fun deserialize(decoder: Decoder): Any =
        error("DynamicLookupSerializer is write-only; deserialization is not supported.")
}
