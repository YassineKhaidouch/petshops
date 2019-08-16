package petshops.developerpet.com.petshops;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import java.util.ArrayList;

import petshops.developerpet.com.petshops.Auth_Activities.LoginActivity;
import petshops.developerpet.com.petshops.fragments.Home_Fragment;;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.PACKAGE_USAGE_STATS;


public class SplashActivity extends Activity{

    private ArrayList<String> permissionsToRequest;
    private ArrayList<String> permissionsRejected = new ArrayList<>();
    private ArrayList<String> permissions = new ArrayList<>();

    private final static int ALL_PERMISSIONS_RESULT = 101;
    LocationTrack locationTrack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.splash_activity);
       // LanguageHelper.changeLocale(getResources(), SharedReferenceHelper.getInstance(this).getLanguage());

        Intent intent = getIntent();
        if(intent != null ){//&& intent.getAction().equals(LAUNCH_FROM_URL)){
            Bundle bundle = intent.getExtras();
            if(bundle != null){

                String msgFromBrowserUrl = bundle.getString("id");
                Toast.makeText(this, "get data : "+msgFromBrowserUrl, Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this, "Normal app lunched ", Toast.LENGTH_SHORT).show();
        }

        permissions.add(ACCESS_FINE_LOCATION);
        permissions.add(ACCESS_COARSE_LOCATION);

        permissionsToRequest = findUnAskedPermissions(permissions);
        //get the permissions we have asked for before but are not granted..
        //we will store this in a global list to access later.


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (permissionsToRequest.size() > 0)
                requestPermissions(permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
        }

        locationTrack = new LocationTrack(SplashActivity.this);

        if (locationTrack.canGetLocation()) {
            //longitude = locationTrack.getLongitude();
             //latitude = locationTrack.getLatitude();
            Home_Fragment coords = new Home_Fragment();
            if(locationTrack.getLatitude() != 0 && locationTrack.getLatitude() != 0){
                coords.x = locationTrack.getLongitude();
                coords.y = locationTrack.getLatitude();
            }
        } else {
            locationTrack.showSettingsAlert();
        }

        Intent i = new Intent(SplashActivity.this, LoginActivity.class);
        startActivity(i);

        overridePendingTransition(R.anim.zoom_out, R.anim.zoom_in);
        finish();
        // get location

        /*
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent i = new Intent(SplashActivity.this, LoginActivity.class);
                startActivity(i);
                finish();
            }
        },3*1000);
*/

                }

        private ArrayList<String> findUnAskedPermissions(ArrayList<String> wanted) {
            ArrayList<String> result = new ArrayList<String>();
            for (String perm : wanted) {
                if (!hasPermission(perm)) {
                    result.add(perm);
                }
            }

            return result;
        }

        private boolean hasPermission(String permission) {
            if (canMakeSmores()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                    return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
                }
            }
            return true;
        }

        private boolean canMakeSmores() {
            return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
        }


        @TargetApi(Build.VERSION_CODES.M)
        @Override
        public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
            switch (requestCode) {
                case ALL_PERMISSIONS_RESULT:
                    for (String perms : permissionsToRequest) {
                        if (!hasPermission(perms)) {
                            permissionsRejected.add(perms);
                        }
                    }
                    if (permissionsRejected.size() > 0) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                            if (shouldShowRequestPermissionRationale(permissionsRejected.get(0))) {
                                showMessageOKCancel("These permissions are mandatory for the application. Please allow access.",
                                        new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                    requestPermissions(permissionsRejected.toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                                }
                                            }
                                        });
                                return;
                            }
                        }
                    }
                    break;
            }
        }
        private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
            new AlertDialog.Builder(SplashActivity.this)
                    .setMessage(message)
                    .setPositiveButton("OK", okListener)
                    .setNegativeButton("Cancel", null)
                    .create()
                    .show();
        }

        @Override
        protected void onDestroy() {
            super.onDestroy();
            locationTrack.stopListener();
        }
}
