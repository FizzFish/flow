//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package bean;

import java.util.List;
import soot.SootMethod;
import soot.Unit;

public class InsertMethod {
    private SootMethod sootMethod;
    private List<Unit> returnList;
    private List<Unit> insertPointList;
    private List<Unit> pjpList;

    public InsertMethod() {
    }

    public InsertMethod(SootMethod sootMethod, List<Unit> returnList) {
        this.sootMethod = sootMethod;
        this.returnList = returnList;
    }

    public InsertMethod(SootMethod sootMethod, List<Unit> returnList, List<Unit> insertPointList) {
        this.sootMethod = sootMethod;
        this.returnList = returnList;
        this.insertPointList = insertPointList;
    }

    public InsertMethod(SootMethod sootMethod, List<Unit> returnList, List<Unit> insertPointList, List<Unit> pjpList) {
        this.sootMethod = sootMethod;
        this.returnList = returnList;
        this.insertPointList = insertPointList;
        this.pjpList = pjpList;
    }

    public SootMethod getSootMethod() {
        return this.sootMethod;
    }

    public void setSootMethod(SootMethod sootMethod) {
        this.sootMethod = sootMethod;
    }

    public List<Unit> getReturnList() {
        return this.returnList;
    }

    public void setReturnList(List<Unit> returnList) {
        this.returnList = returnList;
    }

    public List<Unit> getInsertPointList() {
        return this.insertPointList;
    }

    public void setInsertPointList(List<Unit> insertPointList) {
        this.insertPointList = insertPointList;
    }

    public List<Unit> getPjpList() {
        return this.pjpList;
    }

    public void setPjpList(List<Unit> pjpList) {
        this.pjpList = pjpList;
    }

    public String toString() {
        return "InsertMethod{sootMethod=" + this.sootMethod + ", returnList=" + this.returnList + ", insertPointList=" + this.insertPointList + ", pjpList=" + this.pjpList + '}';
    }
}
