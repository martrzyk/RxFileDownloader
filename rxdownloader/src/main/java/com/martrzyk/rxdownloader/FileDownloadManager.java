package com.martrzyk.rxdownloader;

import java.io.File;
import java.util.concurrent.ConcurrentHashMap;

import lombok.Getter;
import rx.Observable;

class FileDownloadManager {
    @Getter
    final ConcurrentHashMap<String, Observable<File>> cache;

    private static FileDownloadManager instance;

    static FileDownloadManager with() {
        if (instance == null)
            instance = new FileDownloadManager();

        return instance;
    }

    private FileDownloadManager() {
        this.cache = new ConcurrentHashMap<>();
    }

    Observable<File> get(String key) {
        Observable<File> o = cache.get(key);

        if (o != null) {
            return o;
        }

        return null;
    }

    public boolean contains(String key) {
        Observable o = cache.get(key);
        return o != null;
    }

    void remove(String key) {
        cache.remove(key);
    }
}