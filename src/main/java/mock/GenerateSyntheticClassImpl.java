//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package mock;

import analysis.Config;
import analysis.CreateEdge;
import analysis.Implement;
import bean.ConstructorArgBean;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import soot.ArrayType;
import soot.Local;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.Unit;
import soot.Value;
import soot.VoidType;
import soot.jimple.JimpleBody;
import soot.jimple.internal.JIdentityStmt;
import soot.util.Chain;
import utils.BaseBodyGenerator;
import utils.BaseBodyGeneratorFactory;
import utils.JimpleUtils;

public class GenerateSyntheticClassImpl implements GenerateSyntheticClass {
    private static final Logger logger = LoggerFactory.getLogger(GenerateSyntheticClassImpl.class);
    private static final Map<String, SootClass> syntheticMethodImpls = new LinkedHashMap();
    private Map<String, Set<String>> classMethodsMap = new LinkedHashMap();

    public GenerateSyntheticClassImpl() {
    }

    public SootClass generateJoinPointImpl(SootClass abstractClass) {
        JimpleUtils jimpleUtils = Implement.jimpleUtils;
        String implClassName = "synthetic.method." + abstractClass.getShortName() + "Impl";
        SootClass customImplClass;
        if (syntheticMethodImpls.containsKey(implClassName)) {
            customImplClass = (SootClass)syntheticMethodImpls.get(implClassName);
        } else {
            customImplClass = this.createSubClass(implClassName, abstractClass, Scene.v().getObjectType().getSootClass());
            SootClass joinPointSc = Scene.v().getSootClassUnsafe("org.aspectj.lang.JoinPoint");
            if (joinPointSc == null) {
                joinPointSc = Scene.v().makeSootClass("org.aspectj.lang.JoinPoint", 512);
                Scene.v().addClass(joinPointSc);
            }

            customImplClass.addInterface(joinPointSc);
            Scene.v().addClass(customImplClass);
            customImplClass.setApplicationClass();
            SootField field = new SootField("args", ArrayType.v(RefType.v("java.lang.Object"), 1));
            customImplClass.addField(field);
            SootMethod initMethod = jimpleUtils.genDefaultConstructor(customImplClass);
            customImplClass.addMethod(initMethod);

            for(SootClass anInterface : customImplClass.getInterfaces()) {
                this.implCommonMethod(customImplClass, anInterface);
            }

            for(SootClass abstractClassInterface : abstractClass.getInterfaces()) {
                this.implCommonMethod(customImplClass, abstractClassInterface);
            }

            customImplClass.addMethod(jimpleUtils.genCustomMethod(customImplClass, "setArgs_synthetic", Arrays.asList(ArrayType.v(RefType.v("java.lang.Object"), 1)), VoidType.v()));
            syntheticMethodImpls.put(implClassName, customImplClass);
        }

        return customImplClass;
    }

    public SootClass generateMapperImpl(SootClass interfaceClass) {
        JimpleUtils jimpleUtils = Implement.jimpleUtils;
        String implClassName = "synthetic.method." + interfaceClass.getShortName() + "Impl";
        SootClass mapperImplClass;
        if (syntheticMethodImpls.containsKey(implClassName)) {
            mapperImplClass = (SootClass)syntheticMethodImpls.get(implClassName);
        } else {
            mapperImplClass = this.createSubClass(implClassName, interfaceClass, Scene.v().getObjectType().getSootClass());
            Scene.v().addClass(mapperImplClass);
            mapperImplClass.setApplicationClass();
            SootMethod initMethod = jimpleUtils.genDefaultConstructor(mapperImplClass);
            mapperImplClass.addMethod(initMethod);
            this.implCommonMethod(mapperImplClass, interfaceClass);
            syntheticMethodImpls.put(implClassName, mapperImplClass);
        }

        return mapperImplClass;
    }

    public SootClass generateProxy(SootClass targetSootClass) {
        JimpleUtils jimpleUtils = Implement.jimpleUtils;
        String proxyClassName = targetSootClass.getName() + "$" + Config.PROXY_CLASS_SUFFIX;
        SootClass proxyClass;
        if (syntheticMethodImpls.containsKey(proxyClassName)) {
            proxyClass = (SootClass)syntheticMethodImpls.get(proxyClassName);
        } else {
            boolean isInterface = targetSootClass.isInterface();
            if (isInterface) {
                proxyClass = this.createSubClass(proxyClassName, targetSootClass, Scene.v().getObjectType().getSootClass());
            } else {
                proxyClass = this.createSubClass(proxyClassName, (SootClass)null, targetSootClass);
            }

            Scene.v().addClass(proxyClass);
            SootField field = new SootField("target", targetSootClass.getType());
            proxyClass.addField(field);
            SootMethod initMethod;
            if (CreateEdge.prototypeComponents.contains(proxyClass)) {
                initMethod = jimpleUtils.genDefaultConstructor(proxyClass, field, false);
            } else {
                initMethod = jimpleUtils.genDefaultConstructor(proxyClass, field, true);
            }

            proxyClass.addMethod(initMethod);
            proxyClass.setApplicationClass();
            if (isInterface) {
                this.implCommonMethod(proxyClass, targetSootClass);
            } else {
                this.extendCommonMethod(proxyClass, targetSootClass);
            }

            syntheticMethodImpls.put(proxyClassName, proxyClass);
        }

        return proxyClass;
    }

    public void generateSingletonBeanFactory(Set<SootClass> beans, Map<String, List<ConstructorArgBean>> collect) {
        JimpleUtils jimpleUtils = Implement.jimpleUtils;
        if (collect != null && !collect.isEmpty()) {
            for(String className : collect.keySet()) {
                SootClass collectClass = Scene.v().getSootClassUnsafe(className, true);
                beans.add(collectClass);
            }
        }

        String singletonFactoryName = Config.SINGLETON_FACTORY_CNAME;
        SootClass singletonFactory = this.createSubClass(singletonFactoryName, (SootClass)null, Scene.v().getObjectType().getSootClass());
        Scene.v().addClass(singletonFactory);
        singletonFactory.setApplicationClass();
        Set<SootField> fields = new LinkedHashSet();

        for(SootClass bean : beans) {
            if (Config.linkSpringCGLIB_CallEntrySyntheticAndRequestMappingMethods) {
                SootField field = new SootField(bean.getShortName(), bean.getType(), 9);
                fields.add(field);
                singletonFactory.addField(field);
                singletonFactory.addMethod(jimpleUtils.genStaticCustomMethod(singletonFactory, "get" + bean.getShortName(), (List)null, bean.getType(), field));
            } else {
                singletonFactory.addMethod(jimpleUtils.genStaticCustomMethod(singletonFactory, "get" + bean.getShortName(), (List)null, bean.getType(), bean, collect));
            }
        }

        SootMethod initMethod = jimpleUtils.genDefaultConstructor(singletonFactory, (SootField)null, false);
        singletonFactory.addMethod(initMethod);
        SootMethod clinitMethod = jimpleUtils.genDefaultClinit(singletonFactory, fields, collect);
        singletonFactory.addMethod(clinitMethod);
    }

    public SootClass generateHttpServlet(SootClass abstractClass) {
        JimpleUtils jimpleUtils = Implement.jimpleUtils;
        String implClassName = "synthetic.method." + abstractClass.getShortName() + "Impl";
        SootClass customImplClass;
        if (syntheticMethodImpls.containsKey(implClassName)) {
            customImplClass = (SootClass)syntheticMethodImpls.get(implClassName);
        } else {
            customImplClass = this.createSubClass(implClassName, abstractClass, Scene.v().getObjectType().getSootClass());
            Scene.v().addClass(customImplClass);
            customImplClass.setApplicationClass();
            SootMethod initMethod = jimpleUtils.genDefaultConstructor(customImplClass);
            customImplClass.addMethod(initMethod);
            this.implCommonMethod(customImplClass, abstractClass);
            syntheticMethodImpls.put(implClassName, customImplClass);
        }

        return customImplClass;
    }

    public SootClass createSubClass(String implClassName, SootClass interfaceClass, SootClass superClass) {
        SootClass customImplClass = new SootClass(implClassName);
        customImplClass.setResolvingLevel(3);
        if (interfaceClass != null) {
            interfaceClass.setModifiers(interfaceClass.getModifiers() | 512);
            customImplClass.addInterface(interfaceClass);
        }

        customImplClass.setModifiers(1);
        customImplClass.setSuperclass(superClass);
        return customImplClass;
    }

    public void implCommonMethod(SootClass customImplClass, SootClass interfaceClass) {
        JimpleUtils jimpleUtils = Implement.jimpleUtils;

        for(SootMethod method : interfaceClass.getMethods()) {
            if (!method.isStatic() && !method.isPrivate() && !method.isFinal() && !method.isConstructor() && !method.isStaticInitializer()) {
                try {
                    customImplClass.getOrAddMethod(jimpleUtils.genCustomMethod(customImplClass, method.getName(), method.getParameterTypes(), method.getReturnType()));
                } catch (RuntimeException e) {
                    logger.warn("Exception: implCommonMethod customImplClass: {} interfaceClass: {}, e: {}", new Object[]{customImplClass, interfaceClass, e.getMessage()});
                }
            }
        }

        for(SootClass superInterface : interfaceClass.getInterfaces()) {
            this.implCommonMethod(customImplClass, superInterface);
        }

    }

    public void extendCommonMethod(SootClass customSubClass, SootClass superClass) {
        JimpleUtils jimpleUtils = Implement.jimpleUtils;

        for(SootMethod superMethod : superClass.getMethods()) {
            if (!superMethod.isStatic() && !superMethod.isPrivate() && !superMethod.isFinal() && !superMethod.isConstructor() && !superMethod.isStaticInitializer()) {
                SootMethod subMethod = new SootMethod(superMethod.getName(), superMethod.getParameterTypes(), superMethod.getReturnType(), superMethod.getModifiers());
                customSubClass.addMethod(subMethod);
                JimpleBody subMethodBody = (JimpleBody)jimpleUtils.getMethodBody(superMethod).clone();
                Chain<Local> locals = subMethodBody.getLocals();
                BaseBodyGenerator units = BaseBodyGeneratorFactory.get(subMethodBody);
                units.getUnits().removeIf((unit) -> !(unit instanceof JIdentityStmt) || !unit.toString().contains("@parameter"));
                subMethodBody.getTraps().clear();
                Set<Local> tmplocal = new LinkedHashSet();
                if (units.size() == 0) {
                    locals.removeIf(Objects::nonNull);
                } else {
                    for(Unit unit : units.getUnits()) {
                        JIdentityStmt jstmt = (JIdentityStmt)unit;
                        if (locals.contains(jstmt.getLeftOpBox().getValue())) {
                            tmplocal.add((Local)jstmt.getLeftOpBox().getValue());
                        }
                    }

                    locals.removeIf((local) -> !tmplocal.contains(local));
                }

                Local thisRef = jimpleUtils.addLocalVar("this", customSubClass.getType(), subMethodBody);
                jimpleUtils.createIdentityStmt(thisRef, jimpleUtils.createThisRef(customSubClass.getType()), units);
                Local returnRef = null;
                if (!(subMethod.getReturnType() instanceof VoidType)) {
                    returnRef = jimpleUtils.addLocalVar("returnRef", subMethod.getReturnType(), subMethodBody);
                }

                for(SootField field : customSubClass.getFields()) {
                    Local tmpRef = jimpleUtils.addLocalVar("localTarget", field.getType(), subMethodBody);
                    jimpleUtils.createAssignStmt(tmpRef, jimpleUtils.createInstanceFieldRef(thisRef, field.makeRef()), units);
                    if (returnRef != null) {
                        Value returnValue = jimpleUtils.createVirtualInvokeExpr(tmpRef, superMethod, subMethodBody.getParameterLocals());
                        jimpleUtils.createAssignStmt(returnRef, returnValue, units);
                    } else {
                        units.add(jimpleUtils.virtualCallStatement(tmpRef, superMethod, subMethodBody.getParameterLocals()));
                    }
                }

                if (returnRef != null) {
                    jimpleUtils.addCommonReturnStmt(returnRef, units);
                } else {
                    jimpleUtils.addVoidReturnStmt(units);
                }

                subMethod.setActiveBody(subMethodBody);
            }
        }

    }

    public static void clear() {
        syntheticMethodImpls.clear();
    }
}
