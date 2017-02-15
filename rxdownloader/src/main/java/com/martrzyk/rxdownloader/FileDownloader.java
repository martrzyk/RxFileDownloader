package com.martrzyk.rxdownloader;

import android.content.Context;
import android.os.Environment;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okio.BufferedSink;
import okio.Okio;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.exceptions.OnErrorThrowable;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

/**
 * Created by mar3k on 2016-10-14.
 */

public class FileDownloader {
    public static Observable download(String directoryPath, Download things) {
        ArrayList<Download> files = new ArrayList<>();
        files.add(things);

        return download(directoryPath, files);
    }

    public static Observable downloadToPicture(Context ctx, List<Download> things) {
        if (ctx == null)
            return null;

        String directoryPath = ctx.getExternalFilesDir(Environment.DIRECTORY_PICTURES).getAbsolutePath();

        return download(directoryPath, things, 1, 1);
    }

    public static Observable downloadToDocuments(Context ctx, List<Download> things) {
        if (ctx == null)
            return null;

        String directoryPath = ctx.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath();

        return download(directoryPath, things, 1, 1);
    }

    public static Observable download(String directoryPath, List<Download> things) {
        return download(directoryPath, things, 1, 1);
    }

    public static Observable download(String directoryPath, List<Download> files, int bufferSize, int maxConcurrent) {
        final String localDirectoryPath = directoryPath;
        return Observable
                .from(files)
                .flatMap(
                        new Func1<Download, Observable<?>>() {
                            @Override
                            public Observable<?> call(Download fileObj) {
                                File file = new File(localDirectoryPath + File.separator + fileObj.getName());

                                RxFileCache cache = RxFileCache.getInstance();
                                if (file.exists()) {
                                    return Observable.just(file);
                                }

                                if (cache.get(fileObj.getName()) != null) {
                                    return null;
                                }

                                Observable<File> observable = fileDownload(fileObj, file);
                                cache.cache.putIfAbsent(fileObj.getName(), observable);

                                return observable;
                            }
                        }
                )//, maxConcurrent)
                .buffer(bufferSize)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private static Observable<File> fileDownload(final Download fileObj, final File file) {

        Observable<File> fileObservable = Observable.create(
                new Observable.OnSubscribe<File>() {
                    @Override
                    public void call(Subscriber<? super File> sub) {
                        if (sub.isUnsubscribed()) {
                            return;
                        }

                        OkHttpClient client = new OkHttpClient();
                        Request request = new Request.Builder().url(fileObj.getAddress()).build();

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

                                RxFileCache cache = RxFileCache.getInstance();
                                cache.remove(fileObj.getName());
                            }
                        } catch (IOException io) {
                            RxFileCache cache = RxFileCache.getInstance();
                            cache.remove(fileObj.getName());

                            throw OnErrorThrowable.from(OnErrorThrowable.addValueAsLastCause(io, fileObj));
                        }
                    }
                });
        fileObservable.subscribeOn(Schedulers.io());

        return fileObservable;
    }
}
