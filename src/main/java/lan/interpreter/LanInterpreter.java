package lan.interpreter;

import cn.hutool.core.util.NumberUtil;
import lan.ast.Expression;
import lan.ast.BaseExpression;
import lan.ast.expression.EvalExpression;
import lan.ast.expression.PointExpression;
import lan.ast.expression.NumberExpression;
import lan.ast.expression.SymbolExpression;
import lan.base.Definition;
import lan.parser.TextParser;
import lan.parser.Token;

import javax.annotation.Nullable;
import java.util.*;

/**
 * 语言解析器
 * 关键字通过额外的解析器解析，支持扩展
 * word = 123 || "abc" || 'abc' || `abc` || (statement) || [statement] || {statement}
 * term = word || word word
 * operator = term || term op term
 * command = term || term operator operator || operator operator operator
 * statement = operator || command
 */
public class LanInterpreter implements Interpreter {

    /**
     * 分隔符定义
     */
    public static char COMMA = ',';
    public static char SEMICOLON = ';';
    public static char POINT = '.';
    public static char COLON = ':';

    public static char ROUND_BRACKET_LEFT = '(';
    public static char ROUND_BRACKET_RIGHT = ')';

    public static char CURLY_BRACKET_LEFT = '{';
    public static char CURLY_BRACKET_RIGHT = '}';

    public static char SQUARE_BRACKET_LEFT = '[';
    public static char SQUARE_BRACKET_RIGHT = ']';

    public static char ANGLE_BRACKET_LEFT = '<';
    public static char ANGLE_BRACKET_RIGHT = '>';

    public static char SLASH_LEFT = '\\';
    public static char SLASH_RIGHT = '/';

    public static char QUOTE_MARK_DOUBLE = '"';
    public static char QUOTE_MARK_SINGLE = '\'';

    public static char BACK_QUOTE = '`';

    public Set<Character> delimiters = Set.of(
            COMMA,
            SEMICOLON,
            POINT,
            COLON,
            ROUND_BRACKET_LEFT,
            ROUND_BRACKET_RIGHT,
            CURLY_BRACKET_LEFT,
            CURLY_BRACKET_RIGHT,
            SQUARE_BRACKET_LEFT,
            SQUARE_BRACKET_RIGHT,
            ANGLE_BRACKET_LEFT,
            ANGLE_BRACKET_RIGHT,
            SLASH_LEFT,
            SLASH_RIGHT,
            QUOTE_MARK_DOUBLE,
            QUOTE_MARK_SINGLE,
            BACK_QUOTE
    );

    /**
     * 基础文本解析器
     */
    private TextParser parser;

    /**
     * 基础语言定义，运算符，关键字等
     */
    private Definition definition;

    /**
     * 解析栈，有时候需要解析下一个单词 {@link #term}才能确定是否返回;
     * 比如 {@link #operator}, 需要判断下一个单词是否是运算符
     * 可以将已经解析的下一个单词推入此栈中，供其他表达式获取
     */
    private Deque<Expression> termStack = new ArrayDeque<>();

    /**
     * 关键字解析器
     */
    private Map<String, Interpreter> keywordInterpreter = new HashMap<>();

    public LanInterpreter(TextParser parser, Definition definition, @Nullable Map<String, Interpreter> keywordInterpreter) {
        this.parser = parser;
        this.definition = definition;
        this.keywordInterpreter = keywordInterpreter;
        this.parser.addDelimiters(delimiters);
    }

    /**
     * 查找关键字对应的解析器，关键字对应的语法结构通过专门的解析器解析
     * @param keyword
     * @return
     */
    private Interpreter getKeywordInterpreter(String keyword) {
        return this.keywordInterpreter.get(keyword);
    }

    /**
     * 字，原子表达式, 即最小结构的完整表达式
     * expr = 123 || "abc" || 'abc' || `abc` || (statement) || [statement] || {statement}
     * @return
     */
    public Expression word() {
        parser.skipBlank(); // 去掉空白字符
        char current = parser.current();
        if (current == ROUND_BRACKET_LEFT) { // (

        } else if (current == SQUARE_BRACKET_LEFT) { // [

        } else if (current == CURLY_BRACKET_LEFT) { // [

        } else if (current == QUOTE_MARK_DOUBLE) { // "

        } else if (current == QUOTE_MARK_SINGLE) { // '

        } else if (current == BACK_QUOTE) { // `

        } else if (isLineBreak()) {
            return Token.EOL;
        } else if (parser.isDelimiter(current)) { // 间隔符
            parser.next(); // 先去掉
            return word();
        } else if (parser.hasNext()) { // 数字，字面量等
            String token = parser.nextWord();
            // 关键字
            if (this.definition.isKeyWord(token)) {
                // 交给对应的关键字解析器解析
                Interpreter keywordInterpreter = getKeywordInterpreter(token);
                // 调用
                return null;
            }

            if (NumberUtil.isNumber(token)) { //todo 无依赖
                return new NumberExpression(token);
            }
            return new SymbolExpression(token);
        }

        return Token.EOF;
    }

    /**
     * 获取一个单词
     * term = word || word word
     * foo(...) || foo.bar || foo::bar || foo: bar （左强结合）
     * @return
     */
    private Expression term() {
        Expression poll = termStack.poll();
        if (Objects.isNull(poll)) {
            return term(word());
        }

        return poll;
    }

    /**
     * 解析词语，根据 left 继续向下解析一次
     * 不包括运算符表达式 {@link #operator} 和命令表达
     * 式 {@link #command(Expression)}
     * e.g. foo(...) || foo.bar || foo::bar || foo: bar （左强结合）
     * @return
     */
    private Expression term(Expression left) {
        if (parser.skipBlankNotLineBreak()) { // left ... 中间有空白字符
            // expr (...) // 命令调用
            if (parser.currentIs('(') || parser.currentIs('[') || parser.currentIs('{')) {
                return left;
            }
            return term(left);
        }
        char current = parser.current();
        if (current == '(') { // 函数调用 expr(...)
            Expression expression = roundBracketExpr();
            // expr(...)(...)
            return term(expression);
        } else if (current == ',') { // expr1, expr2...
            left = commaListExpr(left);
            return left;
        } else if (current == '.') {

        }

        return left;
    }

    /**
     * 从头解析一个句子，即由表达式 {@link #word()}，运算符 {@link #operator(Expression, Expression)}，
     * 命令 {@link #command(Expression)} 等组成的语句
     * 语句结合顺序：expr -> operator -> command
     * statement = operator || command
     * @return
     */
    public Expression statement() {
        Expression term = term();
        if (term == Token.EOF) { // 结束解析
            return Token.EOF;
        }
        return statement(term);
    }

    /**
     * 解析句子。根据句子开头，解析句子接下来的语句结构
     * 解析完成后吃掉行结束符
     * e.g. head a b... || head + b...
     * statement = operator || command
     * @param head
     * @return
     */
    private Expression statement(Expression head) {
        if (isLineBreakSkipBlank()) {
            parser.next(); // eat
            return head;
        }

        if (!parser.isDelimiter()) {
            Expression term = term();

            // head term... 运算符
            if (definition.isOperator(term)) {
                return operator(head, term);
            }

            // 函数调用 head param...
            return command(head, term);
        }

        Expression commandExpr = command(head);

        // 句子解析结束
        if (isLineBreakSkipBlank()) {
            parser.next(); // eat '\n'
        }

        return commandExpr;
    }

    /**
     * 命令方式的函数调用
     * command = term || term operator operator || operator operator operator
     * @return
     */
    private Expression command(Expression cmd) {
        if (isDelimiterOrEndSkipBlank()) {
            return cmd;
        }
        Expression term = term();
        return command(cmd, term);
    }

    /**
     * command = term || term operator operator || operator operator operator
     * @param cmd
     * @param term 已经解析的下一个词语
     * @return
     */
    private Expression command(Expression cmd, Expression term) {
        BaseExpression baseExpression = new EvalExpression();
        baseExpression.add(cmd);

        // cmd term 语句结束直接返回
        if (isDelimiterOrEndSkipBlank()) {
            baseExpression.add(term);
            return baseExpression;
        }

        // cmd term... 解析参数
        List<Expression> params = new ArrayList<>();
        baseExpression.addAll(listExpr(params, term));
        return baseExpression;
    }

    /**
     * 解析列表表达式，即空格分割的多个表达式
     * term term term || term operator operator || operator operator operator
     * @param list 表达式容器
     * @return
     */
    private List<Expression> listExpr(List<Expression> list, Expression term) {
        if (isDelimiterOrEndSkipBlank()) {
            list.add(term);
            return list;
        }
        Expression nextTerm = term(); // list term nextTerm...
        if (definition.isOperator(nextTerm)) { // operator = term nextTerm... 运算符
            Expression operator = operator(term, nextTerm);
            list.add(operator); // [list operator]
            if (isDelimiterOrEndSkipBlank()) {
                // 运算符可能预取了下一个单词，这里加上
                Expression poll = termStack.poll();
                if (Objects.nonNull(poll)) {
                    list.add(poll);
                }
                return list;
            }
            nextTerm = term(); // [list operator] nextTerm
        } else {
            list.add(term); // [list term] nextTerm
        }

        // list nextTerm
        if (isDelimiterOrEndSkipBlank()) {
            list.add(nextTerm);
            return list;
        }

        // list nextTerm...
        return listExpr(list, nextTerm);
    }

    /**
     * 判断语句是否结束
     * @return
     */
    private boolean isLineBreak() {
        return parser.current() == '\n' || parser.current() == ';';
    }

    /**
     * 是换行符或者文档结束
     * @return
     */
    private boolean isLineBreakOrEnd() {
        return isLineBreak() || !parser.hasNext();
    }

    /**
     * 跳过空格符后，判断当前字符是否换行符
     * @return
     */
    private boolean isLineBreakSkipBlank() {
        parser.skipBlankNotLineBreak();
        return isLineBreak();
    }

    /**
     * 跳过空格符后，判断当前字符是否间隔符
     * @return
     */
    private boolean isDelimiterOrEndSkipBlank() {
        parser.skipBlankNotLineBreak();
        return parser.isDelimiter() || isLineBreakOrEnd();
    }

    /**
     * 解析都好分割的列表
     * e.g. expr1, expr2, expr3...
     * @return
     */
    private Expression commaListExpr(Expression expr) {
        parser.next(); // eat ','

        return null;
    }

    /**
     * 解析括号表达式
     * e.g. (a + b) || (max a b) || (a, b, c)
     * @return
     */
    private Expression roundBracketExpr() {
        parser.next(); // eat '('

        return null;
    }


    /**
     * 解析运算符表达式
     * @param left
     * @param op
     * @return
     */
    private Expression operator(Expression left, Expression op) {
        if (isDelimiterOrEndSkipBlank()) {
            return left;
        }

        // Operator binary = definition.createOperator(String.valueOf(op));

        // 运算符等同函数调用
        // left.op 调用类 left 中的函数 op
        PointExpression point = new PointExpression();
        point.add(left);
        point.add(op);

        // op 函数调用
        EvalExpression eval = new EvalExpression();
        eval.add(point); // left.op ...

        Expression right = term();
        if (isDelimiterOrEndSkipBlank()) { // left op right
            eval.add(right);
            return eval;
        }

        Expression op2 = term(); // left op right op2...

        if (!definition.isOperator(op2)) {
            // left op right term... 运算符在列表中
            eval.add(right);

            termStack.push(op2);

            // 返回运算符表达式 left op right
            return eval;
        }

        int precedence = definition.comparePrecedence(op, op2);
        if (precedence < 0) { // left op (right op2...
            eval.add(operator(right, op2));
            return eval;
        } else { // (left op right) op2...
            eval.add(right);
            return operator(eval, op2);
        }
    }

}
