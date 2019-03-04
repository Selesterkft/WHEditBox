package mobil.selester.hu;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.Toast;

import mobil.selester.wheditbox.WHEditBox;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WHEditBox ceb = findViewById(R.id.customEB);
        WHEditBox ceb1 = findViewById(R.id.customEB1);
        WHEditBox ceb2 = findViewById(R.id.customEB2);
        WHEditBox ceb3 = findViewById(R.id.customEB3);

        // ------------------ Global definition ----------------

        WHEditBox.activity = this;
        WHEditBox.suffix = "#&";

        //------------------------------------------------------

        ceb.setNextFocus(ceb1.EDText);
        ceb1.setNextFocus(ceb2.EDText);
        ceb2.setNextFocus(ceb3.EDText);

        ceb.EDText.setText("None");
        ceb.setDialogTitle("Valami");
        ceb.setOnDetectBarcodeListener(new WHEditBox.OnDetectBarcodeListener() {
            @Override
            public void OnDetectBarcode() {
                Toast.makeText(getBaseContext(),"OK",Toast.LENGTH_LONG).show();
            }
        });

        ceb1.EDText.setText("FocusDialog");
        ceb1.setOnDetectBarcodeListener(new WHEditBox.OnDetectBarcodeListener() {
            @Override
            public void OnDetectBarcode() {
                Toast.makeText(getBaseContext(),"OK1",Toast.LENGTH_LONG).show();
            }
        });

        ceb2.EDText.setText("ClickDialog");
        ceb2.setOnDetectBarcodeListener(new WHEditBox.OnDetectBarcodeListener() {
            @Override
            public void OnDetectBarcode() {
                Toast.makeText(getBaseContext(),"OK2",Toast.LENGTH_LONG).show();
            }
        });

        ceb3.EDText.setText("ClickKeyboard");
        ceb3.setOnDetectBarcodeListener(new WHEditBox.OnDetectBarcodeListener() {
            @Override
            public void OnDetectBarcode() {
                Toast.makeText(getBaseContext(),"OK3",Toast.LENGTH_LONG).show();
            }
        });

    }

}
