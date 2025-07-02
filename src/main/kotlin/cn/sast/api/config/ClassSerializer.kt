package cn.sast.api.config

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptorsKt
import kotlinx.serialization.descriptors.PrimitiveKind.STRING
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

public object ClassSerializer : KSerializer<Class<?>> {
   public open val descriptor: SerialDescriptor = SerialDescriptorsKt.PrimitiveSerialDescriptor("ClassSerializer", STRING.INSTANCE as PrimitiveKind)

   public open fun serialize(encoder: Encoder, value: Class<*>) {
      val var10001: java.lang.String = value.getName();
      encoder.encodeString(var10001);
   }

   public open fun deserialize(decoder: Decoder): Class<*> {
      val var10000: Class = Class.forName(decoder.decodeString());
      return var10000;
   }
}
