package lan.parser.impl;

import lan.ast.Expression;
import lan.base.Definition;
import lan.parser.Interpreter;
import lan.parser.TextParser;
import lan.parser.Token;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * 语言解析器
 * 关键字通过额外的解析器解析，支持扩展
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
     * 解析一个原子表达式, 即最小结构的完整表达式
     * e.g. (...) || {...} || [...] || "string" || 123 || symbol
     * @return
     */
    public Expression expr() {
        char current = parser.current();
        if (current == ROUND_BRACKET_LEFT) { // (

        } else if (current == SQUARE_BRACKET_LEFT) { // [

        } else if (current == CURLY_BRACKET_LEFT) { // [

        } else if (current == QUOTE_MARK_DOUBLE) { // "

        } else if (current == QUOTE_MARK_SINGLE) { // '

        } else if (current == BACK_QUOTE) { // `

        } else if (!parser.isDelimiter(current)) { // 数字，字面量
            String token = parser.nextWord();
            // 关键字
            if (this.definition.isKeyWord(token)) {
                // 交给对应的关键字解析器解析
                Interpreter keywordInterpreter = getKeywordInterpreter(token);
                // 调用
            }
        } else if (parser.hasNext()) {
            return null;
        }

        return Token.EOF;
    }

    /**
     * 从头解析一个句子，即由表达式 {@link #expr()}，运算符 {@link #operatorExpr(Expression, String)}，
     * 命令 {@link #commandExpr(Expression)} 等组成的语句
     * @return
     */
    private Expression statement() {
        parser.skipBlank(); // 去掉空白字符
        if (!parser.hasNext()) {
            return Token.EOF;
        }

        Expression expr = expr();
        return statementTail(expr, true);
    }

    /**
     * 根据句子的开头，解析句子接下来的语句结构
     * @param left
     * @param parseCommand 是否解析命令表达式， e.g. left 3 5
     *                     如果 left 可能是一个命令，则为 true, 否则为 false
     * @return
     */
    private Expression statementTail(Expression left, boolean parseCommand) {
        // expr ... 表达式后面跟空格
        if (parser.skipBlankNotLineBreak()) {
            char current = parser.current();
            if (isLineBreak()) {
                parser.next(); // eat
                return left;
            }

            // 运算符
            if (!parser.isDelimiter(current)) {
                String word = parser.nextWord();

                // left +... 运算符
                if (definition.isOperator(word)) {
                    return operatorExpr(left, word);
                }

                // 函数调用 left param...
                if (parseCommand) {
                    return commandExpr(left, word);
                }
            }

            // expr (... 命令调用，注意中间由空格，无空格表示函数调用
            if (current == '(' || current == '[' || current == '{') {
                Expression commandExpr = commandExpr(left);
                return commandExpr;
            }

        }
        char current = parser.current();
        if (current == '(') { // 函数调用 expr(...)

        } else if (current == ',') { // expr1, expr2...
            left = commaListExpr(left);
            return statementTail(left, false);
        } else if (current == '.') {

        }

        if (isLineBreak()) {
            parser.next(); // eat '\n'
            return left;
        }

        return left;
    }

    /**
     * 命令方式的函数调用
     * e.g. max a b c
     * @return
     */
    private Expression commandExpr(Expression left) {
        return null;
    }

    /**
     *
     * @param left
     * @param nextWord 已经解析的下一个单词
     * @return
     */
    private Expression commandExpr(Expression left, String nextWord) {
        return null;
    }

    /**
     * 判断语句是否结束
     * @return
     */
    private boolean isLineBreak() {
        return parser.current() == '\n' || parser.current() == ';';
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
    private Expression operatorExpr(Expression left, String op) {
        return null;
    }
}
