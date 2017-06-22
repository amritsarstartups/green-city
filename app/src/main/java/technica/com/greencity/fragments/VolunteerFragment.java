package technica.com.greencity.fragments;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Fragment;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
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

import java.util.ArrayList;
import java.util.Calendar;

import technica.com.greencity.activities.Navigation;
import technica.com.greencity.R;
import technica.com.greencity.Utils;
import technica.com.greencity.receivers.VolunteerAlarmReceiver;

import static com.facebook.FacebookSdk.getApplicationContext;
import static technica.com.greencity.Utils.VOLUNTEER_SIGNUP_DONE;


/**
 * Created by Amanpreet Singh on 4/23/2017.
 */
public class VolunteerFragment extends Fragment implements View.OnClickListener {
    Button setDate, setTime, btnvolunteer;
    private int mYear, mMonth, mDay, mHour, mMinute ,mAMPM;
    TextView date_status, time_status;
    final static int RQS_1 = 1;
    boolean dateset , timeset;
    String TAG="Volunteer";
    TimePickerDialog timePickerDialog;
    DatePickerDialog datePickerDialog;
    Calendar cal = Calendar.getInstance();
    public EditText name, number;
    SharedPreferences sharedPreference;
    SharedPreferences.Editor editor;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =
                inflater.inflate(R.layout.volunteer_fragment, container, false);
        sharedPreference = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = sharedPreference.edit();
        initViews(rootView);
        setListeners();

        return rootView;
    }



    private void setListeners() {
        setDate.setOnClickListener(this);
        setTime.setOnClickListener(this);
        btnvolunteer.setOnClickListener(this);
    }

    private void initViews(View rootView) {
        name = (EditText)rootView. findViewById(R.id.edit_name);
        number = (EditText)rootView. findViewById(R.id.edit_number);
        setDate = (Button)rootView. findViewById(R.id.btnSetDate);
        setTime = (Button)rootView. findViewById(R.id.btnSetTime);
        btnvolunteer = (Button)rootView. findViewById(R.id.sumbit_volunteer);
        date_status = (TextView) rootView.findViewById(R.id.txtDateStatus);
        time_status = (TextView) rootView.findViewById(R.id.txtTimeStatus);

    }
    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id){
            case R.id.btnSetDate:
                final Calendar c = Calendar.getInstance();
                mYear = c.get(Calendar.YEAR);
                mMonth = c.get(Calendar.MONTH);
                mDay = c.get(Calendar.DAY_OF_MONTH);
                datePickerDialog = new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener() {

                            @Override
                            public void onDateSet(DatePicker view, int year,
                                                  int monthOfYear, int dayOfMonth) {

                                date_status.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                                cal.set(Calendar.YEAR,year);
                                cal.set(Calendar.MONTH,monthOfYear);
                                cal.set(Calendar.DAY_OF_MONTH,dayOfMonth);

                                dateset=true;

                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
                break;
            case R.id.btnSetTime:
                final Calendar c1 = Calendar.getInstance();
                mHour = c1.get(Calendar.HOUR_OF_DAY);
                mHour = c1.get(Calendar.HOUR_OF_DAY);
                mMinute = c1.get(Calendar.MINUTE);
                mAMPM = c1.get(Calendar.AM_PM);

                // Launch Time Picker Dialog
                timePickerDialog = new TimePickerDialog(getActivity(),
                        new TimePickerDialog.OnTimeSetListener() {

                            @Override
                            public void onTimeSet(TimePicker view, int hourOfDay,
                                                  int minute) {

                                time_status.setText(hourOfDay + ":" + minute);
                                cal.set(Calendar.HOUR_OF_DAY,hourOfDay);
                                cal.set(Calendar.MINUTE,minute);
                                cal.set(Calendar.SECOND,00);
                                timeset = true;
                            }
                        }, mHour, mMinute, false);
                timePickerDialog.show();
                break;
            case R.id.sumbit_volunteer:
                if(dateset&&timeset){ Calendar current = Calendar.getInstance();


                    if(cal.compareTo(current) <= 0){
                        //The set Date/Time already passed
                        Toast.makeText(getApplicationContext(),
                                "Invalid Date/Time",
                                Toast.LENGTH_LONG).show();
                    }else{
                        //process service
                        setAlarm(cal);
                    }
                }
                else{
                    Toast.makeText(getActivity(), "Please set date/time !", Toast.LENGTH_SHORT).show();
                }
                break;
            default://nothing

        }
    }
    private void setAlarm(Calendar targetCal){

        Toast.makeText(getActivity(), "Alarm is set" + targetCal.getTime() , Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(getActivity(), VolunteerAlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getActivity(), RQS_1, intent, 0);
        AlarmManager alarmManager = (AlarmManager)getActivity().getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, targetCal.getTimeInMillis(), pendingIntent);
        new ProcessSign().execute("https://wwwandroidcinemacom.000webhostapp.com/volunteer_signup.php");

    }
    ProgressDialog pDialog1;
    ArrayList<NameValuePair> nameValuePairs;
    class ProcessSign extends AsyncTask<String, String, String>
    {
        @Override
        protected void onPreExecute() {
            pDialog1=new ProgressDialog(getActivity());
            pDialog1.setMessage("Saving Volunteer credentials...");
            pDialog1.setIndeterminate(false);
            pDialog1.setTitle("Please wait...");
            pDialog1.getWindow().setGravity(Gravity.CENTER_VERTICAL);
            pDialog1.setCancelable(false);
            pDialog1.show();
            nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("name", name.getText().toString()));
            nameValuePairs.add(new BasicNameValuePair("email", number.getText().toString()));
             }

        @Override
        protected String doInBackground(String... params) {
            try {
                Log.e(TAG, "doInBackground: yea status true" );
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

            editor.putString(Utils.VOUNTEER_NAME,name.getText().toString());
            editor.putString(Utils.VOLUNTEER_MOBILE,number.getText().toString());
            editor.putBoolean(VOLUNTEER_SIGNUP_DONE,true);
            editor.commit();
            new Utils().showToast(getActivity(),"Your request to become volunteer has been successful.");
            startActivity(new Intent(getApplicationContext(),Navigation.class));
            getActivity().finish();
        }
    }
}

