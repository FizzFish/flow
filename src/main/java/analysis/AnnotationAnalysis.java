//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package analysis;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import soot.Scene;
import soot.SootClass;
import soot.SootField;
import soot.SootMethod;
import soot.Type;
import soot.tagkit.AbstractHost;
import soot.tagkit.AnnotationArrayElem;
import soot.tagkit.AnnotationElem;
import soot.tagkit.AnnotationStringElem;
import soot.tagkit.AnnotationTag;
import soot.tagkit.Tag;
import soot.tagkit.VisibilityAnnotationTag;
import soot.util.Chain;
import utils.JimpleUtils;

public class AnnotationAnalysis {
    private static volatile AnnotationAnalysis INSTANCE = null;
    public static List<Type> autoMethodParams = new ArrayList();
    public static Set<String> mapperPackages = new LinkedHashSet();
    public static Set<SootClass> controllers = new LinkedHashSet();

    public AnnotationAnalysis() {
    }

    public static AnnotationAnalysis getInstance() {
        if (INSTANCE == null) {
            synchronized(AnnotationAnalysis.class) {
                if (INSTANCE == null) {
                    INSTANCE = new AnnotationAnalysis();
                }
            }
        }

        return INSTANCE;
    }

    public Chain<SootField> getClassFields(String className) {
        SootClass sootClass = Scene.v().getSootClass(className);
        return sootClass.getFields();
    }

    public List<Tag> getFieldTags(SootField field) {
        return field.getTags();
    }

    public SootField getFieldWithSpecialAnnos(SootField field, SootMethod initMethod, boolean ambiguous) {
        for(Tag fieldTag : this.getFieldTags(field)) {
            String strtag = fieldTag.toString();
            if (strtag.contains("Autowired") || strtag.contains("Qualifier") || strtag.contains("Resource") || strtag.contains("Inject")) {
                return field;
            }
        }

        if (autoMethodParams.contains(field.getType())) {
            return field;
        } else if (!ambiguous && initMethod.getParameterTypes().contains(field.getType())) {
            return field;
        } else {
            return null;
        }
    }

    public List<Type> getParamOfAutoWiredMethod(SootMethod method) {
        VisibilityAnnotationTag methodTag = (VisibilityAnnotationTag)method.getTag("VisibilityAnnotationTag");
        List<Type> parameterTypes = null;
        if (methodTag != null) {
            for(AnnotationTag annotation : methodTag.getAnnotations()) {
                if (annotation.getType().contains("Autowired")) {
                    parameterTypes = method.getParameterTypes();
                    break;
                }
            }
        }

        return parameterTypes;
    }

    public void findComponents(SootClass ApplicationClass) {
        AnnotationTag annotationScan = this.hasSpecialAnnotation(ApplicationClass);
        if (annotationScan != null) {
            for(AnnotationElem elem : annotationScan.getElems()) {
                if (elem instanceof AnnotationArrayElem && elem.getName().equals("basePackages")) {
                    AnnotationArrayElem arrayElem = (AnnotationArrayElem)elem;

                    for(AnnotationElem value : arrayElem.getValues()) {
                        if (value instanceof AnnotationStringElem) {
                            AnnotationStringElem stringElem = (AnnotationStringElem)value;
                            CreateEdge.componentPackages.add(stringElem.getValue());
                        }
                    }
                    break;
                }
            }
        }

    }

    public Integer getAllComponents(SootClass sootClass) {
        VisibilityAnnotationTag annotationTags = (VisibilityAnnotationTag)sootClass.getTag("VisibilityAnnotationTag");
        int flag = 0;
        if (annotationTags != null) {
            for(AnnotationTag annotation : annotationTags.getAnnotations()) {
                switch (annotation.getType()) {
                    case "Lorg/springframework/web/bind/annotation/RestController;":
                    case "Lorg/springframework/web/bind/annotation/Controller;":
                    case "Lorg/springframework/stereotype/Controller;":
                        controllers.add(sootClass);
                        if (!SpringAnnotationTag.isBean(flag)) {
                            ++flag;
                        }
                        break;
                    case "Lorg/apache/ibatis/annotations/Mapper;":
                    case "Lorg/beetl/sql/core/annotatoin/SqlResource;":
                        if (!SpringAnnotationTag.isMapper(flag)) {
                            flag += 4;
                        }
                    case "Lorg/springframework/stereotype/Component;":
                    case "Lorg/springframework/context/annotation/Configuration;":
                    case "Lorg/springframework/stereotype/Repository;":
                    case "Lorg/springframework/stereotype/Service;":
                        if (!SpringAnnotationTag.isBean(flag)) {
                            ++flag;
                        }
                        break;
                    case "Lorg/springframework/context/annotation/Scope;":
                        for(AnnotationElem elem : annotation.getElems()) {
                            if (elem instanceof AnnotationStringElem) {
                                AnnotationStringElem stringElem = (AnnotationStringElem)elem;
                                if (stringElem.getValue().equals("prototype") && !SpringAnnotationTag.isPrototype(flag)) {
                                    flag += 2;
                                }
                            }
                        }
                        break;
                    case "Lorg/mybatis/spring/annotation/MapperScan;":
                        for(AnnotationElem elem : annotation.getElems()) {
                            if (elem instanceof AnnotationArrayElem) {
                                AnnotationArrayElem arrayElem = (AnnotationArrayElem)elem;

                                for(AnnotationElem value : arrayElem.getValues()) {
                                    if (value instanceof AnnotationStringElem) {
                                        AnnotationStringElem asElem = (AnnotationStringElem)value;
                                        mapperPackages.add(asElem.getValue());
                                    }
                                }
                            }
                        }
                }
            }
        }

        return flag;
    }

    public Set<SootMethod> getAllBeans(SootClass sootClass) {
        Set<SootMethod> allBeans = new LinkedHashSet();

        for(SootMethod method : sootClass.getMethods()) {
            VisibilityAnnotationTag annotationTags = (VisibilityAnnotationTag)method.getTag("VisibilityAnnotationTag");
            if (annotationTags != null) {
                for(AnnotationTag annotation : annotationTags.getAnnotations()) {
                    if (annotation.getType().equals("Lorg/springframework/context/annotation/Bean;")) {
                        allBeans.add(method);
                        SootClass bean = JimpleUtils.getClassUnsafe(method.getReturnType());
                        if (bean != null && bean.isApplicationClass()) {
                            CreateEdge.singletonComponents.add(bean);
                            CreateEdge.interfaceToBeans.put(bean.getName(), bean);
                        }
                    }
                }
            }
        }

        return allBeans;
    }

    public AnnotationTag hasSpecialAnnotation(AbstractHost host) {
        VisibilityAnnotationTag annotationTags = (VisibilityAnnotationTag)host.getTag("VisibilityAnnotationTag");
        if (annotationTags == null) {
            return null;
        } else {
            for(AnnotationTag annotation : annotationTags.getAnnotations()) {
                if (satisfyAnnotation(annotation.getType())) {
                    return annotation;
                }
            }

            return null;
        }
    }

    private static boolean satisfyAnnotation(String type) {
        switch (type) {
            case "Lorg/springframework/web/bind/annotation/RequestMapping;":
            case "Lorg/springframework/web/bind/annotation/PostMapping;":
            case "Lorg/springframework/web/bind/annotation/GetMapping;":
            case "Lorg/springframework/web/bind/annotation/PatchMapping;":
            case "Lorg/springframework/web/bind/annotation/DeleteMapping;":
            case "Lorg/springframework/web/bind/annotation/PutMapping;":
            case "Lorg/springframework/web/bind/annotation/RestController;":
            case "Lorg/springframework/web/bind/annotation/Controller;":
            case "Lorg/springframework/stereotype/Controller;":
                return true;
            default:
                return false;
        }
    }

    public static void clear() {
        INSTANCE = null;
        autoMethodParams.clear();
        mapperPackages.clear();
        controllers.clear();
    }
}
