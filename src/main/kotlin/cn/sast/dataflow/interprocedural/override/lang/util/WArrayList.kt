package cn.sast.dataflow.interprocedural.override.lang.util

import cn.sast.dataflow.interprocedural.analysis.*
import cn.sast.dataflow.interprocedural.check.OverrideModel
import cn.sast.dataflow.interprocedural.check.callback.CalleeCBImpl
import cn.sast.dataflow.interprocedural.check.callback.CallerSiteCBImpl
import kotlinx.collections.immutable.persistentListOf
import soot.G
import soot.IntType
import soot.jimple.IntConstant

/**
 * 简化版 ArrayList Hook，实现 add / get / clear / remove。
 */
class WArrayList : SummaryHandlePackage<IValue> {

   private val intType: IntType = G.v().soot_IntType()

   /** 注册所有 hook */
   override fun ACheckCallAnalysis.register() {
      // new ArrayList()
      listOf(
         "<java.util.ArrayList: void <init>()>",
         "<java.util.ArrayList: void <init>(int)>"
      ).forEach { sig ->
         evalCallAtCaller(sig) { eval ->
            eval.hf.resolveOp(eval.env, eval.`this`)
               .resolve { ev, _, (self) ->
                  eval.out.setValueData(eval.env, self.value, OverrideModel.ArrayList,
                     ListSpace(persistentListOf(), null))
                  true
               }
         }
      }

      // clear()
      evalCallAtCaller("<java.util.ArrayList: void clear()>") { eval ->
         if (!eval.`this`.isSingle()) { eval.isEvalAble = false; return@evalCallAtCaller }
         eval.hf.resolveOp(eval.env, eval.`this`)
            .resolve { ev, _, (self) ->
               eval.out.setValueData(eval.env, self.value, OverrideModel.ArrayList,
                  ListSpace(persistentListOf(), null))
               true
            }
      }

      // get(int)
      evalCallAtCaller("<java.util.ArrayList: java.lang.Object get(int)>") { eval ->
         val listVal = eval.`this`
         val idxVal  = eval.arg(0)
         if (!listVal.isSingle()) { eval.isEvalAble = false; return@evalCallAtCaller }

         val calc = eval.hf.resolveOp(eval.env, listVal, idxVal)
         calc.resolve { ev, res, (self, key) ->
            val data = eval.out.getValueData(self.value, OverrideModel.ArrayList) as? ListSpace ?: return@resolve false
            data[key.value as IntConstant?]?.let(res::add) ?: return@resolve false
            true
         }
         if (calc.isFullySimplified())
            eval.setReturn(calc.res.build())
         else eval.isEvalAble = false
      }

      // add(E)
      evalCall("<java.util.ArrayList: boolean add(java.lang.Object)>") { eval ->
         val self  = eval.`this`; val value = eval.arg(0)
         eval.hf.resolveOp(eval.env, self).resolve { _, res, (arr) ->
            val builder = (eval.out.getValueData(arr.value, OverrideModel.ArrayList) as? ListSpace)
               ?.builder() ?: ListSpaceBuilder(persistentListOf<IHeapValues<IValue>>().builder())
            builder.add(value)
            eval.out.setValueData(eval.env, arr.value, OverrideModel.ArrayList, builder.build())
            res.add(eval.hf.single(eval.hf.toConstVal(true)))
            true
         }
         eval.setReturn(eval.hf.single(eval.hf.toConstVal(true)))
      }

      // remove(int)
      evalCallAtCaller("<java.util.ArrayList: java.lang.Object remove(int)>") { eval ->
         val listVal = eval.`this`
         val idxVal  = eval.arg(0)
         if (!listVal.isSingle()) { eval.isEvalAble = false; return@evalCallAtCaller }
         val calc = eval.hf.resolveOp(eval.env, listVal, idxVal)
         calc.resolve { _, res, (self, key) ->
            val array = eval.out.getValueData(self.value, OverrideModel.Arra
