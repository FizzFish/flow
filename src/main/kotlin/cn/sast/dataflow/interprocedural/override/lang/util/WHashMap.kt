package cn.sast.dataflow.interprocedural.override.lang.util

import cn.sast.dataflow.interprocedural.analysis.*
import cn.sast.dataflow.interprocedural.check.OverrideModel
import cn.sast.dataflow.interprocedural.check.callback.CalleeCBImpl
import cn.sast.dataflow.interprocedural.check.callback.CallerSiteCBImpl
import kotlinx.collections.immutable.persistentHashMapOf
import soot.G
import soot.IntType
import soot.jimple.IntConstant

/**
 * HashMap hook：reimplements put / get / clear / getOrDefault。
 */
class WHashMap : SummaryHandlePackage<IValue> {

   /** Map#get 的公共逻辑 */
   private fun mapGetModel(ana: ACheckCallAnalysis, mapData: IData<IValue>, key: IValue): IHeapValues<IValue>? {
      if (FactValuesKt.isNull(key) == true) return null
      if (key.type != ana.hf.vg.STRING_TYPE) return null
      val str = FactValuesKt.getStringValue(key, false) ?: return null
      val idx = kotlin.math.abs(str.hashCode())
      return (mapData as ArraySpace).get(ana.hf, idx)
   }

   override fun ACheckCallAnalysis.register() {
      // new HashMap()
      listOf(
         "<java.util.HashMap: void <init>()>",
         "<java.util.HashMap: void <init>(int)>"
      ).forEach { sig ->
         evalCallAtCaller(sig) { eval ->
            eval.hf.resolveOp(eval.env, eval.arg(-1)).resolve { _, _, (self) ->
               eval.out.setValueData(
                  eval.env, self.value, OverrideModel.HashMap,
                  ArraySpace.v(
                     eval.hf, eval.env,
                     persistentHashMapOf(), eval.hf.empty(),
                     eval.hf.vg.OBJ_ARRAY_TYPE,
                     eval.hf.push(eval.env,
                        eval.hf.newSummaryVal(eval.env, G.v().soot_IntType() as IntType, "mapSize")
                     ).popHV()
                  )
               )
               true
            }
         }
      }

      // clear()
      evalCallAtCaller("<java.util.HashMap: void clear()>") { eval ->
         if (!eval.`this`.isSingle()) { eval.isEvalAble = false; return@evalCallAtCaller }
         eval.hf.resolveOp(eval.env, eval.`this`).resolve { _, _, (self) ->
            eval.out.setValueData(eval.env, self.value, OverrideModel.HashMap,
               ArraySpace.v(eval.hf, eval.env, persistentHashMapOf(),
                  eval.hf.empty(), eval.hf.vg.OBJ_ARRAY_TYPE,
                  eval.hf.push(eval.env,
                     eval.hf.newSummaryVal(eval.env, G.v().soot_IntType() as IntType, "mapSize"))
                     .popHV())
            )
            true
         }
      }

      // get(Object)
      evalCallAtCaller("<java.util.HashMap: java.lang.Object get(java.lang.Object)>") { eval ->
         val map = eval.`this`
         if (!map.isSingle()) { eval.isEvalAble = false; return@evalCallAtCaller }
         val calc = eval.hf.resolveOp(eval.env, map, eval.arg(0))
         calc.resolve { _, res, (self, key) ->
            mapGetModel(this@register, eval.out.getValueData(self.value, OverrideModel.HashMap) ?: return@resolve false, key.value)
               ?.let(res::add) ?: return@resolve false
            true
         }
         if (calc.isFullySimplified() && !calc.res.isEmpty())
            eval.setReturn(calc.res.build())
         else eval.isEvalAble = false
      }

      // getOrDefault
      evalCallAtCaller("<java.util.HashMap: java.lang.Object getOrDefault(java.lang.Object,java.lang.Object)>") { eval ->
         val map = eval.`this`
         if (!map.isSingle()) { eval.isEvalAble = false; return@evalCallAtCaller }
         val calc = eval.hf.resolveOp(eval.env, map, eval.arg(0), eval.arg(1))
         calc.resolve { _, res, (self, key, defaultV) ->
            mapGetModel(this@register, eval.out.getValueData(self.value, OverrideModel.HashMap) ?: return@resolve false, key.value)
               ?.let(res::add) ?: res.add(defaultV.pop())
            true
         }
         if (calc.isFullySimplified())
            eval.setReturn(calc.res.build())
         else eval.isEvalAble = false
      }

      // put(K,V)
      evalCall("<java.util.HashMap: java.lang.Object put(java.lang.Object,java.lang.Object)>") { eval ->
         val map = eval.`this`
         val key = eval.arg(0)
         val value = eval.arg(1)
         if (!map.isSingle()) { eval.isEvalAble = false; return@evalCall }
         val calc = eval.hf.resolveOp(eval.env, map, key)
         calc.resolve { _, res, (self, k) ->
            val ori = eval.out.getValueData(self.value, OverrideModel.HashMap) as? ArraySpace
               ?: return@resolve false
            val builder = ori.builder()
            val kStr = FactValuesKt.getStringValue(k.value, false)
            val idx = kStr?.hashCode()?.let { kotlin.math.abs(it) }
            builder.set(eval.hf, eval.env, idx, value, true)
            eval.out.setValueData(eval.env, self.value, OverrideModel.HashMap, builder.build())
            res.add(ori.getElement(eval.hf))
            true
         }
         eval.setReturn(calc.res.build())
      }
   }

   companion object { fun v() = WHashMap() }
}
