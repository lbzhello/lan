package lan.ast;

import java.io.Serializable;

/**
 * 抽象语法树顶层类
 */
public interface Expression extends Serializable, Cloneable {
    default Expression eval() {
        return this;
    }

    /**
     * 查找表
     * @param key
     * @return
     */
    default Expression lookup(String key) {
        return null;
    }
}
