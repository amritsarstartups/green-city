package technica.com.greencity.receivers;

/**
 * Created by Aman on 6/16/2017.
 */

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import technica.com.greencity.R;
import technica.com.greencity.Utils;


public class VolunteerAlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context arg0, Intent arg1) {

        Utils.create(arg0, R.drawable.icon_green, new String(arg0.getResources().getString(R.string.app_name)), "Lets contribute in plantation:)");
    }

}
