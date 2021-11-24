package lan.ast;

import java.util.HashMap;
import java.util.Map;

public abstract class AbstractContext<K, V> implements Context<K, V> {
    private Map<K, V> table = new HashMap<>();
    private Context<K, V> parent;

    public AbstractContext() {}

    public AbstractContext(Context<K, V> parent) {
        this.parent = parent;
    }

    @Override
    public V lookup(K key) {
        return table.get(key);
    }

    @Override
    public boolean update(K key, V value) {
        if (table.containsKey(key)) { //update key
            table.put(key, value);
            return true;
        }
        return false;
    }

    @Override
    public V add(K key, V value) {
        return table.put(key, value);
    }

    @Override
    public boolean contains(K key) {
        return table.containsKey(key);
    }

    @Override
    public Context<K, V> getParent() {
        return parent;
    }
}
