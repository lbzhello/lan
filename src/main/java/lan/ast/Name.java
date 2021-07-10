package lan.ast;

/**
 * 表达式可以作为 name 放在 Context 中
 */
public interface Name extends Expression {

    // 常量值
    class Constant implements Operator {
        private String name;

        public Constant(String name) {
            this.name = name;
        }

        @Override
        public Expression eval() {
            return null;
        }

        @Override
        public String toString() {
            return String.valueOf(name);
        }
    }
}
