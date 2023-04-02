package com.liubaozhu.lan.core.ast;

import java.io.Serializable;
import java.util.Objects;

public interface Context<K,V> extends Serializable {
    /**
     * 在当前上下文中搜索，不考虑层级结构
     * 若不存在返回 null
     * @param key
     * @return
     */
    V lookup(K key);

    /**
     * 在所有层级结构中查找
     * 若不存在返回 null
     * @param key
     * @return
     */
    default V lookupHierarchy(K key) {
        V lookup = lookup(key);
        if (Objects.isNull(lookup) && Objects.nonNull(getParent())) {
            return getParent().lookupHierarchy(key);
        }
        return lookup;
    }

    /**
     * 当前上下文是否存在 key，不考虑层级结构
     * @param key
     * @return
     */
    boolean contains(K key);

    /**
     * 所有层级结构中是否存在 key
     * @return
     */
    default boolean containsHierarchy(K key) {
        boolean contains = contains(key);
        if (!contains && Objects.nonNull(getParent())) {
            return getParent().containsHierarchy(key);
        }
        return contains;
    }

    /**
     * add a (key, value) pair in this context
     * @param key
     * @param value
     * @return
     */
    V add(K key, V value);

    /**
     * key 存在时，更新绑定关系；key 不存在直接返回
     * @param key
     * @param value
     * @return true key 存在; false key 不存在
     */
    boolean update(K key, V value);

    /**
     * 在所有层级结构中更新 key
     * @param key
     * @param value
     * @return true key 存在；false key 不存在
     */
    default boolean updateHierarchy(K key, V value) {
        if (update(key, value)) {
            return true;
        }
        if (Objects.nonNull(getParent())) {
            return getParent().updateHierarchy(key, value);
        }

        return false;
    }

    /**
     * 获取父级上下文
     */
    Context<K, V> getParent();
}