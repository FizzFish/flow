//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package analysis;

import java.util.LinkedHashSet;
import java.util.Set;

public class Config {
    public Set<String> bean_xml_paths = new LinkedHashSet();
    public static Boolean linkMainAndController = true;
    public static Boolean linkSpringCGLIB_CallEntrySyntheticAndRequestMappingMethods = true;
    public static String CALL_ENTRY_NAME = "callEntry_synthetic";
    public static String PROXY_CLASS_SUFFIX = "SpringProxyImpl";
    public static String SINGLETON_FACTORY_CNAME = "synthetic.method.SingletonFactory";

    public Config() {
    }
}
