package cn.sast.framework.report.sqlite

import app.cash.sqldelight.Query.Listener
import app.cash.sqldelight.driver.jdbc.ConnectionManager
import app.cash.sqldelight.driver.jdbc.JdbcDriver
import app.cash.sqldelight.driver.jdbc.ConnectionManager.Transaction
import java.sql.Connection
import java.util.LinkedHashMap
import java.util.LinkedHashSet
import kotlin.jvm.internal.SourceDebugExtension

@SourceDebugExtension(["SMAP\nJdbcSqliteDriverDelegate.kt\nKotlin\n*S Kotlin\n*F\n+ 1 JdbcSqliteDriverDelegate.kt\ncn/sast/framework/report/sqlite/JdbcSqliteDriverDelegate\n+ 2 _Arrays.kt\nkotlin/collections/ArraysKt___ArraysKt\n+ 3 Maps.kt\nkotlin/collections/MapsKt__MapsKt\n+ 4 fake.kt\nkotlin/jvm/internal/FakeKt\n+ 5 _Collections.kt\nkotlin/collections/CollectionsKt___CollectionsKt\n*L\n1#1,49:1\n13409#2:50\n13410#2:58\n13409#2,2:59\n13409#2:61\n13410#2:63\n381#3,7:51\n1#4:62\n1863#5,2:64\n*S KotlinDebug\n*F\n+ 1 JdbcSqliteDriverDelegate.kt\ncn/sast/framework/report/sqlite/JdbcSqliteDriverDelegate\n*L\n17#1:50\n17#1:58\n25#1:59,2\n34#1:61\n34#1:63\n18#1:51,7\n36#1:64,2\n*E\n"])
open class JdbcSqliteDriverDelegate(
    protected val delegate: ConnectionManager
) : JdbcDriver, ConnectionManager {
    private val listeners = LinkedHashMap<String, MutableSet<Listener>>()

    open var transaction: Transaction? = null
        internal set

    override fun addListener(vararg queryKeys: String, listener: Listener) {
        synchronized(listeners) {
            for (key in queryKeys) {
                listeners.getOrPut(key) { LinkedHashSet() }.add(listener)
            }
        }
    }

    override fun removeListener(vararg queryKeys: String, listener: Listener) {
        synchronized(listeners) {
            for (key in queryKeys) {
                listeners[key]?.remove(listener)
            }
        }
    }

    override fun notifyListeners(vararg queryKeys: String) {
        val listenersToNotify = LinkedHashSet<Listener>()
        synchronized(listeners) {
            for (key in queryKeys) {
                listeners[key]?.let { listenersToNotify.addAll(it) }
            }
        }

        listenersToNotify.forEach { it.queryResultsChanged() }
    }

    override fun Connection.endTransaction() {
        delegate.endTransaction(this)
        delegate.setTransaction(null)
        closeConnection(this)
    }

    override fun close() {
        delegate.close()
    }

    override fun Connection.beginTransaction() {
        delegate.beginTransaction(this)
    }

    override fun Connection.rollbackTransaction() {
        delegate.rollbackTransaction(this)
    }

    override fun closeConnection(connection: Connection) {
        delegate.closeConnection(connection)
    }

    override fun getConnection(): Connection {
        return delegate.getConnection()
    }

    companion object {
        const val IN_MEMORY: String = TODO("FIXME — uninitialized constant")
    }
}