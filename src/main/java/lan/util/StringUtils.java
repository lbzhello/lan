package lan.util;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class StringUtils {

    /**
     * Return {@code true} if the given CharSequence is {@code null} or contains no elements
     * @param cs the CharSequence to check
     * @return whether the given CharSequence is empty
     */
    public static boolean isEmpty(@Nullable CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    /**
     * Return {@code true} if a CharSequence is not {@code null} and contains at least one element
     * @param cs the CharSequence to check
     * @return whether the given CharSequence is empty
     * @see #isEmpty(CharSequence)
     */
    public static boolean isNotEmpty(@Nullable CharSequence cs) {
        return !isEmpty(cs);
    }

    /**
     * Return {@code true} if the CharSequence is null, empty or whitespace only
     * Whitespace is defined by {@link Character#isWhitespace(char)}
     * @param cs  the CharSequence to check
     * @return whether the given CharSequence is {@link #isEmpty} or whitespace only
     */
    public static boolean isBlank(@Nullable CharSequence cs) {
        if (isEmpty(cs)) {
            return true;
        }

        int len = cs.length();
        for (int i = 0; i < len; i++) {
            if (!Character.isWhitespace(cs.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * Return {@code true} if the CharSequence is not {@link #isBlank(CharSequence)}
     * @param cs the CharSequence to check
     * @return whether the given CharSequence is not {@link #isBlank(CharSequence)}
     */
    public static boolean isNotBlank(@Nullable CharSequence cs) {
        return !isBlank(cs);
    }

    public static <T> String toString(T obj) {
        return format(obj, it -> it.toString());
    }

    public static <T> String format(T obj, Function<T, String> formatter) {
        return obj == null ? "" : formatter.apply(obj);
    }

    public static void main(String[] args) {
        List<String> list = new ArrayList<>();
        format(list, it -> it.toString());
    }
}
