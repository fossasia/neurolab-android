package io.neurolab.utilities;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;

import java.io.File;

public class FilePathUtil {

    public static final String CSV_DIRECTORY = "NeuroLab";
    public static final String LOG_FILE_KEY = "LOGFILE";

    public static String getRealPath(Context context, Uri fileUri) {
        return getRealPathFromURI(context, fileUri);
    }

    public static void setupPath() {
        File csvDirectory = new File(
                Environment.getExternalStorageDirectory().getAbsolutePath() +
                        File.separator + CSV_DIRECTORY);
        if (!csvDirectory.exists()) {
            try {
                csvDirectory.mkdir();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        File categoryDirectory = new File(
                Environment.getExternalStorageDirectory().getAbsolutePath() +
                        File.separator + CSV_DIRECTORY);
        if (!categoryDirectory.exists()) {
            try {
                categoryDirectory.mkdir();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static String getRealPathFromURI(final Context context, final Uri uri) {

        String path = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            if (DocumentsContract.isDocumentUri(context, uri)) {
                if (isExternalStorageDocument(uri)) {
                    final String docId = DocumentsContract.getDocumentId(uri);
                    final String[] split = docId.split(":");
                    final String type = split[0];

                    if ("primary".equalsIgnoreCase(type)) {
                        path = Environment.getExternalStorageDirectory() + "/" + split[1];
                    }
                    if ("home".equalsIgnoreCase(type)) {
                        path = Environment.getExternalStorageDirectory() + "/" + split[1];
                    }

                }
            } else {
                StringBuilder pathbuilder = trimExternal(uri.getPath().substring(1));
                pathbuilder.insert(0, Environment.getExternalStorageDirectory() + "/");
                path = pathbuilder.toString();
            }
        } else {
            path = uri.getPath();
        }
        return path;
    }

    private static boolean isExternalStorageDocument(Uri uri) {
        return (uri.getAuthority().equals("com.android.externalstorage.documents") ||
                uri.getAuthority().equals(Environment.getExternalStorageState()));
    }

    private static StringBuilder trimExternal(String path) {
        StringBuilder trimmedPath = new StringBuilder();
        int tempPath = path.indexOf('/');
        trimmedPath.append(path.substring(tempPath + 1));
        return trimmedPath;
    }
}
