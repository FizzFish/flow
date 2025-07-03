package cn.sast.framework.report.sqldelight

import app.cash.sqldelight.Query
import app.cash.sqldelight.QueryKt
import app.cash.sqldelight.TransacterImpl
import app.cash.sqldelight.db.SqlCursor
import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlPreparedStatement
import kotlin.jvm.functions.Function1
import kotlin.jvm.functions.Function3

public class NoteExtQueries(driver: SqlDriver) : TransacterImpl(driver) {
    public fun <T : Any> selectAll(mapper: (Long, String, String) -> T): Query<T> {
        return QueryKt.Query(
            313069709,
            arrayOf("NoteExt"),
            this.getDriver(),
            "NoteExt.sq",
            "selectAll",
            "SELECT NoteExt.__note_id, NoteExt.attr_name, NoteExt.attr_value\nFROM NoteExt",
            NoteExtQueries::selectAll$lambda$0
        )
    }

    public fun selectAll(): Query<NoteExt> {
        return this.selectAll(NoteExtQueries::selectAll$lambda$1)
    }

    public fun insert(noteExt: NoteExt) {
        this.getDriver()
            .execute(-1109476815, "INSERT OR IGNORE INTO NoteExt (__note_id, attr_name, attr_value)\nVALUES (?, ?, ?)", 3) {
                insert$lambda$2(noteExt, it)
            }
        this.notifyQueries(-1109476815) {
            insert$lambda$3(it)
        }
    }

    @JvmStatic
    private fun selectAll$lambda$0(mapper: Function3<Long, String, String, Any>, cursor: SqlCursor): Any {
        val var10001 = cursor.getLong(0)
        val var10002 = cursor.getString(1)
        val var10003 = cursor.getString(2)
        return mapper.invoke(var10001, var10002, var10003)
    }

    @JvmStatic
    private fun selectAll$lambda$1(__note_id: Long, attr_name: String, attr_value: String): NoteExt {
        return NoteExt(__note_id, attr_name, attr_value)
    }

    @JvmStatic
    private fun insert$lambda$2(noteExt: NoteExt, this$execute: SqlPreparedStatement) {
        this$execute.bindLong(0, noteExt.get__note_id())
        this$execute.bindString(1, noteExt.getAttr_name())
        this$execute.bindString(2, noteExt.getAttr_value())
    }

    @JvmStatic
    private fun insert$lambda$3(emit: Function1<String, Unit>) {
        emit.invoke("NoteExt")
    }
}