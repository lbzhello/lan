package lan.ast;

import java.io.Serializable;

/**
 * 抽象语法树顶层类
 */
public interface Expression extends Serializable, Cloneable {
    default Expression eval() {
        return this;
    }
}
