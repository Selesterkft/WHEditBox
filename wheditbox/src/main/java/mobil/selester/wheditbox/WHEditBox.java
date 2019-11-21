package mobil.selester.wheditbox;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.print.PrinterId;
import android.support.annotation.StyleableRes;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.content.res.TypedArray;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.security.Key;

public class WHEditBox extends LinearLayout {

    public interface OnDetectBarcodeListener{
        public void OnDetectBarcode();
    }

    @StyleableRes
    int index0 = 0;

    @StyleableRes
    int index1 = 1;

    @StyleableRes
    int index2 = 2;

    @StyleableRes
    int index3 = 3;

    ConstraintLayout mainLayout;
    public EditText EDText;
    ImageView delBtn, zoomBtn;
    private AlertDialog popup;
    private Activity activity;
    private String suffix = "";
    private OnDetectBarcodeListener detect;
    private EditText et;
    private Fragment frg;
    private int selectBG, BG;

    public WHEditBox(Context context, AttributeSet attributeSet){
        super(context, attributeSet);
        init(context, attributeSet);
    }

    private void init(Context context, AttributeSet attrs){
        inflate(context, R.layout.layoutcustomview, this);

        TypedArray typedArray = context.obtainStyledAttributes(attrs,R.styleable.WHEditBox);
        Log.i("TAG",""+typedArray.getIndexCount());
        BG = typedArray.getResourceId(R.styleable.WHEditBox_backgroundStyle,0);
        boolean setDelBtn = typedArray.getBoolean(R.styleable.WHEditBox_setDelBtn,false);
        boolean setShowTextBtn = typedArray.getBoolean(R.styleable.WHEditBox_setShowTextBtn,false);
        boolean setClickShowText = typedArray.getBoolean(R.styleable.WHEditBox_setClickShowDialog,false);
        selectBG = typedArray.getResourceId(R.styleable.WHEditBox_selectBackgroundStyle,0);
        typedArray.recycle();
        initComponents();
        setSelectBackgroundFunc(BG);
        setDelBtnFunc( setDelBtn );
        setShowTextBtnFunc( setShowTextBtn );
        setClickShowTextFunc( setClickShowText );
    }

    public void setLocalFragment( Fragment frg ){

    }

    private void setNextTabPositionFunc(Context context, int value) {
        //EditText et = ((Activity)context).findViewById(value);
        //et.requestFocus();
    }


    public void setOnDetectBarcodeListener(OnDetectBarcodeListener onDetectBarcodeListener){
        detect = onDetectBarcodeListener;
    }

    private void initComponents() {
        mainLayout = findViewById(R.id.mainLayout);
        EDText = findViewById(R.id.editText);
        delBtn = findViewById(R.id.delTextImg);
        zoomBtn = findViewById(R.id.showTextImg);
        EDText.addTextChangedListener(textWatcher);
        delBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                EDText.getText().clear();
            }
        });
        setDefaultSetting();
    }

    public void setSuffix(String suffix){
        this.suffix = suffix;
    }

    public String getSuffix(){
        return suffix;
    }

    private void setDefaultSetting(){
        zoomBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("TAG",""+EDText.isShown());
                showDialog();
            }
        });
    }

    public void setDialogBox(Activity activity){
        this.activity = activity;
    }

    void showDialog() {

        if (activity != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            et = new EditText(getContext());
            et.setText(EDText.getText());
            et.setSelection(et.getText().length());
            et.setInputType(EDText.getInputType());
            et.addTextChangedListener(textWatcher);
            et.requestFocus();
            builder.setView(et);
            builder.setPositiveButton("Megadás", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    EDText.setText(et.getText());
                    popup.dismiss();
                }
            });

            builder.setNegativeButton("Mégsem", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    popup.dismiss();
                }
            });
            popup = builder.create();
            popup.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
            popup.setTitle("Adat megadása");
            popup.show();

        } else {
            Log.e("WHEditBox","Activity elérése nincs beállítva!");
        }
    }

    //----- Paramaters --------------------------------------

    public void setClickShowTextFunc(boolean value) {
        if( value ) {
            EDText.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.i("TAG","CLICK");
                    showDialog();
                }
            });
            EDText.setOnFocusChangeListener(new OnFocusChangeListener() {
                @Override
                public void onFocusChange(View view, boolean hasFocus) {
                    if(hasFocus){
                        mainLayout.setBackgroundResource(selectBG);
                    }else{
                        mainLayout.setBackgroundResource(BG);
                    }
                    Log.i("TAG","FOCUS");
                    InputMethodManager imm = (InputMethodManager) EDText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm.isActive()) imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                }
            });
        }
    }

    public void setSelectBackgroundFunc(int value) {
        if(value > 0) {
            mainLayout.setBackgroundResource(value);
        }
        Log.i("PARAM","setSelectBackground");
    }

    public void setDelBtnFunc(boolean value) {
        if( value ) delBtn.setVisibility(VISIBLE); else delBtn.setVisibility(GONE);
        Log.i("PARAM","setDelBtnFunc");
    }

    public void setShowTextBtnFunc(boolean value) {
        if( value ) zoomBtn.setVisibility(VISIBLE); else zoomBtn.setVisibility(GONE);
        Log.i("PARAM","setShowTextBtnFunc");
    }

    public boolean getDelBtnFunc() {
        if (delBtn.getVisibility() == VISIBLE)
            return true;
        else
            return false;
    }

    public boolean getShowTextBtnFunc() {
        if (zoomBtn.getVisibility() == VISIBLE)
            return true;
        else
            return false;
    }


    //-------------------------------------------------------------------------

    TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if( !suffix.equals("") ) {
                int suffixLen = suffix.length();
                Log.i("TAG", s.toString());
                if (s.length() > suffixLen) {
                    if (s.toString().substring(s.length() - suffixLen, s.length()).equals(suffix)) {
                        EDText.setText(s.toString().substring(0, s.toString().length() - suffixLen));
                        InputMethodManager imm = (InputMethodManager) et.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (imm.isActive()) imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
                        popup.dismiss();
                        detect.OnDetectBarcode();
                    }
                }
            }
        }
    };


}
