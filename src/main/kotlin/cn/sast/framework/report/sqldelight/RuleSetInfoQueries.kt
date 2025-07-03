package cn.sast.framework.report.sqldelight

import app.cash.sqldelight.Query
import app.cash.sqldelight.QueryKt
import app.cash.sqldelight.TransacterImpl
import app.cash.sqldelight.db.SqlCursor
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlPreparedStatement
import kotlin.jvm.functions.Function1
import kotlin.jvm.functions.Function8

public class RuleSetInfoQueries(driver: SqlDriver) : TransacterImpl(driver) {
    public fun <T : Any> selectAll(mapper: (String, String, String?, String?, String?, Long?, String, String) -> T): Query<T> {
        return QueryKt.Query(
            1731641704,
            arrayOf("RuleSetInfo"),
            this.getDriver(),
            "RuleSetInfo.sq",
            "selectAll",
            "SELECT RuleSetInfo.name, RuleSetInfo.language, RuleSetInfo.description, RuleSetInfo.prefix, RuleSetInfo.id_pattern, RuleSetInfo.section_level, RuleSetInfo.version, RuleSetInfo.revision\nFROM RuleSetInfo",
            RuleSetInfoQueries::selectAll$lambda$0
        )
    }

    public fun selectAll(): Query<RuleSetInfo> {
        return this.selectAll(RuleSetInfoQueries::selectAll$lambda$1)
    }

    public fun insert(RuleSetInfo: RuleSetInfo) {
        this.getDriver()
            .execute(
                1006841654,
                "INSERT OR IGNORE INTO RuleSetInfo (name, language, description, prefix, id_pattern, section_level, version, revision)\nVALUES (?, ?, ?, ?, ?, ?, ?, ?)",
                8,
                RuleSetInfoQueries::insert$lambda$2
            )
        this.notifyQueries(1006841654, RuleSetInfoQueries::insert$lambda$3)
    }

    @JvmStatic
    fun `selectAll$lambda$0`(`$mapper`: Function8<*, *, *, *, *, *, *, *, *>, cursor: SqlCursor): Any {
        val var10001: String = cursor.getString(0)!!
        val var10002: String = cursor.getString(1)!!
        val var10003: String? = cursor.getString(2)
        val var10004: String? = cursor.getString(3)
        val var10005: String? = cursor.getString(4)
        val var10006: Long? = cursor.getLong(5)
        val var10007: String = cursor.getString(6)!!
        val var10008: String = cursor.getString(7)!!
        return `$mapper`.invoke(var10001, var10002, var10003, var10004, var10005, var10006, var10007, var10008)
    }

    @JvmStatic
    fun `selectAll$lambda$1`(
        name: String,
        language: String,
        description: String?,
        prefix: String?,
        id_pattern: String?,
        section_level: Long?,
        version: String,
        revision: String
    ): RuleSetInfo {
        return RuleSetInfo(name, language, description, prefix, id_pattern, section_level, version, revision)
    }

    @JvmStatic
    fun `insert$lambda$2`(`$RuleSetInfo`: RuleSetInfo, `$this$execute`: SqlPreparedStatement) {
        `$this$execute`.bindString(0, `$RuleSetInfo`.getName())
        `$this$execute`.bindString(1, `$RuleSetInfo`.getLanguage())
        `$this$execute`.bindString(2, `$RuleSetInfo`.getDescription())
        `$this$execute`.bindString(3, `$RuleSetInfo`.getPrefix())
        `$this$execute`.bindString(4, `$RuleSetInfo`.getId_pattern())
        `$this$execute`.bindLong(5, `$RuleSetInfo`.getSection_level())
        `$this$execute`.bindString(6, `$RuleSetInfo`.getVersion())
        `$this$execute`.bindString(7, `$RuleSetInfo`.getRevision())
    }

    @JvmStatic
    fun `insert$lambda$3`(emit: Function1<*, *>) {
        emit.invoke("RuleSetInfo")
    }
}