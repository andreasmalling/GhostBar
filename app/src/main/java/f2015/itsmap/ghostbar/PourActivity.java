package f2015.itsmap.ghostbar;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;

import dk.danskebank.mobilepay.sdk.MobilePay;
import dk.danskebank.mobilepay.sdk.ResultCallback;
import dk.danskebank.mobilepay.sdk.model.FailureResult;
import dk.danskebank.mobilepay.sdk.model.Payment;
import dk.danskebank.mobilepay.sdk.model.SuccessResult;


public class PourActivity extends AppCompatActivity {

    private int MOBILEPAY_PAYMENT_REQUEST_CODE = 1337;

    private SeekBar pourMeter;
    private TextView priceAmount;
    private Button buyButton;
    private int amount = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pour);

        MobilePay.getInstance().init("APPDK0000000000");

        pourMeter = (SeekBar) findViewById(R.id.pourMeter);
        priceAmount = (TextView) findViewById(R.id.priceAmount);
        buyButton = (Button) findViewById(R.id.buyButton);

        pourMeter.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                amount = progress;
                priceAmount.setText(amount + " kr.");
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
                    payment.setProductPrice(new BigDecimal(amount));
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
