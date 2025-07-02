package cn.sast.dataflow.analysis.constant

import java.util.HashMap
import java.util.HashSet
import java.util.Objects
import java.util.function.Function
import soot.Local
import soot.Value
import soot.jimple.AddExpr
import soot.jimple.BinopExpr
import soot.jimple.DivExpr
import soot.jimple.EqExpr
import soot.jimple.GeExpr
import soot.jimple.GtExpr
import soot.jimple.IntConstant
import soot.jimple.LeExpr
import soot.jimple.LtExpr
import soot.jimple.MulExpr
import soot.jimple.NeExpr
import soot.jimple.SubExpr

public class FlowMap @JvmOverloads  public constructor(delegateMap: MutableMap<Local, CPValue> = (new HashMap()) as java.util.Map) {
   public final var delegateMap: MutableMap<Local, CPValue>
      internal set

   init {
      this.delegateMap = delegateMap;
   }

   public operator fun get(local: Local): CPValue {
      val var3: Any = this.delegateMap.computeIfAbsent(local, new Function(FlowMap::get$lambda$0) {
         {
            this.function = function;
         }
      });
      return var3 as CPValue;
   }

   public fun put(local: Local, value: CPValue): CPValue? {
      return this.delegateMap.put(local, value);
   }

   public fun keySet(): Set<Local> {
      return this.delegateMap.keySet();
   }

   public fun copyFrom(flowMap: FlowMap): Boolean {
      this.delegateMap.putAll(flowMap.delegateMap);
      return flowMap.delegateMap == this.delegateMap;
   }

   public fun computeValue(sootValue: Value): CPValue {
      if (sootValue is Local) {
         return this.get(sootValue as Local);
      } else if (sootValue is IntConstant) {
         return CPValue.Companion.makeConstant((sootValue as IntConstant).value);
      } else if (sootValue is BinopExpr) {
         val op1: Value = (sootValue as BinopExpr).getOp1();
         val op1Val: CPValue = this.computeValue(op1);
         val op2: Value = (sootValue as BinopExpr).getOp2();
         val op2Val: CPValue = this.computeValue(op2);
         if (op1Val === CPValue.Companion.getUndef() && op2Val === CPValue.Companion.getUndef()) {
            return CPValue.Companion.getUndef();
         } else if (op1Val === CPValue.Companion.getUndef() || op2Val === CPValue.Companion.getUndef()) {
            return CPValue.Companion.getNac();
         } else if (op1Val != CPValue.Companion.getNac() && op2Val != CPValue.Companion.getNac()) {
            try {
               return if (sootValue as BinopExpr is AddExpr)
                  CPValue.Companion.makeConstant(op1Val.value() + op2Val.value())
                  else
                  (
                     if (sootValue as BinopExpr is SubExpr)
                        CPValue.Companion.makeConstant(op1Val.value() - op2Val.value())
                        else
                        (
                           if (sootValue as BinopExpr is MulExpr)
                              CPValue.Companion.makeConstant(op1Val.value() * op2Val.value())
                              else
                              (
                                 if (sootValue as BinopExpr is DivExpr)
                                    CPValue.Companion.makeConstant(op1Val.value() / op2Val.value())
                                    else
                                    (
                                       if (sootValue as BinopExpr is EqExpr)
                                          CPValue.Companion.makeConstant(op1Val.value() == op2Val.value())
                                          else
                                          (
                                             if (sootValue as BinopExpr is NeExpr)
                                                CPValue.Companion.makeConstant(op1Val.value() != op2Val.value())
                                                else
                                                (
                                                   if (sootValue as BinopExpr is GeExpr)
                                                      CPValue.Companion.makeConstant(op1Val.value() >= op2Val.value())
                                                      else
                                                      (
                                                         if (sootValue as BinopExpr is GtExpr)
                                                            CPValue.Companion.makeConstant(op1Val.value() > op2Val.value())
                                                            else
                                                            (
                                                               if (sootValue as BinopExpr is LeExpr)
                                                                  CPValue.Companion.makeConstant(op1Val.value() <= op2Val.value())
                                                                  else
                                                                  (
                                                                     if (sootValue as BinopExpr is LtExpr)
                                                                        CPValue.Companion.makeConstant(op1Val.value() < op2Val.value())
                                                                        else
                                                                        CPValue.Companion.getNac()
                                                                  )
                                                            )
                                                      )
                                                )
                                          )
                                    )
                              )
                        )
                  );
            } catch (var9: ArithmeticException) {
               return CPValue.Companion.getNac();
            }
         } else {
            return CPValue.Companion.getNac();
         }
      } else {
         return CPValue.Companion.getNac();
      }
   }

   public override operator fun equals(other: Any?): Boolean {
      label13:
      if (this === other) {
         return true;
      } else {
         return other != null && this.getClass() == other.getClass() && this.delegateMap == (other as FlowMap).delegateMap;
      }
   }

   public override fun hashCode(): Int {
      return Objects.hash(this.delegateMap);
   }

   public override fun toString(): String {
      return this.delegateMap.toString();
   }

   @JvmOverloads
   fun FlowMap() {
      this(null, 1, null);
   }

   @JvmStatic
   fun `get$lambda$0`(l: Local): CPValue {
      return CPValue.Companion.getUndef();
   }

   public companion object {
      public fun meet(map1: FlowMap, map2: FlowMap): FlowMap {
         val resultMap: FlowMap = new FlowMap(null, 1, null);
         val localSet: java.util.Set = new HashSet();
         localSet.addAll(map1.keySet());
         localSet.addAll(map2.keySet());

         for (Local local : localSet) {
            resultMap.put(local, CPValue.Companion.meetValue(map1.get(local), map2.get(local)));
         }

         return resultMap;
      }
   }
}
