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
public class DynamicLookupSerializer : KSerializer<Object> {
   public open val descriptor: SerialDescriptor = new ContextualSerializer(Any::class, null, new KSerializer[0]).getDescriptor()

   public open fun serialize(encoder: Encoder, value: Any) {
      var var10000: KSerializer = SerializersModule.getContextual$default(encoder.getSerializersModule(), value.getClass()::class, null, 2, null);
      if (var10000 == null) {
         var10000 = SerializersKt.serializer(value.getClass()::class);
      }

      encoder.encodeSerializableValue(var10000 as SerializationStrategy, value);
   }

   public open fun deserialize(decoder: Decoder): Any {
      throw new IllegalStateException("not support yet".toString());
   }
}
