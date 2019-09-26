package io.neurolab.utilities;

import android.content.Context;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;

import com.opencsv.CSVWriter;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static io.neurolab.activities.DataLoggerActivity.PACKAGE_NAME;
import static io.neurolab.activities.DataLoggerActivity.fileList;
import static io.neurolab.activities.DataLoggerActivity.filesList;
import static io.neurolab.activities.DataLoggerActivity.context;

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

    public static void recordData(String data) {
        setupPath();
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String fileName = sdf.format(currentTime);
        File csvFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +
                File.separator + CSV_DIRECTORY + File.separator + fileName + ".csv");
        if (!csvFile.exists()) {
            try {
                csvFile.createNewFile();
                writeCsvFile(csvFile, data);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static void writeCsvFile(File csvFile, String data) {
        if (csvFile.exists()) {
            try {
                PrintWriter out
                        = new PrintWriter(new BufferedWriter(new FileWriter(csvFile, true)));
                out.write(data + "\n");
                out.flush();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public static void saveData(String importedFilePath) {
        File importedFile = new File(importedFilePath);
        setupPath();
        Date currentTime = Calendar.getInstance().getTime();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String fileName = sdf.format(currentTime);
        File dst = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +
                File.separator + CSV_DIRECTORY + File.separator + fileName + ".csv");
        if (!dst.exists()) {
            try {
                transfer(importedFile, dst);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void readWriteData(String filename, File appDir) {
        String fileName = filename;
        String filePath = appDir + File.separator + fileName + ".csv";
        File file = new File(filePath);

        try {
            FileWriter outputfile = new FileWriter(file);
            CSVWriter writer = new CSVWriter(outputfile);
            String line = "";

            InputStream ins = context.getResources().openRawResource(context.getResources().getIdentifier(filename, "raw", PACKAGE_NAME));
            BufferedReader reader = new BufferedReader(new InputStreamReader(ins, Charset.forName("UTF-8")));

            try {
                while ((line = reader.readLine()) != null) {
                    String[] nextRecord = line.split(",");
                    writer.writeNext(nextRecord);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            writer.close();
            fileList.add(file.getPath());
            filesList.add(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void transfer(File src, File dst) throws IOException {
        InputStream in = new FileInputStream(src);
        try {
            OutputStream out = new FileOutputStream(dst);
            try {
                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            } finally {
                out.close();
            }
        } finally {
            in.close();
        }
    }

    public static void setFileName(String oldName, String newName) {
        String currentFileName = oldName.substring(oldName.lastIndexOf("/"));
        currentFileName = currentFileName.substring(1);

        File directory = new File(Environment.getExternalStorageDirectory().getAbsolutePath() +
                File.separator + CSV_DIRECTORY);
        File from = new File(directory, currentFileName);
        File to = new File(directory, newName.trim() + ".csv");
        from.renameTo(to);
    }

    public static void deleteFile(File csvFile) {
        csvFile.delete();
    }
}
