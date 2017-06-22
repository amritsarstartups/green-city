package technica.com.greencity;

import android.Manifest;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.ContextCompat;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.util.Patterns;
import android.view.Gravity;
import android.widget.Toast;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

/**
 * Created by Amanpreet Singh on 2/10/2017.
 */
public class Utils {
    public static final String YOUR_CURRENT_LOCATION = "your_current_location";
    public static final String SIGNUP_DONE = "signUpDone";
    public static final String NAME = "NAME";
    public static final String CITY = "CITY";
    public static final String PHONE = "PHONE";
    public static final String EMAIL = "EMAIL";
    public static final String IMEI = "IMEI";
//    public static final String IMAGE_UPLOAD_URL = "http://www.androidcinema.com/sb/uploadImage.php";
    public static final String IMAGE_UPLOAD_URL = "http://www.gurbaniworld.org/android/sb/uploadImage.php";
    public static final String DOMAIN = "http://www.gurbaniworld.org/android/sb/uploads/";
    public static final String VOUNTEER_NAME = "volunteer_name";
    public static final String VOLUNTEER_MOBILE = "volunteer_mobile";
    public static final String VOLUNTEER_SIGNUP_DONE = "volunteer_sign_up_done";
    public static final String OPEN_FRAGMENT = "open_fragment";
    public static final String FETCH_DETAILS_URL = "https://wwwandroidcinemacom.000webhostapp.com/fetchDetails.php";

    public static List<String> fetchIndoorData() {
        List<String> categories = new ArrayList<String>();
        categories.add("Asparagus Fern");
        categories.add("Areca Palm");
        categories.add("Snake Plant");
        categories.add("Staghorn Fern");
        categories.add("Golden Pothos");
        return categories;
    }

    public static List<String> fetchOutdoorData() {
        List<String> categories = new ArrayList<String>();

        categories.add("Banyan");
        categories.add("Neem");
        categories.add("Sacred Fig");
        categories.add("Orchid");
        categories.add("Holy Brasil");
        return categories;
    }

    public void showToast(Context context, String message) {
        Toast toast = Toast.makeText(context, message, Toast.LENGTH_LONG);
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.show();
    }

    public boolean isLocationPermission_Granted(Context context) {
        int hasWriteContactsPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
        return hasWriteContactsPermission == PackageManager.PERMISSION_GRANTED;
    }

    public boolean isPhonePermissionGranted(Context context) {
        int hasReadContactsPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_PHONE_STATE);
        return hasReadContactsPermission == PackageManager.PERMISSION_GRANTED;
    }

    public boolean isGPSoN(Context context) {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
    }

    public boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getActiveNetworkInfo();
        return info != null && info.isConnectedOrConnecting();
    }

    public boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    String address = "";

    public String returnAddress(Context context, Location location) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());

        try {

            List<Address> listAddresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

            if (listAddresses != null && listAddresses.size() > 0) {

                Log.i("PlaceInfo", listAddresses.get(0).toString());

                if (listAddresses.get(0).getSubThoroughfare() != null) {
                    address += listAddresses.get(0).getSubThoroughfare() + " ";
                }

                if (listAddresses.get(0).getThoroughfare() != null) {
                    address += listAddresses.get(0).getThoroughfare() + ", ";
                }

                if (listAddresses.get(0).getLocality() != null) {
                    address += listAddresses.get(0).getLocality() + ", ";
                }

                if (listAddresses.get(0).getPostalCode() != null) {
                    address += listAddresses.get(0).getPostalCode() + ", ";
                }

                if (listAddresses.get(0).getCountryName() != null) {
                    address += listAddresses.get(0).getCountryName();
                }


            }

        } catch (IOException e) {
            e.printStackTrace();
        }
        return address;
    }

    String possibleEmail;

    public String getEmailAddress(Context context) {
        Pattern emailPattern = Patterns.EMAIL_ADDRESS; // API level 8+
        Account[] accounts = AccountManager.get(context).getAccounts();
        for (Account account : accounts) {
            if (emailPattern.matcher(account.name).matches()) {
                possibleEmail = account.name;
            }
        }
        return possibleEmail;
    }

    public String getIMEI(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return telephonyManager.getDeviceId();
//        return Build.SERIAL.toString();
    }

    public void shareWithWhatsApp(Context context, String message) {

        PackageManager pm = context.getPackageManager();
        try {

            Intent waIntent = new Intent(Intent.ACTION_SEND);
            waIntent.setType("text/plain");
            String text = message;

            PackageInfo info = pm.getPackageInfo("com.whatsapp", PackageManager.GET_META_DATA);
            //Check if package exists or not. If not then code
            //in catch block will be called
            waIntent.setPackage("com.whatsapp");

            waIntent.putExtra(Intent.EXTRA_TEXT, text);
            context.startActivity(Intent.createChooser(waIntent, "Share with"));

        } catch (PackageManager.NameNotFoundException e) {
            Toast.makeText(context, "WhatsApp not Installed", Toast.LENGTH_SHORT)
                    .show();
        }

    }

    public boolean validate(String input) {
        return Patterns.EMAIL_ADDRESS.matcher(input).matches();
    }

    public String uniqueFileName() {
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
        Date now = new Date();
        return formatter.format(now) + "";
    }

    public static void create(Context context, int smallIcon, String contentTitle, String contentText) {
        NotificationManager manager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Uri alarmSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                .setContentTitle(contentTitle)
                .setContentText(contentText)
                .setSmallIcon(R.drawable.icon_green)
                .setSound(alarmSound)
                .setSmallIcon(smallIcon)
                .setAutoCancel(true);

        Notification n = builder.build();
        manager.notify(0, n);

    }

    public static void cancellNotification(Context context, int id) {
        NotificationManagerCompat nm = NotificationManagerCompat.from(context);
        nm.cancel(id);
    }

    public static void cancellAllNotification(Context context) {
        NotificationManagerCompat nm = NotificationManagerCompat.from(context);
        nm.cancelAll();
    }

    public boolean isReadExtrenalStorageGranted(Context context) {
        int hasReadContactsPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE);
        return hasReadContactsPermission == PackageManager.PERMISSION_GRANTED;
    }


    public boolean isSendSMSPermissionGranted(Context context) {
        int hasReadContactsPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.SEND_SMS);
        return hasReadContactsPermission == PackageManager.PERMISSION_GRANTED;
    }

    public boolean isCallPhonePermissionGranted(Context context) {
        int hasReadContactsPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE);
        return hasReadContactsPermission == PackageManager.PERMISSION_GRANTED;
    }

    public boolean isLocationPermissionGranted(Context context) {
        int hasReadContactsPermission = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION);
        return hasReadContactsPermission == PackageManager.PERMISSION_GRANTED;
    }

    public static String[] thoughts = {"Look deep into nature, and then you will understand everything better.",
            "The best time to plant a tree is twenty years ago. The second best time is now",
            "A seed hidden in the heart of an apple is an orchard invisible.",
            "Knowing trees, I understand the meaning of patience. Knowing grass, I can appreciate persistence."};
}

