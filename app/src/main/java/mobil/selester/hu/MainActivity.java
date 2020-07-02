package mobil.selester.hu;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import mobil.selester.wheditbox.WHEditBox;

public class MainActivity extends AppCompatActivity {

    List<String[]> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        WHEditBox ceb = findViewById(R.id.customEB);
        WHEditBox.activity = this;
        WHEditBox.suffix = "#&";
        ceb.setErrorContent(WHEditBox.ERRORCONTENT_Erase);
        ceb.setTrimFrom(2);
        ceb.setTrimTo(3);

        ceb.setTextType(WHEditBox.TEXTTYPE_String);
        ceb.setUnique(0,1,"100");

        String[] row = new String[5];
        row[0] = "a1";row[1] = "b1";row[2] = "c1";row[3] = "d1";row[4] = "e1";
        list.add( row );

        ceb.setDataSource(list);
        ceb.EDText.setText("proba");
        ceb.setOnDetectBarcodeListener(new WHEditBox.OnDetectBarcodeListener() {
            @Override
            public void OnDetectBarcode(String s)
            {
                Log.i("TAG",s);
                String[] row1 = new String[5];
                row1[0] = "a2";row1[1] = "b2";row1[2] = "c1";row1[3] = "d2";row1[4] = "e2";
                list.add( row1 );

            }

            @Override
            public void OnDetectError(String errorResult, String value) {
                Log.i("TAG",errorResult + " - " + value);

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
