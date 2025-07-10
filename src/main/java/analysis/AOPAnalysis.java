//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package analysis;

import bean.AOPTargetModel;
import bean.AspectModel;
import bean.InsertMethod;
import enums.AdviceEnum;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import mock.MockObject;
import soot.Local;
import soot.SootMethod;
import soot.Type;
import soot.Unit;
import soot.VoidType;
import soot.jimple.JimpleBody;
import soot.jimple.internal.JIdentityStmt;
import utils.BaseBodyGenerator;
import utils.BaseBodyGeneratorFactory;
import utils.JimpleUtils;

public class AOPAnalysis {
    public static Map<String, InsertMethod> insertMethodMap = new LinkedHashMap();
    public static boolean newVersion = false;

    public AOPAnalysis() {
    }

    public void processWeave(AOPTargetModel targetModel) {
        AOPParser ap = new AOPParser();
        SootMethod targetMethod = targetModel.getSootMethod();
        SootMethod proxyMethod = targetModel.getProxyMethod();
        this.modifyJimpleBody(proxyMethod);
        SootMethod currentMethod = proxyMethod;
        SootMethod preMethod = proxyMethod;
        AdviceEnum currentEnum = null;

        for(AspectModel aspectModel : targetModel.getAdvices()) {
            SootMethod insertTargetMethod;
            switch (aspectModel.getAnnotation()) {
                case AOP_AROUND:
                    preMethod = currentMethod;
                    SootMethod aroundMethod = ap.aroundParser(aspectModel, targetMethod);
                    ap.insertAOPAround(currentMethod, aroundMethod);
                    currentMethod = aroundMethod;
                    currentEnum = AdviceEnum.AOP_AROUND;
                    break;
                case AOP_BEFORE:
                    ap.insertAOPBefore(currentMethod, aspectModel.getSootMethod());
                    break;
                case AOP_AFTER:

                    if (newVersion) {
                        insertTargetMethod = currentMethod;
                    } else {
                        insertTargetMethod = preMethod;
                    }

                    ap.insertAOPAfter(insertTargetMethod, aspectModel.getSootMethod());
                    break;
                case AOP_AFTER_RETURNING:
                    if (newVersion) {
                        insertTargetMethod = currentMethod;
                    } else {
                        insertTargetMethod = preMethod;
                    }

                    ap.insertAOPAfterReturning(insertTargetMethod, aspectModel.getSootMethod(), aspectModel.getPointcutExpressions());
                case AOP_AFTER_THROWING:
            }
        }

        ap.insertAOPTarget(currentMethod, targetMethod, currentEnum);
    }

    public void modifyJimpleBody(SootMethod method) {
        JimpleUtils jimpleUtils = Implement.jimpleUtils;
        MockObject mockObject = Implement.mockObject;
        JimpleBody body = (JimpleBody)jimpleUtils.getMethodBody(method);
        List<Unit> returnList = new ArrayList();
        List<Unit> insertPointList = new ArrayList();
        BaseBodyGenerator units = BaseBodyGeneratorFactory.get(body);
        units.getUnits().removeIf((unit) -> !(unit instanceof JIdentityStmt) && !unit.toString().contains("localTarget = "));
        mockObject.mockJoinPoint(body, units);
        Type returnType = method.getReturnType();
        if (returnType instanceof VoidType) {
            jimpleUtils.addVoidReturnStmt(units);
        } else {
            Local returnRef = null;

            for(Local local : body.getLocals()) {
                if (local.getName().equals("returnRef")) {
                    returnRef = local;
                    break;
                }
            }

            if (returnRef == null) {
                returnRef = jimpleUtils.addLocalVar("returnRef", returnType, body);
            }

            jimpleUtils.addCommonReturnStmt(returnRef, units);
        }

        returnList.add(units.getUnits().getLast());
        insertPointList.add(units.getUnits().getLast());
        insertMethodMap.put(method.toString(), new InsertMethod(method, returnList, insertPointList));
    }

    public static void clear() {
        insertMethodMap.clear();
    }
}
