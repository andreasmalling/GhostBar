package f2015.itsmap.ghostbar;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import dk.danskebank.mobilepay.sdk.MobilePay;
import dk.danskebank.mobilepay.sdk.ResultCallback;
import dk.danskebank.mobilepay.sdk.model.FailureResult;
import dk.danskebank.mobilepay.sdk.model.Payment;
import dk.danskebank.mobilepay.sdk.model.SuccessResult;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpConnectionParams;
import org.json.*;


public class PourActivity extends AppCompatActivity {

    private static final String TAG = "PourActivity";
    private int MOBILEPAY_PAYMENT_REQUEST_CODE = 1337;

    private SeekBar pourMeter;
    private TextView priceAmount;
    private TextView pourAmount;
    private Button buyButton;
    private int amount, min = 0;
    private float price, cost = 0;


    private DecimalFormat df;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pour);

        MobilePay.getInstance().init(getResources().getString(R.string.mobile_pay_merchant_id));

        min = getResources().getInteger(R.integer.min_size_cl);
        amount = min;
        cost = getResources().getFraction(R.fraction.kr_per_cl,1,1);
        price = cost*amount;

        ((BeaconProximityDetection) this.getApplicationContext()).setPourActivity(this);

        // Locate gui
        pourMeter = (SeekBar) findViewById(R.id.pourMeter);
        priceAmount = (TextView) findViewById(R.id.priceAmount);
        pourAmount = (TextView) findViewById(R.id.pourAmount);
        buyButton = (Button) findViewById(R.id.buyButton);

        // Format float to #.##
        df = new DecimalFormat();
        df.setMaximumFractionDigits(2);
        df.setMinimumFractionDigits(2);

        pourMeter.setProgress(10);
        priceAmount.setText(df.format(price) + " " + getResources().getString(R.string.currency));
        pourAmount.setText(amount + " " + getResources().getString(R.string.measure));
        //onStopTrackingTouch(pourMeter);
        pourMeter.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                amount = progress;
                price = amount * cost;
                priceAmount.setText(df.format(price) + " " + getResources().getString(R.string.currency));
                pourAmount.setText(amount + " " + getResources().getString(R.string.measure));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if (amount < min) {
                    pourMeter.setProgress(min);
                    Toast.makeText(getApplicationContext(), "Minimum amount is " + min + " " + getResources().getString(R.string.measure), Toast.LENGTH_SHORT).show();
                }
                amount = pourMeter.getProgress();
                price = amount * cost;
                priceAmount.setText(df.format(price) + " " + getResources().getString(R.string.currency));
                pourAmount.setText(amount + " " + getResources().getString(R.string.measure));
            }

        });

        buyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean isMobilePayInstalled = MobilePay.getInstance().isMobilePayInstalled(getApplicationContext());
                if (isMobilePayInstalled) {
                    Payment payment = new Payment();
                    payment.setProductPrice(new BigDecimal(price));
                    payment.setProductName("Beer");
                    payment.setReceiptMessage("Enjoy your beer!");
                    payment.setOrderId("1");

                    Intent paymentIntent = MobilePay.getInstance().createPaymentIntent(payment);
                    startActivityForResult(paymentIntent, MOBILEPAY_PAYMENT_REQUEST_CODE);
                } else {
                    // MobilePay is not installed. Go to Google Play.
                    Intent intent = MobilePay.getInstance().createDownloadMobilePayIntent(getApplicationContext());
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MOBILEPAY_PAYMENT_REQUEST_CODE) {
            // The request code matches our MobilePay Intent
            MobilePay.getInstance().handleResult(resultCode, data, new ResultCallback() {
                @Override
                public void onSuccess(SuccessResult result) {
                    String[] params = new String[4];
                    params[0] = getResources().getString(R.string.url);
                    params[1] = result.getTransactionId();
                    params[2] = String.valueOf(price);
                    params[3] = String.valueOf(amount);
                    new HTTPHandler(getApplicationContext()).execute(params);
                    Toast.makeText(getApplicationContext(), "Payment succes", Toast.LENGTH_SHORT).show();

                    Log.d(TAG, "Signature: " + result.getSignature());
                    Log.d(TAG, "Order ID: " + result.getOrderId());
                    Log.d(TAG, "Trans. ID: " + result.getTransactionId());                    

                    //Intent intent = new Intent(getApplicationContext(), SuccesActivity.class);
                    //startActivity(intent);
                }
                @Override
                public void onFailure(FailureResult result) {
                    Toast.makeText(getApplicationContext(), "Payment fail", Toast.LENGTH_SHORT).show();
                }
                @Override
                public void onCancel() {
                    Toast.makeText(getApplicationContext(), "Payment cancel", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

}
