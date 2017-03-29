package com.martrzyk.filerxdownloader;

import android.content.res.Resources;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.webkit.MimeTypeMap;

import com.martrzyk.filerxdownloader.rxfiledownloader.R;
import com.martrzyk.rxdownloader.utils.SupportUtils;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Resources res = getResources();
        String[] addresses = res.getStringArray(R.array.addresses);

        for (String address : addresses) {
            String mimeType = SupportUtils.getMimeType(address);

            Log.d(MainActivity.class.getSimpleName(), "mimeType = " + mimeType);
        }
    }
}
