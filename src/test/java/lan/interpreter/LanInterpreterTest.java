package lan.interpreter;

import lan.ast.Expression;
import lan.base.impl.LanDefinition;
import lan.parser.TextParser;
import org.junit.jupiter.api.Test;

import java.util.ArrayDeque;
import java.util.Deque;

public class LanInterpreterTest {
    @Test
    public void plusTest() {
        // TextParser parser = TextParser.text("3 + 2");
        TextParser parser = TextParser.text("\"hello\" + \"world\" + 2 + 5");
        LanInterpreter lanInterpreter = new LanInterpreter(parser, new LanDefinition(), null);
        Expression statement = lanInterpreter.statement();
        Expression eval = statement.eval();
        System.out.println(eval);
    }

    @Test
    public void cmdTest() {
        TextParser parser = TextParser.text("max 3 5 + a + 7 88 99");
        LanInterpreter lanInterpreter = new LanInterpreter(parser, new LanDefinition(), null);
        Expression statement = lanInterpreter.statement();
        System.out.println();
    }

    @Test
    public void dequeTest() {
        Deque<String> stack = new ArrayDeque<>();
        String pop = stack.pop();
        System.out.println();
    }

}
