//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package utils;

import analysis.Implement;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import soot.ArrayType;
import soot.Body;
import soot.BooleanType;
import soot.ByteType;
import soot.CharType;
import soot.DoubleType;
import soot.FloatType;
import soot.IntType;
import soot.Local;
import soot.LocalGenerator;
import soot.LongType;
import soot.PrimType;
import soot.RefType;
import soot.Scene;
import soot.ShortType;
import soot.SootClass;
import soot.SootMethod;
import soot.Type;
import soot.Unit;
import soot.UnitPatchingChain;
import soot.Value;
import soot.jimple.AssignStmt;
import soot.jimple.DoubleConstant;
import soot.jimple.EqExpr;
import soot.jimple.FloatConstant;
import soot.jimple.IfStmt;
import soot.jimple.IntConstant;
import soot.jimple.InvokeExpr;
import soot.jimple.Jimple;
import soot.jimple.LongConstant;
import soot.jimple.NewArrayExpr;
import soot.jimple.NewExpr;
import soot.jimple.NullConstant;
import soot.jimple.StringConstant;

public class BaseBodyGenerator {
    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    protected boolean excludeSystemComponents = true;
    protected Map<SootClass, Local> localVarsForClasses = new LinkedHashMap();
    protected final Set<SootClass> failedClasses = new LinkedHashSet();
    protected boolean substituteCallParams = false;
    protected List<String> substituteClasses;
    protected boolean allowSelfReferences = false;
    protected boolean ignoreSystemClassParams = true;
    protected boolean allowNonPublicConstructors = false;
    protected final Set<SootMethod> failedMethods = new LinkedHashSet();
    protected String dummyClassName = "dummyMainClass";
    protected String dummyMethodName = "dummyMainMethod";
    protected boolean shallowMode = false;
    protected boolean overwriteDummyMainMethod = false;
    protected boolean warnOnConstructorLoop = false;
    protected Value intCounter;
    protected int conditionCounter;
    protected final Body body;
    protected final LocalGenerator generator;

    public BaseBodyGenerator(Body body) {
        this.body = body;
        this.generator = Scene.v().createLocalGenerator(body);
    }

    public Set<SootClass> getFailedClasses() {
        return new LinkedHashSet(this.failedClasses);
    }

    public Set<SootMethod> getFailedMethods() {
        return new LinkedHashSet(this.failedMethods);
    }

    public void setSubstituteCallParams(boolean b) {
        this.substituteCallParams = b;
    }

    public void setSubstituteClasses(List<String> l) {
        this.substituteClasses = l;
    }

    protected SootClass getOrCreateDummyMainClass() {
        SootClass mainClass = Scene.v().getSootClassUnsafe(this.dummyClassName);
        if (mainClass == null) {
            mainClass = Scene.v().makeSootClass(this.dummyClassName);
            mainClass.setResolvingLevel(3);
            Scene.v().addClass(mainClass);
        }

        return mainClass;
    }

    protected void createAdditionalFields() {
    }

    protected void createAdditionalMethods() {
    }

    protected String getNonCollidingFieldName(String baseName) {
        String fieldName = baseName;
        int fieldIdx = 0;

        for(SootClass mainClass = this.getOrCreateDummyMainClass(); mainClass.declaresFieldByName(fieldName); fieldName = baseName + "_" + fieldIdx++) {
        }

        return fieldName;
    }

    protected List<Value> getValueForMethod(NewUnits newUnits, SootMethod methodToCall, Local classLocal) {
        return this.getValueForMethod(newUnits, methodToCall, classLocal, Collections.emptySet());
    }

    protected List<Value> getValueForMethod(NewUnits newUnits, SootMethod methodToCall, Local classLocal, Set<SootClass> parentClasses) {
        if (classLocal == null && !methodToCall.isStatic()) {
            this.logger.warn("Cannot call method {}, because there is no local for base object: {}", methodToCall, methodToCall.getDeclaringClass());
            this.failedMethods.add(methodToCall);
            return null;
        } else {
            List<Value> args = new LinkedList();
            if (methodToCall.getParameterCount() > 0) {
                for(Type tp : methodToCall.getParameterTypes()) {
                    Set<SootClass> constructionStack = new LinkedHashSet();
                    if (!this.allowSelfReferences) {
                        constructionStack.add(methodToCall.getDeclaringClass());
                    }

                    args.add(this.getValueForType(newUnits, tp, constructionStack, parentClasses));
                }
            }

            return args;
        }
    }

    protected InvokeExpr buildMethodCall(NewUnits newUnits, SootMethod methodToCall, Local classLocal) {
        return this.buildMethodCall(newUnits, methodToCall, classLocal, Collections.emptySet());
    }

    protected InvokeExpr buildMethodCall(NewUnits newUnits, SootMethod methodToCall, Local classLocal, Set<SootClass> parentClasses) {
        List<Value> args = this.getValueForMethod(newUnits, methodToCall, classLocal, parentClasses);
        if (args == null) {
            return null;
        } else {
            InvokeExpr invokeExpr = buildMethodCall(methodToCall, classLocal, args);
            return invokeExpr;
        }
    }

    public static InvokeExpr buildMethodCall(SootMethod methodToCall, Local classLocal, List<Value> args) {
        InvokeExpr invokeExpr;
        if (methodToCall.getParameterCount() > 0) {
            if (methodToCall.isStatic()) {
                invokeExpr = Jimple.v().newStaticInvokeExpr(methodToCall.makeRef(), args);
            } else {
                assert classLocal != null : "Class local method was null for non-static method call";

                if (methodToCall.isConstructor()) {
                    invokeExpr = Jimple.v().newSpecialInvokeExpr(classLocal, methodToCall.makeRef(), args);
                } else if (methodToCall.getDeclaringClass().isInterface()) {
                    invokeExpr = Jimple.v().newInterfaceInvokeExpr(classLocal, methodToCall.makeRef(), args);
                } else {
                    invokeExpr = Jimple.v().newVirtualInvokeExpr(classLocal, methodToCall.makeRef(), args);
                }
            }
        } else if (methodToCall.isStatic()) {
            invokeExpr = Jimple.v().newStaticInvokeExpr(methodToCall.makeRef());
        } else {
            assert classLocal != null : "Class local method was null for non-static method call";

            if (methodToCall.isConstructor()) {
                invokeExpr = Jimple.v().newSpecialInvokeExpr(classLocal, methodToCall.makeRef());
            } else if (methodToCall.getDeclaringClass().isInterface()) {
                invokeExpr = Jimple.v().newInterfaceInvokeExpr(classLocal, methodToCall.makeRef(), args);
            } else {
                invokeExpr = Jimple.v().newVirtualInvokeExpr(classLocal, methodToCall.makeRef());
            }
        }

        return invokeExpr;
    }

    protected Value getValueForType(INewUnits newUnits, Type tp, Set<SootClass> constructionStack, Set<SootClass> parentClasses) {
        return this.getValueForType(newUnits, tp, constructionStack, parentClasses, (Set)null);
    }

    public Value getValueForType(INewUnits newUnits, Type tp) {
        Set<SootClass> constructionStack = new LinkedHashSet();
        return this.getValueForType(newUnits, tp, constructionStack, Collections.emptySet());
    }

    protected Value getValueForType(INewUnits newUnits, Type tp, Set<SootClass> constructionStack, Set<SootClass> parentClasses, Set<Local> generatedLocals) {
        if (isSimpleType(tp)) {
            return this.getSimpleDefaultValue(tp);
        } else if (tp instanceof RefType) {
            SootClass classToType = ((RefType)tp).getSootClass();
            if (classToType != null) {
                for(SootClass parent : parentClasses) {
                    if (this.isCompatible(parent, classToType)) {
                        Value val = (Value)this.localVarsForClasses.get(parent);
                        if (val != null) {
                            return val;
                        }
                    }
                }

                if (this.ignoreSystemClassParams && this.isClassInSystemPackage(classToType.getName())) {
                    return NullConstant.v();
                } else {
                    Value val = this.generateClassConstructor(newUnits, classToType, constructionStack, parentClasses, generatedLocals);
                    if (val == null) {
                        return NullConstant.v();
                    } else {
                        if (generatedLocals != null && val instanceof Local) {
                            generatedLocals.add((Local)val);
                        }

                        return val;
                    }
                }
            } else {
                throw new RuntimeException("Should never see me");
            }
        } else if (tp instanceof ArrayType) {
            Value arrVal = this.buildArrayOfType(newUnits, (ArrayType)tp, constructionStack, parentClasses, generatedLocals);
            if (arrVal == null) {
                this.logger.warn("Array parameter substituted by null");
                return NullConstant.v();
            } else {
                return arrVal;
            }
        } else {
            this.logger.warn("Unsupported parameter type: {}", tp.toString());
            return null;
        }
    }

    protected Value buildArrayOfType(INewUnits newUnits, ArrayType tp, Set<SootClass> constructionStack, Set<SootClass> parentClasses, Set<Local> generatedLocals) {
        Value singleElement = this.getValueForType(newUnits, tp.getElementType(), constructionStack, parentClasses);
        Local local = this.generator.generateLocal(tp);
        NewArrayExpr newArrayExpr = Jimple.v().newNewArrayExpr(tp.getElementType(), IntConstant.v(1));
        AssignStmt assignArray = Jimple.v().newAssignStmt(local, newArrayExpr);
        newUnits.add(assignArray);
        AssignStmt assign = Jimple.v().newAssignStmt(Jimple.v().newArrayRef(local, IntConstant.v(0)), singleElement);
        newUnits.add(assign);
        return local;
    }

    protected Local generateClassConstructor(NewUnits newUnits, SootClass createdClass) {
        return this.generateClassConstructor(newUnits, createdClass, new LinkedHashSet(), Collections.emptySet(), (Set)null);
    }

    protected Local generateClassConstructor(NewUnits newUnits, SootClass createdClass, Set<SootClass> parentClasses) {
        return this.generateClassConstructor(newUnits, createdClass, new LinkedHashSet(), parentClasses, (Set)null);
    }

    protected boolean acceptClass(SootClass clazz) {
        if (!clazz.isPhantom() && !clazz.isPhantomClass()) {
            return true;
        } else {
            this.logger.warn("Cannot generate constructor for phantom class {}", clazz.getName());
            return false;
        }
    }

    @Nonnull
    public static SootMethod getAnyConstructor(SootClass sc, boolean allowNonPublicConstructors) throws RuntimeException {
        SootMethod constructor = getAnyConstructorUnsafe(sc, allowNonPublicConstructors);
        if (constructor == null) {
            throw new RuntimeException("No constructor in class " + sc.getName());
        } else {
            return constructor;
        }
    }

    @Nullable
    public static SootMethod getAnyConstructorUnsafe(SootClass sc, boolean allowNonPublicConstructors) {
        List<SootMethod> constructors = getConstructorSorted(sc, allowNonPublicConstructors);
        return constructors.isEmpty() ? null : (SootMethod)constructors.get(0);
    }

    public static List<SootMethod> getConstructorSorted(SootClass sc, boolean allowNonPublicConstructors) {
        List<SootMethod> constructors = new ArrayList();

        for(SootMethod currentMethod : sc.getMethods()) {
            if (currentMethod.isConstructor() && (allowNonPublicConstructors || !currentMethod.isPrivate() && !currentMethod.isProtected())) {
                constructors.add(currentMethod);
            }
        }

        constructors.sort(new Comparator<SootMethod>() {
            public int compare(SootMethod o1, SootMethod o2) {
                if ((o1.isPrivate() || o1.isProtected()) == (o2.isPrivate() || o2.isProtected())) {
                    if (o1.getParameterCount() == o2.getParameterCount()) {
                        int o1Prims = 0;
                        int o2Prims = 0;

                        for(int i = 0; i < o1.getParameterCount(); ++i) {
                            if (o1.getParameterType(i) instanceof PrimType) {
                                ++o1Prims;
                            }
                        }

                        for(int i = 0; i < o2.getParameterCount(); ++i) {
                            if (o2.getParameterType(i) instanceof PrimType) {
                                ++o2Prims;
                            }
                        }

                        return o1Prims - o2Prims;
                    } else {
                        return o1.getParameterCount() - o2.getParameterCount();
                    }
                } else {
                    return !o1.isPrivate() && !o1.isProtected() ? -1 : 1;
                }
            }
        });
        return constructors;
    }

    protected Local generateClassConstructor(INewUnits newUnits, SootClass createdClass, Set<SootClass> constructionStack, Set<SootClass> parentClasses, Set<Local> tempLocals) {
        JimpleUtils jimpleUtils = Implement.jimpleUtils;
        if (createdClass != null && !this.failedClasses.contains(createdClass)) {
            Local existingLocal = (Local)this.localVarsForClasses.get(createdClass);
            if (existingLocal != null) {
                return existingLocal;
            } else if (!this.acceptClass(createdClass)) {
                this.failedClasses.add(createdClass);
                return null;
            } else if (isSimpleType(createdClass.getType())) {
                Local varLocal = this.generator.generateLocal(createdClass.getType());
                AssignStmt aStmt = Jimple.v().newAssignStmt(varLocal, this.getSimpleDefaultValue(createdClass.getType()));
                newUnits.add(aStmt);
                return varLocal;
            } else {
                boolean isInnerClass = createdClass.getName().contains("$");
                SootClass outerClass = isInnerClass ? Scene.v().getSootClassUnsafe(createdClass.getName().substring(0, createdClass.getName().lastIndexOf("$"))) : null;
                if (!constructionStack.add(createdClass)) {
                    if (this.warnOnConstructorLoop) {
                        this.logger.warn("Ran into a constructor generation loop for class " + createdClass + ", substituting with null...");
                    }

                    Local tempLocal = this.generator.generateLocal(RefType.v(createdClass));
                    AssignStmt assignStmt = Jimple.v().newAssignStmt(tempLocal, NullConstant.v());
                    newUnits.add(assignStmt);
                    return tempLocal;
                } else if (!createdClass.isInterface() && !createdClass.isAbstract()) {
                    List<SootMethod> constructors = getConstructorSorted(createdClass, this.allowNonPublicConstructors);
                    if (constructors.isEmpty()) {
                        this.failedClasses.add(createdClass);
                        return null;
                    } else {
                        SootMethod currentMethod = (SootMethod)constructors.remove(0);
                        List<Value> params = new LinkedList();

                        for(Type type : currentMethod.getParameterTypes()) {
                            Set<SootClass> newStack = new LinkedHashSet(constructionStack);
                            SootClass typeClass = type instanceof RefType ? ((RefType)type).getSootClass() : null;
                            if (typeClass != null && isInnerClass && typeClass == outerClass && this.localVarsForClasses.containsKey(outerClass)) {
                                params.add((Value)this.localVarsForClasses.get(outerClass));
                            } else if (this.shallowMode) {
                                if (isSimpleType(type)) {
                                    params.add(this.getSimpleDefaultValue(type));
                                } else {
                                    params.add(NullConstant.v());
                                }
                            } else {
                                Value val = this.getValueForType(newUnits, type, newStack, parentClasses, tempLocals);
                                params.add(val);
                            }
                        }

                        NewExpr newExpr = Jimple.v().newNewExpr(RefType.v(createdClass));
                        Local tempLocal = this.generator.generateLocal(RefType.v(createdClass));
                        AssignStmt assignStmt = Jimple.v().newAssignStmt(tempLocal, newExpr);
                        newUnits.add(assignStmt);
                        InvokeExpr vInvokeExpr;
                        if (!params.isEmpty() && !params.contains((Object)null)) {
                            vInvokeExpr = Jimple.v().newSpecialInvokeExpr(tempLocal, currentMethod.makeRef(), params);
                        } else {
                            vInvokeExpr = Jimple.v().newSpecialInvokeExpr(tempLocal, currentMethod.makeRef());
                        }

                        newUnits.add(Jimple.v().newInvokeStmt(vInvokeExpr));
                        if (tempLocals != null) {
                            tempLocals.add(tempLocal);
                        }

                        return tempLocal;
                    }
                } else {
                    return this.generateSubstitutedClassConstructor(newUnits, createdClass, constructionStack, parentClasses);
                }
            }
        } else {
            return null;
        }
    }

    protected Local generateSubstitutedClassConstructor(INewUnits newUnits, SootClass createdClass, Set<SootClass> constructionStack, Set<SootClass> parentClasses) {
        if (!this.substituteCallParams) {
            this.logger.warn("Cannot create valid constructor for {}, because it is {} and cannot substitute with subclass", createdClass, createdClass.isInterface() ? "an interface" : (createdClass.isAbstract() ? "abstract" : ""));
            this.failedClasses.add(createdClass);
            return null;
        } else {
            List<SootClass> classes;
            if (createdClass.isInterface()) {
                classes = Scene.v().getActiveHierarchy().getImplementersOf(createdClass);
            } else {
                classes = Scene.v().getActiveHierarchy().getSubclassesOf(createdClass);
            }

            for(SootClass sClass : classes) {
                if (this.substituteClasses.contains(sClass.toString())) {
                    Local cons = this.generateClassConstructor(newUnits, sClass, constructionStack, parentClasses, (Set)null);
                    if (cons != null) {
                        return cons;
                    }
                }
            }

            this.logger.warn("Cannot create valid constructor for {}, because it is {} and cannot substitute with subclass", createdClass, createdClass.isInterface() ? "an interface" : (createdClass.isAbstract() ? "abstract" : ""));
            this.failedClasses.add(createdClass);
            return null;
        }
    }

    protected static boolean isSimpleType(Type t) {
        if (t instanceof PrimType) {
            return true;
        } else {
            if (t instanceof RefType) {
                RefType rt = (RefType)t;
                if (rt.getSootClass().getName().equals("java.lang.String")) {
                    return true;
                }
            }

            return false;
        }
    }

    protected Value getSimpleDefaultValue(Type t) {
        if (t == RefType.v("java.lang.String")) {
            return StringConstant.v("");
        } else if (t instanceof CharType) {
            return IntConstant.v(0);
        } else if (t instanceof ByteType) {
            return IntConstant.v(0);
        } else if (t instanceof ShortType) {
            return IntConstant.v(0);
        } else if (t instanceof IntType) {
            return IntConstant.v(0);
        } else if (t instanceof FloatType) {
            return FloatConstant.v(0.0F);
        } else if (t instanceof LongType) {
            return LongConstant.v(0L);
        } else if (t instanceof DoubleType) {
            return DoubleConstant.v((double)0.0F);
        } else {
            return (Value)(t instanceof BooleanType ? IntConstant.v(0) : NullConstant.v());
        }
    }

    protected SootMethod findMethod(SootClass currentClass, String subsignature) {
        SootMethod m = currentClass.getMethodUnsafe(subsignature);
        if (m != null) {
            return m;
        } else {
            return currentClass.hasSuperclass() ? this.findMethod(currentClass.getSuperclass(), subsignature) : null;
        }
    }

    protected boolean isCompatible(SootClass actual, SootClass expected) {
        return Scene.v().getOrMakeFastHierarchy().canStoreType(actual.getType(), expected.getType());
    }

    protected void eliminateSelfLoops() {
        Iterator<Unit> unitIt = this.body.getUnits().iterator();

        while(unitIt.hasNext()) {
            Unit u = (Unit)unitIt.next();
            if (u instanceof IfStmt) {
                IfStmt ifStmt = (IfStmt)u;
                if (ifStmt.getTarget() == ifStmt) {
                    unitIt.remove();
                }
            }
        }

    }

    public void setDummyClassName(String dummyClassName) {
        this.dummyClassName = dummyClassName;
    }

    public void setDummyMethodName(String dummyMethodName) {
        this.dummyMethodName = dummyMethodName;
    }

    public void setAllowSelfReferences(boolean value) {
        this.allowSelfReferences = value;
    }

    public void setShallowMode(boolean shallowMode) {
        this.shallowMode = shallowMode;
    }

    public boolean getShallowMode() {
        return this.shallowMode;
    }

    public void setIgnoreSystemClassParams(boolean ignoreSystemClassParams) {
        this.ignoreSystemClassParams = ignoreSystemClassParams;
    }

    public void setAllowNonPublicConstructors(boolean allowNonPublicConstructors) {
        this.allowNonPublicConstructors = allowNonPublicConstructors;
    }

    public void setOverwriteDummyMainMethod(boolean overwriteDummyMainValue) {
        this.overwriteDummyMainMethod = overwriteDummyMainValue;
    }

    public boolean getOverwriteDummyMainMethod() {
        return this.overwriteDummyMainMethod;
    }

    public void setWarnOnConstructorLoop(boolean warnOnConstructorLoop) {
        this.warnOnConstructorLoop = warnOnConstructorLoop;
    }

    public boolean getWarnOnConstructorLoop() {
        return this.warnOnConstructorLoop;
    }

    protected void reset() {
        this.localVarsForClasses.clear();
        this.conditionCounter = 0;
    }

    protected void createIfStmt(Unit target) {
        if (target != null) {
            Jimple jimple = Jimple.v();
            EqExpr cond = jimple.newEqExpr(this.intCounter, IntConstant.v(this.conditionCounter++));
            IfStmt ifStmt = jimple.newIfStmt(cond, target);
            this.body.getUnits().add(ifStmt);
        }
    }

    public boolean isClassInSystemPackage(String className) {
        return (className.startsWith("android.") || className.startsWith("java.") || className.startsWith("javax.") || className.startsWith("sun.") || className.startsWith("org.omg.") || className.startsWith("org.w3c.dom.") || className.startsWith("com.google.") || className.startsWith("com.android.")) && this.excludeSystemComponents;
    }

    public void add(Unit unit) {
        this.body.getUnits().add(unit);
    }

    public void add(Collection<Unit> unit) {
        this.body.getUnits().addAll(unit);
    }

    public void add(NewUnits.BeforeUnit unit) {
        this.body.getUnits().add(unit.getInsertBeforeUnit());
        this.body.getUnits().insertBefore(unit.getInsertBefore().getUnits(), unit.getInsertBeforeUnit());
    }

    public void insertAfter(NewUnits.BeforeUnit unit, Unit point) {
        this.body.getUnits().insertAfter(unit.getInsertBeforeUnit(), point);
        this.body.getUnits().insertBefore(unit.getInsertBefore().getUnits(), unit.getInsertBeforeUnit());
    }

    public void insertAfter(Unit toInsert, Unit point) {
        this.body.getUnits().insertAfter(toInsert, point);
    }

    public void insertAfter(List<Unit> toInsert, Unit point) {
        this.body.getUnits().insertAfter(toInsert, point);
    }

    public void insertBefore(Unit toInsert, Unit point) {
        this.body.getUnits().insertBefore(toInsert, point);
    }

    public void insertBefore(List<Unit> toInsert, Unit point) {
        this.body.getUnits().insertBefore(toInsert, point);
    }

    public void addFirst(List<Unit> toInsert) {
        if (!this.body.getUnits().isEmpty()) {
            Unit lastParamIdentityStmt = NewUnitsAtFirstImmediately.getLastParamIdentityStmt(this.body.getUnits());
            if (lastParamIdentityStmt != null) {
                this.insertAfter(toInsert, lastParamIdentityStmt);
            } else {
                this.insertBefore(toInsert, this.body.getUnits().getFirst());
            }
        } else {
            this.add((Collection)toInsert);
        }

    }

    public UnitPatchingChain getUnits() {
        return this.body.getUnits();
    }

    public int size() {
        return this.body.getUnits().size();
    }

    public LocalGenerator getGenerator() {
        return this.generator;
    }
}
