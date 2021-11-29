package lan.util;

import lan.ast.Expression;

public interface Container<T> {
    void add(T element);

    void addAll(T[] elements);

    T get(int i);

    int size();

    boolean isEmpty();

    /**
     * 返回数组元素拷贝
     * @return
     */
    T[] toArray();

    /**
     * 获取数组切片 [start, end)，左开右闭
     * @param start 包括 start
     * @param end 不包括 end
     * @return
     */
    Expression[] slice(int start, int end);

    /**
     * 获取数组切片 [start, end)，start 开始的所有元素
     * @param start
     * @return
     */
    Expression[] slice(int start);

    void reverse();
}
