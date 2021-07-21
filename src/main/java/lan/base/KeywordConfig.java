package lan.base;


import lan.ast.ExpressionFactory;
import lan.ast.Keyword;

import java.util.HashMap;
import java.util.Map;

/**
 * 关键字配置
 */
public class KeywordConfig {
    public static final String DEFINE = "define";
    public static final String FUNCTION = "fn";
    public static final String LET = "let";

    public static final String DO = "do";
    public static final String END = "end";
    public static final String RETURN = "ret";

    public static final String IF = "if";
    public static final String ELSE = "else";
    public static final String ELSEIF = "elif";
    public static final String FOR = "for";

    public static final String NEW = "new";
    public static final String IMPORT = "import";

    // 关键字容器
    private Map<String, NameOption<Keyword>> pool = new HashMap<>();

    public KeywordConfig() {
        addKeyword(NameOption.valueOf(DEFINE, ExpressionFactory::define));
        addKeyword(NameOption.valueOf(FUNCTION, ExpressionFactory::define));
        addKeyword(NameOption.valueOf(LET, ExpressionFactory::define));

        addKeyword(NameOption.valueOf(DO, ExpressionFactory::define));
        addKeyword(NameOption.valueOf(END, ExpressionFactory::define));
        addKeyword(NameOption.valueOf(RETURN, ExpressionFactory::define));

        addKeyword(NameOption.valueOf(IF, ExpressionFactory::define));
        addKeyword(NameOption.valueOf(ELSE, ExpressionFactory::define));
        addKeyword(NameOption.valueOf(ELSEIF, ExpressionFactory::define));
        addKeyword(NameOption.valueOf(FOR, ExpressionFactory::define));

        addKeyword(NameOption.valueOf(NEW, ExpressionFactory::define));
        addKeyword(NameOption.valueOf(IMPORT, ExpressionFactory::define));
    }

    /**
     * 新增关键字
     * @param nameOption
     */
    public void addKeyword(NameOption<Keyword> nameOption) {
        pool.put(nameOption.getName(), nameOption);
    }

    /**
     * 获取关键字
     * @return
     */
    public NameOption<Keyword> getKeyword(String name) {
        return pool.get(name);
    }
}
