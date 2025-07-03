package cn.sast.framework.report.sqldelight.coraxframework

import app.cash.sqldelight.db.SqlDriver
import app.cash.sqldelight.db.SqlSchema
import app.cash.sqldelight.db.QueryResult.Value
import cn.sast.framework.report.sqldelight.Database
import kotlin.reflect.KClass

internal val schema: SqlSchema<Value<Unit>>
    internal get() {
        return DatabaseImpl.Schema.INSTANCE
    }

internal fun KClass<Database>.newInstance(driver: SqlDriver): Database {
    return DatabaseImpl(driver)
}