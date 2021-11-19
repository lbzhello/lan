package lan.interpreter;

import lan.ast.Expression;
import lan.ast.impl.EvalExpression;
import lan.base.Definition;
import lan.parser.TextParser;
import lan.parser.Token;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

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
            }
        }

        return Token.EOF;
    }

    /**
     * 解析词语，根据 left 继续向下解析一次
     * 不包括运算符表达式 {@link #operator(Expression)} 和命令表达
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
    private Expression statement() {
        Expression term = term(word());
        if (term == Token.EOF) { // 结束解析
            return Token.EOF;
        }
        return statement(term);
    }

    /**
     * 解析句子。根据句子开头，解析句子接下来的语句结构
     * 会吃掉行结束符
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
            Expression term = term(word());

            // head +... 运算符
            if (definition.isOperator(term)) {
                return operator(head, term);
            }

            // 函数调用 head param...
            return command(head, term);
        }

        Expression commandExpr = command(head);

        // 句子解析结束
        if (isLineBreak()) {
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
        if (isLineBreakOrEndSkipBlank()) {
            return cmd;
        }
        Expression term = term(word());
        return command(cmd, term);
    }

    /**
     *
     * @param cmd
     * @param term 已经解析的下一个词语
     * @return
     */
    private Expression command(Expression cmd, Expression term) {
        EvalExpression evalExpression = new EvalExpression();
        evalExpression.add(cmd);

        // cmd term 语句结束直接返回
        if (isLineBreakOrEndSkipBlank()) {
            evalExpression.add(term);
            return evalExpression;
        }

        // cmd term1 term2... 解析参数
        Expression term1 = term;
        do {
            Expression term2 = term(word());
            if (definition.isOperator(term2)) { // 运算符
                Expression operator = operator(term1, term2);
                evalExpression.add(operator);
                if (isLineBreakOrEndSkipBlank()) { // cmd ... operator
                    break;
                }
                term1 = term(word());
            } else {
                evalExpression.add(term1);
                term1 = term2;
            }
            // cmd ... term1 term2
            if (isLineBreakOrEndSkipBlank()) {
                evalExpression.add(term1);
                break;
            }
        } while (parser.hasNext());
        return evalExpression;
    }

    /**
     * 解析命令
     * @param evalExpression
     * @return
     */
    private Expression parseParam(EvalExpression evalExpression, Expression term) {
        Expression nextTerm = term(word());
        if (definition.isOperator(nextTerm)) { // 运算符
            Expression operator = operator(term, nextTerm);
            evalExpression.add(operator);
            if (isLineBreakOrEndSkipBlank()) { // cmd ... operator
                return evalExpression;
            }
            nextTerm = term(word());
        } else {
            evalExpression.add(term);
        }

        // cmd ... term nextTerm
        if (isLineBreakOrEndSkipBlank()) {
            evalExpression.add(nextTerm);
            return evalExpression;
        }

        return parseParam(evalExpression, nextTerm);
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
     * 跳过空格符后，判断当前字符是否换行符，或者结束符
     * @return
     */
    private boolean isLineBreakOrEndSkipBlank() {
        parser.skipBlankNotLineBreak();
        return isLineBreakOrEnd();
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
     * @return
     */
    private Expression operator(Expression left, Expression op) {
        return null;
    }

    /**
     * 解析 operator 或直接返回
     * operator = expr || expr op expr
     * @param left
     * @return
     */
    private Expression operator(Expression left) {
        return null;
    }
}
