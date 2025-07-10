//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package utils;

import java.util.ArrayList;
import java.util.List;
import soot.Unit;
import soot.Value;

public class NewUnits implements INewUnits {
    private final List<Unit> units = new ArrayList();

    private NewUnits() {
    }

    public static NewUnits alloc() {
        return new NewUnits();
    }

    public List<Unit> getUnits() {
        return this.units;
    }

    public NewUnits addAll(List<Unit> units) {
        this.units.addAll(units);
        return this;
    }

    public NewUnits addAll(NewUnits units) {
        this.units.addAll(units.units);
        return this;
    }

    public NewUnits add(Unit unit) {
        this.units.add(unit);
        return this;
    }

    public BeforeUnit before(Unit unit) {
        return new BeforeUnit(unit);
    }

    public BeforeRhs before(Value rhs) {
        return new BeforeRhs(rhs);
    }

    public class BeforeUnit {
        private final Unit insertBefore;

        public BeforeUnit(Unit insertBefore) {
            this.insertBefore = insertBefore;
        }

        public NewUnits getInsertBefore() {
            return NewUnits.this;
        }

        public Unit getInsertBeforeUnit() {
            return this.insertBefore;
        }
    }

    public class BeforeRhs {
        private final Value insertBefore;

        public BeforeRhs(Value insertBefore) {
            this.insertBefore = insertBefore;
        }

        public NewUnits getInsertBefore() {
            return NewUnits.this;
        }

        public Value getInsertBeforeRhs() {
            return this.insertBefore;
        }
    }
}
