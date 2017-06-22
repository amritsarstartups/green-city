package technica.com.greencity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import technica.com.greencity.activities.Navigation;

public class SignupForm extends AppCompatActivity {
    private static final int READ_PHONE_STATE_CODE = 101;
    Button btnSignUp;
    EditText name, city, phone, email;
    String strName, strCity, strEmail, strPhone;
    ProgressDialog pDialog1;
    ArrayList<NameValuePair> nameValuePairs;
    private String TAG = "SignUpForm";
    SharedPreferences sharedPreference;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup_form);
        name = (EditText) findViewById(R.id.name);
        city = (EditText) findViewById(R.id.edt_city);
        phone = (EditText) findViewById(R.id.edt_phone);
        email = (EditText) findViewById(R.id.email);
        sharedPreference = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = sharedPreference.edit();
        btnSignUp = (Button) findViewById(R.id.btnSignUp);
        email.setText(new Utils().getEmailAddress(SignupForm.this));

        signUpCode();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if (!new Utils().isPhonePermissionGranted(getApplicationContext())) {
                requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE},
                        READ_PHONE_STATE_CODE);
            } else {
                //  new FetchDetails().execute("https://wwwandroidcinemacom.000webhostapp.com/fetchDetails.php");

                new FetchDetails().execute(Utils.FETCH_DETAILS_URL);
            }
        }
    }


    private void signUpCode() {

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                strName = name.getText().toString();
                strCity = city.getText().toString();
                strPhone = phone.getText().toString();
                strEmail = email.getText().toString();

                if (strName.length() == 0 || strCity.length() == 0 ||
                        strPhone.length() == 0 || strEmail.length() == 0) {
                    Toast.makeText(SignupForm.this, "please fill all the fields.", Toast.LENGTH_SHORT).show();
                    if (strEmail.length() > 0) {
                        if (!new Utils().validate(strEmail))
                            Toast.makeText(getApplicationContext(), "Please check you email.", Toast.LENGTH_SHORT).show();
                    }
                } else if (new Utils().validate(strEmail) && strCity.length() > 0 && strPhone.length() > 0
                        && strName.length() > 0) {
                    new ProcessSign().execute("https://wwwandroidcinemacom.000webhostapp.com/signup.php");
                }
            }
        });
    }

    class ProcessSign extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            pDialog1 = new ProgressDialog(SignupForm.this);
            pDialog1.setMessage("Saving data...");
            pDialog1.setIndeterminate(false);
            pDialog1.setTitle("Please wait while we are creating your account.");
            pDialog1.getWindow().setGravity(Gravity.CENTER_VERTICAL);
            pDialog1.setCancelable(false);
            pDialog1.show();
            nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("name", strName));
            nameValuePairs.add(new BasicNameValuePair("email", strEmail));
            nameValuePairs.add(new BasicNameValuePair("city", strCity));
            nameValuePairs.add(new BasicNameValuePair("phone", strPhone));
            nameValuePairs.add(new BasicNameValuePair("imei", new Utils().getIMEI(SignupForm.this)));
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                Log.e(TAG, "doInBackground: yea status true");
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(params[0]);
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse httpResponse = httpClient.execute(httpPost);
                HttpEntity httpEntity = httpResponse.getEntity();
                String response = EntityUtils.toString(httpEntity);
                Log.d("msg", "POST Response >>>" + response);

                return response;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pDialog1.dismiss();
            // <br>Toast.makeText(getApplicationContext(),"result"+result,Toast.LENGTH_LONG).show();

            Log.e(TAG, result);

            editor.putString(Utils.NAME, strName);
            editor.putString(Utils.CITY, strCity);
            editor.putString(Utils.PHONE, strPhone);
            editor.putString(Utils.EMAIL, strEmail);
            editor.putString(Utils.IMEI, new Utils().getIMEI(SignupForm.this));
            editor.putBoolean(Utils.SIGNUP_DONE, true);
            editor.commit();
            new Utils().showToast(SignupForm.this, "Sign up successful.");
            startActivity(new Intent(getApplicationContext(), Navigation.class));
            finish();
        }
    }


    class FetchDetails extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            pDialog1 = new ProgressDialog(SignupForm.this);
            pDialog1.setMessage("Fetching data...");
            pDialog1.setIndeterminate(false);
            pDialog1.setTitle("Please wait while we are fetching account details.");
            pDialog1.getWindow().setGravity(Gravity.CENTER_VERTICAL);
            pDialog1.setCancelable(false);
            pDialog1.show();
            nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("imei", new Utils().getIMEI(SignupForm.this)));
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                Log.e(TAG, "doInBackground: yea status true");
                HttpClient httpClient = new DefaultHttpClient();
                HttpPost httpPost = new HttpPost(params[0]);
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse httpResponse = httpClient.execute(httpPost);
                HttpEntity httpEntity = httpResponse.getEntity();
                String response = EntityUtils.toString(httpEntity);
                Log.d("msg", "POST Response >>>" + response);

                return response;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            return null;


        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            pDialog1.dismiss();

            Log.e(TAG, "onPostExecute: fetched : " + result);
            // <br>Toast.makeText(getApplicationContext(),"result"+result,Toast.LENGTH_LONG).show();

            try {
                JSONObject jsonObject = new JSONObject(result);
                JSONArray jsonArray = jsonObject.getJSONArray("data");
                for (int i = 0; i < jsonArray.length(); i++) {
                    JSONObject tempJsonO = jsonArray.getJSONObject(i);


                    name.setText(tempJsonO.getString("name"));
                    phone.setText(tempJsonO.getString("phone"));
                    city.setText(tempJsonO.getString("city"));
                    email.setText(tempJsonO.getString("email"));

                    strName = name.getText().toString();
                    strCity = city.getText().toString();
                    strPhone = phone.getText().toString();
                    strEmail = email.getText().toString();
                    editor.putString(Utils.NAME, strName);
                    editor.putString(Utils.CITY, strCity);
                    editor.putString(Utils.PHONE, strPhone);
                    editor.putString(Utils.EMAIL, strEmail);
                    editor.putString(Utils.IMEI, new Utils().getIMEI(SignupForm.this));
                    editor.putBoolean(Utils.SIGNUP_DONE, true);
                    editor.commit();
                    new Utils().showToast(SignupForm.this, "Sign up successful.");
                    startActivity(new Intent(getApplicationContext(), Navigation.class));
                    finish();
                }


            } catch (JSONException e) {
                e.printStackTrace();
                Log.e(TAG, "onPostExecute: exception : " + e.toString());


            }
        }
    }
}

