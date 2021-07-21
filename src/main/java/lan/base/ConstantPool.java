package lan.base;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * 常量池
 */
public class ConstantPool<T> {

    private final Map<String, T> pool = new HashMap<>();

    /**
     * 获取或创建一个值
      * @param name
     * @return
     */
    public final T getOrElse(String name, Function<String, T> creator) {
        return pool.computeIfAbsent(name, creator);
    }

    /**
     * 值是否已存在池中
     * @param name
     * @return
     */
    public boolean exists(String name) {
        return pool.containsKey(name);
    }

}
