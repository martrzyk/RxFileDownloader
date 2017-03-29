package com.martrzyk.rxdownloader.model;


import rx.Observable;

/**
 * Created by Marek on 2017-03-27.
 */

public interface DownloadManager {
    void add(String key, Observable value);

    void remove(String removeKey);

    boolean contains(String containsKey);

    Observable get(String getKey);
}
