//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package analysis;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import soot.AmbiguousMethodException;
import soot.Hierarchy;
import soot.Local;
import soot.PatchingChain;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.SootField;
import soot.SootFieldRef;
import soot.SootMethod;
import soot.Type;
import soot.Unit;
import soot.Value;
import soot.VoidType;
import soot.jimple.JimpleBody;
import soot.jimple.internal.JReturnStmt;
import soot.jimple.internal.JReturnVoidStmt;
import utils.BaseBodyGenerator;
import utils.BaseBodyGeneratorFactory;
import utils.JimpleUtils;

public class IOCParser {
    private static final Logger logger = LoggerFactory.getLogger(IOCParser.class);
    private final Map<SootClass, String> initMap = new LinkedHashMap();
    private final Map<SootClass, SootClass> potentialImpl = new LinkedHashMap();

    public IOCParser() {
    }

    public void getIOCObject(SootClass sootClass, Set<SootMethod> allBeans) {
        JimpleUtils jimpleUtils = Implement.jimpleUtils;
        AnnotationAnalysis annotationAnalysis = new AnnotationAnalysis();
        Hierarchy hierarchy = Scene.v().getActiveHierarchy();
        String cName = null;
        String initStr = null;
        SootMethod initMethod = null;
        boolean ambiguous = false;

        try {
            initMethod = sootClass.getMethodByName("<init>");
        } catch (AmbiguousMethodException var25) {
            ambiguous = true;
        }

        for(SootMethod method : sootClass.getMethods()) {
            List<Type> paramOfAutoWiredMethod = annotationAnalysis.getParamOfAutoWiredMethod(method);
            if (paramOfAutoWiredMethod != null) {
                AnnotationAnalysis.autoMethodParams.addAll(paramOfAutoWiredMethod);
            }

            if (ambiguous && method.getName().equals("<init>")) {
                initMethod = method;
                break;
            }
        }

        for(SootField classField : sootClass.getFields()) {
            SootField field = annotationAnalysis.getFieldWithSpecialAnnos(classField, initMethod, ambiguous);
            if (field != null) {
                SootFieldRef sootFieldRef = field.makeRef();
                cName = field.getDeclaringClass().getName();
                String vtype = field.getType().toString();
                SootClass fieldClass = JimpleUtils.getClassUnsafe(field.getType());
                SootClass aClass = null;
                if (fieldClass != null) {
                    aClass = (SootClass)CreateEdge.interfaceToBeans.getOrDefault(fieldClass.getName(), null);
                }

                if (aClass != null && !aClass.isInterface()) {
                    SootClass beanClass = (SootClass)AOPParser.proxyMap.getOrDefault(aClass.getName(), aClass);
                    String realType = beanClass.getType().toString();
                    initStr = this.mapInitMethod(beanClass, this.initMap);

                    assert initMethod != null;

                    if (beanClass == aClass && !CreateEdge.prototypeComponents.contains(aClass)) {
                        this.initIOCObjectBySingleton(sootFieldRef, initMethod, vtype, aClass);
                    } else {
                        this.initIOCObjectByPrototype(sootFieldRef, initMethod, vtype, realType, initStr);
                    }
                } else {
                    assert initMethod != null;

                    if (fieldClass != null) {
                        if (fieldClass.isPhantom()) {
                            logger.warn("can't find this bean: " + fieldClass.getName() + " in " + sootClass);
                        } else if (!this.filterBaseClass(fieldClass)) {
                            if (!fieldClass.isInterface() && !fieldClass.isAbstract()) {
                                initStr = this.mapInitMethod(fieldClass, this.initMap);
                                this.initIOCObjectByPrototype(sootFieldRef, initMethod, vtype, fieldClass.getType().toString(), initStr);
                            } else {
                                SootClass hierarchyClass = null;
                                if (this.potentialImpl.containsKey(fieldClass)) {
                                    hierarchyClass = (SootClass)this.potentialImpl.get(fieldClass);
                                } else {
                                    List<SootClass> hierarchyClasses = fieldClass.isInterface() ? hierarchy.getImplementersOf(fieldClass) : hierarchy.getSubclassesOf(fieldClass);
                                    if (hierarchyClasses.size() > 0) {
                                        hierarchyClass = (SootClass)hierarchyClasses.get(0);
                                        this.potentialImpl.put(fieldClass, hierarchyClass);
                                    }
                                }

                                if (hierarchyClass != null) {
                                    initStr = this.mapInitMethod(hierarchyClass, this.initMap);
                                    this.initIOCObjectByPrototype(sootFieldRef, initMethod, vtype, hierarchyClass.getType().toString(), initStr);
                                } else {
                                    logger.warn("can't find this bean: " + fieldClass.getName() + " in " + sootClass);

                                    for(SootMethod bean : allBeans) {
                                        PatchingChain<Unit> units = jimpleUtils.getMethodBody(bean).getUnits();
                                        RefType returnType = null;
                                        SootClass returnClass = null;
                                        if (!(bean.getReturnType() instanceof VoidType) && (field.getType().equals(bean.getReturnType()) || ((RefType)bean.getReturnType()).getSootClass().getInterfaces().contains(((RefType)bean.getReturnType()).getSootClass()))) {
                                            for(Unit unit : units) {
                                                if (unit instanceof JReturnStmt) {
                                                    returnType = (RefType)((JReturnStmt)unit).getOpBox().getValue().getType();
                                                    returnClass = returnType.getSootClass();
                                                    break;
                                                }
                                            }

                                            assert initMethod != null;

                                            if (returnClass != null && !returnClass.isInterface() && !returnClass.isAbstract() && returnClass.getMethodUnsafe("void <init>()") != null) {
                                                initStr = returnClass.getMethodUnsafe("void <init>()").toString();
                                                String realType = returnType.toString();
                                                this.initIOCObjectByPrototype(sootFieldRef, initMethod, vtype, realType, initStr);
                                            } else {
                                                initStr = JimpleUtils.getMinConstructorOrCreate(bean.getDeclaringClass()).toString();
                                                String callStr = bean.toString();
                                                this.initIOCObjectByPrototype(sootFieldRef, initMethod, vtype, bean.getDeclaringClass().toString(), initStr, callStr);
                                            }
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

    }

    private boolean filterBaseClass(SootClass sc) {
        switch (sc.getName()) {
            case "java.lang.String":
                return true;
            default:
                return false;
        }
    }

    private String mapInitMethod(SootClass sootClass, Map<SootClass, String> initMap) {
        String initStr = null;
        if (initMap.containsKey(sootClass)) {
            initStr = (String)initMap.get(sootClass);
        } else {
            initStr = JimpleUtils.getMinConstructorOrCreate(sootClass).toString();
            initMap.put(sootClass, initStr);
        }

        return initStr;
    }

    private void initIOCObjectByPrototype(SootFieldRef sootFieldRef, SootMethod initMethod, String vtype, String declType, String initStr, String callStr) {
        JimpleUtils jimpleUtils = Implement.jimpleUtils;
        JimpleBody body = (JimpleBody)jimpleUtils.getMethodBody(initMethod);
        Local tmpRef = jimpleUtils.addLocalVar(vtype.substring(vtype.lastIndexOf(".") + 1).toLowerCase(), RefType.v(vtype), body);
        Local declRef = jimpleUtils.addLocalVar(declType.substring(declType.lastIndexOf(".") + 1).toLowerCase(), RefType.v(declType), body);
        Local thisRef = body.getThisLocal();
        BaseBodyGenerator units = BaseBodyGeneratorFactory.get(body);
        units.getUnits().removeIf((unit) -> unit instanceof JReturnVoidStmt);
        jimpleUtils.createAssignStmt(declRef, jimpleUtils.createNewExpr(declType), units);
        units.add(jimpleUtils.specialCallStatement(declRef, initStr, units));
        SootMethod toCall2 = Scene.v().getMethod(callStr);
        jimpleUtils.createAssignStmt(tmpRef, jimpleUtils.createVirtualInvokeExpr(declRef, toCall2, units), units);
        if (!sootFieldRef.isStatic()) {
            jimpleUtils.createAssignStmt(jimpleUtils.createInstanceFieldRef(thisRef, sootFieldRef), tmpRef, units);
        }

        jimpleUtils.addVoidReturnStmt(units);
    }

    public void initIOCObjectByPrototype(SootFieldRef sootFieldRef, SootMethod initMethod, String vtype, String realType, String initStr) {
        JimpleUtils jimpleUtils = Implement.jimpleUtils;
        JimpleBody body = (JimpleBody)jimpleUtils.getMethodBody(initMethod);
        Local thisRef = body.getThisLocal();
        Local tmpRef = jimpleUtils.addLocalVar(vtype.substring(vtype.lastIndexOf(".") + 1).toLowerCase(), vtype, body);
        BaseBodyGenerator units = BaseBodyGeneratorFactory.get(body);
        units.getUnits().removeIf((unit) -> unit instanceof JReturnVoidStmt);
        jimpleUtils.createAssignStmt(tmpRef, jimpleUtils.createNewExpr(realType), units);
        units.add(jimpleUtils.specialCallStatement(tmpRef, initStr, units));
        if (!sootFieldRef.isStatic()) {
            jimpleUtils.createAssignStmt(jimpleUtils.createInstanceFieldRef(thisRef, sootFieldRef), tmpRef, units);
            jimpleUtils.addVoidReturnStmt(units);
        }

    }

    public void initIOCObjectBySingleton(SootFieldRef sootFieldRef, SootMethod initMethod, String vtype, SootClass fieldClass) {
        JimpleUtils jimpleUtils = Implement.jimpleUtils;
        JimpleBody body = (JimpleBody)jimpleUtils.getMethodBody(initMethod);
        Local thisRef = body.getThisLocal();
        Local tmpRef = jimpleUtils.addLocalVar(vtype.substring(vtype.lastIndexOf(".") + 1).toLowerCase(), vtype, body);
        BaseBodyGenerator units = BaseBodyGeneratorFactory.get(body);
        units.getUnits().removeIf((unit) -> unit instanceof JReturnVoidStmt);
        SootClass singletonFactory = Scene.v().getSootClass(Config.SINGLETON_FACTORY_CNAME);
        Value returnValue = jimpleUtils.createStaticInvokeExpr(singletonFactory.getMethod(fieldClass.getName() + " get" + fieldClass.getShortName() + "()"), Collections.emptyList());
        jimpleUtils.createAssignStmt(tmpRef, returnValue, units);

        try {
            jimpleUtils.createAssignStmt(jimpleUtils.createInstanceFieldRef(thisRef, sootFieldRef), tmpRef, units);
        } catch (Exception var13) {
        }

        jimpleUtils.addVoidReturnStmt(units);
    }
}
