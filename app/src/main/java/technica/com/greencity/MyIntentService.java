package technica.com.greencity;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class MyIntentService extends IntentService {
    // TODO: Rename actions, choose action names that describe tasks that this
    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_FOO = "technica.com.greencity.action.FOO";
    private static final String ACTION_BAZ = "technica.com.greencity.action.BAZ";
    LatLng myLocation;
    // TODO: Rename parameters
    private static final String EXTRA_PARAM1 = "technica.com.greencity.extra.PARAM1";
    private static final String EXTRA_PARAM2 = "technica.com.greencity.extra.PARAM2";
    String lattitudeString, longitudeString;
    SharedPreferences sharedPreference;
    SharedPreferences.Editor editor;

    public MyIntentService() {
        super("MyIntentService");
    }

    String TAG = "MyIntentService";

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionFoo(Context context, String param1, String param2) {
        Intent intent = new Intent(context, MyIntentService.class);
        intent.setAction(ACTION_FOO);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    /**
     * Starts this service to perform action Baz with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    // TODO: Customize helper method
    public static void startActionBaz(Context context, String param1, String param2) {
        Intent intent = new Intent(context, MyIntentService.class);
        intent.setAction(ACTION_BAZ);
        intent.putExtra(EXTRA_PARAM1, param1);
        intent.putExtra(EXTRA_PARAM2, param2);
        context.startService(intent);
    }

    public technica.com.greencity.FusedLocation fusedLocation;

    @Override
    public int onStartCommand(@Nullable Intent intent, int flags, int startId) {
        Log.e(TAG, "onStartCommand: ");
        sharedPreference = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = sharedPreference.edit();
        locatePoints();

        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(TAG, "onHandleIntent: working");
        locatePoints();
    }

    private void locatePoints() {
        fusedLocation = new technica.com.greencity.FusedLocation(getApplicationContext(), new technica.com.greencity.FusedLocation.Callback() {
            @Override
            public void onLocationResult(Location location) {
                //Do as you wish with location here
                if (myLocation == null) {
                    myLocation = new LatLng(location.getLatitude(), location.getLongitude());

                    String myLocParams = location.getLatitude() + "," + location.getLongitude();
                    longitudeString = location.getLongitude() + "";

                    lattitudeString = location.getLatitude() + "";
                    Log.e(TAG, "onLocationResult: your location : " + myLocParams +
                            "latts longs:" + lattitudeString + " " + longitudeString);

                    editor.putString(Utils.YOUR_CURRENT_LOCATION, returnAddress(location));
                    editor.commit();
                    //   Toast.makeText(GridHOME.this, "you are at ; "+myLocParams, Toast.LENGTH_SHORT).show();
                    try {
                      /*  String timeIs=new DurationAsync().execute(
                                new String[]{myLocParams,teriLoc}).get();*/
//                        Log.e(TAG, "onCreate: timeIs : "+timeIs );
                    } catch (Exception e) {
                        Log.e(TAG, "onCreate: lao ji exception ");
                    }
                    //  mMap.addMarker(yourLocationMarker);
                }
            }
        });
        if (!fusedLocation.isGPSEnabled()) {
            fusedLocation.showSettingsAlert();
        } else {
            fusedLocation.getLastKnownLocation(10, 50);
            Log.e(TAG, "locatePoints: getting last known location");
            fusedLocation.getCurrentLocation(3);
        }
    }

    String address = "";

    public String returnAddress(Location location) {
        Geocoder geocoder = new Geocoder(getApplicationContext(), Locale.getDefault());

        try {

            List<Address> listAddresses = geocoder.getFromLocation(location.getLatitude(), location.getLongitude(), 1);

            if (listAddresses != null && listAddresses.size() > 0) {

                Log.i("PlaceInfo", listAddresses.get(0).toString());

                if (listAddresses.get(0).getSubThoroughfare() != null) {
                    address += listAddresses.get(0).getSubThoroughfare() + "&&&";
                }

                if (listAddresses.get(0).getThoroughfare() != null) {
                    address += listAddresses.get(0).getThoroughfare() + ",";
                }

                if (listAddresses.get(0).getLocality() != null) {
                    address += listAddresses.get(0).getLocality() + ",";
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


    /**
     * Handle action Foo in the provided background thread with the provided
     * parameters.
     */
    private void handleActionFoo(String param1, String param2) {
        // TODO: Handle action Foo
        throw new UnsupportedOperationException("Not yet implemented");
    }

    /**
     * Handle action Baz in the provided background thread with the provided
     * parameters.
     */
    private void handleActionBaz(String param1, String param2) {
        // TODO: Handle action Baz
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
