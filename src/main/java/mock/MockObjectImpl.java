//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package mock;

import analysis.Implement;
import java.util.Collections;
import java.util.List;
import soot.ArrayType;
import soot.Local;
import soot.PrimType;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.SootMethodRef;
import soot.Unit;
import soot.jimple.Jimple;
import soot.jimple.JimpleBody;
import utils.BaseBodyGenerator;
import utils.JimpleUtils;
import utils.NewUnits;

public class MockObjectImpl implements MockObject {
    GenerateSyntheticClass gsc = new GenerateSyntheticClassImpl();

    public MockObjectImpl() {
    }

    public void mockJoinPoint(JimpleBody body, BaseBodyGenerator units) {
        JimpleUtils jimpleUtils = Implement.jimpleUtils;
        SootClass abstractClass = Scene.v().getSootClass("org.aspectj.lang.ProceedingJoinPoint");
        SootClass joinPointImpl = this.gsc.generateJoinPointImpl(abstractClass);
        Local joinPointLocal = jimpleUtils.addLocalVar(joinPointImpl.getShortName(), joinPointImpl.getName(), body);
        Local paramArray = jimpleUtils.addLocalVar("paramArray", ArrayType.v(RefType.v("java.lang.Object"), 1), body);
        int paramSize = body.getParameterLocals().size();
        jimpleUtils.createAssignStmt(joinPointLocal, joinPointImpl.getName(), units);
        NewUnits.BeforeUnit specialInit = jimpleUtils.specialCallStatement(joinPointLocal, JimpleUtils.getMinConstructorOrCreate(joinPointImpl).toString(), units);
        units.add(specialInit);
        jimpleUtils.createAssignStmt(paramArray, jimpleUtils.createNewArrayExpr("java.lang.Object", paramSize), units);

        for(int i = 0; i < paramSize; ++i) {
            Local paramLocal = body.getParameterLocal(i);
            Local paramLocalConverted;
            if (paramLocal.getType() instanceof PrimType) {
                RefType boxedType = ((PrimType)paramLocal.getType()).boxedType();
                SootMethodRef ref = Scene.v().makeMethodRef(boxedType.getSootClass(), "valueOf", Collections.singletonList(paramLocal.getType()), boxedType, true);
                paramLocalConverted = jimpleUtils.addLocalVar("primTypeParam" + i, boxedType, body);
                jimpleUtils.createAssignStmt(paramLocalConverted, Jimple.v().newStaticInvokeExpr(ref, paramLocal), units);
            } else {
                paramLocalConverted = paramLocal;
            }

            jimpleUtils.createAssignStmt(jimpleUtils.createArrayRef(paramArray, i), paramLocalConverted, units);
        }

        units.add(jimpleUtils.virtualCallStatement(joinPointLocal, joinPointImpl.getMethodByName("setArgs_synthetic").toString(), Collections.singletonList(paramArray)));
    }

    public Local mockBean(JimpleBody body, BaseBodyGenerator units, SootClass sootClass, SootMethod toCall) {
        JimpleUtils jimpleUtils = Implement.jimpleUtils;
        Local paramRef = jimpleUtils.addLocalVar(toCall.getName() + sootClass.getShortName(), sootClass.getName(), body);
        jimpleUtils.createAssignStmt(paramRef, sootClass.getName(), units);
        SootMethod init = JimpleUtils.getMinConstructor(sootClass);
        if (init != null) {
            NewUnits.BeforeUnit specialInit = jimpleUtils.specialCallStatement(paramRef, init, units);
            units.add(specialInit);
        }

        List<SootClass> classes;
        if (sootClass.isInterface()) {
            classes = Collections.emptyList();
        } else {
            classes = Scene.v().getActiveHierarchy().getSuperclassesOfIncluding(sootClass);
        }

        for(SootClass sc : classes) {
            for(SootMethod beanMethod : sc.getMethods()) {
                if (beanMethod.getName().startsWith("set") && !beanMethod.getParameterTypes().isEmpty()) {
                    units.add(jimpleUtils.buildCallStatement(paramRef, beanMethod, units));
                }
            }
        }

        return paramRef;
    }

    public Local mockHttpServlet(JimpleBody body, BaseBodyGenerator units, SootClass sootClass, SootMethod toCall) {
        JimpleUtils jimpleUtils = Implement.jimpleUtils;
        Local paramRef = jimpleUtils.addLocalVar(toCall.getName() + sootClass.getShortName(), sootClass.getName(), body);
        SootClass HttpServletImpl = this.gsc.generateHttpServlet(sootClass);
        jimpleUtils.createAssignStmt(paramRef, HttpServletImpl.getName(), units);
        Unit specialInit = jimpleUtils.specialCallStatement(paramRef, HttpServletImpl.getMethod("void <init>()").toString(), Collections.emptyList());
        units.add(specialInit);
        return paramRef;
    }
}
