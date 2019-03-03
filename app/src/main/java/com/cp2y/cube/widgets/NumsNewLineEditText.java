package com.cp2y.cube.widgets;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.widget.EditText;

import com.cp2y.cube.R;

/**
 * Created by yangfan
 * nrainyseason@163.com
 */

public class NumsNewLineEditText extends EditText {
    private String textTemp;
    private int nums=0;
    public NumsNewLineEditText(Context context) {
        super(context);
    }

    public NumsNewLineEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.NumsNewLineEditText);
        nums = (int) array.getInteger(R.styleable.NumsNewLineEditText_NewLineNums, 0);
        addTextChangedListener(textWatcher);
    }

    public void setText(String text){
        if(!TextUtils.isEmpty(text)&&nums>0){
            String s=text.trim();
            StringBuilder stringBuilder=new StringBuilder();
            for(int i=0,size=text.length();i<size;i++){
                stringBuilder.append(s.substring(i,i+1));
                if(stringBuilder.toString().length()%nums==0)stringBuilder.append("\n");
            }
        }
        setText(text,BufferType.NORMAL);
        setSelection(getText().length()-1);
    }

    private TextWatcher textWatcher=new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            if(nums>0){
                String text=s.toString();
                if(!TextUtils.isEmpty(textTemp)&&textTemp.length()>text.length()){//删除操作
                    textTemp=text;
                    //中间删除处理

                }else{//添加  中间添加处理
                    if(text.endsWith("\n"))return;
                    String text1=text.indexOf("\n")>0?text.replace("\n",""):text;
                    if(text1.length()%3==0){
                        textTemp=text.concat("\n");
                        setText(textTemp,BufferType.NORMAL);
                        setSelection(getText().length());
                    }
                }
            }
        }
    };
}
