package com.martrzyk.rxdownloader;

import android.content.Context;
import android.os.Environment;

import com.martrzyk.rxdownloader.model.Download;
import com.martrzyk.rxdownloader.model.DownloadManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import lombok.Builder;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okio.BufferedSink;
import okio.Okio;
import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.exceptions.OnErrorThrowable;
import rx.schedulers.Schedulers;

@Builder
public class Downloader {
    private DownloadManager downloadManager;
    @Builder.Default
    private int bufferSize = 10;
    @Builder.Default
    private int maxConcurrent = 10;

    public Observable download(String directoryPath, Download things) {
        if (downloadManager == null)
            throw new IllegalArgumentException("You must provide instance of DownloadManager");

        ArrayList<Download> files = new ArrayList<>();
        files.add(things);

        return download(directoryPath, files, bufferSize, maxConcurrent);
    }

    private Observable download(String directoryPath, List<Download> files, int bufferSize, int maxConcurrent) {
        final String localDirectoryPath = directoryPath;
        return Observable
                .from(files)
                .flatMap(fileObj -> {
                            File file = new File(localDirectoryPath + File.separator + fileObj.getName());

                            FileDownloadManager cache = FileDownloadManager.with();
                            if (file.exists()) {
                                return Observable.just(file);
                            }

                            if (cache.get(fileObj.getTag()) != null) {
                                return null;
                            }

                            Observable<File> observable = fileDownload(fileObj, file);
                            cache.cache.putIfAbsent(fileObj.getTag(), observable);

                            return observable;
                        }
                )
                .buffer(bufferSize)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private Observable<File> fileDownload(final Download fileObj, final File file) {

        Observable<File> fileObservable = Observable.create(
                sub -> {
                    if (sub.isUnsubscribed()) {
                        return;
                    }

                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder().url(fileObj.getUrl()).build();

                    Response response;
                    try {
                        response = client.newCall(request).execute();
                        if (!response.isSuccessful()) {
                            throw new IOException();
                        }

                        if (!sub.isUnsubscribed()) {
                            BufferedSink sink = Okio.buffer(Okio.sink(file));
                            sink.writeAll(response.body().source());
                            sink.flush();
                            sink.close();
                            sub.onNext(file);
                            sub.onCompleted();

                            FileDownloadManager cache = FileDownloadManager.with();
                            cache.remove(fileObj.getName());
                        }
                    } catch (IOException io) {
                        FileDownloadManager cache = FileDownloadManager.with();
                        cache.remove(fileObj.getName());

                        throw OnErrorThrowable.from(OnErrorThrowable.addValueAsLastCause(io, fileObj));
                    }
                });
        fileObservable.subscribeOn(Schedulers.io());

        return fileObservable;
    }
}
