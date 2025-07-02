package cn.sast.common

import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.parsers.ParserConfigurationException
import org.apache.xerces.jaxp.DocumentBuilderFactoryImpl

public object SafeDomParserFactory {
   public fun createDocumentBuilder(namespaceAware: Boolean): DocumentBuilder {
      try {
         val documentBuilderFactory: DocumentBuilderFactory = (new DocumentBuilderFactoryImpl()) as DocumentBuilderFactory;
         documentBuilderFactory.setFeature("http://javax.xml.XMLConstants/feature/secure-processing", true);
         documentBuilderFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
         documentBuilderFactory.setFeature("http://xml.org/sax/features/external-general-entities", false);
         documentBuilderFactory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
         documentBuilderFactory.setFeature("http://apache.org/xml/features/dom/create-entity-ref-nodes", false);
         documentBuilderFactory.setFeature("http://apache.org/xml/features/disallow-doctype-decl", false);
         documentBuilderFactory.setValidating(false);
         documentBuilderFactory.setExpandEntityReferences(false);
         documentBuilderFactory.setNamespaceAware(namespaceAware);
         documentBuilderFactory.setXIncludeAware(false);
         val e: DocumentBuilder = documentBuilderFactory.newDocumentBuilder();
         e.setErrorHandler(null);
         return e;
      } catch (var4: ParserConfigurationException) {
         throw new IllegalStateException(var4);
      }
   }
}
