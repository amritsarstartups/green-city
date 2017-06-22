package technica.com.greencity.fragments;

import android.app.Fragment;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.text.Html;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map;

import technica.com.greencity.DatabaseApp;
import technica.com.greencity.activities.Navigation;
import technica.com.greencity.R;
import technica.com.greencity.Utils;

import static android.app.Activity.RESULT_OK;


/**
 * Created by Amanpreet Singh on 4/23/2017.
 */
public class TreeRequestForm extends Fragment implements CompoundButton.OnCheckedChangeListener,
        AdapterView.OnItemSelectedListener {
    CheckBox sameAsAbove;
    TextView youAreAt;
    Spinner spinner;
    String plantType = "Indoor";
    RadioGroup radioGroup;
    RadioButton radioOutdoor, radioIndoor;
    Button uploadPic, btnSignUp;
    EditText edt_city, streetName/* address_line*/;
    ProgressDialog dialog = null;
    SharedPreferences sharedPreference;
    SharedPreferences.Editor editor;
    ImageView profilePic;
    private String TAG = "TreeRequestForm", pathOfImageUrl, plantName;
    private Bitmap bitmap;
    String status = "pending";
    private int PICK_IMAGE_REQUEST = 1;
    private String KEY_IMAGE = "image", fullFileName;
    private String KEY_NAME = "name", splittedAmpersand;
    DatabaseApp mainDb;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =
                inflater.inflate(R.layout.tree_request, container, false);
        mainDb = new DatabaseApp(getActivity());
        sameAsAbove = (CheckBox) rootView.findViewById(R.id.sameAsAbove);
        youAreAt = (TextView) rootView.findViewById(R.id.youAreAt);
        edt_city = (EditText) rootView.findViewById(R.id.edt_city);
        //  address_line = (EditText) rootView.findViewById(R.id.address_line);
        streetName = (EditText) rootView.findViewById(R.id.streetName);
        sameAsAbove.setOnCheckedChangeListener(this);
        profilePic = (ImageView) rootView.findViewById(R.id.profilePic);
        sharedPreference = PreferenceManager.getDefaultSharedPreferences(getActivity());
        editor = sharedPreference.edit();
        youAreAt.setText(Html.fromHtml("<i><b>You are at: </b></i>") + sharedPreference.getString(Utils.YOUR_CURRENT_LOCATION, "").replace("&&&", " "));
        uploadPic = (Button) rootView.findViewById(R.id.uploadPic);
        btnSignUp = (Button) rootView.findViewById(R.id.btnSignUp);
        uploadPic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showFileChooser();
            }
        });
        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (new Utils().isNetworkAvailable(getActivity()))
                    submission();
                else
                    new Utils().showToast(getActivity(), "Please connect to the internet.");
            }
        });


        spinner = (Spinner) rootView.findViewById(R.id.spinner);
        radioGroup = (RadioGroup) rootView.findViewById(R.id.myRadioGroup);
        radioIndoor = (RadioButton) rootView.findViewById(R.id.radioIndoor);
        radioOutdoor = (RadioButton) rootView.findViewById(R.id.radioOutdoor);
        // Spinner click listener
        spinner.setOnItemSelectedListener(this);
        spinnerCode();
        return rootView;
    }

    //    http://www.androidcinema.com/sb/saveTreeRequest.php
    private void submission() {
        splittedAmpersand = youAreAt.getText().toString().replace("&&&", " ");


        nameValuePairs = new ArrayList<NameValuePair>();
        nameValuePairs.add(new BasicNameValuePair("gmail", sharedPreference.getString(Utils.EMAIL, "")));
        nameValuePairs.add(new BasicNameValuePair("plant_type", plantType));
        nameValuePairs.add(new BasicNameValuePair("plant_name", plantName));
        nameValuePairs.add(new BasicNameValuePair("url_image", fullFileName));
        nameValuePairs.add(new BasicNameValuePair("complete_address", splittedAmpersand));
        nameValuePairs.add(new BasicNameValuePair("status", status));

        new ProcessSign().execute("http://www.androidcinema.com/sb/saveTreeRequest.php");

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        String completeAddress = sharedPreference.getString(Utils.YOUR_CURRENT_LOCATION, null);
        Log.e(TAG, "onCheckedChanged: you are getting complete address : " + completeAddress);
        String addressLines[] = completeAddress.split("&&&");

        if (buttonView.getId() == R.id.sameAsAbove) {
            edt_city.setText(addressLines[0].replace("&&&", ""));
            try {
                streetName.setText(addressLines[1].replace("&&&", ""));
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
    }

    private void showFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri filePath = data.getData();
            try {
                //Getting the Bitmap from Gallery
                bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), filePath);
                //Setting the Bitmap to ImageView
                profilePic.setImageBitmap(bitmap);
                profilePic.setVisibility(View.VISIBLE);
                uploadImage();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public String getStringImage(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] imageBytes = baos.toByteArray();
        String encodedImage = Base64.encodeToString(imageBytes, Base64.DEFAULT);
        return encodedImage;
    }


    private void uploadImage() {
        //Showing the progress dialog
        final ProgressDialog loading = ProgressDialog.show(getActivity(), "Uploading...", "Please wait...", false, false);
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Utils.IMAGE_UPLOAD_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String s) {
                        //Disimissing the progress dialog
                        loading.dismiss();
                        //Showing toast message of the response
                        Toast.makeText(getActivity(), s, Toast.LENGTH_LONG).show();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError volleyError) {
                        //Dismissing the progress dialog
                        loading.dismiss();

                        //Showing toast
                        try {
                            Toast.makeText(getActivity(), volleyError.getMessage().toString(), Toast.LENGTH_LONG).show();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                //Converting Bitmap to String
                String image = getStringImage(bitmap);

                //Getting Image Name
                fullFileName = sharedPreference.getString(Utils.EMAIL, "") + "__" + new Utils().uniqueFileName() + ".png";
                fullFileName = fullFileName.replace("@", "_");

                pathOfImageUrl = fullFileName;
                //Creating parameters
                Map<String, String> params = new Hashtable<String, String>();
                Log.e(TAG, "getParams: path of image url : " + pathOfImageUrl);
                //Adding parameters
                params.put(KEY_IMAGE, image);
                params.put(KEY_NAME, pathOfImageUrl);

                //returning parameters
                return params;
            }
        };

        //Creating a Request Queue
        RequestQueue requestQueue = Volley.newRequestQueue(getActivity());

        //Adding request to the queue
        requestQueue.add(stringRequest);
    }

    public void spinnerCode() {

        // Spinner Drop down elements
        List<String> indoorData = Utils.fetchIndoorData();
        List<String> outdootData = Utils.fetchOutdoorData();
        // Creating adapter for spinner
        final ArrayAdapter<String> indoorAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, indoorData);
        final ArrayAdapter<String> outdoorAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_item, outdootData);

        // Drop down layout style - list view with radio button
        indoorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        outdoorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(indoorAdapter);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                // find which radio button is selected
                if (checkedId == R.id.radioIndoor) {
                    spinner.setAdapter(indoorAdapter);
                    plantType = "indoor";

                } else if (checkedId == R.id.radioOutdoor) {
                    spinner.setAdapter(outdoorAdapter);
                    plantType = "outdoor";


                }
            }

        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        // On selecting a spinner item
        String item = parent.getItemAtPosition(position).toString();

        // Showing selected spinner item
        plantName = item;
    }

    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub

    }

    ProgressDialog pDialog1;
    ArrayList<NameValuePair> nameValuePairs;

    class ProcessSign extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            pDialog1 = new ProgressDialog(getActivity());
            pDialog1.setMessage("Saving data...");
            pDialog1.setIndeterminate(false);
            pDialog1.setTitle("Please wait while we submitting your request.");
            pDialog1.getWindow().setGravity(Gravity.CENTER_VERTICAL);
            pDialog1.setCancelable(false);
            pDialog1.show();
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

            mainDb.insert_inLocal(plantType, plantName, fullFileName, splittedAmpersand, status);
            showDialog();
            Intent intent = new Intent(getActivity(), Navigation.class);
            intent.putExtra(Utils.OPEN_FRAGMENT, 1);
            startActivity(intent);

            Log.e(TAG, result);


            //Toast.makeText(getActivity(), "data saved good", Toast.LENGTH_SHORT).show();
        }
    }

    public void showDialog() {
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case DialogInterface.BUTTON_POSITIVE:
                        dialog.cancel();

                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                        dialog.cancel();

                        break;
                }
            }
        };
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.create();
        builder.setMessage(Html.fromHtml("<i>Your Request has been successfully submitted.</i>"))
                .setPositiveButton("Ok", dialogClickListener)
                .show();
    }
}


