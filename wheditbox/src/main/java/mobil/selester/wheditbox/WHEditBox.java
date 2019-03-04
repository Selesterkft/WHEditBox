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
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.security.Key;

public class WHEditBox extends LinearLayout {

    public interface OnDetectBarcodeListener{
        public void OnDetectBarcode();
    }

    ConstraintLayout mainLayout;
    public EditText EDText;
    public static String suffix = "";
    public static Activity activity;

    ImageView delBtn;
    private AlertDialog popup;
    private OnDetectBarcodeListener detect;
    private EditText et;
    private int selectBG, BG;
    private int writeType;
    private EditText nextET = null;
    private String dialogTitle = "Adat megadása";
    private static int WRITETYPE_None = 0;
    private static int WRITETYPE_ClickDialog = 1;
    private static int WRITETYPE_FocusDialog = 2;
    private static int WRITETYPE_ClickKeyboard = 3;


    public static int dpToPx(Context context, int dp) {
        float density = context.getResources()
                .getDisplayMetrics()
                .density;
        return Math.round((float) dp * density);
    }

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
        selectBG = typedArray.getResourceId(R.styleable.WHEditBox_selectBackgroundStyle,0);
        int textSize = typedArray.getDimensionPixelSize(R.styleable.WHEditBox_textSize,0);
        int innerPadding = typedArray.getDimensionPixelSize(R.styleable.WHEditBox_innerPadding,0);
        writeType = typedArray.getInt(R.styleable.WHEditBox_writeType, WRITETYPE_None);
        typedArray.recycle();

        initComponents();
        if( BG > 0) setSelectBackgroundFunc(BG);
        setDelBtnFunc( setDelBtn );
        if(textSize > 0) EDText.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        setInnerPadding(innerPadding);
    }

    public void setOnDetectBarcodeListener(OnDetectBarcodeListener onDetectBarcodeListener){
        detect = onDetectBarcodeListener;
    }

    private void initComponents() {
        mainLayout = findViewById(R.id.mainLayout);
        EDText = findViewById(R.id.editText);
        delBtn = findViewById(R.id.delTextImg);
        EDText.addTextChangedListener(textWatcher);
        delBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                EDText.getText().clear();
            }
        });
        setEditTextFunction();
    }

    TextView.OnEditorActionListener onEditorActionListener = new TextView.OnEditorActionListener() {
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
                fireFunction();
                detect.OnDetectBarcode();
            }
            return false;
        }
    };


    private void setEditTextFunction() {
        if( writeType != WRITETYPE_None) {
            EDText.setOnTouchListener(new OnTouchListener(){

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    int inType = EDText.getInputType(); // backup the input type
                    EDText.setInputType(InputType.TYPE_NULL); // disable soft input
                    EDText.onTouchEvent(event); // call native handler
                    EDText.setInputType(inType); // restore input type
                    return true; // consume touch even
                }
            });
        }

        if( writeType == WRITETYPE_ClickKeyboard || writeType == WRITETYPE_ClickDialog || writeType == WRITETYPE_FocusDialog) {
            EDText.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.i("TAG", "CLICK: " + EDText.getText().toString());
                    if (writeType == WRITETYPE_ClickDialog || writeType == WRITETYPE_FocusDialog) {
                        showDialog();
                    } else if (writeType == WRITETYPE_ClickKeyboard) {
                        Log.i("TAG", "SHOW KEYBOARD");
                        InputMethodManager imm = (InputMethodManager) EDText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (imm.isActive()) imm.toggleSoftInput(1, 0);
                    }
                }
            });
        }

        EDText.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                Log.i("TAG","FOCUS: "+EDText.getText().toString() + ", hasFocus: " + hasFocus );

                activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(EDText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                if( writeType != WRITETYPE_None ) {
                    Log.i("TAG","HIDE KEYBOARD");

                    if( writeType == WRITETYPE_FocusDialog ){
                        if(hasFocus){
                            showDialog();
                        }
                    }
                }
                if (selectBG > 0) {
                    if(hasFocus){
                        mainLayout.setBackgroundResource(selectBG);
                    }else{
                        mainLayout.setBackgroundResource(BG);
                    }
                }
            }
        });
        EDText.setOnEditorActionListener(onEditorActionListener);
    }

    void showDialog() {
        if (activity != null) {
            AlertDialog.Builder builder = new AlertDialog.Builder(activity);
            et = new EditText(getContext());
            et.setText(EDText.getText());
            et.setSelection(et.getText().length());
            et.addTextChangedListener(textWatcher);
            et.setImeOptions(EditorInfo.IME_ACTION_NEXT);
            et.setSingleLine(true);
            et.setOnEditorActionListener(onEditorActionListener);

            builder.setView(et);
            builder.setPositiveButton("Megadás", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    EDText.setText(et.getText());
                    InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(popup.getWindow().getDecorView().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    popup.dismiss();
                }
            });

            builder.setNegativeButton("Mégsem", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    popup.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                    InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(popup.getWindow().getDecorView().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                    popup.dismiss();
                }
            });
            popup = builder.create();
            popup.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
            popup.setTitle(dialogTitle);
            popup.setCanceledOnTouchOutside(false);
            popup.show();
        } else {
            Log.e("WHEditBox","Activity elérése nincs beállítva!");
        }
    }

    public void setDialogTitle(String dialogTitle){

    }

    public void setNextFocus(EditText et){
        this.nextET = et;
    }

    //----- Paramaters --------------------------------------

    public void setInnerPadding(int value){
        EDText.setPadding(value,value,value,value);
    }

    private OnTouchListener otl = new OnTouchListener() {
        public boolean onTouch (View v, MotionEvent event) {
            return true; // the listener has consumed the event
        }
    };


    public void setSelectBackgroundFunc(int value) {
        if(value > 0) {
            Log.i("TAG","" + value + ""+dpToPx(getContext(),value));
            mainLayout.setBackgroundResource( value );
        }
        Log.i("PARAM","setSelectBackground");
    }

    public void setDelBtnFunc(boolean value) {
        if( value ) delBtn.setVisibility(VISIBLE); else delBtn.setVisibility(GONE);
        Log.i("PARAM","setDelBtnFunc");
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
                        if( popup != null){
                            InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(popup.getWindow().getDecorView().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                            popup.dismiss();
                        }
                        fireFunction();
                        detect.OnDetectBarcode();
                    }
                }
            }
        }
    };

    private void fireFunction(){
        if( nextET != null ){
            nextET.requestFocus();
        }
    }


}
