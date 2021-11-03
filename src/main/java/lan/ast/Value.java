package lan.ast;

/**
 * 值表达式，求值 {@link #eval()} 时不会再进行计算
 * 直接返回自身
 * 如字符串，数字，布尔值等
 */
public interface Value extends Expression {
    Constant NIL = new Constant("nil");
    Constant TRUE = new Constant("true");
    Constant FALSE = new Constant("false");

    default Expression eval() {
        return this;
    }

    // 常量值
    class Constant implements Value {
        private Object value;

        public Constant(Object value) {
            this.value = value;
        }

        @Override
        public Expression eval() {
            return null;
        }

        @Override
        public String toString() {
            return String.valueOf(value);
        }
    }
}
