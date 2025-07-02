package cn.sast.api.config

import kotlin.properties.ReadWriteProperty
import org.utbot.common.AbstractSettings

public object ExtSettings : AbstractSettings(ExtSettingsKt.access$getLogger$p(), "corax.settings.path", ExtSettingsKt.access$getDefaultSettingsPath$p()) {
   public final var dataFlowIteratorCountForAppClasses: Int by INSTANCE.getIntProperty(12, 1, Integer.MAX_VALUE)
      .provideDelegate(INSTANCE, $$delegatedProperties[0]) as ReadWriteProperty
      public final get() {
         return (dataFlowIteratorCountForAppClasses$delegate.getValue(this, $$delegatedProperties[0]) as java.lang.Number).intValue();
      }

      public final set(<set-?>) {
         dataFlowIteratorCountForAppClasses$delegate.setValue(this, $$delegatedProperties[0], var1);
      }


   public final var dataFlowIteratorCountForLibClasses: Int by INSTANCE.getIntProperty(8, 1, Integer.MAX_VALUE)
      .provideDelegate(INSTANCE, $$delegatedProperties[1]) as ReadWriteProperty
      public final get() {
         return (dataFlowIteratorCountForLibClasses$delegate.getValue(this, $$delegatedProperties[1]) as java.lang.Number).intValue();
      }

      public final set(<set-?>) {
         dataFlowIteratorCountForLibClasses$delegate.setValue(this, $$delegatedProperties[1], var1);
      }


   public final var dataFlowIteratorIsFixPointSizeLimit: Int by INSTANCE.getIntProperty(4, 1, Integer.MAX_VALUE)
      .provideDelegate(INSTANCE, $$delegatedProperties[2]) as ReadWriteProperty
      public final get() {
         return (dataFlowIteratorIsFixPointSizeLimit$delegate.getValue(this, $$delegatedProperties[2]) as java.lang.Number).intValue();
      }

      public final set(<set-?>) {
         dataFlowIteratorIsFixPointSizeLimit$delegate.setValue(this, $$delegatedProperties[2], var1);
      }


   public final var dataFlowMethodUnitsSizeLimit: Int by INSTANCE.getIntProperty(1000, -1, Integer.MAX_VALUE)
      .provideDelegate(INSTANCE, $$delegatedProperties[3]) as ReadWriteProperty
      public final get() {
         return (dataFlowMethodUnitsSizeLimit$delegate.getValue(this, $$delegatedProperties[3]) as java.lang.Number).intValue();
      }

      public final set(<set-?>) {
         dataFlowMethodUnitsSizeLimit$delegate.setValue(this, $$delegatedProperties[3], var1);
      }


   public final var dataFlowCacheExpireAfterAccess: Long by INSTANCE.getLongProperty(30000L, 1L, java.lang.Long.MAX_VALUE)
      .provideDelegate(INSTANCE, $$delegatedProperties[4]) as ReadWriteProperty
      public final get() {
         return (dataFlowCacheExpireAfterAccess$delegate.getValue(this, $$delegatedProperties[4]) as java.lang.Number).longValue();
      }

      public final set(<set-?>) {
         dataFlowCacheExpireAfterAccess$delegate.setValue(this, $$delegatedProperties[4], var1);
      }


   public final var dataFlowCacheMaximumWeight: Long by INSTANCE.getLongProperty(10000L, 1L, java.lang.Long.MAX_VALUE)
      .provideDelegate(INSTANCE, $$delegatedProperties[5]) as ReadWriteProperty
      public final get() {
         return (dataFlowCacheMaximumWeight$delegate.getValue(this, $$delegatedProperties[5]) as java.lang.Number).longValue();
      }

      public final set(<set-?>) {
         dataFlowCacheMaximumWeight$delegate.setValue(this, $$delegatedProperties[5], var1);
      }


   public final var dataFlowCacheMaximumSizeFactor: Double by INSTANCE.getProperty(
         5.0, new Triple(1.0E-4, java.lang.Double.MAX_VALUE, <unrepresentable>.INSTANCE), <unrepresentable>.INSTANCE
      )
      .provideDelegate(INSTANCE, $$delegatedProperties[6]) as ReadWriteProperty
      public final get() {
         return (dataFlowCacheMaximumSizeFactor$delegate.getValue(this, $$delegatedProperties[6]) as java.lang.Number).doubleValue();
      }

      public final set(<set-?>) {
         dataFlowCacheMaximumSizeFactor$delegate.setValue(this, $$delegatedProperties[6], var1);
      }


   public final var calleeDepChainMaxNumForLibClassesInInterProceduraldataFlow: Int by INSTANCE.getIntProperty(5, -1, Integer.MAX_VALUE)
      .provideDelegate(INSTANCE, $$delegatedProperties[7]) as ReadWriteProperty
      public final get() {
         return (calleeDepChainMaxNumForLibClassesInInterProceduraldataFlow$delegate.getValue(this, $$delegatedProperties[7]) as java.lang.Number).intValue();
      }

      public final set(<set-?>) {
         calleeDepChainMaxNumForLibClassesInInterProceduraldataFlow$delegate.setValue(this, $$delegatedProperties[7], var1);
      }


   public final var dataFlowInterProceduralCalleeDepChainMaxNum: Long by INSTANCE.getLongProperty(30L, -1L, java.lang.Long.MAX_VALUE)
      .provideDelegate(INSTANCE, $$delegatedProperties[8]) as ReadWriteProperty
      public final get() {
         return (dataFlowInterProceduralCalleeDepChainMaxNum$delegate.getValue(this, $$delegatedProperties[8]) as java.lang.Number).longValue();
      }

      public final set(<set-?>) {
         dataFlowInterProceduralCalleeDepChainMaxNum$delegate.setValue(this, $$delegatedProperties[8], var1);
      }


   public final var dataFlowInterProceduralCalleeTimeOut: Int by INSTANCE.getIntProperty(30000, -1, Integer.MAX_VALUE)
      .provideDelegate(INSTANCE, $$delegatedProperties[9]) as ReadWriteProperty
      public final get() {
         return (dataFlowInterProceduralCalleeTimeOut$delegate.getValue(this, $$delegatedProperties[9]) as java.lang.Number).intValue();
      }

      public final set(<set-?>) {
         dataFlowInterProceduralCalleeTimeOut$delegate.setValue(this, $$delegatedProperties[9], var1);
      }


   public final var dataFlowResolveTargetsMaxNum: Long by INSTANCE.getLongProperty(8L, -1L, java.lang.Long.MAX_VALUE)
      .provideDelegate(INSTANCE, $$delegatedProperties[10]) as ReadWriteProperty
      public final get() {
         return (dataFlowResolveTargetsMaxNum$delegate.getValue(this, $$delegatedProperties[10]) as java.lang.Number).longValue();
      }

      public final set(<set-?>) {
         dataFlowResolveTargetsMaxNum$delegate.setValue(this, $$delegatedProperties[10], var1);
      }


   public final var dataFlowResultPathOnlyStmt: Boolean by INSTANCE.getBooleanProperty(true).provideDelegate(INSTANCE, $$delegatedProperties[11]) as ReadWriteProperty
      public final get() {
         return dataFlowResultPathOnlyStmt$delegate.getValue(this, $$delegatedProperties[11]) as java.lang.Boolean;
      }

      public final set(<set-?>) {
         dataFlowResultPathOnlyStmt$delegate.setValue(this, $$delegatedProperties[11], var1);
      }


   public final var enableProcessBar: Boolean by INSTANCE.getBooleanProperty(true).provideDelegate(INSTANCE, $$delegatedProperties[12]) as ReadWriteProperty
      public final get() {
         return enableProcessBar$delegate.getValue(this, $$delegatedProperties[12]) as java.lang.Boolean;
      }

      public final set(<set-?>) {
         enableProcessBar$delegate.setValue(this, $$delegatedProperties[12], var1);
      }


   public final var showMetadata: Boolean by INSTANCE.getBooleanProperty(true).provideDelegate(INSTANCE, $$delegatedProperties[13]) as ReadWriteProperty
      public final get() {
         return showMetadata$delegate.getValue(this, $$delegatedProperties[13]) as java.lang.Boolean;
      }

      public final set(<set-?>) {
         showMetadata$delegate.setValue(this, $$delegatedProperties[13], var1);
      }


   public final var tabSize: Int by INSTANCE.getIntProperty(4).provideDelegate(INSTANCE, $$delegatedProperties[14]) as ReadWriteProperty
      public final get() {
         return (tabSize$delegate.getValue(this, $$delegatedProperties[14]) as java.lang.Number).intValue();
      }

      public final set(<set-?>) {
         tabSize$delegate.setValue(this, $$delegatedProperties[14], var1);
      }


   public final var dumpCompleteDotCg: Boolean by INSTANCE.getBooleanProperty(false).provideDelegate(INSTANCE, $$delegatedProperties[15]) as ReadWriteProperty
      public final get() {
         return dumpCompleteDotCg$delegate.getValue(this, $$delegatedProperties[15]) as java.lang.Boolean;
      }

      public final set(<set-?>) {
         dumpCompleteDotCg$delegate.setValue(this, $$delegatedProperties[15], var1);
      }


   public final var prettyPrintJsonReport: Boolean by INSTANCE.getBooleanProperty(true).provideDelegate(INSTANCE, $$delegatedProperties[16]) as ReadWriteProperty
      public final get() {
         return prettyPrintJsonReport$delegate.getValue(this, $$delegatedProperties[16]) as java.lang.Boolean;
      }

      public final set(<set-?>) {
         prettyPrintJsonReport$delegate.setValue(this, $$delegatedProperties[16], var1);
      }


   public final var prettyPrintPlistReport: Boolean by INSTANCE.getBooleanProperty(false).provideDelegate(INSTANCE, $$delegatedProperties[17]) as ReadWriteProperty
      public final get() {
         return prettyPrintPlistReport$delegate.getValue(this, $$delegatedProperties[17]) as java.lang.Boolean;
      }

      public final set(<set-?>) {
         prettyPrintPlistReport$delegate.setValue(this, $$delegatedProperties[17], var1);
      }


   public final var sqliteJournalMode: String by INSTANCE.getStringProperty("WAL").provideDelegate(INSTANCE, $$delegatedProperties[18]) as ReadWriteProperty
      public final get() {
         return sqliteJournalMode$delegate.getValue(this, $$delegatedProperties[18]) as java.lang.String;
      }

      public final set(<set-?>) {
         sqliteJournalMode$delegate.setValue(this, $$delegatedProperties[18], var1);
      }


   public final var jdCoreDecompileTimeOut: Int by INSTANCE.getIntProperty(20000, -1, Integer.MAX_VALUE).provideDelegate(INSTANCE, $$delegatedProperties[19]) as ReadWriteProperty
      public final get() {
         return (jdCoreDecompileTimeOut$delegate.getValue(this, $$delegatedProperties[19]) as java.lang.Number).intValue();
      }

      public final set(<set-?>) {
         jdCoreDecompileTimeOut$delegate.setValue(this, $$delegatedProperties[19], var1);
      }


   public final var skip_large_class_by_maximum_methods: Int by INSTANCE.getIntProperty(2000, -1, Integer.MAX_VALUE)
      .provideDelegate(INSTANCE, $$delegatedProperties[20]) as ReadWriteProperty
      public final get() {
         return (skip_large_class_by_maximum_methods$delegate.getValue(this, $$delegatedProperties[20]) as java.lang.Number).intValue();
      }

      public final set(<set-?>) {
         skip_large_class_by_maximum_methods$delegate.setValue(this, $$delegatedProperties[20], var1);
      }


   public final var skip_large_class_by_maximum_fields: Int by INSTANCE.getIntProperty(2000, -1, Integer.MAX_VALUE)
      .provideDelegate(INSTANCE, $$delegatedProperties[21]) as ReadWriteProperty
      public final get() {
         return (skip_large_class_by_maximum_fields$delegate.getValue(this, $$delegatedProperties[21]) as java.lang.Number).intValue();
      }

      public final set(<set-?>) {
         skip_large_class_by_maximum_fields$delegate.setValue(this, $$delegatedProperties[21], var1);
      }


   public final var castNeverFailsOfPhantomClass: Boolean by INSTANCE.getBooleanProperty(false).provideDelegate(INSTANCE, $$delegatedProperties[22]) as ReadWriteProperty
      public final get() {
         return castNeverFailsOfPhantomClass$delegate.getValue(this, $$delegatedProperties[22]) as java.lang.Boolean;
      }

      public final set(<set-?>) {
         castNeverFailsOfPhantomClass$delegate.setValue(this, $$delegatedProperties[22], var1);
      }


   public final var printAliasInfo: Boolean by INSTANCE.getBooleanProperty(false).provideDelegate(INSTANCE, $$delegatedProperties[23]) as ReadWriteProperty
      public final get() {
         return printAliasInfo$delegate.getValue(this, $$delegatedProperties[23]) as java.lang.Boolean;
      }

      public final set(<set-?>) {
         printAliasInfo$delegate.setValue(this, $$delegatedProperties[23], var1);
      }


   public final var UseRoaringPointsToSet: Boolean by INSTANCE.getBooleanProperty(false).provideDelegate(INSTANCE, $$delegatedProperties[24]) as ReadWriteProperty
      public final get() {
         return UseRoaringPointsToSet$delegate.getValue(this, $$delegatedProperties[24]) as java.lang.Boolean;
      }

      public final set(<set-?>) {
         UseRoaringPointsToSet$delegate.setValue(this, $$delegatedProperties[24], var1);
      }


   public final var hashVersion: Int by INSTANCE.getIntProperty(2).provideDelegate(INSTANCE, $$delegatedProperties[25]) as ReadWriteProperty
      public final get() {
         return (hashVersion$delegate.getValue(this, $$delegatedProperties[25]) as java.lang.Number).intValue();
      }

      public final set(<set-?>) {
         hashVersion$delegate.setValue(this, $$delegatedProperties[25], var1);
      }


   public fun defaultSettingsPath(): String {
      return ExtSettingsKt.access$getDefaultSettingsPath$p();
   }

   @JvmStatic
   public fun getPath(): String {
      var var10000: java.lang.String = System.getProperty("corax.settings.path");
      if (var10000 == null) {
         var10000 = ExtSettingsKt.access$getDefaultSettingsPath$p();
      }

      return var10000;
   }

   @JvmStatic
   fun `_init_$lambda$0`(): Any {
      return "ExtSettingsPath: ${getPath()}";
   }

   @JvmStatic
   fun {
      ExtSettingsKt.access$getLogger$p().info(ExtSettings::_init_$lambda$0);
   }
}
