package cn.sast.dataflow.interprocedural.check.printer

import java.util.ArrayList
import soot.Local
import soot.Unit
import soot.UnitBox
import soot.UnitPrinter
import soot.Value
import soot.grimp.Grimp
import soot.grimp.internal.ExprBox
import soot.grimp.internal.GDynamicInvokeExpr
import soot.grimp.internal.GInterfaceInvokeExpr
import soot.grimp.internal.GSpecialInvokeExpr
import soot.grimp.internal.GStaticInvokeExpr
import soot.grimp.internal.GVirtualInvokeExpr
import soot.jimple.AbstractExprSwitch
import soot.jimple.AbstractStmtSwitch
import soot.jimple.AddExpr
import soot.jimple.AndExpr
import soot.jimple.ArrayRef
import soot.jimple.AssignStmt
import soot.jimple.BreakpointStmt
import soot.jimple.CastExpr
import soot.jimple.CmpExpr
import soot.jimple.CmpgExpr
import soot.jimple.CmplExpr
import soot.jimple.DivExpr
import soot.jimple.DynamicInvokeExpr
import soot.jimple.EnterMonitorStmt
import soot.jimple.EqExpr
import soot.jimple.ExitMonitorStmt
import soot.jimple.Expr
import soot.jimple.GeExpr
import soot.jimple.GotoStmt
import soot.jimple.GtExpr
import soot.jimple.IdentityStmt
import soot.jimple.IfStmt
import soot.jimple.InstanceFieldRef
import soot.jimple.InstanceOfExpr
import soot.jimple.IntConstant
import soot.jimple.InterfaceInvokeExpr
import soot.jimple.InvokeExpr
import soot.jimple.InvokeStmt
import soot.jimple.LeExpr
import soot.jimple.LengthExpr
import soot.jimple.LookupSwitchStmt
import soot.jimple.LtExpr
import soot.jimple.MulExpr
import soot.jimple.NeExpr
import soot.jimple.NegExpr
import soot.jimple.NewArrayExpr
import soot.jimple.NewExpr
import soot.jimple.NewMultiArrayExpr
import soot.jimple.NopStmt
import soot.jimple.OrExpr
import soot.jimple.RemExpr
import soot.jimple.ReturnStmt
import soot.jimple.ReturnVoidStmt
import soot.jimple.ShlExpr
import soot.jimple.ShrExpr
import soot.jimple.SpecialInvokeExpr
import soot.jimple.StaticInvokeExpr
import soot.jimple.Stmt
import soot.jimple.SubExpr
import soot.jimple.TableSwitchStmt
import soot.jimple.ThrowStmt
import soot.jimple.UshrExpr
import soot.jimple.VirtualInvokeExpr
import soot.jimple.XorExpr
import soot.tagkit.Tag
import soot.util.Switch

public open class GrimpInline : Grimp(null) {
   public open fun newExpr(value: Value): Value {
      if (value is Expr) {
         val var3: ExprBox = new ExprBox(IntConstant.v(0) as Value);
         value.apply(
            (
               new AbstractExprSwitch<Object>(var3, this) {
                  {
                     this.$returnedExpr = `$returnedExpr`;
                     this.this$0 = `$receiver`;
                  }

                  public void caseAddExpr(AddExpr v) {
                     val var10000: ExprBox = this.$returnedExpr;
                     val var10001: GrimpInline = this.this$0;
                     val var10002: GrimpInline = this.this$0;
                     val var10003: Value = v.getOp1();
                     val var2: Value = var10002.newExpr(var10003);
                     val var3: GrimpInline = this.this$0;
                     val var10004: Value = v.getOp2();
                     var10000.setValue(var10001.newAddExpr(var2, var3.newExpr(var10004)) as Value);
                  }

                  public void caseAndExpr(AndExpr v) {
                     val var10000: ExprBox = this.$returnedExpr;
                     val var10001: GrimpInline = this.this$0;
                     val var10002: GrimpInline = this.this$0;
                     val var10003: Value = v.getOp1();
                     val var2: Value = var10002.newExpr(var10003);
                     val var3: GrimpInline = this.this$0;
                     val var10004: Value = v.getOp2();
                     var10000.setValue(var10001.newAndExpr(var2, var3.newExpr(var10004)) as Value);
                  }

                  public void caseCmpExpr(CmpExpr v) {
                     val var10000: ExprBox = this.$returnedExpr;
                     val var10001: GrimpInline = this.this$0;
                     val var10002: GrimpInline = this.this$0;
                     val var10003: Value = v.getOp1();
                     val var2: Value = var10002.newExpr(var10003);
                     val var3: GrimpInline = this.this$0;
                     val var10004: Value = v.getOp2();
                     var10000.setValue(var10001.newCmpExpr(var2, var3.newExpr(var10004)) as Value);
                  }

                  public void caseCmpgExpr(CmpgExpr v) {
                     val var10000: ExprBox = this.$returnedExpr;
                     val var10001: GrimpInline = this.this$0;
                     val var10002: GrimpInline = this.this$0;
                     val var10003: Value = v.getOp1();
                     val var2: Value = var10002.newExpr(var10003);
                     val var3: GrimpInline = this.this$0;
                     val var10004: Value = v.getOp2();
                     var10000.setValue(var10001.newCmpgExpr(var2, var3.newExpr(var10004)) as Value);
                  }

                  public void caseCmplExpr(CmplExpr v) {
                     val var10000: ExprBox = this.$returnedExpr;
                     val var10001: GrimpInline = this.this$0;
                     val var10002: GrimpInline = this.this$0;
                     val var10003: Value = v.getOp1();
                     val var2: Value = var10002.newExpr(var10003);
                     val var3: GrimpInline = this.this$0;
                     val var10004: Value = v.getOp2();
                     var10000.setValue(var10001.newCmplExpr(var2, var3.newExpr(var10004)) as Value);
                  }

                  public void caseDivExpr(DivExpr v) {
                     val var10000: ExprBox = this.$returnedExpr;
                     val var10001: GrimpInline = this.this$0;
                     val var10002: GrimpInline = this.this$0;
                     val var10003: Value = v.getOp1();
                     val var2: Value = var10002.newExpr(var10003);
                     val var3: GrimpInline = this.this$0;
                     val var10004: Value = v.getOp2();
                     var10000.setValue(var10001.newDivExpr(var2, var3.newExpr(var10004)) as Value);
                  }

                  public void caseEqExpr(EqExpr v) {
                     val var10000: ExprBox = this.$returnedExpr;
                     val var10001: GrimpInline = this.this$0;
                     val var10002: GrimpInline = this.this$0;
                     val var10003: Value = v.getOp1();
                     val var2: Value = var10002.newExpr(var10003);
                     val var3: GrimpInline = this.this$0;
                     val var10004: Value = v.getOp2();
                     var10000.setValue(var10001.newEqExpr(var2, var3.newExpr(var10004)) as Value);
                  }

                  public void caseNeExpr(NeExpr v) {
                     val var10000: ExprBox = this.$returnedExpr;
                     val var10001: GrimpInline = this.this$0;
                     val var10002: GrimpInline = this.this$0;
                     val var10003: Value = v.getOp1();
                     val var2: Value = var10002.newExpr(var10003);
                     val var3: GrimpInline = this.this$0;
                     val var10004: Value = v.getOp2();
                     var10000.setValue(var10001.newNeExpr(var2, var3.newExpr(var10004)) as Value);
                  }

                  public void caseGeExpr(GeExpr v) {
                     val var10000: ExprBox = this.$returnedExpr;
                     val var10001: GrimpInline = this.this$0;
                     val var10002: GrimpInline = this.this$0;
                     val var10003: Value = v.getOp1();
                     val var2: Value = var10002.newExpr(var10003);
                     val var3: GrimpInline = this.this$0;
                     val var10004: Value = v.getOp2();
                     var10000.setValue(var10001.newGeExpr(var2, var3.newExpr(var10004)) as Value);
                  }

                  public void caseGtExpr(GtExpr v) {
                     val var10000: ExprBox = this.$returnedExpr;
                     val var10001: GrimpInline = this.this$0;
                     val var10002: GrimpInline = this.this$0;
                     val var10003: Value = v.getOp1();
                     val var2: Value = var10002.newExpr(var10003);
                     val var3: GrimpInline = this.this$0;
                     val var10004: Value = v.getOp2();
                     var10000.setValue(var10001.newGtExpr(var2, var3.newExpr(var10004)) as Value);
                  }

                  public void caseLeExpr(LeExpr v) {
                     val var10000: ExprBox = this.$returnedExpr;
                     val var10001: GrimpInline = this.this$0;
                     val var10002: GrimpInline = this.this$0;
                     val var10003: Value = v.getOp1();
                     val var2: Value = var10002.newExpr(var10003);
                     val var3: GrimpInline = this.this$0;
                     val var10004: Value = v.getOp2();
                     var10000.setValue(var10001.newLeExpr(var2, var3.newExpr(var10004)) as Value);
                  }

                  public void caseLtExpr(LtExpr v) {
                     val var10000: ExprBox = this.$returnedExpr;
                     val var10001: GrimpInline = this.this$0;
                     val var10002: GrimpInline = this.this$0;
                     val var10003: Value = v.getOp1();
                     val var2: Value = var10002.newExpr(var10003);
                     val var3: GrimpInline = this.this$0;
                     val var10004: Value = v.getOp2();
                     var10000.setValue(var10001.newLtExpr(var2, var3.newExpr(var10004)) as Value);
                  }

                  public void caseMulExpr(MulExpr v) {
                     val var10000: ExprBox = this.$returnedExpr;
                     val var10001: GrimpInline = this.this$0;
                     val var10002: GrimpInline = this.this$0;
                     val var10003: Value = v.getOp1();
                     val var2: Value = var10002.newExpr(var10003);
                     val var3: GrimpInline = this.this$0;
                     val var10004: Value = v.getOp2();
                     var10000.setValue(var10001.newMulExpr(var2, var3.newExpr(var10004)) as Value);
                  }

                  public void caseOrExpr(OrExpr v) {
                     val var10000: ExprBox = this.$returnedExpr;
                     val var10001: GrimpInline = this.this$0;
                     val var10002: GrimpInline = this.this$0;
                     val var10003: Value = v.getOp1();
                     val var2: Value = var10002.newExpr(var10003);
                     val var3: GrimpInline = this.this$0;
                     val var10004: Value = v.getOp2();
                     var10000.setValue(var10001.newOrExpr(var2, var3.newExpr(var10004)) as Value);
                  }

                  public void caseRemExpr(RemExpr v) {
                     val var10000: ExprBox = this.$returnedExpr;
                     val var10001: GrimpInline = this.this$0;
                     val var10002: GrimpInline = this.this$0;
                     val var10003: Value = v.getOp1();
                     val var2: Value = var10002.newExpr(var10003);
                     val var3: GrimpInline = this.this$0;
                     val var10004: Value = v.getOp2();
                     var10000.setValue(var10001.newRemExpr(var2, var3.newExpr(var10004)) as Value);
                  }

                  public void caseShlExpr(ShlExpr v) {
                     val var10000: ExprBox = this.$returnedExpr;
                     val var10001: GrimpInline = this.this$0;
                     val var10002: GrimpInline = this.this$0;
                     val var10003: Value = v.getOp1();
                     val var2: Value = var10002.newExpr(var10003);
                     val var3: GrimpInline = this.this$0;
                     val var10004: Value = v.getOp2();
                     var10000.setValue(var10001.newShlExpr(var2, var3.newExpr(var10004)) as Value);
                  }

                  public void caseShrExpr(ShrExpr v) {
                     val var10000: ExprBox = this.$returnedExpr;
                     val var10001: GrimpInline = this.this$0;
                     val var10002: GrimpInline = this.this$0;
                     val var10003: Value = v.getOp1();
                     val var2: Value = var10002.newExpr(var10003);
                     val var3: GrimpInline = this.this$0;
                     val var10004: Value = v.getOp2();
                     var10000.setValue(var10001.newShrExpr(var2, var3.newExpr(var10004)) as Value);
                  }

                  public void caseUshrExpr(UshrExpr v) {
                     val var10000: ExprBox = this.$returnedExpr;
                     val var10001: GrimpInline = this.this$0;
                     val var10002: GrimpInline = this.this$0;
                     val var10003: Value = v.getOp1();
                     val var2: Value = var10002.newExpr(var10003);
                     val var3: GrimpInline = this.this$0;
                     val var10004: Value = v.getOp2();
                     var10000.setValue(var10001.newUshrExpr(var2, var3.newExpr(var10004)) as Value);
                  }

                  public void caseSubExpr(SubExpr v) {
                     val var10000: ExprBox = this.$returnedExpr;
                     val var10001: GrimpInline = this.this$0;
                     val var10002: GrimpInline = this.this$0;
                     val var10003: Value = v.getOp1();
                     val var2: Value = var10002.newExpr(var10003);
                     val var3: GrimpInline = this.this$0;
                     val var10004: Value = v.getOp2();
                     var10000.setValue(var10001.newSubExpr(var2, var3.newExpr(var10004)) as Value);
                  }

                  public void caseXorExpr(XorExpr v) {
                     val var10000: ExprBox = this.$returnedExpr;
                     val var10001: GrimpInline = this.this$0;
                     val var10002: GrimpInline = this.this$0;
                     val var10003: Value = v.getOp1();
                     val var2: Value = var10002.newExpr(var10003);
                     val var3: GrimpInline = this.this$0;
                     val var10004: Value = v.getOp2();
                     var10000.setValue(var10001.newXorExpr(var2, var3.newExpr(var10004)) as Value);
                  }

                  public void caseInterfaceInvokeExpr(InterfaceInvokeExpr v) {
                     val newArgList: ArrayList = new ArrayList();
                     var i: Int = 0;

                     for (int var4 = v.getArgCount(); i < var4; i++) {
                        val var10001: GrimpInline = this.this$0;
                        val var10002: Value = v.getArg(i);
                        newArgList.add(var10001.newExpr(var10002));
                     }

                     val var10000: ExprBox = this.$returnedExpr;
                     val var7: GrimpInline = this.this$0;
                     val var8: Value = v.getBase();
                     var10000.setValue((new GInterfaceInvokeExpr(newArgList, var7.newExpr((var8 as Local) as Value), v.getMethodRef()) {
                        {
                           super(`$super_call_param$1`, `$super_call_param$2`, `$newArgList`);
                        }

                        public void toString(UnitPrinter up) {
                           GrimpInlineKt.invokeToString(this as InvokeExpr, up);
                        }
                     }) as Value);
                  }

                  public void caseSpecialInvokeExpr(SpecialInvokeExpr v) {
                     val newArgList: ArrayList = new ArrayList();
                     var i: Int = 0;

                     for (int var4 = v.getArgCount(); i < var4; i++) {
                        val var10001: GrimpInline = this.this$0;
                        val var10002: Value = v.getArg(i);
                        newArgList.add(var10001.newExpr(var10002));
                     }

                     val var10000: ExprBox = this.$returnedExpr;
                     val var7: GrimpInline = this.this$0;
                     val var8: Value = v.getBase();
                     var10000.setValue((new GSpecialInvokeExpr(newArgList, var7.newExpr((var8 as Local) as Value), v.getMethodRef()) {
                        {
                           super(`$super_call_param$1`, `$super_call_param$2`, `$newArgList`);
                        }

                        public void toString(UnitPrinter up) {
                           GrimpInlineKt.invokeToString(this as InvokeExpr, up);
                        }
                     }) as Value);
                  }

                  public void caseStaticInvokeExpr(StaticInvokeExpr v) {
                     val newArgList: ArrayList = new ArrayList();
                     var i: Int = 0;

                     for (int var4 = v.getArgCount(); i < var4; i++) {
                        val var10001: GrimpInline = this.this$0;
                        val var10002: Value = v.getArg(i);
                        newArgList.add(var10001.newExpr(var10002));
                     }

                     this.$returnedExpr.setValue((new GStaticInvokeExpr(newArgList, v.getMethodRef()) {
                        {
                           super(`$super_call_param$1`, `$newArgList`);
                        }

                        public void toString(UnitPrinter up) {
                           GrimpInlineKt.invokeToString(this as InvokeExpr, up);
                        }
                     }) as Value);
                  }

                  public void caseVirtualInvokeExpr(VirtualInvokeExpr v) {
                     val newArgList: ArrayList = new ArrayList();
                     var i: Int = 0;

                     for (int var4 = v.getArgCount(); i < var4; i++) {
                        val var10001: GrimpInline = this.this$0;
                        val var10002: Value = v.getArg(i);
                        newArgList.add(var10001.newExpr(var10002));
                     }

                     val var10000: ExprBox = this.$returnedExpr;
                     val var7: GrimpInline = this.this$0;
                     val var8: Value = v.getBase();
                     var10000.setValue((new GVirtualInvokeExpr(newArgList, var7.newExpr((var8 as Local) as Value), v.getMethodRef()) {
                        {
                           super(`$super_call_param$1`, `$super_call_param$2`, `$newArgList`);
                        }

                        public void toString(UnitPrinter up) {
                           GrimpInlineKt.invokeToString(this as InvokeExpr, up);
                        }
                     }) as Value);
                  }

                  public void caseDynamicInvokeExpr(DynamicInvokeExpr v) {
                     val newArgList: ArrayList = new ArrayList();
                     var i: Int = 0;

                     for (int var4 = v.getArgCount(); i < var4; i++) {
                        val var10001: GrimpInline = this.this$0;
                        val var10002: Value = v.getArg(i);
                        newArgList.add(var10001.newExpr(var10002));
                     }

                     this.$returnedExpr
                        .setValue((new GDynamicInvokeExpr(newArgList, v.getBootstrapMethodRef(), v.getBootstrapArgs(), v.getMethodRef(), v.getHandleTag()) {
                           {
                              super(`$super_call_param$1`, `$super_call_param$2`, `$super_call_param$3`, `$super_call_param$4`, `$newArgList`);
                           }

                           public void toString(UnitPrinter up) {
                              GrimpInlineKt.invokeToString(this as InvokeExpr, up);
                           }
                        }) as Value);
                  }

                  public void caseCastExpr(CastExpr v) {
                     val var10000: ExprBox = this.$returnedExpr;
                     val var10001: GrimpInline = this.this$0;
                     val var10002: GrimpInline = this.this$0;
                     val var10003: Value = v.getOp();
                     var10000.setValue(var10001.newCastExpr(var10002.newExpr(var10003), v.getType()) as Value);
                  }

                  public void caseInstanceOfExpr(InstanceOfExpr v) {
                     val var10000: ExprBox = this.$returnedExpr;
                     val var10001: GrimpInline = this.this$0;
                     val var10002: GrimpInline = this.this$0;
                     val var10003: Value = v.getOp();
                     var10000.setValue(var10001.newInstanceOfExpr(var10002.newExpr(var10003), v.getCheckType()) as Value);
                  }

                  public void caseNewArrayExpr(NewArrayExpr v) {
                     this.$returnedExpr.setValue(this.this$0.newNewArrayExpr(v.getBaseType(), v.getSize()) as Value);
                  }

                  public void caseNewMultiArrayExpr(NewMultiArrayExpr v) {
                     this.$returnedExpr.setValue(this.this$0.newNewMultiArrayExpr(v.getBaseType(), v.getSizes()) as Value);
                  }

                  public void caseNewExpr(NewExpr v) {
                     this.$returnedExpr.setValue(v as Value);
                  }

                  public void caseLengthExpr(LengthExpr v) {
                     val var10000: ExprBox = this.$returnedExpr;
                     val var10001: GrimpInline = this.this$0;
                     val var10002: GrimpInline = this.this$0;
                     val var10003: Value = v.getOp();
                     var10000.setValue(var10001.newLengthExpr(var10002.newExpr(var10003)) as Value);
                  }

                  public void caseNegExpr(NegExpr v) {
                     val var10000: ExprBox = this.$returnedExpr;
                     val var10001: GrimpInline = this.this$0;
                     val var10002: GrimpInline = this.this$0;
                     val var10003: Value = v.getOp();
                     var10000.setValue(var10001.newNegExpr(var10002.newExpr(var10003)) as Value);
                  }

                  public void defaultCase(Object v) {
                     this.$returnedExpr.setValue((v as Expr) as Value);
                  }
               }
            ) as Switch
         );
         val var4: Value = var3.getValue();
         return var4;
      } else if (value is ArrayRef) {
         val var10001: Value = (value as ArrayRef).getBase();
         val var10003: Value = (value as ArrayRef).getIndex();
         val var10000: ArrayRef = this.newArrayRef(var10001, this.newExpr(var10003));
         return var10000 as Value;
      } else if (value is InstanceFieldRef) {
         val var10002: Value = (value as InstanceFieldRef).getBase();
         val returnedExpr: InstanceFieldRef = this.newInstanceFieldRef(this.newExpr(var10002), (value as InstanceFieldRef).getFieldRef());
         return returnedExpr as Value;
      } else {
         return value;
      }
   }

   public fun inline(u: Unit): Unit {
      val newStmtBox: UnitBox = this.newStmtBox(null);
      u.apply(
         (
            new AbstractStmtSwitch<Object>(newStmtBox, this) {
               {
                  this.$newStmtBox = `$newStmtBox`;
                  this.$grimp = `$grimp`;
               }

               public void caseAssignStmt(AssignStmt s) {
                  val var10000: UnitBox = this.$newStmtBox;
                  val var10001: GrimpInline = this.$grimp;
                  val var10002: Value = s.getLeftOp();
                  val var10003: GrimpInline = this.$grimp;
                  val var10004: Value = s.getRightOp();
                  var10000.setUnit(var10001.newAssignStmt(var10002, var10003.newExpr(var10004)) as Unit);
               }

               public void caseIdentityStmt(IdentityStmt s) {
                  val var10000: UnitBox = this.$newStmtBox;
                  val var10001: GrimpInline = this.$grimp;
                  val var10002: Value = s.getLeftOp();
                  val var10003: GrimpInline = this.$grimp;
                  val var10004: Value = s.getRightOp();
                  var10000.setUnit(var10001.newIdentityStmt(var10002, var10003.newExpr(var10004)) as Unit);
               }

               public void caseBreakpointStmt(BreakpointStmt s) {
                  this.$newStmtBox.setUnit(this.$grimp.newBreakpointStmt(s) as Unit);
               }

               public void caseInvokeStmt(InvokeStmt s) {
                  val var10000: GrimpInline = this.$grimp;
                  val var10001: InvokeExpr = s.getInvokeExpr();
                  this.$newStmtBox.setUnit(this.$grimp.newInvokeStmt(var10000.newExpr(var10001 as Value)) as Unit);
               }

               public void caseEnterMonitorStmt(EnterMonitorStmt s) {
                  val var10000: UnitBox = this.$newStmtBox;
                  val var10001: GrimpInline = this.$grimp;
                  val var10002: GrimpInline = this.$grimp;
                  val var10003: Value = s.getOp();
                  var10000.setUnit(var10001.newEnterMonitorStmt(var10002.newExpr(var10003)) as Unit);
               }

               public void caseExitMonitorStmt(ExitMonitorStmt s) {
                  val var10000: UnitBox = this.$newStmtBox;
                  val var10001: GrimpInline = this.$grimp;
                  val var10002: GrimpInline = this.$grimp;
                  val var10003: Value = s.getOp();
                  var10000.setUnit(var10001.newExitMonitorStmt(var10002.newExpr(var10003)) as Unit);
               }

               public void caseGotoStmt(GotoStmt s) {
                  this.$newStmtBox.setUnit(this.$grimp.newGotoStmt(s) as Unit);
               }

               public void caseIfStmt(IfStmt s) {
                  val var10000: UnitBox = this.$newStmtBox;
                  val var10001: GrimpInline = this.$grimp;
                  val var10002: GrimpInline = this.$grimp;
                  val var10003: Value = s.getCondition();
                  var10000.setUnit(var10001.newIfStmt(var10002.newExpr(var10003), s.getTarget() as Unit) as Unit);
               }

               public void caseLookupSwitchStmt(LookupSwitchStmt s) {
                  val var10000: UnitBox = this.$newStmtBox;
                  val var10001: GrimpInline = this.$grimp;
                  val var10002: GrimpInline = this.$grimp;
                  val var10003: Value = s.getKey();
                  var10000.setUnit(var10001.newLookupSwitchStmt(var10002.newExpr(var10003), s.getLookupValues(), s.getTargets(), s.getDefaultTarget()) as Unit);
               }

               public void caseNopStmt(NopStmt s) {
                  this.$newStmtBox.setUnit(this.$grimp.newNopStmt(s) as Unit);
               }

               public void caseReturnStmt(ReturnStmt s) {
                  val var10000: UnitBox = this.$newStmtBox;
                  val var10001: GrimpInline = this.$grimp;
                  val var10002: GrimpInline = this.$grimp;
                  val var10003: Value = s.getOp();
                  var10000.setUnit(var10001.newReturnStmt(var10002.newExpr(var10003)) as Unit);
               }

               public void caseReturnVoidStmt(ReturnVoidStmt s) {
                  this.$newStmtBox.setUnit(this.$grimp.newReturnVoidStmt(s) as Unit);
               }

               public void caseTableSwitchStmt(TableSwitchStmt s) {
                  val var10000: UnitBox = this.$newStmtBox;
                  val var10001: GrimpInline = this.$grimp;
                  val var10002: GrimpInline = this.$grimp;
                  val var10003: Value = s.getKey();
                  var10000.setUnit(
                     var10001.newTableSwitchStmt(var10002.newExpr(var10003), s.getLowIndex(), s.getHighIndex(), s.getTargets(), s.getDefaultTarget()) as Unit
                  );
               }

               public void caseThrowStmt(ThrowStmt s) {
                  val var10000: UnitBox = this.$newStmtBox;
                  val var10001: GrimpInline = this.$grimp;
                  val var10002: GrimpInline = this.$grimp;
                  val var10003: Value = s.getOp();
                  var10000.setUnit(var10001.newThrowStmt(var10002.newExpr(var10003)) as Unit);
               }
            }
         ) as Switch
      );
      val var10000: Unit = newStmtBox.getUnit();
      val newStmt: Stmt = var10000 as Stmt;
      val lnTag: Tag = u.getTag("LineNumberTag");
      if (lnTag != null) {
         newStmt.addTag(lnTag);
      }

      val slpTag: Tag = u.getTag("SourceLnPosTag");
      if (slpTag != null) {
         newStmt.addTag(slpTag);
      }

      return newStmt as Unit;
   }
}
