package lan.base;

import lan.ast.Expression;
import lan.ast.ExpressionFactory;
import lan.ast.Keyword;
import lan.ast.Name;

import java.util.function.Supplier;

/**
 * 运算符
 */
public enum KeywordEnum implements Name {
    DEFINE("define", ExpressionFactory::define),
    FUNCTION("fn", ExpressionFactory::define),
    LET("let", ExpressionFactory::define),

    DO("do", ExpressionFactory::define),
    END("end", ExpressionFactory::define),
    RETURN("ret", ExpressionFactory::define),

    IF("if", ExpressionFactory::define),
    ELSE("else", ExpressionFactory::define),
    ELSEIF("elif", ExpressionFactory::define),
    FOR("for", ExpressionFactory::define),

    NEW("new", ExpressionFactory::define),
    IMPORT("import", ExpressionFactory::define),
    ;

    // 运算符名称
    private String name;
    // 语法树
    private Supplier<Keyword> astSupplier;

    KeywordEnum(String name, Supplier<Keyword> astSupplier) {
        this.name = name;
        this.astSupplier = astSupplier;
    }

    public String getName() {
        return this.name;
    }

    /**
     * 获取关键字语法树
     * @return
     */
    public Keyword getAst() {
        return astSupplier.get();
    }

    @Override
    public Expression eval() {
        return this;
    }

    @Override
    public String toString() {
        return String.valueOf(name);
    }
}
