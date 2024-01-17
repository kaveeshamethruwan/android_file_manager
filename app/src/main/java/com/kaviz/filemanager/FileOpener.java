package com.kaviz.filemanager;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.IOException;

public class FileOpener {

    public static Intent openFile(Context context, File file) throws IOException {

        Uri uri = FileProvider.getUriForFile(context, context.getPackageName()+".provider", file);

        Intent intent = new Intent(Intent.ACTION_VIEW);

        String uriString = uri.toString().toLowerCase();

        if (uriString.endsWith(".doc")) {

            intent.setDataAndType(uri, "application/msword");

        } else if (uriString.endsWith(".pdf")) {

            intent.setDataAndType(uri, "application/pdf");

        } else if (uriString.endsWith(".mp3") || uriString.endsWith(".wav")) {

            intent.setDataAndType(uri, "audio/x-wav");

        } else if (uriString.endsWith(".jpeg") || uriString.endsWith(".jpg") || uriString.endsWith(".png")) {

            intent.setDataAndType(uri, "image/jpeg");

        } else if (uriString.endsWith(".mp4")) {

            intent.setDataAndType(uri, "video/x-*");

        } else {

            intent.setDataAndType(uri, "*/*");

        }

        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);

        return intent;

    }

}
