package cn.sast.api.config

import cn.sast.common.ResourceImplKt
import cn.sast.common.ResourceKt
import java.util.ArrayList
import java.util.Map.Entry
import kotlin.jvm.internal.SourceDebugExtension
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient

@Serializable
@SerialName("PreAnalysisConfig")
@SourceDebugExtension(["SMAP\nPreAnalysisConfig.kt\nKotlin\n*S Kotlin\n*F\n+ 1 PreAnalysisConfig.kt\ncn/sast/api/config/PreAnalysisConfig\n+ 2 _Maps.kt\nkotlin/collections/MapsKt___MapsKt\n+ 3 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n+ 4 fake.kt\nkotlin/jvm/internal/FakeKt\n*L\n1#1,58:1\n126#2:59\n153#2,2:60\n155#2:66\n153#2,3:68\n1557#3:62\n1628#3,3:63\n1#4:67\n*S KotlinDebug\n*F\n+ 1 PreAnalysisConfig.kt\ncn/sast/api/config/PreAnalysisConfig\n*L\n50#1:59\n50#1:60,2\n50#1:66\n50#1:68,3\n50#1:62\n50#1:63,3\n*E\n"])
public data class PreAnalysisConfig(cancelAnalysisInErrorCount: Int = 10,
   largeFileSize: Int = 512000,
   largeFileSemaphorePermits: Int = 3,
   _fileExtensionToSizeThreshold: Map<String, Int> = MapsKt.mapOf(
         new Pair[]{
            TuplesKt.to(CollectionsKt.joinToString$default(ResourceKt.getJavaExtensions(), ",", null, null, 0, null, null, 62, null), 1048576),
            TuplesKt.to(CollectionsKt.joinToString$default(ResourceImplKt.getZipExtensions(), ",", null, null, 0, null, null, 62, null), -1),
            TuplesKt.to(
               CollectionsKt.joinToString$default(
                  CollectionsKt.listOf(
                     new java.lang.String[]{"html", "htm", "adoc", "gradle", "properties", "config", "cnf", "txt", "json", "xml", "yml", "yaml", "toml", "ini"}
                  ),
                  ",",
                  null,
                  null,
                  0,
                  null,
                  null,
                  62,
                  null
               ),
               5242880
            ),
            TuplesKt.to("*", 5242880)
         }
      ),
   maximumFileSizeThresholdWarnings: Int = 20
) {
   @SerialName("cancel_analysis_in_error_count")
   public final val cancelAnalysisInErrorCount: Int

   @SerialName("large_file_size")
   public final val largeFileSize: Int

   @SerialName("large_file_semaphore_permits")
   public final val largeFileSemaphorePermits: Int

   @SerialName("file_extension_to_size_threshold")
   private final val _fileExtensionToSizeThreshold: Map<String, Int>

   @SerialName("maximum_file_size_threshold_warnings")
   public final val maximumFileSizeThresholdWarnings: Int

   @Transient
   private final val fileExtensionToSizeThreshold: Map<String, Int>

   init {
      this.cancelAnalysisInErrorCount = cancelAnalysisInErrorCount;
      this.largeFileSize = largeFileSize;
      this.largeFileSemaphorePermits = largeFileSemaphorePermits;
      this._fileExtensionToSizeThreshold = _fileExtensionToSizeThreshold;
      this.maximumFileSizeThresholdWarnings = maximumFileSizeThresholdWarnings;
      val `$this$map$iv`: java.util.Map = this._fileExtensionToSizeThreshold;
      val `destination$iv$iv`: java.util.Collection = new ArrayList(this._fileExtensionToSizeThreshold.size());

      for (Entry item$iv$iv : $this$map$iv.entrySet()) {
         val k: java.lang.String = `item$iv$iv`.getKey() as java.lang.String;
         val v: Int = (`item$iv$iv`.getValue() as java.lang.Number).intValue();
         val var29: java.lang.Iterable = StringsKt.split$default(k, new java.lang.String[]{","}, false, 0, 6, null);
         val `destination$iv$ivx`: java.util.Collection = new ArrayList(CollectionsKt.collectionSizeOrDefault(var29, 10));

         for (Object item$iv$ivx : $this$map$iv) {
            `destination$iv$ivx`.add(TuplesKt.to(`item$iv$ivx` as java.lang.String, v));
         }

         `destination$iv$iv`.add(`destination$iv$ivx` as java.util.List);
      }

      this.fileExtensionToSizeThreshold = MapsKt.toMap(CollectionsKt.flatten(`destination$iv$iv` as java.util.List));
   }

   public fun getSizeThreshold(extension: String): Int? {
      var var10000: Int = this.fileExtensionToSizeThreshold.get(extension);
      if (var10000 == null) {
         var10000 = this.fileExtensionToSizeThreshold.get("*");
      }

      return if (var10000 != null) (if (var10000.intValue() > 0) var10000 else null) else null;
   }

   public fun fileSizeThresholdExceeded(extension: String, fileSize: Long): Boolean {
      val var10000: Int = this.getSizeThreshold(extension);
      if (var10000 != null) {
         return fileSize > var10000.intValue();
      } else {
         return false;
      }
   }

   public operator fun component1(): Int {
      return this.cancelAnalysisInErrorCount;
   }

   public operator fun component2(): Int {
      return this.largeFileSize;
   }

   public operator fun component3(): Int {
      return this.largeFileSemaphorePermits;
   }

   private operator fun component4(): Map<String, Int> {
      return this._fileExtensionToSizeThreshold;
   }

   public operator fun component5(): Int {
      return this.maximumFileSizeThresholdWarnings;
   }

   public fun copy(
      cancelAnalysisInErrorCount: Int = this.cancelAnalysisInErrorCount,
      largeFileSize: Int = this.largeFileSize,
      largeFileSemaphorePermits: Int = this.largeFileSemaphorePermits,
      _fileExtensionToSizeThreshold: Map<String, Int> = this._fileExtensionToSizeThreshold,
      maximumFileSizeThresholdWarnings: Int = this.maximumFileSizeThresholdWarnings
   ): PreAnalysisConfig {
      return new PreAnalysisConfig(
         cancelAnalysisInErrorCount, largeFileSize, largeFileSemaphorePermits, _fileExtensionToSizeThreshold, maximumFileSizeThresholdWarnings
      );
   }

   public override fun toString(): String {
      return "PreAnalysisConfig(cancelAnalysisInErrorCount=${this.cancelAnalysisInErrorCount}, largeFileSize=${this.largeFileSize}, largeFileSemaphorePermits=${this.largeFileSemaphorePermits}, _fileExtensionToSizeThreshold=${this._fileExtensionToSizeThreshold}, maximumFileSizeThresholdWarnings=${this.maximumFileSizeThresholdWarnings})";
   }

   public override fun hashCode(): Int {
      return (
               (
                        (Integer.hashCode(this.cancelAnalysisInErrorCount) * 31 + Integer.hashCode(this.largeFileSize)) * 31
                           + Integer.hashCode(this.largeFileSemaphorePermits)
                     )
                     * 31
                  + this._fileExtensionToSizeThreshold.hashCode()
            )
            * 31
         + Integer.hashCode(this.maximumFileSizeThresholdWarnings);
   }

   public override operator fun equals(other: Any?): Boolean {
      if (this === other) {
         return true;
      } else if (other !is PreAnalysisConfig) {
         return false;
      } else {
         val var2: PreAnalysisConfig = other as PreAnalysisConfig;
         if (this.cancelAnalysisInErrorCount != (other as PreAnalysisConfig).cancelAnalysisInErrorCount) {
            return false;
         } else if (this.largeFileSize != var2.largeFileSize) {
            return false;
         } else if (this.largeFileSemaphorePermits != var2.largeFileSemaphorePermits) {
            return false;
         } else if (!(this._fileExtensionToSizeThreshold == var2._fileExtensionToSizeThreshold)) {
            return false;
         } else {
            return this.maximumFileSizeThresholdWarnings == var2.maximumFileSizeThresholdWarnings;
         }
      }
   }

   fun PreAnalysisConfig() {
      this(0, 0, 0, null, 0, 31, null);
   }

   public companion object {
      public fun serializer(): KSerializer<PreAnalysisConfig> {
         return PreAnalysisConfig.$serializer.INSTANCE as KSerializer<PreAnalysisConfig>;
      }
   }
}
