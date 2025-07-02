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
public open class JdbcSqliteDriverDelegate(delegate: ConnectionManager) : JdbcDriver, ConnectionManager {
   protected final val delegate: ConnectionManager
   private final val listeners: LinkedHashMap<String, MutableSet<Listener>>

   public open var transaction: Transaction?
      internal final set

   init {
      this.delegate = delegate;
      this.listeners = new LinkedHashMap<>();
   }

   public open fun addListener(vararg queryKeys: String, listener: Listener) {
      synchronized (this.listeners) {
         for (Object element$iv : queryKeys) {
            val `$this$getOrPut$iv`: java.util.Map = this.listeners;
            val `value$iv`: Any = this.listeners.get(`element$iv`);
            val var10000: Any;
            if (`value$iv` == null) {
               val var18: Any = new LinkedHashSet();
               `$this$getOrPut$iv`.put(`element$iv`, var18);
               var10000 = var18;
            } else {
               var10000 = `value$iv`;
            }

            (var10000 as java.util.Set).add(listener);
         }
      }
   }

   public open fun removeListener(vararg queryKeys: String, listener: Listener) {
      synchronized (this.listeners) {
         for (Object element$iv : queryKeys) {
            val var10000: java.util.Set = this.listeners.get(`element$iv`);
            if (var10000 != null) {
               var10000.remove(listener);
            }
         }
      }
   }

   public open fun notifyListeners(vararg queryKeys: String) {
      val listenersToNotify: LinkedHashSet = new LinkedHashSet();
      synchronized (this.listeners) {
         for (Object element$iv : queryKeys) {
            val var10000: java.util.Set = this.listeners.get(`element$iv`);
            if (var10000 != null) {
               listenersToNotify.addAll(var10000);
            }
         }
      }

      val `$this$forEach$iv`: java.lang.Iterable;
      for (Object element$ivx : $this$forEach$iv) {
         (`element$ivx` as Listener).queryResultsChanged();
      }
   }

   public open fun Connection.endTransaction() {
      this.delegate.endTransaction(`$this$endTransaction`);
      this.delegate.setTransaction(null);
      this.closeConnection(`$this$endTransaction`);
   }

   public open fun close() {
      this.delegate.close();
   }

   public open fun Connection.beginTransaction() {
      this.delegate.beginTransaction(`$this$beginTransaction`);
   }

   public open fun Connection.rollbackTransaction() {
      this.delegate.rollbackTransaction(`$this$rollbackTransaction`);
   }

   public open fun closeConnection(connection: Connection) {
      this.delegate.closeConnection(connection);
   }

   public open fun getConnection(): Connection {
      return this.delegate.getConnection();
   }

   public companion object {
      public const val IN_MEMORY: String
   }
}
