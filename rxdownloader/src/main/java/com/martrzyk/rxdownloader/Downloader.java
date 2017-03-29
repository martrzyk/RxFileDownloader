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
import lombok.Getter;
import lombok.Setter;
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
@Getter
@Setter
public class Downloader {
    private DownloadManager downloadManager;
    @Builder.Default
    private int bufferSize = 10;
    @Builder.Default
    private int maxConcurrent = 10;

    Observable download(String directoryPath, Download downloadData) {
        if (downloadManager == null)
            throw new IllegalArgumentException("You must provide instance of DownloadManager");

        ArrayList<Download> files = new ArrayList<>();
        files.add(downloadData);

        return download(directoryPath, files, bufferSize, maxConcurrent);
    }

    private Observable download(String directoryPath, List<Download> files, int bufferSize, int maxConcurrent) {
        final String localDirectoryPath = directoryPath;
        return Observable
                .from(files)
                .flatMap(fileObj -> {
                            File file = new File(localDirectoryPath + File.separator + fileObj.getName());

                            if (file.exists()) {
                                return Observable.just(file);
                            }

                            if (downloadManager.contains(fileObj.getTag())) {
                                return null;
                            }

                            Observable observable = fileDownload(fileObj, file);
                            downloadManager.add(fileObj.getTag(), observable);

                            return observable;
                        }
                )
                .buffer(bufferSize)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread());
    }

    private Observable fileDownload(final Download downloadData, final File outputFile) {

        return Observable.create(
                subscriber -> {
                    if (subscriber.isUnsubscribed()) {
                        return;
                    }

                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder().url(downloadData.getUrl()).build();

                    Response response;
                    try {
                        response = client.newCall(request).execute();
                        if (!response.isSuccessful()) {
                            throw new IOException();
                        }

                        if (!subscriber.isUnsubscribed()) {
                            BufferedSink sink = Okio.buffer(Okio.sink(outputFile));
                            sink.writeAll(response.body().source());
                            sink.flush();
                            sink.close();
                            subscriber.onNext(outputFile);
                            subscriber.onCompleted();

                            FileDownloadManager cache = FileDownloadManager.with();
                            cache.remove(downloadData.getTag());
                        }
                    } catch (IOException io) {
                        FileDownloadManager cache = FileDownloadManager.with();
                        cache.remove(downloadData.getTag());

                        throw OnErrorThrowable.from(OnErrorThrowable.addValueAsLastCause(io, downloadData));
                    }
                })
                .subscribeOn(Schedulers.io());
    }
}
