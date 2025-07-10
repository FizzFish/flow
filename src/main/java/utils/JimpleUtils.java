//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package utils;

import analysis.Config;
import analysis.Implement;
import bean.ConstructorArgBean;
import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import mock.MockObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import soot.ArrayType;
import soot.Body;
import soot.BooleanType;
import soot.DoubleType;
import soot.FloatType;
import soot.IntType;
import soot.Local;
import soot.LongType;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.Type;
import soot.Unit;
import soot.UnitPatchingChain;
import soot.Value;
import soot.VoidType;
import soot.dexpler.Util;
import soot.jimple.Constant;
import soot.jimple.DoubleConstant;
import soot.jimple.FloatConstant;
import soot.jimple.IntConstant;
import soot.jimple.Jimple;
import soot.jimple.JimpleBody;
import soot.jimple.LongConstant;
import soot.jimple.NullConstant;
import soot.jimple.ParameterRef;
import soot.jimple.StringConstant;
import soot.jimple.internal.JIdentityStmt;
import soot.jimple.internal.JimpleLocal;

public class JimpleUtils extends BaseJimpleUtils {
    private static final Logger log = LoggerFactory.getLogger(JimpleUtils.class);
    final Map<SootMethod, Body> methodToBody = new HashMap();

    public JimpleUtils() {
    }

    public Map<Type, Type> getImplType() {
        Map<Type, Type> map = new LinkedHashMap();
        map.put(RefType.v("java.util.List"), RefType.v("java.util.ArrayList"));
        map.put(RefType.v("java.util.Map"), RefType.v("java.util.HashMap"));
        map.put(RefType.v("java.util.Set"), RefType.v("java.util.HashSet"));
        return map;
    }

    public SootMethod genDefaultConstructor(SootClass customImplClass) {
        return this.genDefaultConstructor(customImplClass, (SootField)null, false);
    }

    public SootMethod genDefaultConstructor(SootClass customImplClass, SootField field, boolean singleton) {
        SootMethod initMethod = new SootMethod("<init>", (List)null, VoidType.v(), 1);
        SootMethod signature = customImplClass.getSuperclass().getMethodUnsafe("void <init>()");
        if (signature == null) {
            for(SootMethod method : customImplClass.getSuperclass().getMethods()) {
                if (method.isConstructor()) {
                    signature = method;
                    break;
                }
            }
        }

        if (signature != null) {
            JimpleBody initBody = this.createInitJimpleBody(initMethod, signature, customImplClass.getName(), field, singleton);
            initMethod.setActiveBody(initBody);
        }

        return initMethod;
    }

    public SootMethod genDefaultClinit(SootClass customImplClass, Set<SootField> fields, Map<String, List<ConstructorArgBean>> collect) {
        SootMethod initMethod = new SootMethod("<clinit>", (List)null, VoidType.v(), 8);
        JimpleBody initBody = this.createClinitJimpleBody(initMethod, customImplClass.getName(), fields, collect);
        initMethod.setActiveBody(initBody);
        return initMethod;
    }

    public SootMethod genStaticCustomMethod(SootClass customImplClass, String methodName, List<Type> parameterTypes, Type returnType, SootField field) {
        SootMethod implMethod = new SootMethod(methodName, parameterTypes, returnType, 9);
        JimpleBody body = this.createNewJimpleStaticBody(implMethod, customImplClass.getName(), field);
        implMethod.setActiveBody(body);
        return implMethod;
    }

    public SootMethod genStaticCustomMethod(SootClass customImplClass, String methodName, List<Type> parameterTypes, Type returnType, SootClass bean, Map<String, List<ConstructorArgBean>> collect) {
        SootMethod implMethod = new SootMethod(methodName, parameterTypes, returnType, 9);
        JimpleBody body = this.createNewJimpleStaticBody(implMethod, customImplClass.getName(), bean, collect);
        implMethod.setActiveBody(body);
        return implMethod;
    }

    public SootMethod genCustomMethod(SootClass customImplClass, String methodName, List<Type> parameterTypes, Type returnType) {
        SootMethod implMethod = new SootMethod(methodName, parameterTypes, returnType, 1);
        JimpleBody body = this.createNewJimpleBody(implMethod, new ArrayList(), customImplClass.getName());
        implMethod.setActiveBody(body);
        return implMethod;
    }

    public JimpleBody createInitJimpleBody(SootMethod method, SootMethod signature, String cName, SootField field, boolean singleton) {
        JimpleBody body = this.newMethodBody(method);
        Local thisRef = this.addLocalVar("this", cName, body);
        BaseBodyGenerator units = BaseBodyGeneratorFactory.get(body);
        this.createIdentityStmt(thisRef, this.createThisRef(cName), units);
        SootMethod toCall = Scene.v().getMethod(signature.getSignature());
        units.add(this.specialCallStatement(thisRef, toCall, units));
        if (field != null) {
            String vtype = field.getType().toString();
            Local tmpRef = this.addLocalVar(vtype.substring(vtype.lastIndexOf(".") + 1).toLowerCase(), vtype, body);
            if (!singleton && field.getType() instanceof RefType && !((RefType)field.getType()).getSootClass().isAbstract()) {
                this.createAssignStmt(tmpRef, this.createNewExpr(field.getType().toString()), units);
                units.add(this.specialCallStatement(tmpRef, getMinConstructorOrCreate(((RefType)field.getType()).getSootClass()).toString(), units));
            } else {
                SootClass singletonFactory = Scene.v().getSootClass(Config.SINGLETON_FACTORY_CNAME);
                Value returnValue = this.createStaticInvokeExpr(singletonFactory.getMethod(((RefType)field.getType()).getSootClass().getName() + " get" + ((RefType)field.getType()).getSootClass().getShortName() + "()"), Collections.emptyList());
                this.createAssignStmt(tmpRef, returnValue, units);
            }

            this.createAssignStmt(this.createInstanceFieldRef(thisRef, field.makeRef()), tmpRef, units);
        }

        this.addVoidReturnStmt(units);
        return body;
    }

    public Map.Entry<SootMethod, List<Value>> initArgBeans(SootClass argClazz, List<ConstructorArgBean> constructorArgBeans, Function<Type, Value> mismatchParam) {
        for(SootMethod argClazzMethod : argClazz.getMethods()) {
            if (argClazzMethod.isConstructor() && argClazzMethod.getParameterCount() == constructorArgBeans.size()) {
                UnitPatchingChain argmethodunit = this.getMethodBody(argClazzMethod).getUnits();
                List<Unit> argunitlist = new LinkedList(argmethodunit);
                LinkedHashMap<String, String> parammap = new LinkedHashMap();

                for(int i = 1; i <= argClazzMethod.getParameterCount(); ++i) {
                    if (argunitlist.get(i) instanceof JIdentityStmt) {
                        JIdentityStmt stmt = (JIdentityStmt)argunitlist.get(i);
                        JimpleLocal leftval = (JimpleLocal)stmt.getLeftOpBox().getValue();
                        String key = leftval.getName();
                        ParameterRef rightref = (ParameterRef)stmt.getRightOpBox().getValue();
                        String val = rightref.getType().toString();
                        parammap.put(key, val);
                    }
                }

                int index = 0;
                List<Value> parmas = new ArrayList();

                while(index < constructorArgBeans.size()) {
                    Type parameterType = argClazzMethod.getParameterType(index);
                    ConstructorArgBean argBean = (ConstructorArgBean)constructorArgBeans.get(index);
                    if (argBean.getArgType() != null) {
                        if (parameterType.toString().equals(argBean.getArgType())) {
                            Value c = this.getConstant(argBean.getArgType(), argBean.getArgValue(), false);
                            if (c != null) {
                                parmas.add(c);
                            }
                        }
                    } else if (argBean.getArgName() != null) {
                        if (parammap.containsKey(argBean.getArgName())) {
                            Value c = this.getConstant((String)parammap.get(argBean.getArgName()), argBean.getArgValue(), false);
                            if (c != null) {
                                parmas.add(c);
                            }
                        }
                    } else if (argBean.getRefType() != null && parammap.containsKey(argBean.getRefType())) {
                        Value c = this.getConstant("", "", true);
                        if (c != null) {
                            parmas.add(c);
                        }
                    }

                    ++index;
                    if (parmas.size() != index) {
                        Value parameter = (Value)mismatchParam.apply(parameterType);
                        parmas.add(parameter);
                    }
                }

                List<Value> arguments = (List)parmas.stream().filter(Objects::nonNull).collect(Collectors.toList());
                if (arguments.size() == argClazzMethod.getParameterCount()) {
                    return new AbstractMap.SimpleEntry(argClazzMethod, arguments);
                }
            }
        }

        return null;
    }

    public Value newBeanObjectAndReturn(BaseBodyGenerator units, SootClass bean, Map<String, List<ConstructorArgBean>> collect) {
        boolean hasargFlag = false;
        if (collect != null && collect.size() > 0) {
            hasargFlag = true;
        }

        RefType beanType = bean.getType();
        String vtype = beanType.toString();
        Body body = units.body;
        NewUnitsAtFirstImmediately insertAtFirst = new NewUnitsAtFirstImmediately(units);
        boolean hasConstructorFlag = false;
        Value beanObject;
        if (bean.isConcrete()) {
            beanObject = this.createNewExpr(bean.getType().toString());
        } else {
            beanObject = units.getValueForType(insertAtFirst, beanType);
            hasConstructorFlag = true;
        }

        Local tmpRef = this.addLocalVar(vtype.substring(vtype.lastIndexOf(".") + 1).toLowerCase(), vtype, body);
        this.createAssignStmt(tmpRef, beanObject, units);
        if (hasargFlag && collect.containsKey(vtype)) {
            SootClass argClazz = getClassUnsafe(vtype);
            if (argClazz == null) {
                argClazz = beanType.getSootClass();
            }

            List<ConstructorArgBean> constructorArgBeans = (List)collect.get(vtype);
            Map.Entry<SootMethod, List<Value>> arguments = this.initArgBeans(argClazz, constructorArgBeans, (parameterType) -> null);
            if (arguments != null) {
                arguments = this.initArgBeans(argClazz, constructorArgBeans, (parameterType) -> units.getValueForType(insertAtFirst, parameterType));
            }

            if (arguments != null) {
                units.add(this.specialCallStatement(tmpRef, ((SootMethod)arguments.getKey()).toString(), (List)arguments.getValue()));
            }
        } else if (!hasConstructorFlag) {
            for(SootMethod sootMethod : Scene.v().getSootClass(vtype).getMethods()) {
                if (sootMethod.isConstructor()) {
                    units.add(this.specialCallStatement(tmpRef, sootMethod, units));
                    break;
                }
            }
        }

        return tmpRef;
    }

    public JimpleBody createClinitJimpleBody(SootMethod method, String cName, Set<SootField> fields, Map<String, List<ConstructorArgBean>> collect) {
        JimpleBody body = this.newMethodBody(method);
        BaseBodyGenerator units = BaseBodyGeneratorFactory.get(body);
        if (Config.linkSpringCGLIB_CallEntrySyntheticAndRequestMappingMethods) {
            Map<SootClass, Value> exists = new HashMap(fields.size());

            for(SootField field : fields) {
                if (field.getType() instanceof RefType) {
                    SootClass fieldTypeClass = ((RefType)field.getType()).getSootClass();
                    Value tmpRef;
                    if (exists.containsKey(fieldTypeClass)) {
                        tmpRef = (Value)exists.get(fieldTypeClass);
                    } else {
                        tmpRef = this.newBeanObjectAndReturn(units, ((RefType)field.getType()).getSootClass(), collect);
                        exists.put(fieldTypeClass, tmpRef);
                    }

                    this.createAssignStmt(this.createStaticFieldRef(field.makeRef()), tmpRef, units);
                }
            }
        }

        this.addVoidReturnStmt(units);
        return body;
    }

    private Constant getConstant(String typesign, String value, boolean isclazz) {
        String s = typesign.toLowerCase();
        if (isclazz) {
            return NullConstant.v();
        } else if (value == null) {
            return null;
        } else if (s.contains("string")) {
            return StringConstant.v(value);
        } else if (s.contains("int")) {
            return IntConstant.v(Integer.parseInt(value));
        } else if (s.contains("double")) {
            return DoubleConstant.v(Double.parseDouble(value));
        } else if (s.contains("float")) {
            return FloatConstant.v(Float.parseFloat(value));
        } else {
            return s.contains("long") ? LongConstant.v(Long.parseLong(value)) : null;
        }
    }

    public JimpleBody createNewJimpleBody(SootMethod method, List<SootMethod> signatures, String cName) {
        List<Value> parameterValues = new ArrayList();
        JimpleBody body = this.newMethodBody(method);
        Local thisRef = this.addLocalVar("this", cName, body);
        BaseBodyGenerator units = BaseBodyGeneratorFactory.get(body);
        this.createIdentityStmt(thisRef, this.createThisRef(cName), units);

        for(int i = 0; i < method.getParameterCount(); ++i) {
            Type parameterType = method.getParameterType(i);
            Local param = this.addLocalVar("param" + i, parameterType, body);
            this.createIdentityStmt(param, this.createParamRef(parameterType, i), units);
            parameterValues.add(param);
        }

        if (method.getName().equals("setArgs_synthetic")) {
            SootField sootField = Scene.v().getSootClass(cName).getFieldByName("args");
            Local param = body.getParameterLocal(0);
            this.createAssignStmt(this.createInstanceFieldRef(thisRef, sootField.makeRef()), param, units);
        }

        NewUnitsAtFirstImmediately insertAtFirst = new NewUnitsAtFirstImmediately(units);

        for(int i = 0; i < signatures.size(); ++i) {
            SootMethod toCall = Scene.v().getMethod(((SootMethod)signatures.get(i)).toString());
            int parameterCount = toCall.getParameterCount();
            List<Value> paramList = new ArrayList();

            for(int j = 0; j < parameterCount - parameterValues.size(); ++j) {
                Type parameterTy = toCall.getParameterType(j);
                Value p = units.getValueForType(insertAtFirst, parameterTy);
                paramList.add(p);
            }

            paramList.addAll(parameterValues);
            String declaringClassName = ((SootMethod)signatures.get(i)).getDeclaringClass().getName();
            if (!declaringClassName.equals(cName) && !declaringClassName.equals("java.lang.Object") && !(((SootMethod)signatures.get(i)).getReturnType() instanceof VoidType)) {
                Local virtualRef = this.addLocalVar("virtual" + i, declaringClassName, body);
                this.createAssignStmt(virtualRef, declaringClassName, units);
                units.add(this.specialCallStatement(virtualRef, ((SootMethod)signatures.get(i)).getDeclaringClass().getMethod("void <init>()").toString(), Collections.emptyList()));
            } else if (((SootMethod)signatures.get(i)).getReturnType() instanceof VoidType || method.getName().startsWith(Config.CALL_ENTRY_NAME)) {
                units.add(this.specialCallStatement(thisRef, toCall.toString(), paramList));
            }
        }

        Type returnType = method.getReturnType();
        Supplier<Value> ret = () -> units.getValueForType(insertAtFirst, returnType);
        if (returnType instanceof RefType) {
            this.addCommonReturnStmt((Value)ret.get(), units);
        } else if (returnType instanceof VoidType) {
            this.addVoidReturnStmt(units);
        } else if (returnType instanceof BooleanType) {
            this.addCommonReturnStmt((Value)ret.get(), units);
        } else if (returnType instanceof IntType) {
            this.addCommonReturnStmt((Value)ret.get(), units);
        } else if (returnType instanceof LongType) {
            this.addCommonReturnStmt((Value)ret.get(), units);
        } else if (returnType instanceof DoubleType) {
            this.addCommonReturnStmt((Value)ret.get(), units);
        } else if (returnType instanceof ArrayType) {
            if (Scene.v().getSootClass(cName).getFields().size() > 0) {
                SootField sootField = Scene.v().getSootClass(cName).getFieldByName("args");
                if (sootField != null && sootField.getType().equals(returnType) && method.getName().contains("getArgs")) {
                    Local returnRef = this.addLocalVar("returnRef", returnType, body);
                    this.createAssignStmt(returnRef, this.createInstanceFieldRef(thisRef, sootField.makeRef()), units);
                    this.addCommonReturnStmt(returnRef, units);
                }
            } else {
                this.addCommonReturnStmt((Value)ret.get(), units);
            }
        }

        return body;
    }

    public JimpleBody createNewJimpleStaticBody(SootMethod method, String cName, SootField field) {
        JimpleBody body = this.newMethodBody(method);
        BaseBodyGenerator units = BaseBodyGeneratorFactory.get(body);
        Local localRef = null;
        if (method.getName().equals("get" + field.getName())) {
            localRef = this.addLocalVar(field.getName().toLowerCase(), field.getType(), body);
            this.createAssignStmt(localRef, this.createStaticFieldRef(field.makeRef()), units);
        }

        NewUnitsAtFirstImmediately insertAtFirst = new NewUnitsAtFirstImmediately(units);
        Type returnType = method.getReturnType();
        if (returnType instanceof RefType) {
            if (localRef != null) {
                this.addCommonReturnStmt(localRef, units);
            } else {
                Value ret = units.getValueForType(insertAtFirst, returnType);
                this.addCommonReturnStmt(ret, units);
            }
        }

        return body;
    }

    public JimpleBody createNewJimpleStaticBody(SootMethod method, String cName, SootClass bean, Map<String, List<ConstructorArgBean>> collect) {
        JimpleBody body = this.newMethodBody(method);
        BaseBodyGenerator units = BaseBodyGeneratorFactory.get(body);
        Local localRef = null;
        if (method.getName().equals("get" + bean.getShortName())) {
            localRef = this.addLocalVar(bean.getShortName().toLowerCase(), bean.getType(), body);
            Value tmpRef = this.newBeanObjectAndReturn(units, bean, collect);
            this.createAssignStmt(localRef, tmpRef, units);
        }

        NewUnitsAtFirstImmediately insertAtFirst = new NewUnitsAtFirstImmediately(units);
        Type returnType = method.getReturnType();
        if (returnType instanceof RefType) {
            if (localRef != null) {
                this.addCommonReturnStmt(localRef, units);
            } else {
                Value ret = units.getValueForType(insertAtFirst, returnType);
                this.addCommonReturnStmt(ret, units);
            }
        }

        return body;
    }

    @Nonnull
    public Body getMethodBody(@Nonnull SootMethod m) {
        if (m.hasActiveBody()) {
            return m.getActiveBody();
        } else {
            Body body = (Body)this.methodToBody.get(m);
            if (body != null) {
                return body;
            } else {
                SootClass declaringClass = m.getDeclaringClass();
                if (declaringClass != null && !declaringClass.isPhantomClass() && m.isConcrete() && m.getSource() != null) {
                    body = m.retrieveActiveBody();
                }

                if (body == null) {
                    JimpleBody jimpleBody = Jimple.v().newBody(m);
                    jimpleBody.insertIdentityStmts();
                    Util.emptyBody(jimpleBody);
                    if (m.isConcrete()) {
                        m.setActiveBody(jimpleBody);
                    }

                    body = jimpleBody;
                }

                this.methodToBody.putIfAbsent(m, body);
                return body;
            }
        }
    }

    public JimpleBody createJimpleBody(SootMethod method, List<SootMethod> signatures, String cName) {
        MockObject mockObject = Implement.mockObject;
        Value virtualCallWithReturn = null;
        JimpleBody body = this.newMethodBody(method);
        BaseBodyGenerator units = BaseBodyGeneratorFactory.get(body);
        Local thisRef;
        if (method.isStatic()) {
            thisRef = this.addLocalVar("instance", cName, body);
            Unit localInitAssign = this.createAssignStmt(thisRef, cName);
            units.getUnits().addFirst(localInitAssign);
            units.insertAfter(this.specialCallStatement(thisRef, getMinConstructorOrCreate(method.getDeclaringClass()).toString(), units), localInitAssign);
        } else {
            thisRef = this.addLocalVar("this", cName, body);
            this.createIdentityStmt(thisRef, this.createThisRef(cName), units);
        }

        NewUnitsAtFirstImmediately insertAtFirst = new NewUnitsAtFirstImmediately(units);

        for(int i = 0; i < signatures.size(); ++i) {
            SootMethod toCall = Scene.v().getMethod(((SootMethod)signatures.get(i)).toString());
            int parameterCount = toCall.getParameterCount();
            List<Value> paramList = new ArrayList();

            for(int j = 0; j < parameterCount; ++j) {
                Type parameterTy = toCall.getParameterType(j);
                if (this.isBaseTypes(parameterTy)) {
                    Value p = units.getValueForType(insertAtFirst, parameterTy);
                    paramList.add(p);
                } else {
                    Type t = Scene.v().getTypeUnsafe(parameterTy.toQuotedString(), true);
                    if (t instanceof RefType) {
                        SootClass sootClass = ((RefType)t).getSootClass();
                        if (sootClass != null && !sootClass.isPhantom() && sootClass.isConcrete() && !sootClass.isJavaLibraryClass() && !sootClass.getMethods().isEmpty()) {
                            try {
                                Local paramRef = mockObject.mockBean(body, units, sootClass, toCall);
                                paramList.add(paramRef);
                            } catch (Exception e) {
                                e.printStackTrace();
                                Value p = units.getValueForType(insertAtFirst, parameterTy);
                                paramList.add(p);
                            }
                        } else if (sootClass != null && sootClass.getName().contains("HttpServlet")) {
                            Local paramRef = mockObject.mockHttpServlet(body, units, sootClass, toCall);
                            paramList.add(paramRef);
                        } else if (sootClass != null && sootClass.getName().equals("java.lang.String")) {
                            Value str = units.getValueForType(insertAtFirst, parameterTy);
                            paramList.add(str);
                        } else {
                            Value p = units.getValueForType(insertAtFirst, parameterTy);
                            paramList.add(p);
                        }
                    } else {
                        Set<SootClass> constructionStack = new LinkedHashSet();
                        paramList.add(units.getValueForType(insertAtFirst, t, constructionStack, Collections.emptySet()));
                    }
                }
            }

            String declaringClassName = ((SootMethod)signatures.get(i)).getDeclaringClass().getName();
            if (!declaringClassName.equals(cName) && !declaringClassName.equals("java.lang.Object") && !(((SootMethod)signatures.get(i)).getReturnType() instanceof VoidType)) {
                Local virtualRef = this.addLocalVar("virtual" + i, declaringClassName, body);
                this.createAssignStmt(virtualRef, declaringClassName, units);
                units.add(this.specialCallStatement(virtualRef, ((SootMethod)signatures.get(i)).getDeclaringClass().getMethod("void <init>()").toString(), Collections.emptyList()));
                virtualCallWithReturn = this.createVirtualInvokeExpr(virtualRef, toCall, paramList);
            } else if (!(((SootMethod)signatures.get(i)).getReturnType() instanceof VoidType) && !method.getName().startsWith(Config.CALL_ENTRY_NAME)) {
                virtualCallWithReturn = this.createSpecialInvokeExpr(thisRef, toCall, paramList);
            } else {
                units.add(this.buildCallStatement(thisRef, toCall, paramList));
            }
        }

        Type returnType = method.getReturnType();
        if (returnType instanceof RefType) {
            Local returnRef = this.addLocalVar("returnRef", returnType, body);
            if (((RefType)returnType).getSootClass().isInterface()) {
                returnType = (Type)this.getImplType().get(returnType);
            }

            if (virtualCallWithReturn != null) {
                this.createAssignStmt(returnRef, virtualCallWithReturn, units);
            } else {
                this.createAssignStmt(returnRef, this.createNewExpr((RefType)returnType), units);
                units.add(this.specialCallStatement(returnRef, ((RefType)returnType).getSootClass().getMethod("void <init>()"), Collections.emptyList()));
            }

            this.addCommonReturnStmt(returnRef, units);
        } else if (returnType instanceof VoidType) {
            this.addVoidReturnStmt(units);
        } else if (returnType instanceof IntType) {
            this.addCommonReturnStmt(IntConstant.v(0), units);
        } else if (returnType instanceof LongType) {
            this.addCommonReturnStmt(LongConstant.v(0L), units);
        } else if (returnType instanceof DoubleType) {
            this.addCommonReturnStmt(DoubleConstant.v((double)0.0F), units);
        }

        return body;
    }

    private boolean isBaseTypes(Type target) {
        return target instanceof LongType || target instanceof IntType || target instanceof DoubleType || target instanceof FloatType || target instanceof ArrayType || target instanceof BooleanType;
    }

    @Nullable
    public static SootClass getClassUnsafe(Type t) {
        return t instanceof RefType ? ((RefType)t).getSootClass() : null;
    }

    @Nullable
    public static SootClass getClassUnsafe(String type) {
        return getClassUnsafe(Scene.v().getTypeUnsafe(type, true));
    }

    @Nullable
    public static SootMethod getMinConstructor(SootClass sootClass) {
        SootMethod defaultInit = sootClass.getMethodUnsafe("void <init>()");
        if (defaultInit != null) {
            return defaultInit;
        } else {
            Optional<SootMethod> first = sootClass.getMethods().stream().filter(SootMethod::isConstructor).sorted(Comparator.comparing(SootMethod::getSubSignature)).min(Comparator.comparingInt(SootMethod::getParameterCount));
            return first.isPresent() ? (SootMethod)first.get() : null;
        }
    }

    public static SootMethod getMinConstructorOrCreate(SootClass sootClass) {
        SootMethod constructor = getMinConstructor(sootClass);
        if (constructor == null) {
            SootMethod createMethod = new SootMethod("<init>", (List)null, VoidType.v(), 1);
            sootClass.addMethod(createMethod);
            constructor = createMethod;
        }

        return constructor;
    }
}
