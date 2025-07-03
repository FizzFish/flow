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
            val var3 = ExprBox(IntConstant.v(0) as Value)
            value.apply(
                object : AbstractExprSwitch<Any>(var3, this) {
                    override fun caseAddExpr(v: AddExpr) {
                        val var10000 = this.$returnedExpr
                        val var10001 = this.this$0
                        val var10002 = this.this$0
                        val var10003 = v.getOp1()
                        val var2 = var10002.newExpr(var10003)
                        val var3 = this.this$0
                        val var10004 = v.getOp2()
                        var10000.setValue(var10001.newAddExpr(var2, var3.newExpr(var10004)) as Value)
                    }

                    override fun caseAndExpr(v: AndExpr) {
                        val var10000 = this.$returnedExpr
                        val var10001 = this.this$0
                        val var10002 = this.this$0
                        val var10003 = v.getOp1()
                        val var2 = var10002.newExpr(var10003)
                        val var3 = this.this$0
                        val var10004 = v.getOp2()
                        var10000.setValue(var10001.newAndExpr(var2, var3.newExpr(var10004)) as Value)
                    }

                    override fun caseCmpExpr(v: CmpExpr) {
                        val var10000 = this.$returnedExpr
                        val var10001 = this.this$0
                        val var10002 = this.this$0
                        val var10003 = v.getOp1()
                        val var2 = var10002.newExpr(var10003)
                        val var3 = this.this$0
                        val var10004 = v.getOp2()
                        var10000.setValue(var10001.newCmpExpr(var2, var3.newExpr(var10004)) as Value)
                    }

                    override fun caseCmpgExpr(v: CmpgExpr) {
                        val var10000 = this.$returnedExpr
                        val var10001 = this.this$0
                        val var10002 = this.this$0
                        val var10003 = v.getOp1()
                        val var2 = var10002.newExpr(var10003)
                        val var3 = this.this$0
                        val var10004 = v.getOp2()
                        var10000.setValue(var10001.newCmpgExpr(var2, var3.newExpr(var10004)) as Value)
                    }

                    override fun caseCmplExpr(v: CmplExpr) {
                        val var10000 = this.$returnedExpr
                        val var10001 = this.this$0
                        val var10002 = this.this$0
                        val var10003 = v.getOp1()
                        val var2 = var10002.newExpr(var10003)
                        val var3 = this.this$0
                        val var10004 = v.getOp2()
                        var10000.setValue(var10001.newCmplExpr(var2, var3.newExpr(var10004)) as Value)
                    }

                    override fun caseDivExpr(v: DivExpr) {
                        val var10000 = this.$returnedExpr
                        val var10001 = this.this$0
                        val var10002 = this.this$0
                        val var10003 = v.getOp1()
                        val var2 = var10002.newExpr(var10003)
                        val var3 = this.this$0
                        val var10004 = v.getOp2()
                        var10000.setValue(var10001.newDivExpr(var2, var3.newExpr(var10004)) as Value)
                    }

                    override fun caseEqExpr(v: EqExpr) {
                        val var10000 = this.$returnedExpr
                        val var10001 = this.this$0
                        val var10002 = this.this$0
                        val var10003 = v.getOp1()
                        val var2 = var10002.newExpr(var10003)
                        val var3 = this.this$0
                        val var10004 = v.getOp2()
                        var10000.setValue(var10001.newEqExpr(var2, var3.newExpr(var10004)) as Value)
                    }

                    override fun caseNeExpr(v: NeExpr) {
                        val var10000 = this.$returnedExpr
                        val var10001 = this.this$0
                        val var10002 = this.this$0
                        val var10003 = v.getOp1()
                        val var2 = var10002.newExpr(var10003)
                        val var3 = this.this$0
                        val var10004 = v.getOp2()
                        var10000.setValue(var10001.newNeExpr(var2, var3.newExpr(var10004)) as Value)
                    }

                    override fun caseGeExpr(v: GeExpr) {
                        val var10000 = this.$returnedExpr
                        val var10001 = this.this$0
                        val var10002 = this.this$0
                        val var10003 = v.getOp1()
                        val var2 = var10002.newExpr(var10003)
                        val var3 = this.this$0
                        val var10004 = v.getOp2()
                        var10000.setValue(var10001.newGeExpr(var2, var3.newExpr(var10004)) as Value)
                    }

                    override fun caseGtExpr(v: GtExpr) {
                        val var10000 = this.$returnedExpr
                        val var10001 = this.this$0
                        val var10002 = this.this$0
                        val var10003 = v.getOp1()
                        val var2 = var10002.newExpr(var10003)
                        val var3 = this.this$0
                        val var10004 = v.getOp2()
                        var10000.setValue(var10001.newGtExpr(var2, var3.newExpr(var10004)) as Value)
                    }

                    override fun caseLeExpr(v: LeExpr) {
                        val var10000 = this.$returnedExpr
                        val var10001 = this.this$0
                        val var10002 = this.this$0
                        val var10003 = v.getOp1()
                        val var2 = var10002.newExpr(var10003)
                        val var3 = this.this$0
                        val var10004 = v.getOp2()
                        var10000.setValue(var10001.newLeExpr(var2, var3.newExpr(var10004)) as Value)
                    }

                    override fun caseLtExpr(v: LtExpr) {
                        val var10000 = this.$returnedExpr
                        val var10001 = this.this$0
                        val var10002 = this.this$0
                        val var10003 = v.getOp1()
                        val var2 = var10002.newExpr(var10003)
                        val var3 = this.this$0
                        val var10004 = v.getOp2()
                        var10000.setValue(var10001.newLtExpr(var2, var3.newExpr(var10004)) as Value)
                    }

                    override fun caseMulExpr(v: MulExpr) {
                        val var10000 = this.$returnedExpr
                        val var10001 = this.this$0
                        val var10002 = this.this$0
                        val var10003 = v.getOp1()
                        val var2 = var10002.newExpr(var10003)
                        val var3 = this.this$0
                        val var10004 = v.getOp2()
                        var10000.setValue(var10001.newMulExpr(var2, var3.newExpr(var10004)) as Value)
                    }

                    override fun caseOrExpr(v: OrExpr) {
                        val var10000 = this.$returnedExpr
                        val var10001 = this.this$0
                        val var10002 = this.this$0
                        val var10003 = v.getOp1()
                        val var2 = var10002.newExpr(var10003)
                        val var3 = this.this$0
                        val var10004 = v.getOp2()
                        var10000.setValue(var10001.newOrExpr(var2, var3.newExpr(var10004)) as Value)
                    }

                    override fun caseRemExpr(v: RemExpr) {
                        val var10000 = this.$returnedExpr
                        val var10001 = this.this$0
                        val var10002 = this.this$0
                        val var10003 = v.getOp1()
                        val var2 = var10002.newExpr(var10003)
                        val var3 = this.this$0
                        val var10004 = v.getOp2()
                        var10000.setValue(var10001.newRemExpr(var2, var3.newExpr(var10004)) as Value)
                    }

                    override fun caseShlExpr(v: ShlExpr) {
                        val var10000 = this.$returnedExpr
                        val var10001 = this.this$0
                        val var10002 = this.this$0
                        val var10003 = v.getOp1()
                        val var2 = var10002.newExpr(var10003)
                        val var3 = this.this$0
                        val var10004 = v.getOp2()
                        var10000.setValue(var10001.newShlExpr(var2, var3.newExpr(var10004)) as Value)
                    }

                    override fun caseShrExpr(v: ShrExpr) {
                        val var10000 = this.$returnedExpr
                        val var10001 = this.this$0
                        val var10002 = this.this$0
                        val var10003 = v.getOp1()
                        val var2 = var10002.newExpr(var10003)
                        val var3 = this.this$0
                        val var10004 = v.getOp2()
                        var10000.setValue(var10001.newShrExpr(var2, var3.newExpr(var10004)) as Value)
                    }

                    override fun caseUshrExpr(v: UshrExpr) {
                        val var10000 = this.$returnedExpr
                        val var10001 = this.this$0
                        val var10002 = this.this$0
                        val var10003 = v.getOp1()
                        val var2 = var10002.newExpr(var10003)
                        val var3 = this.this$0
                        val var10004 = v.getOp2()
                        var10000.setValue(var10001.newUshrExpr(var2, var3.newExpr(var10004)) as Value)
                    }

                    override fun caseSubExpr(v: SubExpr) {
                        val var10000 = this.$returnedExpr
                        val var10001 = this.this$0
                        val var10002 = this.this$0
                        val var10003 = v.getOp1()
                        val var2 = var10002.newExpr(var10003)
                        val var3 = this.this$0
                        val var10004 = v.getOp2()
                        var10000.setValue(var10001.newSubExpr(var2, var3.newExpr(var10004)) as Value)
                    }

                    override fun caseXorExpr(v: XorExpr) {
                        val var10000 = this.$returnedExpr
                        val var10001 = this.this$0
                        val var10002 = this.this$0
                        val var10003 = v.getOp1()
                        val var2 = var10002.newExpr(var10003)
                        val var3 = this.this$0
                        val var10004 = v.getOp2()
                        var10000.setValue(var10001.newXorExpr(var2, var3.newExpr(var10004)) as Value)
                    }

                    override fun caseInterfaceInvokeExpr(v: InterfaceInvokeExpr) {
                        val newArgList = ArrayList<Value>()
                        var i = 0

                        while (i < v.getArgCount()) {
                            val var10001 = this.this$0
                            val var10002 = v.getArg(i)
                            newArgList.add(var10001.newExpr(var10002))
                            i++
                        }

                        val var10000 = this.$returnedExpr
                        val var7 = this.this$0
                        val var8 = v.getBase()
                        var10000.setValue(object : GInterfaceInvokeExpr(newArgList, var7.newExpr(var8 as Local) as Value, v.getMethodRef()) {
                            override fun toString(up: UnitPrinter) {
                                GrimpInlineKt.invokeToString(this as InvokeExpr, up)
                            }
                        } as Value)
                    }

                    override fun caseSpecialInvokeExpr(v: SpecialInvokeExpr) {
                        val newArgList = ArrayList<Value>()
                        var i = 0

                        while (i < v.getArgCount()) {
                            val var10001 = this.this$0
                            val var10002 = v.getArg(i)
                            newArgList.add(var10001.newExpr(var10002))
                            i++
                        }

                        val var10000 = this.$returnedExpr
                        val var7 = this.this$0
                        val var8 = v.getBase()
                        var10000.setValue(object : GSpecialInvokeExpr(newArgList, var7.newExpr(var8 as Local) as Value, v.getMethodRef()) {
                            override fun toString(up: UnitPrinter) {
                                GrimpInlineKt.invokeToString(this as InvokeExpr, up)
                            }
                        } as Value)
                    }

                    override fun caseStaticInvokeExpr(v: StaticInvokeExpr) {
                        val newArgList = ArrayList<Value>()
                        var i = 0

                        while (i < v.getArgCount()) {
                            val var10001 = this.this$0
                            val var10002 = v.getArg(i)
                            newArgList.add(var10001.newExpr(var10002))
                            i++
                        }

                        this.$returnedExpr.setValue(object : GStaticInvokeExpr(newArgList, v.getMethodRef()) {
                            override fun toString(up: UnitPrinter) {
                                GrimpInlineKt.invokeToString(this as InvokeExpr, up)
                            }
                        } as Value)
                    }

                    override fun caseVirtualInvokeExpr(v: VirtualInvokeExpr) {
                        val newArgList = ArrayList<Value>()
                        var i = 0

                        while (i < v.getArgCount()) {
                            val var10001 = this.this$0
                            val var10002 = v.getArg(i)
                            newArgList.add(var10001.newExpr(var10002))
                            i++
                        }

                        val var10000 = this.$returnedExpr
                        val var7 = this.this$0
                        val var8 = v.getBase()
                        var10000.setValue(object : GVirtualInvokeExpr(newArgList, var7.newExpr(var8 as Local) as Value, v.getMethodRef()) {
                            override fun toString(up: UnitPrinter) {
                                GrimpInlineKt.invokeToString(this as InvokeExpr, up)
                            }
                        } as Value)
                    }

                    override fun caseDynamicInvokeExpr(v: DynamicInvokeExpr) {
                        val newArgList = ArrayList<Value>()
                        var i = 0

                        while (i < v.getArgCount()) {
                            val var10001 = this.this$0
                            val var10002 = v.getArg(i)
                            newArgList.add(var10001.newExpr(var10002))
                            i++
                        }

                        this.$returnedExpr.setValue(object : GDynamicInvokeExpr(newArgList, v.getBootstrapMethodRef(), v.getBootstrapArgs(), v.getMethodRef(), v.getHandleTag()) {
                            override fun toString(up: UnitPrinter) {
                                GrimpInlineKt.invokeToString(this as InvokeExpr, up)
                            }
                        } as Value)
                    }

                    override fun caseCastExpr(v: CastExpr) {
                        val var10000 = this.$returnedExpr
                        val var10001 = this.this$0
                        val var10002 = this.this$0
                        val var10003 = v.getOp()
                        var10000.setValue(var10001.newCastExpr(var10002.newExpr(var10003), v.getType()) as Value)
                    }

                    override fun caseInstanceOfExpr(v: InstanceOfExpr) {
                        val var10000 = this.$returnedExpr
                        val var10001 = this.this$0
                        val var10002 = this.this$0
                        val var10003 = v.getOp()
                        var10000.setValue(var10001.newInstanceOfExpr(var10002.newExpr(var10003), v.getCheckType()) as Value)
                    }

                    override fun caseNewArrayExpr(v: NewArrayExpr) {
                        this.$returnedExpr.setValue(this.this$0.newNewArrayExpr(v.getBaseType(), v.getSize()) as Value)
                    }

                    override fun caseNewMultiArrayExpr(v: NewMultiArrayExpr) {
                        this.$returnedExpr.setValue(this.this$0.newNewMultiArrayExpr(v.getBaseType(), v.getSizes()) as Value)
                    }

                    override fun caseNewExpr(v: NewExpr) {
                        this.$returnedExpr.setValue(v as Value)
                    }

                    override fun caseLengthExpr(v: LengthExpr) {
                        val var10000 = this.$returnedExpr
                        val var10001 = this.this$0
                        val var10002 = this.this$0
                        val var10003 = v.getOp()
                        var10000.setValue(var10001.newLengthExpr(var10002.newExpr(var10003)) as Value)
                    }

                    override fun caseNegExpr(v: NegExpr) {
                        val var10000 = this.$returnedExpr
                        val var10001 = this.this$0
                        val var10002 = this.this$0
                        val var10003 = v.getOp()
                        var10000.setValue(var10001.newNegExpr(var10002.newExpr(var10003)) as Value)
                    }

                    override fun defaultCase(v: Any) {
                        this.$returnedExpr.setValue((v as Expr) as Value)
                    }
                } as Switch
            )
            val var4 = var3.getValue()
            return var4
        } else if (value is ArrayRef) {
            val var10001 = (value as ArrayRef).getBase()
            val var10003 = (value as ArrayRef).getIndex()
            val var10000 = this.newArrayRef(var10001, this.newExpr(var10003))
            return var10000 as Value
        } else if (value is InstanceFieldRef) {
            val var10002 = (value as InstanceFieldRef).getBase()
            val returnedExpr = this.newInstanceFieldRef(this.newExpr(var10002), (value as InstanceFieldRef).getFieldRef())
            return returnedExpr as Value
        } else {
            return value
        }
    }

    public fun inline(u: Unit): Unit {
        val newStmtBox = this.newStmtBox(null)
        u.apply(
            object : AbstractStmtSwitch<Any>(newStmtBox, this) {
                override fun caseAssignStmt(s: AssignStmt) {
                    val var10000 = this.$newStmtBox
                    val var10001 = this.$grimp
                    val var10002 = s.getLeftOp()
                    val var10003 = this.$grimp
                    val var10004 = s.getRightOp()
                    var10000.setUnit(var10001.newAssignStmt(var10002, var10003.newExpr(var10004)) as Unit)
                }

                override fun caseIdentityStmt(s: IdentityStmt) {
                    val var10000 = this.$newStmtBox
                    val var10001 = this.$grimp
                    val var10002 = s.getLeftOp()
                    val var10003 = this.$grimp
                    val var10004 = s.getRightOp()
                    var10000.setUnit(var10001.newIdentityStmt(var10002, var10003.newExpr(var10004)) as Unit)
                }

                override fun caseBreakpointStmt(s: BreakpointStmt) {
                    this.$newStmtBox.setUnit(this.$grimp.newBreakpointStmt(s) as Unit)
                }

                override fun caseInvokeStmt(s: InvokeStmt) {
                    val var10000 = this.$grimp
                    val var10001 = s.getInvokeExpr()
                    this.$newStmtBox.setUnit(this.$grimp.newInvokeStmt(var10000.newExpr(var10001 as Value)) as Unit)
                }

                override fun caseEnterMonitorStmt(s: EnterMonitorStmt) {
                    val var10000 = this.$newStmtBox
                    val var10001 = this.$grimp
                    val var10002 = this.$grimp
                    val var10003 = s.getOp()
                    var10000.setUnit(var10001.newEnterMonitorStmt(var10002.newExpr(var10003)) as Unit)
                }

                override fun caseExitMonitorStmt(s: ExitMonitorStmt) {
                    val var10000 = this.$newStmtBox
                    val var10001 = this.$grimp
                    val var10002 = this.$grimp
                    val var10003 = s.getOp()
                    var10000.setUnit(var10001.newExitMonitorStmt(var10002.newExpr(var10003)) as Unit)
                }

                override fun caseGotoStmt(s: GotoStmt) {
                    this.$newStmtBox.setUnit(this.$grimp.newGotoStmt(s) as Unit)
                }

                override fun caseIfStmt(s: IfStmt) {
                    val var10000 = this.$newStmtBox
                    val var10001 = this.$grimp
                    val var10002 = this.$grimp
                    val var10003 = s.getCondition()
                    var10000.setUnit(var10001.newIfStmt(var10002.newExpr(var10003), s.getTarget() as Unit) as Unit)
                }

                override fun caseLookupSwitchStmt(s: LookupSwitchStmt) {
                    val var10000 = this.$newStmtBox
                    val var10001 = this.$grimp
                    val var10002 = this.$grimp
                    val var10003 = s.getKey()
                    var10000.setUnit(var10001.newLookupSwitchStmt(var10002.newExpr(var10003), s.getLookupValues(), s.getTargets(), s.getDefaultTarget()) as Unit)
                }

                override fun caseNopStmt(s: NopStmt) {
                    this.$newStmtBox.setUnit(this.$grimp.newNopStmt(s) as Unit)
                }

                override fun caseReturnStmt(s: ReturnStmt) {
                    val var10000 = this.$newStmtBox
                    val var10001 = this.$grimp
                    val var10002 = this.$grimp
                    val var10003 = s.getOp()
                    var10000.setUnit(var10001.newReturnStmt(var10002.newExpr(var10003)) as Unit)
                }

                override fun caseReturnVoidStmt(s: ReturnVoidStmt) {
                    this.$newStmtBox.setUnit(this.$grimp.newReturnVoidStmt(s) as Unit)
                }

                override fun caseTableSwitchStmt(s: TableSwitchStmt) {
                    val var10000 = this.$newStmtBox
                    val var10001 = this.$grimp
                    val var10002 = this.$grimp
                    val var10003 = s.getKey()
                    var10000.setUnit(
                        var10001.newTableSwitchStmt(var10002.newExpr(var10003), s.getLowIndex(), s.getHighIndex(), s.getTargets(), s.getDefaultTarget()) as Unit
                    )
                }

                override fun caseThrowStmt(s: ThrowStmt) {
                    val var10000 = this.$newStmtBox
                    val var10001 = this.$grimp
                    val var10002 = this.$grimp
                    val var10003 = s.getOp()
                    var10000.setUnit(var10001.newThrowStmt(var10002.newExpr(var10003)) as Unit)
                }
            } as Switch
        )
        val var10000 = newStmtBox.getUnit()
        val newStmt = var10000 as Stmt
        val lnTag = u.getTag("LineNumberTag")
        if (lnTag != null) {
            newStmt.addTag(lnTag)
        }

        val slpTag = u.getTag("SourceLnPosTag")
        if (slpTag != null) {
            newStmt.addTag(slpTag)
        }

        return newStmt as Unit
    }
}