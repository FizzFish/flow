//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package utils;

import java.util.List;
import soot.Unit;
import soot.UnitPatchingChain;

public class NewUnitsAtLastImmediately implements INewUnits {
    UnitPatchingChain unitPatchingChain;

    public NewUnitsAtLastImmediately(UnitPatchingChain unitPatchingChain) {
        this.unitPatchingChain = unitPatchingChain;
    }

    public NewUnitsAtLastImmediately addAll(List<Unit> units) {
        this.unitPatchingChain.addAll(units);
        return this;
    }

    public NewUnitsAtLastImmediately add(Unit unit) {
        this.unitPatchingChain.add(unit);
        return this;
    }
}
