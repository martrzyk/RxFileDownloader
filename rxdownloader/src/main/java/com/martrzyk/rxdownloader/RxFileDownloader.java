package com.martrzyk.rxdownloader;

import com.martrzyk.rxdownloader.model.Download;

/**
 * File downloader and caching manager
 * <p>
 * Created by mar3k on 2017-03-27.
 */

@SuppressWarnings("WeakerAccess") //Public implementation
public class RxFileDownloader {
    private FileDownloadManager downloadManager;
    private Downloader downloader;

    private static RxFileDownloader instance;

    public static RxFileDownloader init() {
        if (instance == null)
            instance = new RxFileDownloader();

        return instance;
    }

    private RxFileDownloader() {
        this.downloader = new Downloader();
    }

    public void addFileToDownload() {
        new Download("", "", "");
    }
}
