package cn.sast.framework.plugin

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable

@Serializable
public sealed class ConfigSerializable protected constructor() : IConfig, java.lang.Comparable<ConfigSerializable> {
   public abstract val name: String

   public open operator fun compareTo(other: ConfigSerializable): Int {
      val var10000: java.lang.String = other.getClass().getName();
      val var10001: java.lang.String = this.getClass().getName();
      return if (var10000.compareTo(var10001) == 0) this.getName().compareTo(other.getName()) else 0;
   }

   public companion object {
      public fun serializer(): KSerializer<ConfigSerializable> {
         return this.get$cachedSerializer();
      }
   }
}
