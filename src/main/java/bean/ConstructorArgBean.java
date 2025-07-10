package bean;


public class ConstructorArgBean {
    private String xml;
    private String argName;
    private Integer argIndex;
    private String argType;
    private String refType;
    private String argValue;
    private String clazzName;

    @Override
    public String toString() {
        return "ConstructorArgBean{argName='" + this.argName + '\'' + ", argIndex=" + this.argIndex + ", argType='" + this.argType + '\'' + ", refType='" + this.refType + '\'' + ", argValue='" + this.argValue + '\'' + ", clazzName='" + this.clazzName + '\'' + '}';
    }

    public String getArgName() {
        return argName;
    }

    public void setArgName(String argName) {
        this.argName = argName;
    }

    public Integer getArgIndex() {
        return argIndex;
    }

    public void setArgIndex(Integer argIndex) {
        this.argIndex = argIndex;
    }

    public String getArgType() {
        return argType;
    }

    public void setArgType(String argType) {
        this.argType = argType;
    }

    public String getRefType() {
        return refType;
    }

    public void setRefType(String refType) {
        this.refType = refType;
    }

    public String getArgValue() {
        return argValue;
    }

    public void setArgValue(String argValue) {
        this.argValue = argValue;
    }

    public String getClazzName() {
        return clazzName;
    }

    public void setClazzName(String clazzName) {
        this.clazzName = clazzName;
    }
    public void setXml(String xml) {
        this.xml = xml;
    }

    public String getXml() {
        return this.xml;
    }
}
