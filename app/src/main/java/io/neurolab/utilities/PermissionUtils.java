package io.neurolab.utilities;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;

public class PermissionUtils {

    /**
     * checkRuntimePermissions takes in an Object instance(can be of type Activity or Fragment),
     * an array of permission and checks for if all the permissions are granted ot not
     *
     * @param context     can be of type Activity or Fragment
     * @param permissions string array of permissions
     * @return true if all permissions are granted, otherwise false
     */
    public static boolean checkRuntimePermissions(Object context, String[] permissions) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String permission : permissions) {
                if ((ContextCompat.checkSelfPermission(retrieveContext(context),
                        permission)
                        != PackageManager.PERMISSION_GRANTED)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * requestRuntimePermissions takes in an Object instance(can be of type Activity or Fragment),
     * a String array of permissions and
     * a permission request code and requests for the permission
     *
     * @param context     can be of type Activity or Fragment
     * @param permissions string array of permissions
     * @param requestCode permission request code
     */
    public static void requestRuntimePermissions(Object context, String[] permissions,
                                                 int requestCode) {
        if (context instanceof Activity) {
            ActivityCompat.requestPermissions((AppCompatActivity) context,
                    permissions, requestCode);
        } else if (context instanceof Fragment) {
            ((Fragment) context).requestPermissions(permissions, requestCode);
        }
    }

    /**
     * retrieves context of passed in non-null object, context can be of type
     * AppCompatActivity or Fragment
     *
     * @param context     can be of type AppCompatActivity or Fragment
     */
    private static Context retrieveContext(@NonNull Object context) {
        if (context instanceof AppCompatActivity) {
            return ((AppCompatActivity) context).getApplicationContext();
        } else {
            return ((Fragment) context).requireActivity();
        }
    }
}
