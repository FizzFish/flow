//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package analysis;

import bean.AOPTargetModel;
import bean.AopXMLResultBean;
import bean.AspectModel;
import bean.ConstructorArgBean;
import enums.AdviceEnum;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import mock.GenerateSyntheticClass;
import mock.GenerateSyntheticClassImpl;
import org.dom4j.Document;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import soot.ArrayType;
import soot.FastHierarchy;
import soot.Hierarchy;
import soot.Local;
import soot.RefType;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.Unit;
import soot.VoidType;
import soot.jimple.Jimple;
import soot.jimple.JimpleBody;
import soot.jimple.internal.JReturnVoidStmt;
import soot.tagkit.AnnotationElem;
import soot.tagkit.AnnotationStringElem;
import soot.tagkit.AnnotationTag;
import soot.tagkit.VisibilityAnnotationTag;
import soot.util.Chain;
import utils.BaseBodyGenerator;
import utils.BaseBodyGeneratorFactory;
import utils.EnumUtils;
import utils.JimpleUtils;
import utils.XMLDocumentHolder;

public class CreateEdge {
    public static final Set<String> componentPackages = new LinkedHashSet();
    private static final Logger log = LoggerFactory.getLogger(CreateEdge.class);
    private final AnnotationAnalysis annotationAnalysis = new AnnotationAnalysis();
    private final IOCParser iocParser = new IOCParser();
    private final AOPAnalysis aopAnalysis = new AOPAnalysis();
    private final Set<SootMethod> allBeans = new LinkedHashSet();
    public static final Set<SootClass> allBeansAndInterfaces = new LinkedHashSet();
    public static final Set<SootClass> singletonComponents = new LinkedHashSet();
    public static final Set<SootClass> prototypeComponents = new LinkedHashSet();
    public static final Map<String, SootClass> interfaceToBeans = new LinkedHashMap();
    public final Chain<SootClass> sootClassChain = Scene.v().getApplicationClasses();
    public static final Map<String, AspectModel> aspectModelMap = new LinkedHashMap();
    protected String dummyClassName = "synthetic.method.dummyMainClass";
    public SootMethod projectMainMethod;

    public CreateEdge() {
    }

    public void initCallGraph(Config config) {
        this.scanAllBeans(config);
        this.aspectAnalysis(config);
        this.dependencyInjectAnalysis();
        this.generateEntryPoints();
    }

    public void clear() {
        AnnotationAnalysis.clear();
        AOPAnalysis.clear();
        AOPParser.clear();
        GenerateSyntheticClassImpl.clear();
        componentPackages.clear();
        allBeansAndInterfaces.clear();
        singletonComponents.clear();
        prototypeComponents.clear();
        interfaceToBeans.clear();
        aspectModelMap.clear();
    }

    public void scanAllBeans(Config config) {
        Set<String> bean_xml_paths = config.bean_xml_paths;
        if (!bean_xml_paths.isEmpty()) {
            for(XmlBeanClazz xmlBeanSootClazz : this.getXMLBeanSootClazzes(bean_xml_paths)) {
                for(SootClass anInterface : xmlBeanSootClazz.getSootClass().getInterfaces()) {
                    interfaceToBeans.put(anInterface.getName(), xmlBeanSootClazz.getSootClass());
                }

                interfaceToBeans.put(xmlBeanSootClazz.getSootClass().getName(), xmlBeanSootClazz.getSootClass());
                if (xmlBeanSootClazz.getScope().equals("singleton")) {
                    singletonComponents.add(xmlBeanSootClazz.getSootClass());
                } else if (xmlBeanSootClazz.getScope().equals("prototype")) {
                    prototypeComponents.add(xmlBeanSootClazz.getSootClass());
                }
            }
        }

        XMLDocumentHolder xmlHolder = this.getXMLHolder(bean_xml_paths);
        Map<String, List<ConstructorArgBean>> collect = null;
        if (xmlHolder != null) {
            List<ConstructorArgBean> argConstructors = xmlHolder.getArgConstructors();
            collect = (Map)argConstructors.stream().filter((c) -> c.getClazzName() != null).collect(Collectors.groupingBy(ConstructorArgBean::getClazzName, Collectors.toList()));
        }

        GenerateSyntheticClass gsc = new GenerateSyntheticClassImpl();
        Collection<SootClass> elementsUnsorted = (Collection)this.sootClassChain.getElementsUnsorted().stream().sorted(Comparator.comparing(SootClass::getName)).collect(Collectors.toList());
        Iterator<SootClass> iterator = elementsUnsorted.iterator();
        Set<SootClass> prototypeInterfaces = new LinkedHashSet();

        while(iterator.hasNext()) {
            SootClass sootClass = (SootClass)iterator.next();
            this.allBeans.addAll(this.annotationAnalysis.getAllBeans(sootClass));
            int annotationTag = this.annotationAnalysis.getAllComponents(sootClass);
            if (SpringAnnotationTag.isBean(annotationTag)) {
                SootClass implClass = sootClass;
                allBeansAndInterfaces.add(sootClass);
                if (SpringAnnotationTag.isMapper(annotationTag)) {
                    implClass = gsc.generateMapperImpl(sootClass);
                }

                this.findAllSuperclass(implClass, implClass);
                interfaceToBeans.put(implClass.getName(), implClass);
                if (SpringAnnotationTag.isPrototype(annotationTag)) {
                    prototypeComponents.add(implClass);
                } else {
                    singletonComponents.add(implClass);
                }
            } else if (SpringAnnotationTag.isPrototype(annotationTag)) {
                prototypeInterfaces.add(sootClass);
            }
        }

        Iterator<SootClass> iteratorMapper = elementsUnsorted.iterator();
        Hierarchy hierarchy = Scene.v().getActiveHierarchy();
        Set<SootClass> databaseModels = new LinkedHashSet();

        while(iteratorMapper.hasNext()) {
            SootClass sootClass = (SootClass)iteratorMapper.next();
            if (sootClass.isInterface() && hierarchy.getImplementersOf(sootClass).isEmpty() && !sootClass.isPhantom()) {
                boolean impl = false;

                for(String mapperPackage : AnnotationAnalysis.mapperPackages) {
                    if (sootClass.getPackageName().startsWith(mapperPackage)) {
                        databaseModels.add(sootClass);
                        impl = true;
                        break;
                    }
                }

                if (!impl) {
                    for(SootClass anInterface : sootClass.getInterfaces()) {
                        if (anInterface.getPackageName().startsWith("org.springframework.data") || anInterface.getName().equals("com.baomidou.mybatisplus.core.mapper.BaseMapper")) {
                            databaseModels.add(sootClass);
                            impl = true;
                            break;
                        }
                    }
                }

                if (!impl && (sootClass.getName().toLowerCase().endsWith("dao") || sootClass.getName().toLowerCase().endsWith("mapper") || sootClass.getName().toLowerCase().endsWith("repository"))) {
                    databaseModels.add(sootClass);
                }
            }
        }

        for(SootClass databaseModel : databaseModels) {
            this.implMapper(gsc, prototypeInterfaces, databaseModel);
        }

        gsc.generateSingletonBeanFactory(singletonComponents, collect);
    }

    private void implMapper(GenerateSyntheticClass gsc, Set<SootClass> prototypeInterfaces, SootClass sootClass) {
        SootClass mapperImplClass = gsc.generateMapperImpl(sootClass);
        interfaceToBeans.put(sootClass.getName(), mapperImplClass);
        allBeansAndInterfaces.add(sootClass);
        if (prototypeInterfaces.contains(sootClass)) {
            prototypeComponents.add(mapperImplClass);
        } else {
            singletonComponents.add(mapperImplClass);
        }

    }

    private void findAllSuperclass(SootClass superclass, SootClass implClass) {
        FastHierarchy fh = Scene.v().getOrMakeFastHierarchy();

        for(SootClass anInterface : superclass.getInterfaces()) {
            allBeansAndInterfaces.add(anInterface);
            SootClass exists = (SootClass)interfaceToBeans.get(anInterface.getName());
            if (exists == null) {
                interfaceToBeans.put(anInterface.getName(), implClass);
            } else if (fh.canStoreClass(implClass, exists)) {
                interfaceToBeans.put(anInterface.getName(), implClass);
            }
        }

        if (superclass.hasSuperclass() && !superclass.getSuperclass().getName().equals("java.lang.Object")) {
            this.findAllSuperclass(superclass.getSuperclass(), implClass);
        }

    }

    public void dependencyInjectAnalysis() {
        for(SootClass sootClass : allBeansAndInterfaces) {
            if (!sootClass.getName().equals(Config.SINGLETON_FACTORY_CNAME) && !sootClass.getName().startsWith("synthetic.") && !sootClass.isInterface()) {
                this.iocParser.getIOCObject(sootClass, this.allBeans);
            }
        }

    }

    public void generateEntryPoints() {
        SootMethod psm = this.findMainMethod();
        if (psm == null) {
            SootClass sClass = new SootClass(this.dummyClassName, 1);
            sClass.setSuperclass(Scene.v().getObjectType().getSootClass());
            Scene.v().addClass(sClass);
            sClass.setApplicationClass();
            SootMethod mainMethod = new SootMethod("main", Arrays.asList(ArrayType.v(RefType.v("java.lang.String"), 1)), VoidType.v(), 9);
            sClass.addMethod(mainMethod);
            JimpleBody jimpleBody = this.createJimpleBody(mainMethod);
            mainMethod.setActiveBody(jimpleBody);
            psm = mainMethod;
        }

        for(SootClass controller : AnnotationAnalysis.controllers) {
            this.linkMainAndController(controller, psm);
        }

        this.projectMainMethod = psm;
    }

    private SootMethod findMainMethod() {
        JimpleUtils jimpleUtils = Implement.jimpleUtils;

        for(SootClass sootClass : this.sootClassChain.getElementsUnsorted()) {
            List<SootMethod> sootMethods = sootClass.getMethods();
            if (sootMethods.size() > 1) {
                for(SootMethod sootMethod : sootMethods) {
                    if (sootMethod.getSubSignature().contains("void main") && sootMethod.isStatic()) {
                        for(Unit unit : jimpleUtils.getMethodBody(sootMethod).getUnits()) {
                            if (unit.toString().contains("SpringApplication")) {
                                return sootMethod;
                            }
                        }
                    }
                }
            }
        }

        return null;
    }

    private JimpleBody createJimpleBody(SootMethod method) {
        JimpleBody body = Jimple.v().newBody(method);
        Local frm1 = Jimple.v().newLocal("frm1", ArrayType.v(RefType.v("java.lang.String"), 1));
        body.getLocals().add(frm1);
        BaseBodyGenerator units = BaseBodyGeneratorFactory.get(body);
        units.add(Jimple.v().newIdentityStmt(frm1, Jimple.v().newParameterRef(ArrayType.v(RefType.v("java.lang.String"), 1), 0)));
        units.add(Jimple.v().newReturnVoidStmt());
        return body;
    }

    public void linkMainAndController(SootClass controller, SootMethod psm) {
        JimpleUtils jimpleUtils = Implement.jimpleUtils;
        List<SootMethod> signatures = new ArrayList();
        SootClass sootClass = (SootClass)AOPParser.proxyMap.getOrDefault(controller.getName(), controller);

        for(SootMethod method : controller.getMethods()) {
            AnnotationTag requestMapping = this.annotationAnalysis.hasSpecialAnnotation(method);
            if (requestMapping != null) {
                if (sootClass == controller) {
                    signatures.add(method);
                } else {
                    SootMethod proxyMethod = sootClass.getMethodUnsafe(method.getSubSignature());
                    if (proxyMethod != null) {
                        signatures.add(proxyMethod);
                    } else {
                        signatures.add(method);
                    }
                }
            }
        }

        if (Config.linkSpringCGLIB_CallEntrySyntheticAndRequestMappingMethods) {
            SootMethod createMethod = new SootMethod(Config.CALL_ENTRY_NAME, (List)null, VoidType.v(), 1);
            sootClass.addMethod(createMethod);
            JimpleBody jimpleBody = jimpleUtils.createJimpleBody(createMethod, signatures, sootClass.getName());
            createMethod.setActiveBody(jimpleBody);
            SootMethod constructor = BaseBodyGenerator.getAnyConstructorUnsafe(sootClass, true);
            if (constructor == null) {
                return;
            }

            if (Config.linkMainAndController) {
                this.processMain(psm, sootClass.getShortName(), sootClass.getName(), constructor.toString(), createMethod.toString());
            }
        } else {
            int i = 0;

            for(SootMethod signature : signatures) {
                SootMethod createMethod = new SootMethod(String.format("%s_%d_%s", Config.CALL_ENTRY_NAME, i++, signature.getName()), (List)null, VoidType.v(), 8);
                sootClass.addMethod(createMethod);
                JimpleBody jimpleBody = jimpleUtils.createJimpleBody(createMethod, Collections.singletonList(signature), sootClass.getName());
                createMethod.setActiveBody(jimpleBody);
            }
        }

    }

    public void aspectAnalysis(Config config) {
        AOPParser aopParser = new AOPParser();
        Set<SootClass> allComponents = new LinkedHashSet(interfaceToBeans.values());
        List<AspectModel> allAspects = aopParser.getAllAspects(allComponents);
        List<AspectModel> allAdvices = new ArrayList();

        for(AspectModel aspect : allAspects) {
            HashMap<String, String> pcutmethod = new LinkedHashMap();

            for(SootMethod method : aspect.getSootClass().getMethods()) {
                VisibilityAnnotationTag annotationTags = (VisibilityAnnotationTag)method.getTag("VisibilityAnnotationTag");
                if (annotationTags != null && annotationTags.getAnnotations() != null) {
                    for(AnnotationTag annotation : annotationTags.getAnnotations()) {
                        if (annotation.getType().contains("Pointcut")) {
                            for(AnnotationElem elem : annotation.getElems()) {
                                if (elem instanceof AnnotationStringElem) {
                                    AnnotationStringElem ase = (AnnotationStringElem)elem;
                                    String expression = ase.getValue();
                                    pcutmethod.put(method.getName(), expression);
                                }
                            }
                        }
                    }
                }
            }

            for(SootMethod aspectMethod : aspect.getSootClass().getMethods()) {
                VisibilityAnnotationTag annotationTags = (VisibilityAnnotationTag)aspectMethod.getTag("VisibilityAnnotationTag");
                if (annotationTags != null) {
                    for(AnnotationTag annotation : annotationTags.getAnnotations()) {
                        if (annotation.getType().contains("Lorg/aspectj/lang/annotation/")) {
                            if (annotation.getType().contains("Pointcut") || annotation.getType().contains("AfterThrowing")) {
                                break;
                            }

                            for(AnnotationElem elem : annotation.getElems()) {
                                if (elem instanceof AnnotationStringElem) {
                                    AnnotationStringElem ase = (AnnotationStringElem)elem;
                                    if (ase.getName().equals("value") || ase.getName().equals("pointcut")) {
                                        String expression = ase.getValue();

                                        for(String s : pcutmethod.keySet()) {
                                            if (expression.contains(s)) {
                                                expression = expression.replace(s + "()", (CharSequence)pcutmethod.get(s));
                                            }
                                        }

                                        if (expression.contains("execution") || expression.contains("within") || expression.contains("args") || expression.contains("@annotation") || expression.contains("@within")) {
                                            aopParser.processDiffAopExp(expression, aspectMethod);
                                        }

                                        if (EnumUtils.getEnumObject(annotation.getType()) != null) {
                                            AspectModel adviceModel = this.getAspectModelInstance(aspect, expression, annotation, aspectMethod);
                                            if (!allAdvices.contains(adviceModel)) {
                                                allAdvices.add(adviceModel);
                                            }

                                            Collections.sort(allAdvices);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        allAdvices.addAll(this.xmlaspectAnalysis(config, aopParser));
        aopParser.addAdviceToTarget(allAdvices);

        for(AOPTargetModel aopTargetModel : AOPParser.modelMap.values()) {
            this.aopAnalysis.processWeave(aopTargetModel);
        }

    }

    private XMLDocumentHolder getXMLHolder(Set<String> xmlpaths) {
        if (xmlpaths.isEmpty()) {
            return null;
        } else {
            XMLDocumentHolder holder = new XMLDocumentHolder();

            for(String xmlpath : xmlpaths) {
                Document document = holder.getDocument(xmlpath);
                if (document != null) {
                    holder.addElements(document);
                    holder.hasArgConstructorBean(xmlpath, document);
                }
            }

            return holder;
        }
    }

    public Set<XmlBeanClazz> getXMLBeanSootClazzes(Set<String> xmlpaths) {
        XMLDocumentHolder holder = this.getXMLHolder(xmlpaths);
        if (holder == null) {
            return null;
        } else {
            Map<String, String> allClassMap = holder.getAllClassMap();
            Set<XmlBeanClazz> res = new LinkedHashSet();

            for(String value : allClassMap.values()) {
                String[] split = value.split(";");
                SootClass xmlsootclass = JimpleUtils.getClassUnsafe(split[0]);
                if (xmlsootclass != null) {
                    XmlBeanClazz xmlBeanClazz = new XmlBeanClazz(xmlsootclass, split[1]);
                    res.add(xmlBeanClazz);
                }
            }

            return res;
        }
    }

    private List<AspectModel> xmlaspectAnalysis(Config config, AOPParser aopParser) {
        XMLDocumentHolder holder = new XMLDocumentHolder();
        List<AspectModel> allAdvices = new ArrayList();
        Set<String> bean_xml_paths = config.bean_xml_paths;
        if (bean_xml_paths.isEmpty()) {
            return allAdvices;
        } else {
            for(String bean_xml_path : bean_xml_paths) {
                Document document = holder.getDocument(bean_xml_path);
                if (document != null) {
                    holder.addElements(document);
                    List<AopXMLResultBean> beanList = holder.processAopElements(document);
                    Set<String> aopClasses = new LinkedHashSet();

                    for(AopXMLResultBean aopXMLResultBean : beanList) {
                        aopClasses.add(aopXMLResultBean.getAopclass());
                    }

                    for(String aopclass : aopClasses) {
                        SootClass sootClass = JimpleUtils.getClassUnsafe(aopclass);
                        if (sootClass != null) {
                            for(SootMethod method : sootClass.getMethods()) {
                                for(AopXMLResultBean aopXMLResultBean : beanList) {
                                    if (method.getName().equals(aopXMLResultBean.getAopmethod())) {
                                        aopParser.processDiffAopExp(aopXMLResultBean.getExper(), method);
                                        AspectModel aspectModel = this.copyXmlBeanToAspectModel(aopXMLResultBean, sootClass, method);
                                        allAdvices.add(aspectModel);
                                        Collections.sort(allAdvices);
                                    }
                                }
                            }
                        }
                    }
                }
            }

            return allAdvices;
        }
    }

    private AspectModel getAspectModelInstance(AspectModel aspect, String expression, AnnotationTag annotation, SootMethod aspectMethod) {
        AspectModel adviceModel;
        if (aspectModelMap.containsKey(aspectMethod.toString())) {
            adviceModel = (AspectModel)aspectModelMap.get(aspectMethod.toString());
            adviceModel.addPointcutExpressions(expression);
        } else {
            adviceModel = new AspectModel();
            adviceModel.setOrder(aspect.getOrder());
            adviceModel.setSootClass(aspect.getSootClass());
            adviceModel.addPointcutExpressions(expression);
            adviceModel.setSootMethod(aspectMethod);
            adviceModel.setAnnotation(EnumUtils.getEnumObject(annotation.getType()));
            aspectModelMap.put(aspectMethod.toString(), adviceModel);
        }

        return adviceModel;
    }

    private AspectModel copyXmlBeanToAspectModel(AopXMLResultBean aopXMLResultBean, SootClass sootClass, SootMethod sootMethod) {
        AspectModel adviceModel;
        if (aspectModelMap.containsKey(sootMethod.toString())) {
            adviceModel = (AspectModel)aspectModelMap.get(sootMethod.toString());
            adviceModel.addPointcutExpressions(aopXMLResultBean.getExper());
        } else {
            adviceModel = new AspectModel();
            adviceModel.setOrder(aopXMLResultBean.getOrder());
            adviceModel.setSootClass(sootClass);
            adviceModel.addPointcutExpressions(aopXMLResultBean.getExper());
            adviceModel.setSootMethod(sootMethod);
            List<AdviceEnum> collect = (List)Arrays.stream(AdviceEnum.values()).filter((x) -> x.getAnnotationClassName().toLowerCase().contains(aopXMLResultBean.getActivetype())).collect(Collectors.toList());
            adviceModel.setAnnotation(collect != null ? (AdviceEnum)collect.get(0) : AdviceEnum.AOP_BEFORE);
            aspectModelMap.put(sootMethod.toString(), adviceModel);
        }

        return adviceModel;
    }

    public void processMain(SootMethod method, String objName, String objType, String initSign, String runSign) {
        JimpleUtils jimpleUtils = Implement.jimpleUtils;
        JimpleBody body = (JimpleBody)jimpleUtils.getMethodBody(method);
        Local localModel = jimpleUtils.addLocalVar(objName, objType, body);
        BaseBodyGenerator units = BaseBodyGeneratorFactory.get(body);
        units.getUnits().removeIf((unit) -> unit instanceof JReturnVoidStmt);
        jimpleUtils.createAssignStmt(localModel, objType, units);
        units.add(jimpleUtils.specialCallStatement(localModel, initSign, units));
        units.add(jimpleUtils.virtualCallStatement(localModel, runSign, units));
        jimpleUtils.addVoidReturnStmt(units);
    }

    static class XmlBeanClazz {
        private SootClass sootClass;
        private String scope;

        public XmlBeanClazz() {
        }

        public XmlBeanClazz(SootClass sootClass, String scope) {
            this.sootClass = sootClass;
            this.scope = scope;
        }

        public String toString() {
            return "XmlBeanClazz{sootClass=" + this.sootClass + ", scope='" + this.scope + '\'' + '}';
        }

        public SootClass getSootClass() {
            return this.sootClass;
        }

        public void setSootClass(SootClass sootClass) {
            this.sootClass = sootClass;
        }

        public String getScope() {
            return this.scope;
        }

        public void setScope(String scope) {
            this.scope = scope;
        }
    }
}
