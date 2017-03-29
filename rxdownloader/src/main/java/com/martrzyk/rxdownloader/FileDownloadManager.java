package com.martrzyk.rxdownloader;

import com.martrzyk.rxdownloader.model.DownloadManager;

import java.io.File;
import java.util.concurrent.ConcurrentHashMap;

import lombok.Getter;
import rx.Observable;

class FileDownloadManager implements DownloadManager {
    @Getter
    final ConcurrentHashMap<String, Observable> cache;

    private static FileDownloadManager instance;

    static FileDownloadManager with() {
        if (instance == null)
            instance = new FileDownloadManager();

        return instance;
    }

    private FileDownloadManager() {
        this.cache = new ConcurrentHashMap<>();
    }

    @Override
    public Observable get(String key) {
        Observable o = cache.get(key);

        if (o != null) {
            return o;
        }

        return null;
    }

    @Override
    public boolean contains(String key) {
        Observable o = cache.get(key);
        return o != null;
    }

    @Override
    public void add(String key, Observable value) {
        cache.putIfAbsent(key, value);
    }

    @Override
    public void remove(String key) {
        cache.remove(key);
    }
}