package com.martrzyk.rxdownloader;

import java.io.File;
import java.util.concurrent.ConcurrentHashMap;

import lombok.Getter;
import rx.Observable;

public class RxFileCache {
    @Getter
    final ConcurrentHashMap<String, Observable<File>> cache;

    private static RxFileCache instance;

    public static RxFileCache getInstance() {
        if(instance == null)
            instance = new RxFileCache();

        return instance;
    }

    public RxFileCache() {
        this.cache = new ConcurrentHashMap<>();
    }

    public Observable<String> get(String key) {
        Observable o = cache.get(key);

        if (o != null) {
            return o;
        }

        return null;
    }

    public boolean contains(String key)
    {
        Observable o = cache.get(key);
        return o != null;
    }

    public void remove(String key) {
        cache.remove(key);
    }
}