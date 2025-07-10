//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package utils;

import soot.Body;

public abstract class BaseBodyGeneratorFactory {
    public static BaseBodyGeneratorFactory instance = new BaseBodyGeneratorFactory() {
        public BaseBodyGenerator create(Body body) {
            return new BaseBodyGenerator(body);
        }
    };

    public BaseBodyGeneratorFactory() {
    }

    public abstract BaseBodyGenerator create(Body var1);

    public static BaseBodyGenerator get(Body body) {
        return instance.create(body);
    }
}
