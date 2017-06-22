package technica.com.greencity.activities;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import technica.com.greencity.R;
import technica.com.greencity.Utils;
import technica.com.greencity.fragments.SubmittedRequests;
import technica.com.greencity.fragments.TreeRequestForm;
import technica.com.greencity.fragments.VolunteerFragment;

public class Navigation extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    FragmentManager fragmentManager = null;
    FragmentTransaction transaction = null;
    SharedPreferences sharedPreference;
    SharedPreferences.Editor editor;
    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        sharedPreference = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
        editor = sharedPreference.edit();
        intent = getIntent();
        if (intent != null) {
            openFragment(intent.getIntExtra(Utils.OPEN_FRAGMENT, 1));
        } else
            openFragment(0);
    }
    long back_pressed;
    @Override
    public void onBackPressed() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
           backPressedToast();
        }
    }

    private void backPressedToast() {
        try {

            if(back_pressed+2000>System.currentTimeMillis())
            {
                super.onBackPressed();
            }
            else
            {
                Toast.makeText(this, "Double Tap to Exit.", Toast.LENGTH_SHORT).show();

            }
            back_pressed=System.currentTimeMillis();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }

    /* @Override
     public boolean onCreateOptionsMenu(Menu menu) {
         // Inflate the menu; this adds items to the action bar if it is present.
         getMenuInflater().inflate(R.menu.navigation, menu);
         return true;
     }
 */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            openFragment(0);
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {
            openFragment(1);


        } else if (id == R.id.nav_slideshow) {
            openFragment(2);

        } else if (id == R.id.nav_send) {

            Intent emailIntent2 = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto", "amansachdeva93@gmail.com", null));
            emailIntent2.putExtra(Intent.EXTRA_SUBJECT, "Feedback for green_city");
            emailIntent2.putExtra(Intent.EXTRA_TEXT, "Mention here.");
            startActivity(Intent.createChooser(emailIntent2, "Send email..."));

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void openFragment(int i) {
        switch (i) {
            case 0:
                setTitle("Tree Request Form");
                Fragment fragment = new TreeRequestForm();
                fragmentManager = getFragmentManager();
                transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.content_frame, fragment);
                transaction.commit();
                break;

            case 1:
                setTitle("Submitted Requests");
                Fragment fragment2 = new SubmittedRequests();
                fragmentManager = getFragmentManager();
                transaction = fragmentManager.beginTransaction();
                transaction.replace(R.id.content_frame, fragment2);
                transaction.commit();
                break;
            case 2:
                setTitle("Volunteer");
                if (sharedPreference.getBoolean(Utils.VOLUNTEER_SIGNUP_DONE, false)) {
                    showAlertYouAreAlreadyVolunteer();
                } else {
                    Fragment fragment3 = new VolunteerFragment();
                    fragmentManager = getFragmentManager();
                    transaction = fragmentManager.beginTransaction();
                    transaction.replace(R.id.content_frame, fragment3);
                    transaction.commit();
                    break;
                }
        }

    }

    public void showAlertYouAreAlreadyVolunteer() {
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
        final AlertDialog.Builder builder = new AlertDialog.Builder(Navigation.this);
        builder.create();
        builder.setMessage("Welcome to being a volunteer.         \n\n" +
                Utils.thoughts[(int) (Math.random() * Utils.thoughts.length - 1)] + "")
                .setPositiveButton("Ok", dialogClickListener)
                .show();
    }

}
