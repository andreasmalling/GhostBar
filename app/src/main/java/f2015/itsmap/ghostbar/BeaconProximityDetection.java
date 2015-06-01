package f2015.itsmap.ghostbar;

import android.app.Application;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;

import org.altbeacon.beacon.BeaconManager;
import org.altbeacon.beacon.Identifier;
import org.altbeacon.beacon.Region;
import org.altbeacon.beacon.startup.BootstrapNotifier;
import org.altbeacon.beacon.startup.RegionBootstrap;


public class BeaconProximityDetection extends Application implements BootstrapNotifier {
    private static final String TAG = "BPD";
    private RegionBootstrap regionBootstrap;
    private PourActivity pourActivity = null;

    public void onCreate() {
        super.onCreate();

        BeaconManager beaconManager = BeaconManager.getInstanceForApplication(this);

        Log.d(TAG, "bootstrap start");

        // FIXME: Big battery-drain
        beaconManager.setBackgroundBetweenScanPeriod(0);
        beaconManager.setBackgroundScanPeriod(1100);

        Region region = new Region("com.example.backgroundRegion",
                Identifier.parse(getResources().getString(R.string.beacon_id1)), null, null);
        regionBootstrap = new RegionBootstrap(this, region);

        Log.d(TAG, "bootstrap succes");
    }

    @Override
    public void didEnterRegion(Region arg0) {
        Log.d(TAG, "did enter region.");

        /*
        Intent intent = new Intent(this, PourActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Important:  make sure to add android:launchMode="singleInstance" in the manifest
        // to keep multiple copies of this activity from getting created if the user has
        // already manually launched the app.
        this.startActivity(intent);*/

        // If we have already seen beacons before, but the monitoring activity is not in
        // the foreground, we send a notification to the user on subsequent detections.

        sendNotification();
    }

    @Override
    public void didExitRegion(Region region) {

    }

    @Override
    public void didDetermineStateForRegion(int i, Region region) {

    }

    public void setPourActivity(PourActivity activity) {
        this.pourActivity = activity;
    }

    private void sendNotification() {
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setContentTitle("GhostBar")
                        .setContentText("A GhostBar is nearby.")
                        .setSmallIcon(R.mipmap.ic_launcher);

        TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
        stackBuilder.addNextIntent(new Intent(this, PourActivity.class));
        PendingIntent resultPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );
        builder.setContentIntent(resultPendingIntent);
        NotificationManager notificationManager =
                (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, builder.build());
    }
}
