package cn.sast.dataflow.interprocedural.analysis

/**
 * 对象重映射接口：在克隆阶段将旧对象替换为新对象。
 */
interface IReNew<V> {

   /** 若需要替换 [old]，返回新对象，否则返回 `null` */
   fun checkNeedReplace(old: V): V? = null

   /** 伴随值版本 */
   fun checkNeedReplace(c: CompanionV<V>): CompanionV<V>? = null

   /**
    * 为某个子级派生一个新的 *上下文敏感* 替换器
    * （默认返回 `this` 本身）
    */
   fun context(value: Any): IReNew<V> = this

   /* ---------- 默认静态代理（兼容 Java 调用） ---------- */
   object DefaultImpls {
      @JvmStatic
      fun <V> checkNeedReplace(self: IReNew<V>, old: V): V? =
         self.checkNeedReplace(old)

      @JvmStatic
      fun <V> checkNeedReplace(self: IReNew<V>, c: CompanionV<V>): CompanionV<V>? =
         self.checkNeedReplace(c)

      @JvmStatic
      fun <V> context(self: IReNew<V>, value: Any): IReNew<V> =
         self.context(value)
   }
}
