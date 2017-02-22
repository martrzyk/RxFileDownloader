package com.martrzyk.filerxdownloader.recycler_view;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.Map;

import lombok.Getter;
import lombok.Setter;
import rx.Subscription;

public abstract class RecyclerViewItem {
    private static final String DEFAULT_SUBSCRIPTION_TAG = "DEFAULT_SUBSCRIPTION_TAG";
    private Map<String, Subscription> subscriptions = new HashMap<>();
    @Setter
    @Getter
    private int viewType = 0;

    public abstract RecyclerView.ViewHolder getViewHolder(ViewGroup parent);

    public abstract void bind(RecyclerView.ViewHolder holder);

    protected void registerSubscription(Subscription subscription) {
        registerSubscription(DEFAULT_SUBSCRIPTION_TAG, subscription);
    }

    protected void registerSubscription(String tag, Subscription subscription) {
        unsubscribe(tag);
        this.subscriptions.put(tag, subscription);
    }

    private void unsubscribeAll() {
        for (String tag : subscriptions.keySet()) {
            unsubscribe(subscriptions.get(tag));
        }
        subscriptions.clear();
    }

    private void unsubscribe(String tag) {
        unsubscribe(subscriptions.remove(tag));
    }

    public void unsubscribe(Subscription subscription) {
        if (subscription != null && !subscription.isUnsubscribed())
            subscription.unsubscribe();
    }

    public void onDestroy() {
        unsubscribeAll();
    }

    public boolean onBackPressed() {
        return true;
    }
}
