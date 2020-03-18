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
        WHEditBox.activity = this;
        WHEditBox.suffix = "#&";
        ceb.setErrorContent(WHEditBox.ERRORCONTENT_Erase);
        ceb.setTrimFrom(4);
        ceb.setTrimTo(7);

        ceb.setTextType(WHEditBox.TEXTTYPE_Int);

        ceb.EDText.setText("proba");
        ceb.setOnDetectBarcodeListener(new WHEditBox.OnDetectBarcodeListener() {
            @Override
            public void OnDetectBarcode(String s) {
                Log.i("TAG","OK");
            }

            @Override
            public void OnDetectError(String errorResult, String value) {
                Log.i("TAG",errorResult);
            }

            @Override
            public void OnFocusOutListener(String value) {
                Log.i("TAG","OUT");
            }

            @Override
            public void OnFocusInListener(String value) {
                Log.i("TAG","IN");
            }
        });
    }

}
