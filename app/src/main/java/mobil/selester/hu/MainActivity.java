package mobil.selester.hu;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;

import mobil.selester.wheditbox.WHEditBox;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        WHEditBox ceb = findViewById(R.id.customEB);
        ceb.setDialogBox(this);
        ceb.setSuffix("#&");
        ceb.EDText.setText("proba");
        ceb.EDText.setInputType(InputType.TYPE_CLASS_NUMBER);
        ceb.setOnDetectBarcodeListener(new WHEditBox.OnDetectBarcodeListener() {
            @Override
            public void OnDetectBarcode() {
                Log.i("TAG","OK");
            }
        });
        WHEditBox ceb1 = findViewById(R.id.customEB1);
        ceb1.setDialogBox(this);
    }

}
