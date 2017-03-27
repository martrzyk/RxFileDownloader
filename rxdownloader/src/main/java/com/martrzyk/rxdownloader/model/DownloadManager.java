package com.martrzyk.rxdownloader.model;

/**
 * Created by Marek on 2017-03-27.
 */

public interface DownloadManager<K, V> {
    void add(K key, V value);

    void remove(K removeKey);

    boolean contains(K containsKey);

    V get(K getKey);
}
