package petshops.developerpet.com.petshops;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import petshops.developerpet.com.petshops.service.SensorService;

public class SensorBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        context.startService(new Intent(context, SensorService.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
    }

}
