//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package utils;

import bean.AopXMLResultBean;
import bean.ConstructorArgBean;
import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XMLDocumentHolder {
    private static final Logger logger = LoggerFactory.getLogger(XMLDocumentHolder.class);
    private Map<String, Document> docs = new LinkedHashMap();
    private Map<String, Element> elements = new LinkedHashMap();
    private List<ConstructorArgBean> argConstructors = new ArrayList();

    public XMLDocumentHolder() {
    }

    public Document getDocument(String filePath) {
        Document doc = (Document)this.docs.get(filePath);
        if (doc == null) {
            this.docs.put(filePath, this.readDocument(filePath));
        }

        return (Document)this.docs.get(filePath);
    }

    private Document readDocument(String filePath) {
        Document doc = null;

        try {
            SAXReader reader = new SAXReader();
            File xmlFile = new File(filePath);
            if (xmlFile.exists()) {
                doc = reader.read(xmlFile);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return doc;
    }

    public void addElements(Document doc) {
        for(Element e : doc.getRootElement().elements("bean")) {
            String id = e.attributeValue("id");
            if (id != null) {
                this.elements.put(id, e);
            }
        }

    }

    public String getCustomMapperAnnotationClazz(Document doc) {
        for(Element ele : doc.getRootElement().elements("bean")) {
            if (ele.attribute("class") != null && "org.mybatis.spring.mapper.MapperScannerConfigurer".equals(ele.attribute("class").getValue())) {
                for(Element property : ele.elements("property")) {
                    if (property.attribute("name") != null && "annotationClass".equals(property.attribute("name").getValue()) && property.attribute("value") != null) {
                        return property.attribute("value").getValue();
                    }
                }
            }
        }

        return "";
    }

    public void hasArgConstructorBean(String xmlpath, Document doc) {
        for(Element ele : doc.getRootElement().elements("bean")) {
            List<Element> elelist = ele.elements("constructor-arg");
            if (elelist != null && !elelist.isEmpty()) {
                String id = ele.attributeValue("id");

                for(Element element : elelist) {
                    ConstructorArgBean argBean = new ConstructorArgBean();
                    argBean.setXml(xmlpath);
                    if (ele.attribute("class") != null) {
                        argBean.setClazzName(ele.attribute("class").getText());
                    }

                    if (element.attribute("name") != null) {
                        argBean.setArgName(element.attribute("name").getText());
                    }

                    if (element.attribute("index") != null) {
                        argBean.setArgIndex(Integer.valueOf(element.attribute("index").getText()));
                    }

                    if (element.attribute("type") != null) {
                        argBean.setArgType(element.attribute("type").getText());
                    }

                    if (element.attribute("ref") != null) {
                        String ref = element.attribute("ref").getText();
                        Element refelem = (Element)this.elements.get(ref);
                        if (refelem != null && refelem.attribute("class") != null) {
                            String aClass = refelem.attribute("class").getText();
                            argBean.setRefType(aClass);
                        }
                    }

                    if (element.attribute("value") != null) {
                        argBean.setArgValue(element.attribute("value").getText());
                    } else {
                        Element value = element.element("value");
                        if (value != null) {
                            argBean.setArgValue(value.getTextTrim());
                        }
                    }

                    this.argConstructors.add(argBean);
                }
            }
        }

    }

    public Map<String, String> getAllClassMap() {
        Map<String, String> map = new LinkedHashMap();

        for(String s : this.elements.keySet()) {
            Element element = (Element)this.elements.get(s);
            if (element.attribute("class") != null) {
                String aClass = element.attribute("class").getText();
                if (element.attribute("scope") == null) {
                    aClass = aClass + ";singleton";
                } else {
                    aClass = aClass + ";" + element.attribute("scope").getText();
                }

                map.put(s, aClass);
            }
        }

        return map;
    }

    public List<ConstructorArgBean> getArgConstructors() {
        return this.argConstructors;
    }

    public List<AopXMLResultBean> processAopElements(Document doc) {
        List<Element> configlist = doc.getRootElement().elements("config");
        List<Element> pointcutelements = new ArrayList();
        List<Element> partpointcutele = new ArrayList();
        List<Element> aspelement = new ArrayList();
        List<Element> beforeelement = new ArrayList();
        List<Element> afterelement = new ArrayList();
        List<Element> aroundelement = new ArrayList();
        List<Element> areturnelement = new ArrayList();
        List<Element> athrowelement = new ArrayList();
        Map<String, String> partpointmap = new LinkedHashMap();
        Map<String, String> stringStringMap = new LinkedHashMap();
        Map<String, String> tmppointmap = new LinkedHashMap();
        List<AopXMLResultBean> resbenas = new ArrayList();
        int gorder = 1;

        for(Element config : configlist) {
            aspelement.clear();
            stringStringMap.clear();
            pointcutelements.addAll(config.elements("pointcut"));
            if (pointcutelements.size() > 0) {
                stringStringMap.putAll(this.pointcutelesProcess(pointcutelements));
            }

            aspelement.addAll(config.elements("aspect"));

            for(Element asp : aspelement) {
                partpointmap.clear();
                tmppointmap.clear();
                beforeelement.clear();
                afterelement.clear();
                aroundelement.clear();
                areturnelement.clear();
                athrowelement.clear();
                if (asp.attribute("ref") != null) {
                    String id = asp.attribute("ref").getText();
                    int order = 1;
                    if (asp.attribute("order") != null) {
                        order = Integer.parseInt(asp.attribute("order").getText());
                    } else {
                        order = gorder++;
                    }

                    Element element = (Element)this.elements.get(id);
                    if (element != null && element.attribute("class") != null) {
                        String aClass = element.attribute("class").getText();
                        partpointcutele.addAll(asp.elements("pointcut"));
                        if (partpointcutele.size() > 0) {
                            partpointmap.putAll(this.pointcutelesProcess(partpointcutele));
                        }

                        tmppointmap.putAll(stringStringMap);
                        tmppointmap.putAll(partpointmap);
                        beforeelement.addAll(asp.elements("before"));
                        Map<String, String> beforemap = this.alertProcess(beforeelement);
                        resbenas.addAll(this.ProcessActiveAndPointcut(tmppointmap, beforemap, aClass, "before", order));
                        afterelement.addAll(asp.elements("after"));
                        Map<String, String> aftermap = this.alertProcess(afterelement);
                        resbenas.addAll(this.ProcessActiveAndPointcut(tmppointmap, aftermap, aClass, "after", order));
                        aroundelement.addAll(asp.elements("around"));
                        Map<String, String> aroundmap = this.alertProcess(aroundelement);
                        resbenas.addAll(this.ProcessActiveAndPointcut(tmppointmap, aroundmap, aClass, "around", order));
                        areturnelement.addAll(asp.elements("after-returning"));
                        Map<String, String> areturnmap = this.alertProcess(areturnelement);
                        resbenas.addAll(this.ProcessActiveAndPointcut(tmppointmap, areturnmap, aClass, "afterreturn", order));
                        athrowelement.addAll(asp.elements("after-throwing"));
                        Map<String, String> athrowmap = this.alertProcess(athrowelement);
                        resbenas.addAll(this.ProcessActiveAndPointcut(tmppointmap, athrowmap, aClass, "afterthrow", order));
                    }
                }
            }
        }

        return resbenas;
    }

    public List<AopXMLResultBean> ProcessActiveAndPointcut(Map<String, String> pointcutmap, Map<String, String> activemap, String aclazz, String activetype, int order) {
        List<AopXMLResultBean> beanList = new ArrayList();
        if (pointcutmap.size() > 0 && activemap.size() > 0) {
            for(String value : activemap.values()) {
                String method = value.split(";")[0];
                String pcutref = value.split(";")[1];

                for(String s : pointcutmap.keySet()) {
                    if (pcutref.equals(s)) {
                        AopXMLResultBean aopXMLResultBean = new AopXMLResultBean();
                        aopXMLResultBean.setAopclass(aclazz);
                        aopXMLResultBean.setAopmethod(method);
                        aopXMLResultBean.setActivetype(activetype);
                        aopXMLResultBean.setExper((String)pointcutmap.get(s));
                        aopXMLResultBean.setOrder(order);
                        beanList.add(aopXMLResultBean);
                    }
                }
            }
        }

        return beanList;
    }

    public Map<String, String> pointcutelesProcess(List<Element> pointcutelements) {
        Map<String, String> res = new LinkedHashMap();

        for(Element pointcutelement : pointcutelements) {
            if (pointcutelement.attribute("id") != null) {
                String id = pointcutelement.attribute("id").getText();
                if (pointcutelement.attribute("expression") != null) {
                    String expression = pointcutelement.attribute("expression").getText();
                    res.put(id, expression);
                }
            }
        }

        return res;
    }

    public Map<String, String> alertProcess(List<Element> alertelement) {
        Map<String, String> res = new LinkedHashMap();
        if (alertelement.size() > 0) {
            for(Element element : alertelement) {
                if (element.attribute("method") != null) {
                    String method = element.attribute("method").getText();
                    if (element.attribute("pointcut-ref") != null) {
                        String pintcutref = element.attribute("pointcut-ref").getText();
                        res.put(method + pintcutref, method + ";" + pintcutref);
                    }
                }
            }
        }

        return res;
    }

    public List getChildElements(Element element) {
        List elements = element.elements();
        return elements;
    }

    public Element getElement(String id) {
        return (Element)this.elements.get(id);
    }
}
