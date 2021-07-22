package lan.base;

import lan.ast.Expression;
import lan.ast.Name;

import java.util.function.Supplier;

/**
 * 保留字
 */
public class NameOption<T> implements Name {
    // 名称
    private String name;
    // 语法树
    private Supplier<T> supplier;

    public static <S> NameOption<S> valueOf(String name, Supplier<S> supplier) {
        return new NameOption<>(name, supplier);
    }

    private NameOption(String name, Supplier<T> supplier) {
        this.name = name;
        this.supplier = supplier;
    }

    public T getAst() {
        return supplier.get();
    }

    public String getName() {
        return name;
    }

    @Override
    public Expression eval() {
        return this;
    }
}
