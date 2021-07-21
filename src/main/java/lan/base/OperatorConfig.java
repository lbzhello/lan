package lan.base;

import lan.ast.ExpressionFactory;
import lan.ast.Keyword;
import lan.ast.Operator;

import java.util.HashMap;
import java.util.Map;

/**
 * 运算符配置
 */
public class OperatorConfig {
    public static final String NOT = "!";
    public static final String AUTO_DEC = "--";
    public static final String AUTO_INC = "++";

    //--------- Binary -----------//
    public static final String COLON = ":";
    public static final String COLON2 = "::";
    public static final String ARROW = "->";
    public static final String ASSIGN = "=";
    public static final String OR = "||";
    public static final String AND = "&&";

    public static final String EQUAL = "==";
    public static final String NOT_EQUAL = "!=";
    public static final String GT = ">";
    public static final String GE = ">=";
    public static final String LT = "<";
    public static final String LE = "<=";

    public static final String PLUS = "+";
    public static final String MINUS = "-";
    public static final String MUL = "*";
    public static final String DIV = "/";

    //--------- Other -----------//
    public static final String POINT = ".";
    public static final String PRINT = "print";
    public static final String FILE = "file";

    // 关键字容器
    private Map<String, NameOption<Operator>> pool = new HashMap<>();

    public OperatorConfig() {
        addKeyword(NameOption.valueOf(COLON, ExpressionFactory::plus));
        addKeyword(NameOption.valueOf(NOT, ExpressionFactory::plus));
        addKeyword(NameOption.valueOf(AUTO_DEC, ExpressionFactory::plus));
        addKeyword(NameOption.valueOf(AUTO_INC, ExpressionFactory::plus));

        //--------- Binary -----------//
        addKeyword(NameOption.valueOf(COLON, ExpressionFactory::plus));
        addKeyword(NameOption.valueOf(COLON2, ExpressionFactory::plus));
        addKeyword(NameOption.valueOf(ARROW, ExpressionFactory::plus));
        addKeyword(NameOption.valueOf(ASSIGN, ExpressionFactory::plus));
        addKeyword(NameOption.valueOf(OR, ExpressionFactory::plus));
        addKeyword(NameOption.valueOf(AND, ExpressionFactory::plus));

        addKeyword(NameOption.valueOf(EQUAL, ExpressionFactory::plus));
        addKeyword(NameOption.valueOf(NOT_EQUAL, ExpressionFactory::plus));
        addKeyword(NameOption.valueOf(GT, ExpressionFactory::plus));
        addKeyword(NameOption.valueOf(GE, ExpressionFactory::plus));
        addKeyword(NameOption.valueOf(LT, ExpressionFactory::plus));
        addKeyword(NameOption.valueOf(LE, ExpressionFactory::plus));

        addKeyword(NameOption.valueOf(PLUS, ExpressionFactory::plus));
        addKeyword(NameOption.valueOf(MINUS, ExpressionFactory::plus));
        addKeyword(NameOption.valueOf(MUL, ExpressionFactory::plus));
        addKeyword(NameOption.valueOf(DIV, ExpressionFactory::plus));

        //--------- Other -----------//
        addKeyword(NameOption.valueOf(POINT, ExpressionFactory::plus));
        addKeyword(NameOption.valueOf(PRINT, ExpressionFactory::plus));
        addKeyword(NameOption.valueOf(FILE, ExpressionFactory::plus));
    }

    /**
     * 新增关键字
     * @param nameOption
     */
    public void addKeyword(NameOption<Operator> nameOption) {
        pool.put(nameOption.getName(), nameOption);
    }

    /**
     * 获取关键字
     * @return
     */
    public NameOption<Operator> getKeyword(String name) {
        return pool.get(name);
    }
}
