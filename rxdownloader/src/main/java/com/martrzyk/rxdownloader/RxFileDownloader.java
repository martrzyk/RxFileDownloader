package com.martrzyk.rxdownloader;

import android.annotation.SuppressLint;
import android.content.Context;
import android.support.v4.text.TextUtilsCompat;
import android.text.TextUtils;
import android.util.Log;

import com.martrzyk.rxdownloader.model.Download;
import com.martrzyk.rxdownloader.utils.SupportUtils;

import org.apache.commons.codec.binary.StringUtils;

import rx.Observable;

/**
 * File downloader and caching manager
 * <p>
 * Created by mar3k on 2017-03-27.
 */

@SuppressWarnings("WeakerAccess") //Public implementation
public class RxFileDownloader {
    private static final String TAG = RxFileDownloader.class.getSimpleName();

    private FileDownloadManager downloadManager;
    private Downloader downloader;
    private Context context;

    @SuppressLint("StaticFieldLeak") //context is necessary for this class to work to work
    static volatile RxFileDownloader singleton;

    public static RxFileDownloader with(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("context == null");
        }

        if (singleton == null) {
            synchronized (RxFileDownloader.class) {
                if (singleton == null) {
                    singleton = new RxFileDownloader(context);
                }
            }
        }
        return singleton;
    }

    private RxFileDownloader(Context context) {
        initContext(context);
        initDownloaders();
    }

    private void initContext(Context context) {
        if (context == null) {
            throw new IllegalArgumentException("Context must not be null.");
        }
        this.context = context.getApplicationContext();
    }


    private void initDownloaders() {
        if (this.downloadManager == null)
            this.downloadManager = FileDownloadManager.with();

        if (this.downloader == null)
            this.downloader = Downloader.builder().downloadManager(downloadManager).build();
    }

    public Observable cacheFile(String name, String tag, String url) {
        Download fileToDownload = new Download(name, tag, url);
        return this.downloader.download("", fileToDownload);
    }

    public Observable cacheFileByType(String name, String tag, String url) {
        if (SupportUtils.isAnyTextEmpty(name, tag, url)) {
            Log.e(TAG, "one of arguments is empty!");
            return Observable.empty();
        }

        SupportUtils.getMimeType(url);


        Download fileToDownload = new Download(name, tag, url);
        return this.downloader.download("", fileToDownload);
    }
}
