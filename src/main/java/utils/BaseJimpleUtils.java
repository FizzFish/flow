//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package utils;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import soot.Body;
import soot.Local;
import soot.RefType;
import soot.Scene;
import soot.SootFieldRef;
import soot.SootMethod;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.jimple.ArrayRef;
import soot.jimple.InstanceFieldRef;
import soot.jimple.IntConstant;
import soot.jimple.InvokeExpr;
import soot.jimple.Jimple;
import soot.jimple.JimpleBody;
import soot.jimple.NewArrayExpr;
import soot.jimple.NewExpr;
import soot.jimple.ParameterRef;
import soot.jimple.SpecialInvokeExpr;
import soot.jimple.StaticFieldRef;
import soot.jimple.StaticInvokeExpr;
import soot.jimple.ThisRef;
import soot.jimple.VirtualInvokeExpr;

public class BaseJimpleUtils {
    private static final Logger log = LoggerFactory.getLogger(BaseJimpleUtils.class);

    public BaseJimpleUtils() {
    }

    private Local newLocalVar(String localName, String vtype) {
        return Jimple.v().newLocal(localName, RefType.v(vtype));
    }

    private Local newLocalVar(String localName, Type vtype) {
        return Jimple.v().newLocal(localName, vtype);
    }

    public Local addLocalVar(String localName, String vtype, Body body) {
        Local local = this.newLocalVar(localName, vtype);
        body.getLocals().add(local);
        return local;
    }

    public Local addLocalVar(String localName, Type vtype, Body body) {
        Local local = this.newLocalVar(localName, vtype);
        body.getLocals().add(local);
        return local;
    }

    public Unit createAssignStmt(Local local, String realType) {
        return Jimple.v().newAssignStmt(local, this.createNewExpr(realType));
    }

    public void createAssignStmt(Local local, String realType, BaseBodyGenerator units) {
        units.add(Jimple.v().newAssignStmt(local, this.createNewExpr(realType)));
    }

    public Unit createAssignStmt(Value var, Value realvar) {
        return Jimple.v().newAssignStmt(var, realvar);
    }

    public void createAssignStmt(Value left, Value right, BaseBodyGenerator units) {
        units.add(Jimple.v().newAssignStmt(left, right));
    }

    public void createAssignStmt(Value left, NewUnits.BeforeRhs right, BaseBodyGenerator units) {
        units.add(right.getInsertBefore().before(Jimple.v().newAssignStmt(left, right.getInsertBeforeRhs())));
    }

    public NewExpr createNewExpr(String declType) {
        return this.createNewExpr(RefType.v(declType));
    }

    public NewExpr createNewExpr(RefType declType) {
        if (declType.hasSootClass() && !declType.getSootClass().isConcrete()) {
            log.warn("{} is not a concrete class when create NewExpr", declType);
        }

        return Jimple.v().newNewExpr(declType);
    }

    public NewArrayExpr createNewArrayExpr(String type, int paramSize) {
        return Jimple.v().newNewArrayExpr(RefType.v(type), IntConstant.v(paramSize));
    }

    public SpecialInvokeExpr createSpecialInvokeExpr(NewUnits newUnits, Local localModel, SootMethod calleeMethod, BaseBodyGenerator units) {
        return this.createSpecialInvokeExpr(localModel, calleeMethod, units.getValueForMethod(newUnits, calleeMethod, localModel));
    }

    public SpecialInvokeExpr createSpecialInvokeExpr(Local localModel, SootMethod calleeMethod, List<? extends Value> values) {
        if (calleeMethod.getParameterCount() != values.size()) {
            log.error("calleeMethod: {} with incorrect num of args: {}", calleeMethod, values.size());
        }

        return Jimple.v().newSpecialInvokeExpr(localModel, calleeMethod.makeRef(), values);
    }

    public NewUnits.BeforeUnit specialCallStatement(Local localModel, String methodSign, BaseBodyGenerator units) {
        SootMethod toCall = Scene.v().getMethod(methodSign);
        return this.specialCallStatement(localModel, toCall, units);
    }

    public NewUnits.BeforeUnit specialCallStatement(Local localModel, SootMethod calleeMethod, BaseBodyGenerator units) {
        NewUnits newUnits = NewUnits.alloc();
        return newUnits.before(Jimple.v().newInvokeStmt(this.createSpecialInvokeExpr(newUnits, localModel, calleeMethod, units)));
    }

    public Unit specialCallStatement(Local localModel, SootMethod calleeMethod, List<? extends Value> values) {
        return Jimple.v().newInvokeStmt(this.createSpecialInvokeExpr(localModel, calleeMethod, values));
    }

    public Unit specialCallStatement(Local localModel, String methodSign, List<? extends Value> values) {
        SootMethod toCall = Scene.v().getMethod(methodSign);
        return this.specialCallStatement(localModel, toCall, values);
    }

    private VirtualInvokeExpr createVirtualInvokeExpr(NewUnits newUnits, Local localModel, SootMethod calleeMethod, BaseBodyGenerator units) {
        return this.createVirtualInvokeExpr(localModel, calleeMethod, units.getValueForMethod(newUnits, calleeMethod, localModel));
    }

    public NewUnits.BeforeRhs createVirtualInvokeExpr(Local localModel, SootMethod calleeMethod, BaseBodyGenerator units) {
        NewUnits newUnits = NewUnits.alloc();
        return newUnits.before(this.createVirtualInvokeExpr(newUnits, localModel, calleeMethod, units));
    }

    public VirtualInvokeExpr createVirtualInvokeExpr(Local localModel, SootMethod calleeMethod, List<? extends Value> values) {
        if (calleeMethod.getParameterCount() != values.size()) {
            log.error("calleeMethod: {} with incorrect num of args: {}", calleeMethod, values.size());
        }

        return Jimple.v().newVirtualInvokeExpr(localModel, calleeMethod.makeRef(), values);
    }

    public NewUnits.BeforeUnit virtualCallStatement(Local localModel, String methodSign, BaseBodyGenerator units) {
        SootMethod toCall = Scene.v().getMethod(methodSign);
        return this.virtualCallStatement(localModel, toCall, units);
    }

    public Unit virtualCallStatement(Local localModel, String methodSign, List<? extends Value> values) {
        SootMethod toCall = Scene.v().getMethod(methodSign);
        return this.virtualCallStatement(localModel, toCall, values);
    }

    public NewUnits.BeforeUnit virtualCallStatement(Local localModel, SootMethod calleeMethod, BaseBodyGenerator units) {
        NewUnits newUnits = NewUnits.alloc();
        return newUnits.before(Jimple.v().newInvokeStmt(this.createVirtualInvokeExpr(newUnits, localModel, calleeMethod, units)));
    }

    public Unit virtualCallStatement(Local localModel, SootMethod calleeMethod, List<? extends Value> values) {
        return Jimple.v().newInvokeStmt(this.createVirtualInvokeExpr(localModel, calleeMethod, values));
    }

    public StaticInvokeExpr createStaticInvokeExpr(SootMethod calleeMethod, List<? extends Value> values) {
        if (calleeMethod.getParameterCount() != values.size()) {
            log.error("calleeMethod: {} with incorrect num of args: {}", calleeMethod, values.size());
        }

        return Jimple.v().newStaticInvokeExpr(calleeMethod.makeRef(), values);
    }

    public Unit staticCallStatement(SootMethod toCall, List<Value> values) {
        return Jimple.v().newInvokeStmt(this.createStaticInvokeExpr(toCall, values));
    }

    public Unit buildCallStatement(Local base, SootMethod methodToCall, List<Value> args) {
        return Jimple.v().newInvokeStmt(this.buildCallInvokeExpr(base, methodToCall, args));
    }

    public NewUnits.BeforeUnit buildCallStatement(Local base, SootMethod methodToCall, BaseBodyGenerator units) {
        NewUnits newUnits = NewUnits.alloc();
        return newUnits.before(Jimple.v().newInvokeStmt(this.buildCallInvokeExpr(base, methodToCall, units.getValueForMethod(newUnits, methodToCall, base))));
    }

    public InvokeExpr buildCallInvokeExpr(Local base, SootMethod methodToCall, List<Value> args) {
        return BaseBodyGenerator.buildMethodCall(methodToCall, base, args);
    }

    public Unit createIdentityStmt(Value var, Value identvar) {
        return Jimple.v().newIdentityStmt(var, identvar);
    }

    public void createIdentityStmt(Value var, Value identvar, BaseBodyGenerator units) {
        units.add(this.createIdentityStmt(var, identvar));
    }

    public ParameterRef createParamRef(Type type, int index) {
        return Jimple.v().newParameterRef(type, index);
    }

    public ThisRef createThisRef(String type) {
        return this.createThisRef(RefType.v(type));
    }

    public ThisRef createThisRef(RefType type) {
        return Jimple.v().newThisRef(type);
    }

    public ArrayRef createArrayRef(Value type, int index) {
        return Jimple.v().newArrayRef(type, IntConstant.v(index));
    }

    public void addVoidReturnStmt(BaseBodyGenerator units) {
        units.add(Jimple.v().newReturnVoidStmt());
    }

    public void addCommonReturnStmt(Value returnRef, BaseBodyGenerator units) {
        units.add(Jimple.v().newReturnStmt(returnRef));
    }

    public InstanceFieldRef createInstanceFieldRef(Value local, SootFieldRef sootFieldRef) {
        return Jimple.v().newInstanceFieldRef(local, sootFieldRef);
    }

    public StaticFieldRef createStaticFieldRef(SootFieldRef sootFieldRef) {
        return Jimple.v().newStaticFieldRef(sootFieldRef);
    }

    public JimpleBody newMethodBody(SootMethod sootMethod) {
        return Jimple.v().newBody(sootMethod);
    }
}
