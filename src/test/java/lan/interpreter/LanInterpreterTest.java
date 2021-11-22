package lan.interpreter;

import lan.ast.Expression;
import lan.base.impl.LanDefinition;
import lan.parser.TextParser;
import org.junit.jupiter.api.Test;

public class LanInterpreterTest {
    @Test
    public void cmdTest() {
        TextParser parser = TextParser.text("max 3 5 + a");
        LanInterpreter lanInterpreter = new LanInterpreter(parser, new LanDefinition(), null);
        Expression statement = lanInterpreter.statement();
        System.out.println();
    }
}
