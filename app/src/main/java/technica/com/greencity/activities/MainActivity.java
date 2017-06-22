package technica.com.greencity.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.facebook.CallbackManager;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;
import org.json.JSONObject;

import technica.com.greencity.FusedLocation;
import technica.com.greencity.MyIntentService;
import technica.com.greencity.R;
import technica.com.greencity.SignupForm;
import technica.com.greencity.Utils;

public class MainActivity extends AppCompatActivity {
    LatLng myLocation;
    String lattitudeString, longitudeString;
    Button fbBtn;
    CallbackManager callbackManager;
    private LoginButton loginButton;
    String imei, name, email, firstName, gender;
    public FusedLocation fusedLocation;
    private String TAG = "MainActivity";
    SharedPreferences sharedPreference;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sharedPreference = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = sharedPreference.edit();
        // locatePoints();
        Log.e(TAG, "onCreate: status : " + sharedPreference.getBoolean(Utils.SIGNUP_DONE, false));
        if (sharedPreference.getBoolean(Utils.SIGNUP_DONE, false)) {
            startActivity(new Intent(MainActivity.this, Navigation.class));
            finish();
        }
        try {
            startService(new Intent(MainActivity.this, MyIntentService.class));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void signup(View view) {
        startActivity(new Intent(MainActivity.this, SignupForm.class));
        finish();

    }

    public void facebook(View view) {


    }

    public void twitter(View view) {

    }

    private void setFacebookData(final LoginResult loginResult) {
        GraphRequest request = GraphRequest.newMeRequest(
                loginResult.getAccessToken(),
                new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(JSONObject object, GraphResponse response) {
                        // Application code
                        try {
                            Log.e("Response", response.toString());

                            String email = response.getJSONObject().getString("email");
                            firstName = response.getJSONObject().getString("first_name");
                            String lastName = response.getJSONObject().getString("last_name");
                            gender = response.getJSONObject().getString("gender");
                            Log.e(TAG, "onCompleted: " + email + firstName + lastName + gender);
                            Profile profile = Profile.getCurrentProfile();
                            String id = profile.getId();
                            String link = profile.getLinkUri().toString();
                            Log.d("Link", link);
                            if (Profile.getCurrentProfile() != null) {
                                Log.e("Login", "ProfilePic" + Profile.getCurrentProfile().getProfilePictureUri(200, 200));
                              /*  Intent main = new Intent(MainActivity.this, AfterLogin.class);
                                main.putExtra("name", profile.getFirstName());
                                main.putExtra("surname", profile.getLastName());
                                String parameters = "imei=" + imei + "&&name=" + firstName + "&&email=" + email;

                                Log.e(TAG, "onClick: PARAMETERS: " + parameters);

                                startActivity(main);
*/
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,email,first_name,last_name,gender");
        request.setParameters(parameters);
        request.executeAsync();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
    }
}
