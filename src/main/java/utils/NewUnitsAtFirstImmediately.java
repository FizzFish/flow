//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package utils;

import java.util.Collections;
import java.util.List;
import javax.annotation.Nullable;
import soot.Unit;
import soot.jimple.IdentityStmt;
import soot.jimple.ParameterRef;
import soot.jimple.ThisRef;
import soot.util.Chain;

public class NewUnitsAtFirstImmediately implements INewUnits {
    BaseBodyGenerator bodyGenerator;
    private Unit afterUnit;

    public NewUnitsAtFirstImmediately(BaseBodyGenerator bodyGenerator) {
        this.bodyGenerator = bodyGenerator;
        this.afterUnit = getLastParamIdentityStmt(bodyGenerator.getUnits());
    }

    @Nullable
    public static Unit getLastParamIdentityStmt(Chain<Unit> units) {
        Unit afterUnit = null;
        if (!units.isEmpty()) {
            for(Unit unit : units) {
                if (!(unit instanceof IdentityStmt)) {
                    break;
                }

                IdentityStmt identityStmt = (IdentityStmt)unit;
                if (!(identityStmt.getRightOp() instanceof ThisRef) && !(identityStmt.getRightOp() instanceof ParameterRef)) {
                    break;
                }

                afterUnit = unit;
            }
        }

        return afterUnit;
    }

    public NewUnitsAtFirstImmediately addAll(List<Unit> units) {
        if (units.isEmpty()) {
            return this;
        } else {
            if (this.afterUnit == null) {
                this.bodyGenerator.addFirst(units);
            } else {
                this.bodyGenerator.insertAfter(units, this.afterUnit);
            }

            this.afterUnit = (Unit)units.get(units.size() - 1);
            return this;
        }
    }

    public NewUnitsAtFirstImmediately add(Unit unit) {
        return this.addAll(Collections.singletonList(unit));
    }
}
