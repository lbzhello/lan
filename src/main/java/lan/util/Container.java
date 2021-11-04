package lan.util;

public interface Container<T> {
    void add(T element);

    void add(T[] elements);

    T get(int i);

    int size();

    boolean isEmpty();

    T[] toArray();
}
