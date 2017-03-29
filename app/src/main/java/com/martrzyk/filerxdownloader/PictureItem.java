package com.martrzyk.filerxdownloader;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import com.martrzyk.filerxdownloader.recycler_view.RecyclerViewItem;
import com.martrzyk.filerxdownloader.rxfiledownloader.R;
import com.martrzyk.rxdownloader.model.Download;
import com.martrzyk.rxdownloader.Downloader;
import com.martrzyk.rxdownloader.utils.SupportUtils;

/**
 * Created by mar3k on 2017-02-22.
 */

class PictureItem extends RecyclerViewItem {
    private Picture picture;
    private Context context;


    public PictureItem(Picture picture, Context context) {
        this.picture = picture;
        this.context = context;
    }

    @Override
    public RecyclerView.ViewHolder getViewHolder(ViewGroup parent) {
        return null;
    }

    @Override
    public void bind(RecyclerView.ViewHolder holder) {
        String savePath = context.getExternalFilesDir(null) + "/" + context.getResources().getString(R.string.pictures);

        Download object = new Download(SupportUtils.createMd5FromText(picture.address), SupportUtils.createMd5FromText(picture.address), picture.address);
//        Downloader
//                .download(savePath, object)
//                .subscribe(o -> {
//                });
    }
}
