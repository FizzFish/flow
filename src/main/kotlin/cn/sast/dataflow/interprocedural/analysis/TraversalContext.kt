package cn.sast.dataflow.interprocedural.analysis

import soot.jimple.DefinitionStmt
import soot.jimple.ReturnStmt
import soot.jimple.Stmt
import soot.jimple.SwitchStmt
import soot.jimple.internal.JAssignStmt
import soot.jimple.internal.JIdentityStmt
import soot.jimple.internal.JIfStmt
import soot.jimple.internal.JInvokeStmt
import soot.jimple.internal.JRetStmt
import soot.jimple.internal.JReturnStmt
import soot.jimple.internal.JReturnVoidStmt
import soot.jimple.internal.JThrowStmt

public interface TraversalContext<V, A> {
   public val voidValue: Any

   public abstract fun traverseAssignStmt(current: JAssignStmt) {
   }

   public abstract fun traverseIdentityStmt(current: JIdentityStmt) {
   }

   public abstract fun traverseIfStmt(current: JIfStmt) {
   }

   public abstract fun traverseInvokeStmt(current: JInvokeStmt) {
   }

   public abstract fun traverseSwitchStmt(current: SwitchStmt) {
   }

   public abstract fun traverseThrowStmt(current: JThrowStmt) {
   }

   public abstract fun processResult(current: MethodResult<Any>) {
   }

   public abstract fun symbolicSuccess(stmt: ReturnStmt): MethodResult<Any> {
   }

   public abstract fun offerState(state: Any) {
   }

   public open fun traverseStmt(current: Stmt) {
   }

   // $VF: Class flags could not be determined
   internal class DefaultImpls {
      @JvmStatic
      fun <V, A> traverseStmt(`$this`: TraversalContext<V, A>, current: Stmt) {
         if (current is JAssignStmt) {
            `$this`.traverseAssignStmt(current as JAssignStmt);
         } else if (current is JIdentityStmt) {
            `$this`.traverseIdentityStmt(current as JIdentityStmt);
         } else if (current is JIfStmt) {
            `$this`.traverseIfStmt(current as JIfStmt);
         } else if (current is JInvokeStmt) {
            `$this`.traverseInvokeStmt(current as JInvokeStmt);
         } else if (current is SwitchStmt) {
            `$this`.traverseSwitchStmt(current as SwitchStmt);
         } else if (current is JReturnStmt) {
            `$this`.processResult(`$this`.symbolicSuccess(current as ReturnStmt));
         } else if (current is JReturnVoidStmt) {
            `$this`.processResult(new SymbolicSuccess<>(`$this`.getVoidValue()));
         } else {
            if (current is JRetStmt) {
               throw new IllegalStateException(("This one should be already removed by Soot: $current").toString());
            }

            if (current !is JThrowStmt) {
               if (current is DefinitionStmt) {
                  throw new NotImplementedError("An operation is not implemented: ${java.lang.String.valueOf(current)}");
               }

               throw new IllegalStateException(("Unsupported: ${current.getClass()::class}").toString());
            }

            `$this`.traverseThrowStmt(current as JThrowStmt);
         }
      }
   }
}
