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

import java.io.InputStream;
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
    private int amount = 0;
    private float price, cost = 0;

    private DecimalFormat df;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pour);

        MobilePay.getInstance().init(getResources().getString(R.string.mobile_pay_merchant_id));

        cost = getResources().getFraction(R.fraction.kr_per_cl,1,1);

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

        priceAmount.setText(df.format(price) + " " + getResources().getString(R.string.currency));
        pourAmount.setText(amount+ " " + getResources().getString(R.string.measure));

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
                    Toast.makeText(getApplicationContext(), "Payment succes", Toast.LENGTH_SHORT).show();
<<<<<<< HEAD

                    /*JSONObject jsonObj;

                    try {
                        jsonObj = new JSONObject("{\"transactionId\":\"1\",\"price\":\"2.5\",\"amount\":\"150\"}");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }*/

                    String jsonString = "{\"transactionId\":\"1\",\"price\":\"2.5\",\"amount\":\"150\"}";
                    HttpClient client = new DefaultHttpClient();
                    HttpConnectionParams.setConnectionTimeout(client.getParams(), 10000); //Timeout Limit
                    HttpResponse response;
                    String URL = "http://2party.dk/itsmap/beerbot_post.php";

                    try{
                        HttpPost post = new HttpPost(URL);

                        // Create a NameValuePair out of the JSONObject + a name
                        //List<NameValuePair> nVP = new ArrayList<NameValuePair>(2);
                        //nVP.add(new BasicNameValuePair("json", json.toString()));

                        // Hand the NVP to the POST
                        if(jsonString !=null) {
                            post.setEntity(new StringEntity(jsonString));
                            Log.i("main", "TestPOST - nVP = " + jsonString.toString());
                        }

                        // Collect the response
                        response = client.execute(post);

            /*Checking response */
                        if(response!=null){
                            InputStream in = response.getEntity().getContent(); //Get the data in the entity
                            int stuff = in.read();
                            Toast.makeText(getApplicationContext(),stuff, Toast.LENGTH_SHORT).show();
                        }
                    }
                    catch(Exception e){
                        e.printStackTrace();
                        //createDialog("Error", "Cannot Establish Connection");
                    }

=======
                    Log.d(TAG, "Signature: " + result.getSignature());
                    Log.d(TAG, "Order ID: " + result.getOrderId());
                    Log.d(TAG, "Trans. ID: " + result.getTransactionId());
>>>>>>> 086ef9de573f9e18886d85325488dcf37c910340
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
