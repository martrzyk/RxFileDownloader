package com.martrzyk.rxdownloader;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Environment;
import android.support.v4.os.EnvironmentCompat;
import android.support.v4.text.TextUtilsCompat;
import android.text.TextUtils;
import android.util.Log;

import com.martrzyk.rxdownloader.model.Download;
import com.martrzyk.rxdownloader.model.FileOutputConfig;
import com.martrzyk.rxdownloader.model.MediaType;
import com.martrzyk.rxdownloader.utils.SupportUtils;

import org.apache.commons.codec.binary.StringUtils;

import java.io.File;

import filerxdownloader.martrzyk.com.rxdownloader.R;
import lombok.Getter;
import rx.Observable;

/**
 * File downloader and caching manager
 * <p>
 * Created by mar3k on 2017-03-27.
 */

@SuppressWarnings("WeakerAccess") //Public implementation
public class RxFileDownloader {
    private static final String TAG = RxFileDownloader.class.getSimpleName();

    @SuppressLint("StaticFieldLeak") //context is necessary for this class to work to work
    static volatile RxFileDownloader singleton;

    private FileDownloadManager downloadManager;
    private Downloader downloader;
    private String basePathForSaving;

    public static RxFileDownloader with() {
        if (singleton == null) {
            synchronized (RxFileDownloader.class) {
                if (singleton == null) {
                    singleton = new RxFileDownloader();
                }
            }
        }
        return singleton;
    }

    protected RxFileDownloader() {
        initDownloadManager();
        initDownloaders();
        initConfiguration();
    }

    protected void initDownloadManager() {
        if (this.downloadManager == null)
            this.downloadManager = FileDownloadManager.with();
    }

    /**
     * Initialize downloaders
     * <p>
     * Creating downlaoder and injecting Manager
     * Download manager must be NOT NULL
     */
    protected void initDownloaders() {
        if (this.downloader == null)
            this.downloader = Downloader.builder().downloadManager(downloadManager).build();

        if (this.downloader.getDownloadManager() != downloadManager) //swapping manager if different one is used now
            this.downloader.setDownloadManager(downloadManager);
    }

    /**
     * Initializing all additional configurations needed for correct downloading
     */
    protected void initConfiguration() {
        basePathForSaving = Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    public Observable cacheFile(String name, String tag, String url) {
        if (SupportUtils.isAnyTextEmpty(name, tag, url)) {
            Log.e(TAG, "one of arguments is empty!");
            return Observable.empty(); //return error in future?
        }

        String pathForSaving = getPathForSaving();

        Download fileToDownload = new Download(name, tag, url);
        return this.downloader.download(pathForSaving, fileToDownload);
    }

    public Observable cacheFileByType(String name, String tag, String url) {
        if (SupportUtils.isAnyTextEmpty(name, tag, url)) {
            Log.e(TAG, "one of arguments is empty!");
            return Observable.empty(); //return error in future?
        }

        String mimeType = SupportUtils.getMimeType(url);
        String pathForSaving = getPathForSaving(mimeType);

        Download fileToDownload = new Download(name, tag, url);
        return this.downloader.download(pathForSaving, fileToDownload);
    }

    private String getPathForSaving() {
        return getPathForSaving(null);
    }

    private String getPathForSaving(String mimeType) {
        String pathForSaving = basePathForSaving + File.separator;

        if (mimeType != null && (mimeType.equals(MediaType.IMAGE_GIF) || mimeType.equals(MediaType.IMAGE_JPEG) || mimeType.equals(MediaType.IMAGE_PNG))) {
            pathForSaving += File.separator + FileOutputConfig.PICTURES;
        } else {
            pathForSaving += File.separator + FileOutputConfig.OTHER;
        }

        return pathForSaving;
    }
}
