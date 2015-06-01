package f2015.itsmap.ghostbar;

import android.graphics.drawable.AnimationDrawable;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;


public class SuccesActivity extends ActionBarActivity {

    AnimationDrawable glassAnimation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_succes);

        ImageView rocketImage = (ImageView) findViewById(R.id.imageView);
        rocketImage.setBackgroundResource(R.drawable.place_beer_animation);
        glassAnimation = (AnimationDrawable) rocketImage.getBackground();
        glassAnimation.start();
    }
}
