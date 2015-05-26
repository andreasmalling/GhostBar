package f2015.itsmap.ghostbar;

import android.app.Application;
import android.content.Intent;
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

        // TODO: Big battery-drain
        beaconManager.setBackgroundBetweenScanPeriod(0l);
        beaconManager.setBackgroundScanPeriod(1100l);

        Region region = new Region("com.example.backgroundRegion",
                Identifier.parse(getResources().getString(R.string.beacon_id1)), null, null);
        regionBootstrap = new RegionBootstrap(this, region);

        Log.d(TAG, "bootstrap succes");
    }

    @Override
    public void didEnterRegion(Region arg0) {
        Log.d(TAG, "did enter region.");

        Intent intent = new Intent(this, PourActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        // Important:  make sure to add android:launchMode="singleInstance" in the manifest
        // to keep multiple copies of this activity from getting created if the user has
        // already manually launched the app.
        this.startActivity(intent);
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

}
