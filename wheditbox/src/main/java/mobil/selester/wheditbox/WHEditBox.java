package mobil.selester.wheditbox;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.support.constraint.ConstraintLayout;
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
import java.util.ArrayList;
import java.util.List;

public class WHEditBox extends LinearLayout {

    public interface OnDetectBarcodeListener{
        void OnDetectBarcode(String value);
        void OnDetectError(String errorResult, String value);
        void OnFocusOutListener(String value);
        void OnFocusInListener(String value);
    }

    public EditText EDText;
    public static String suffix = "";
    public static Activity activity;

    public static int WRITETYPE_None            = 0;
    public static int WRITETYPE_ClickDialog     = 1;
    public static int WRITETYPE_FocusDialog     = 2;
    public static int WRITETYPE_ClickKeyboard   = 3;

    public static int TEXTTYPE_String           = 0;
    public static int TEXTTYPE_Int              = 1;
    public static int TEXTTYPE_Long             = 2;

    public static int ERRORCONTENT_Nothing      = 0;
    public static int ERRORCONTENT_Erase        = 1;
    public static int ERRORCONTENT_SelectedAll  = 2;


    private ConstraintLayout mainLayout;
    private ImageView delBtn;
    private AlertDialog popup;
    private OnDetectBarcodeListener detect;
    private EditText et;

    private int selectBG, BG;
    private int writeType;
    private EditText nextET = null;
    private String dialogTitle = "Adat megadása";
    private int minLength, maxLength, trimFrom, trimTo, textType;
    private String chkString;
    private int errorContent;
    private List<String[]> dataSource = null;

    private String ownRowID = "";
    private int rowColumn = 0;
    private int uniqueColumn = -1;

    public void setUnique(int rowColumn, int uniqueColumn, String ownRowID){
        this.ownRowID       = ownRowID;
        this.uniqueColumn   = uniqueColumn;
        this.rowColumn      = rowColumn;
    }

    private int counterColumn = -1;
    private int maxCount = -1;

    public void setCounter(int counterColumn, int maxCount) {
        this.counterColumn = counterColumn;
        this.maxCount = maxCount;
    }

    public void setDataSource(List<String[]> dataSource) {
        this.dataSource = dataSource;
    }

    public int getErrorContent() {
        return errorContent;
    }

    public void setErrorContent(int errorContent) {
        this.errorContent = errorContent;
    }

    public int getTextType() {
        return textType;
    }

    public void setTextType(int textType) {
        this.textType = textType;
        if(textType == TEXTTYPE_Int){
            EDText.setRawInputType(Configuration.KEYBOARD_QWERTY);
        }
    }

    public int getMinLength() {
        return minLength;
    }

    public void setMinLength(int minLength) {
        this.minLength = minLength;
    }

    public int getMaxLength() {
        return maxLength;
    }

    public void setMaxLength(int maxLength) {
        this.maxLength = maxLength;
    }

    public int getTrimFrom() {
        return trimFrom;
    }

    public void setTrimFrom(int trimFrom) {
        this.trimFrom = trimFrom;
    }

    public int getTrimTo() {
        return trimTo;
    }

    public void setTrimTo(int trimTo) {
        this.trimTo = trimTo;
    }

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
        setMinLength(0);
        setMaxLength(0);
        setTrimFrom(0);
        setTrimTo(0);
        setTextType(TEXTTYPE_String);
        errorContent = ERRORCONTENT_Nothing;
        chkString ="00000";
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
        detect = new OnDetectBarcodeListener() {
            @Override
            public void OnDetectBarcode(String value) {

            }

            @Override
            public void OnDetectError(String errorResult, String value) {

            }

            @Override
            public void OnFocusOutListener(String value) {

            }

            @Override
            public void OnFocusInListener(String value) {

            }
        };
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
                String _text = EDText.getText().toString();
                String errorString = checker(_text);
                EDText.setText(_text);
                if( !errorString.equals("00000") ){
                    if(getErrorContent() == ERRORCONTENT_Erase){
                        EDText.setText("");
                    }else if(getErrorContent() == ERRORCONTENT_SelectedAll){
                        EDText.selectAll();
                    }
                    detect.OnDetectError( errorString, _text );
                }else {
                    if (popup != null) {
                        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(popup.getWindow().getDecorView().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                        popup.dismiss();
                    }
                    detect.OnDetectBarcode( _text );
                }
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
                   //Log.i("TAG", "CLICK: " + EDText.getText().toString());
                    if (writeType == WRITETYPE_ClickDialog || writeType == WRITETYPE_FocusDialog) {
                        showDialog();
                    } else if (writeType == WRITETYPE_ClickKeyboard) {
                        //Log.i("TAG", "SHOW KEYBOARD");
                        InputMethodManager imm = (InputMethodManager) EDText.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                        if (imm.isActive()) imm.toggleSoftInput(1, 0);
                    }
                }
            });
        }

        EDText.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                activity.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
                InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(EDText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                if( writeType != WRITETYPE_None ) {
                   // Log.i("TAG","HIDE KEYBOARD");

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
                if(!hasFocus) {
                    String _text = EDText.getText().toString();
                    String errorString = checker(_text);
                    EDText.setText(_text);
                    if( !errorString.equals("00000") ){
                        if(getErrorContent() == ERRORCONTENT_Erase){
                            EDText.setText("");
                        }else if(getErrorContent() == ERRORCONTENT_SelectedAll){
                            EDText.selectAll();
                        }
                        detect.OnDetectError( errorString, _text );
                    }else{
                        detect.OnFocusOutListener(EDText.getText().toString());
                    }
                }else{
                    detect.OnFocusInListener(EDText.getText().toString());
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
            //Log.i("TAG","" + value + ""+dpToPx(getContext(),value));
            mainLayout.setBackgroundResource( value );
        }
        //Log.i("PARAM","setSelectBackground");
    }

    public void setDelBtnFunc(boolean value) {
        if( value ) delBtn.setVisibility(VISIBLE); else delBtn.setVisibility(GONE);
        //Log.i("PARAM","setDelBtnFunc");
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
                //Log.i("TAG", s.toString());
                if (s.length() > suffixLen) {
                    if (s.toString().substring(s.length() - suffixLen, s.length()).equals(suffix)) {
                        String _text = s.toString().substring(0, s.toString().length() - suffixLen);
                        _text = trimFromTo( _text );
                        String errorString = checker(_text);
                        EDText.setText(_text);
                        if( !errorString.equals("00000") ){
                            if(getErrorContent() == ERRORCONTENT_Nothing){
                            }else if(getErrorContent() == ERRORCONTENT_Erase){
                                EDText.setText("");
                            }else if(getErrorContent() == ERRORCONTENT_SelectedAll){
                                EDText.selectAll();
                            }
                            detect.OnDetectError( errorString, _text );
                        }else {
                            if (popup != null) {
                                InputMethodManager imm = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(popup.getWindow().getDecorView().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
                                popup.dismiss();
                            }
                            detect.OnDetectBarcode( EDText.getText().toString() );
                        }
                    }
                }
            }
        }
    };

    public void fireFunction(){
        if( nextET != null ){
            nextET.requestFocus();
        }
    }

    private String changeStringatIndex(int position, String text, char value){
        char[] myNameChars = text.toCharArray();
        myNameChars[position] = value;
        text = String.valueOf(myNameChars);
        return text;
    }

    private String trimFromTo(String EdText){
        if(getTrimFrom() > 0 && getTrimTo() == 0){
            EdText = EdText.substring(getTrimFrom() - 1);
        }else if(getTrimFrom() == 0 && getTrimTo() > 0){
            if( EdText.length() >= getTrimTo()) {
                EdText = EdText.substring(0, getTrimTo());
            }
        }else if(getTrimFrom() > 0 && getTrimTo() > 0){
            if( EdText.length() >= getTrimTo()) {
                EdText = EdText.substring(getTrimFrom() - 1, getTrimTo());
            }
        }
        return EdText;
    }

    private String checker(String EdText){

        // MinLength, MaxLength, TextType, Unique

        String result = "00000";
        if(getMinLength() > 0){
            if( EdText.length() < getMinLength() ){
                result = changeStringatIndex(0, result, '1');
            }
        }
        if(getMaxLength() > 0){
            if( EdText.length() > getMaxLength() ){
                result = changeStringatIndex(1, result, '1');
            }
        }
        if(getTextType() == TEXTTYPE_Int){
            if( !isInteger(EdText) ){
                result = changeStringatIndex(2, result, '1');
            }
        }
        if(getTextType() == TEXTTYPE_Long){
            if( !isLong(EdText) ){
                result = changeStringatIndex(2, result, '1');
            }
        }
        if (uniqueColumn > -1) {
            for (int dsNum = 0; dsNum < dataSource.size(); dsNum++) {
                if (dataSource.get(dsNum)[uniqueColumn].equals(EdText) && !dataSource.get(dsNum)[rowColumn].equals(ownRowID)) {
                    result = changeStringatIndex(3, result, '1');
                }
            }
        }
        if( maxCount > 0 && counterColumn > -1){
            int count = 0;
            for (int dsNum = 0; dsNum < dataSource.size(); dsNum++) {
                if (dataSource.get(dsNum)[counterColumn].equals(EdText)) {
                    count++;
                }
            }
            if(count >= maxCount){
                result = changeStringatIndex(4, result, '1');
            }
        }
        return  result;
    }

    public static boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
        } catch(NumberFormatException e) {
            return false;
        } catch(NullPointerException e) {
            return false;
        }
        return true;
    }

    public static boolean isLong(String s) {
        try {
            Long.parseLong(s);
        } catch(NumberFormatException e) {
            return false;
        } catch(NullPointerException e) {
            return false;
        }
        return true;
    }
}