package lan.parser;

public interface Tokenizer {

    Token current();

    Token next();

    boolean hasNext();

    default int getLineNumber() {
        return 1;
    }

    /**
     * 获取
     * @return
     */
    int[] position();
}
