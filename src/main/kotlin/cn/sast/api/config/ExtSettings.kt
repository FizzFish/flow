package cn.sast.api.config

import kotlin.properties.ReadWriteProperty
import org.utbot.common.AbstractSettings

public object ExtSettings : AbstractSettings(ExtSettingsKt.access$getLogger$p(), "corax.settings.path", ExtSettingsKt.access$getDefaultSettingsPath$p()) {
    public var dataFlowIteratorCountForAppClasses: Int by getIntProperty(12, 1, Int.MAX_VALUE)
    public var dataFlowIteratorCountForLibClasses: Int by getIntProperty(8, 1, Int.MAX_VALUE)
    public var dataFlowIteratorIsFixPointSizeLimit: Int by getIntProperty(4, 1, Int.MAX_VALUE)
    public var dataFlowMethodUnitsSizeLimit: Int by getIntProperty(1000, -1, Int.MAX_VALUE)
    public var dataFlowCacheExpireAfterAccess: Long by getLongProperty(30000L, 1L, Long.MAX_VALUE)
    public var dataFlowCacheMaximumWeight: Long by getLongProperty(10000L, 1L, Long.MAX_VALUE)
    public var dataFlowCacheMaximumSizeFactor: Double by getProperty(5.0, Triple(1.0E-4, Double.MAX_VALUE, TODO("FIXME — unrepresentable value")), TODO("FIXME — unrepresentable value"))
    public var calleeDepChainMaxNumForLibClassesInInterProceduraldataFlow: Int by getIntProperty(5, -1, Int.MAX_VALUE)
    public var dataFlowInterProceduralCalleeDepChainMaxNum: Long by getLongProperty(30L, -1L, Long.MAX_VALUE)
    public var dataFlowInterProceduralCalleeTimeOut: Int by getIntProperty(30000, -1, Int.MAX_VALUE)
    public var dataFlowResolveTargetsMaxNum: Long by getLongProperty(8L, -1L, Long.MAX_VALUE)
    public var dataFlowResultPathOnlyStmt: Boolean by getBooleanProperty(true)
    public var enableProcessBar: Boolean by getBooleanProperty(true)
    public var showMetadata: Boolean by getBooleanProperty(true)
    public var tabSize: Int by getIntProperty(4)
    public var dumpCompleteDotCg: Boolean by getBooleanProperty(false)
    public var prettyPrintJsonReport: Boolean by getBooleanProperty(true)
    public var prettyPrintPlistReport: Boolean by getBooleanProperty(false)
    public var sqliteJournalMode: String by getStringProperty("WAL")
    public var jdCoreDecompileTimeOut: Int by getIntProperty(20000, -1, Int.MAX_VALUE)
    public var skip_large_class_by_maximum_methods: Int by getIntProperty(2000, -1, Int.MAX_VALUE)
    public var skip_large_class_by_maximum_fields: Int by getIntProperty(2000, -1, Int.MAX_VALUE)
    public var castNeverFailsOfPhantomClass: Boolean by getBooleanProperty(false)
    public var printAliasInfo: Boolean by getBooleanProperty(false)
    public var UseRoaringPointsToSet: Boolean by getBooleanProperty(false)
    public var hashVersion: Int by getIntProperty(2)

    public fun defaultSettingsPath(): String {
        return ExtSettingsKt.access$getDefaultSettingsPath$p()
    }

    @JvmStatic
    public fun getPath(): String {
        return System.getProperty("corax.settings.path") ?: ExtSettingsKt.access$getDefaultSettingsPath$p()
    }

    @JvmStatic
    fun `_init_$lambda$0`(): Any {
        return "ExtSettingsPath: ${getPath()}"
    }

    @JvmStatic
    fun init() {
        ExtSettingsKt.access$getLogger$p().info(::`_init_$lambda$0`)
    }
}