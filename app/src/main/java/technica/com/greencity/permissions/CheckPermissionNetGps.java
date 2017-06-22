package technica.com.greencity.permissions;

import android.Manifest;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import technica.com.greencity.activities.MainActivity;
import technica.com.greencity.activities.Navigation;
import technica.com.greencity.R;
import technica.com.greencity.Utils;

public class CheckPermissionNetGps extends AppCompatActivity {
    private static final int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 101;
    private static final String TAG = "CheckPermissionNetGps";
    LinearLayout waiting;
    TextView waitingText;
    MyHandler handler;
    ProgressBar progressBar;
    SharedPreferences sharedPreference;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_permission_net_gps);
        sharedPreference = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = sharedPreference.edit();
        Log.e(TAG, "onCreate: status : " + sharedPreference.getBoolean(Utils.SIGNUP_DONE, false));
        if (sharedPreference.getBoolean(Utils.SIGNUP_DONE, false)) {
            startActivity(new Intent(CheckPermissionNetGps.this, Navigation.class));
            finish();
        }
        waiting = (LinearLayout) findViewById(R.id.waiting);
        waitingText = (TextView) findViewById(R.id.waitingText);
        handler = new MyHandler();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            askForPermissions();
            checkGpsNetViews();
        } else {
            checkGpsNet();
        }

    }

    private void askForPermissions() {
        requestPermissions(new String[]{
                        Manifest.permission.GET_ACCOUNTS,
                        Manifest.permission.READ_PHONE_STATE,
                        Manifest.permission.ACCESS_FINE_LOCATION,
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.CALL_PHONE},
                REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
    }

    public void checkGpsNet() {

        if (!isOnline() && !isGPSoN()) {
            waitingText.setText("Enable Data connection");
            Toast.makeText(CheckPermissionNetGps.this, "Please enable your GPS", Toast.LENGTH_LONG).show();


        } else if (isOnline() && !isGPSoN()) {
            waitingText.setText("Enable GPS");
            Toast.makeText(CheckPermissionNetGps.this, "Please enable your GPS", Toast.LENGTH_LONG).show();

            Thread thread = new Thread() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                    }

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    });
                }
            };
            thread.start();

        } else if (isOnline() && isGPSoN()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    int counter = 0;

    private boolean isAllPermissionEnabled() {
        return new Utils().isPhonePermissionGranted(CheckPermissionNetGps.this) &&
                new Utils().isReadExtrenalStorageGranted(CheckPermissionNetGps.this) &&
                new Utils().isCallPhonePermissionGranted(CheckPermissionNetGps.this) &&
                new Utils().isLocationPermissionGranted(CheckPermissionNetGps.this);
    }

    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {

        }
    }

    public void tapToContinue(View v) {
        checkGpsNetViews();
    }

    public void checkGpsNetViews() {
        if (isOnline() && !isGPSoN()) {

            waitingText.setText("Enable GPS");
            Toast.makeText(CheckPermissionNetGps.this, "Please enable your GPS", Toast.LENGTH_LONG).show();
            startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
        } else if (isGPSoN() && !isOnline()) {
            waitingText.setText("Enable your data connection (WIFI).");
            Toast.makeText(CheckPermissionNetGps.this, "Please enable your GPS", Toast.LENGTH_LONG).show();

        } else if (isGPSoN() && isOnline() && !isAllPermissionEnabled()) {
            waitingText.setText("Enable all permissions.");
            //showAlertEnableSmsInMarshmallow();
            Toast.makeText(CheckPermissionNetGps.this, "Please enable your All permissions", Toast.LENGTH_LONG).show();

        } else if (isOnline() && isGPSoN() && isAllPermissionEnabled()) {

            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();

        }
    }


    public boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        return info != null && info.isConnectedOrConnecting();
    }

    public boolean isGPSoN() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS:
                if (new Utils().isPhonePermissionGranted(CheckPermissionNetGps.this)
                        && new Utils().isReadExtrenalStorageGranted(CheckPermissionNetGps.this) &&
                        new Utils().isSendSMSPermissionGranted(CheckPermissionNetGps.this) &&
                        new Utils().isCallPhonePermissionGranted(CheckPermissionNetGps.this) &&
                        new Utils().isLocationPermissionGranted(CheckPermissionNetGps.this) && isGPSoN() && isOnline()
                        ) {
                    Log.e(TAG, "onRequestPermissionsResult: " );
                    startActivity(new Intent(this, MainActivity.class));
                    finish();

                } else if (!isGPSoN()) {
                    waitingText.setText("Please enable your GPS.");

                }
                Log.e(TAG, "onRequestPermissionsResult: grantresult: " + grantResults[0]);
                if (!isGPSoN())
                    waitingText.setText("Please enable your GPS.");

                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                    if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.GET_ACCOUNTS) ||
                            !ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_PHONE_STATE) ||
                            !ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION) ||
                            !ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CALL_PHONE)) {
                        showAlertEnableSmsInMarshmallow();
                        //Toast.makeText(this, "YOU MUST ENABLE ALL PERMISSION.S", Toast.LENGTH_SHORT).show();
                    } else
                        askForPermissions();
                }
                // other 'case' lines to check for other
                // permissions this app might request

                break;

        }
        // other 'case' lines to check for other
        // permissions this app might request
    }


    public void showAlertEnableSmsInMarshmallow() {

        List<String> permissionsNeeded = new ArrayList<String>();
        final List<String> permissionsList = new ArrayList<String>();

        if (!addPermission(permissionsList, Manifest.permission.READ_PHONE_STATE))
            permissionsNeeded.add("read phone state");
        if (!addPermission(permissionsList, Manifest.permission.ACCESS_FINE_LOCATION))
            permissionsNeeded.add("location");

        if (!addPermission(permissionsList, Manifest.permission.CALL_PHONE))
            permissionsNeeded.add("call phone");
        if (permissionsList.size() > 0) {
            if (permissionsNeeded.size() > 0) {
                // Need Rationale
                String message = "You need to grant access to " + permissionsNeeded.get(0);
                for (int i = 1; i < permissionsNeeded.size(); i++)
                    message = message + ", " + permissionsNeeded.get(i);

                showMessageOKCancel(message,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                                        REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                            }
                        });
                return;
            }
            requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                    REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
            return;
        }
        // method called always()
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        try {
            new AlertDialog.Builder(CheckPermissionNetGps.this)
                    .setMessage(message)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            settingsPage();
                        }
                    })
                    .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setCancelable(false)
                    .create()
                    .show();
        } catch (Exception e) {
            Log.e(TAG, "showMessageOKCancel: error occured: " + e.toString());
        }
    }

    public void settingsPage() {
        final Intent i = new Intent();
        i.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        i.addCategory(Intent.CATEGORY_DEFAULT);
        i.setData(Uri.parse("package:" + getPackageName()));
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        i.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        i.addFlags(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS);
        startActivity(i);

    }

    private boolean addPermission(List<String> permissionsList, String permission) {
        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);
            // Check for Rationale Option
            if (!shouldShowRequestPermissionRationale(permission))
                return false;
        }
        return true;
    }
}
