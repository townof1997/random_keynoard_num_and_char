package com.kh.keyboard;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.os.SystemClock;
import android.text.Editable;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;


public class KhKeyboardView {
    private Activity mContext;
    private View parentView;
    private KeyboardView mLetterView;   //字母键盘view
    private KeyboardView mNumberView;   //数字键盘View
    private Keyboard mNumberKeyboard;   // 数字键盘
    private Keyboard mLetterKeyboard;   // 字母键盘
    private Keyboard mSymbolKeyboard;   // 符号键盘

    private boolean isNumber = false;    // 是否数字键盘
    public static   boolean isUpper = false;    // 是否大写
    private boolean isSymbol = false;   // 是否是符号
    private boolean isToSymbol = false; //从数字跳转到特殊字符
    private EditText mEditText;
    private View headerView;

    public void setEditText(EditText text) {
        mEditText = text;
    }

    public KhKeyboardView(Activity context, View view) {
        mContext = context;
        parentView = view;

        mNumberKeyboard = new Keyboard(mContext, R.xml.keyboard_numbers);
        mLetterKeyboard = new Keyboard(mContext, R.xml.keyboard_word);
        mSymbolKeyboard = new Keyboard(mContext, R.xml.keyboard_symbol);
        mNumberView = (KeyboardView) parentView.findViewById(R.id.keyboard_view);
        mLetterView = (KeyboardView) parentView.findViewById(R.id.keyboard_view_2);

//        mNumberView.setKeyboard(mNumberKeyboard);
        randomdigkey(mNumberKeyboard);
        mNumberView.setEnabled(true);
        mNumberView.setPreviewEnabled(false);
        mNumberView.setOnKeyboardActionListener(listener);
//        mLetterView.setKeyboard(mLetterKeyboard);
        randomalpkey(mLetterKeyboard);
        mLetterView.setEnabled(true);
        mLetterView.setPreviewEnabled(true);
        mLetterView.setOnKeyboardActionListener(listener);
        headerView = parentView.findViewById(R.id.keyboard_header);

    }

    private KeyboardView.OnKeyboardActionListener listener = new KeyboardView.OnKeyboardActionListener() {
        @Override
        public void onPress(int primaryCode) {
            Log.d("primaryCode","onPress--"+primaryCode);
            if (primaryCode == Keyboard.KEYCODE_SHIFT) {
                List<Keyboard.Key> keyList = mLetterKeyboard.getKeys();

                mLetterView.setPreviewEnabled(false);
            } else if (primaryCode == Keyboard.KEYCODE_DELETE) {
                mLetterView.setPreviewEnabled(false);
            } else if (primaryCode == 32) {
                mLetterView.setPreviewEnabled(false);
            } else {
                mLetterView.setPreviewEnabled(true);
            }

        }

        @Override
        public void onRelease(int primaryCode) {
            Log.d("primaryCode","onRelease--"+primaryCode);

//            if(primaryCode== -1){
//                if(isUpper){
//                    isUpper=false;
//                }else {
//                    isUpper=true;
//                }
//            }
        }

        @Override
        public void onKey(int primaryCode, int[] keyCodes) {
            Log.d("primaryCode","onKey--"+primaryCode);
            try {
                if (mEditText == null)
                    return;
                Editable editable = mEditText.getText();
                int start = mEditText.getSelectionStart();
                if (primaryCode == Keyboard.KEYCODE_CANCEL) {
                    // 隐藏键盘
                    hideKeyboard();
                } else if (primaryCode == Keyboard.KEYCODE_DELETE || primaryCode == -35) {

                    // 回退键,删除字符
                    if (editable != null && editable.length() > 0) {
                        if (start > 0) {
                            editable.delete(start - 1, start);
                        }
                    }
                } else if (primaryCode == Keyboard.KEYCODE_SHIFT) {
                    // 大小写切换
                    changeKeyboart();
                    mLetterView.setKeyboard(mLetterKeyboard);

                } else if (primaryCode == Keyboard.KEYCODE_MODE_CHANGE) {
                    // 数字与字母键盘互换
                    if (isNumber) {
                        showLetterView();
                        showLetterView2();
                        isToSymbol = false;
                    } else {
                        showNumberView();
                        isToSymbol = true;
                    }
                } else if (primaryCode == 90001) {
                    if (isToSymbol) {
                        isToSymbol = false;
                        showLetterView();
                        showSymbolView();
                    } else {
                        // 字母与符号切换
                        if (isSymbol) {
                            showLetterView2();
                        } else {
                            showSymbolView();
                        }

                    }

                } else {
                    // 输入键盘值
                    editable.insert(start, Character.toString((char) primaryCode));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        @Override
        public void onText(CharSequence text) {

        }

        @Override
        public void swipeLeft() {

        }

        @Override
        public void swipeRight() {

        }

        @Override
        public void swipeDown() {

        }

        @Override
        public void swipeUp() {

        }
    };

    //  字母-符号,显示字母
    private void showLetterView2() {
        if (mLetterView != null) {
            isSymbol = false;
//            mLetterView.setKeyboard(mLetterKeyboard);
            randomalpkey(mLetterKeyboard);
        }
    }

    //  字母-符号,显示符号
    private void showSymbolView() {
        try {
            if (mLetterKeyboard != null) {
                isSymbol = true;
                mLetterView.setKeyboard(mSymbolKeyboard);
//                randomInterpunctionkey(mSymbolKeyboard);
            }
        } catch (Exception e) {
        }
    }

    //  数字-字母,显示字母键盘
    private void showLetterView() {
        try {
            if (mLetterView != null && mNumberView != null) {
                isNumber = false;
                mLetterView.setVisibility(View.VISIBLE);
                mNumberView.setVisibility(View.INVISIBLE);
                randomalpkey(mLetterKeyboard);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    // 数字-字母, 显示数字键盘
    private void showNumberView() {
        try {
            if (mLetterView != null && mNumberView != null) {
                isNumber = true;
                mLetterView.setVisibility(View.INVISIBLE);
                mNumberView.setVisibility(View.VISIBLE);
                randomdigkey(mNumberKeyboard);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 切换大小写
     */
    private void changeKeyboart() {
        List<Keyboard.Key> keyList = mLetterKeyboard.getKeys();
        if (isUpper) {
            // 大写切换小写
            isUpper = false;
            for (Keyboard.Key key : keyList) {
                Drawable icon = key.icon;

                if (key.label != null && isLetter(key.label.toString())) {
                    key.label = key.label.toString().toLowerCase();
                    key.codes[0] = key.codes[0] + 32;
                }
            }
        } else {
            // 小写切换成大写
            isUpper = true;
            for (Keyboard.Key key : keyList) {
                if (key.label != null && isLetter(key.label.toString())) {
                    key.label = key.label.toString().toUpperCase();
                    key.codes[0] = key.codes[0] - 32;
                }
            }
        }
    }

    /**
     * 判断是否是字母
     */
    private boolean isLetter(String str) {
        String wordStr = "abcdefghijklmnopqrstuvwxyz";
        return wordStr.contains(str.toLowerCase());
    }

    public void hideKeyboard() {
        try {
            int visibility = mLetterView.getVisibility();
            if (visibility == View.VISIBLE) {
                headerView.setVisibility(View.GONE);
                mLetterView.setVisibility(View.GONE);
            }
            visibility = mNumberView.getVisibility();
            if (visibility == View.VISIBLE) {
                headerView.setVisibility(View.GONE);
                mNumberView.setVisibility(View.GONE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
     * 显示键盘
     *
     * @param editText
     */
    public void showKeyboard(EditText editText) {
        try {
            this.mEditText = editText;
            int visibility = 0;
            int inputText = mEditText.getInputType();
            headerView.setVisibility(View.VISIBLE);
            switch (inputText) {
                case InputType.TYPE_CLASS_NUMBER:
                    showNumberView();
                    break;
                case InputType.TYPE_CLASS_PHONE:
                    showNumberView();
                    break;
                case InputType.TYPE_NUMBER_FLAG_DECIMAL:
                    showNumberView();
                    break;
                default:
                    showLetterView();
                    break;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


    private boolean isNumber(String str) {
        String wordstr = "0123456789";
        if (wordstr.indexOf(str) > -1) {
            return true;
        }
        return false;
    }

    // 实现数字随机键盘布局
    private void randomdigkey(Keyboard mKeyboard) {
        if (mKeyboard == null) {
            return;
        }

        List<Keyboard.Key> keyList = mKeyboard.getKeys();

        // 查找出0-9的数字键
        List<Keyboard.Key> newkeyList = new ArrayList<Keyboard.Key>();
        for (int i = 0, size = keyList.size(); i < size; i++) {
            Keyboard.Key key = keyList.get(i);
            CharSequence label = key.label;
            if (label != null && isNumber(label.toString())) {
                newkeyList.add(key);
            }
        }

        int count = newkeyList.size();

        List<KeyModel> resultList = new ArrayList<KeyModel>();

        LinkedList<KeyModel> temp = new LinkedList<KeyModel>();

        for (int i = 0; i < count; i++) {
            temp.add(new KeyModel(48 + i, i + ""));
        }

        Random rand = new SecureRandom();
        rand.setSeed(SystemClock.currentThreadTimeMillis());

        for (int i = 0; i < count; i++) {
            int num = rand.nextInt(count - i);
            KeyModel model = temp.get(num);
            resultList.add(new KeyModel(model.getCode(), model.getLable()));
            temp.remove(num);
        }

        for (int i = 0, size = newkeyList.size(); i < size; i++) {
            Keyboard.Key newKey = newkeyList.get(i);
            KeyModel resultmodle = resultList.get(i);
            newKey.label = resultmodle.getLable();
            newKey.codes[0] = resultmodle.getCode();
        }
//        mKeyboardView.setKeyboard(mKeyboard);
        mNumberView.setKeyboard(mKeyboard);
    }

    // 随机字母键盘
    private void randomalpkey(Keyboard mKeyboard) {
        List<Keyboard.Key> keyList = mKeyboard.getKeys();
        // 查找出a-z的数字键
        List<Keyboard.Key> newkeyList = new ArrayList<Keyboard.Key>();
        for (int i = 0; i < keyList.size(); i++) {
            if (keyList.get(i).label != null
                    && isword(keyList.get(i).label.toString())) {
                newkeyList.add(keyList.get(i));
            }
        }
        // 数组长度
        int count = newkeyList.size();
        // 结果集
        List<KeyModel> resultList = new ArrayList<KeyModel>();
        // 用一个LinkedList作为中介
        LinkedList<KeyModel> temp = new LinkedList<KeyModel>();
        // 初始化temp
        for (int i = 0; i < count ; i++) {//count - 1
            temp.add(new KeyModel(97 + i, "" + (char) (97 + i)));
        }
//		temp.add(new KeyModel(64, "" + (char) 64));// .
        // 取数
        Random rand = new Random();
        for (int i = 0; i < count; i++) {
            int num = rand.nextInt(count - i);
            resultList.add(new KeyModel(temp.get(num).getCode(), temp.get(num)
                    .getLable()));
            temp.remove(num);
        }
        for (int i = 0; i < newkeyList.size(); i++) {
            newkeyList.get(i).label = resultList.get(i).getLable();
            newkeyList.get(i).codes[0] = resultList.get(i).getCode();
        }

//        mKeyboardView.setKeyboard(mKeyboard);
        mLetterView.setKeyboard(mKeyboard);
    }
    // 判断是否为字母
    private boolean isword(String str) {
        String wordstr = "abcdefghijklmnopqrstuvwxyz";
        if (wordstr.indexOf(str.toLowerCase()) > -1) {
            return true;
        }
        return false;
    }
    class KeyModel {

        private Integer code;
        private String label;

        public KeyModel(Integer code, String lable) {
            this.code = code;
            this.label = lable;
        }

        public Integer getCode() {
            return code;
        }

        public void setCode(Integer code) {
            this.code = code;
        }

        public String getLable() {
            return label;
        }

        public void setLabel(String lable) {
            this.label = lable;
        }

    }

    private boolean isInterpunction(String str) {
//		String characterstr = "[]{}#%^~*+=-_/:;?。$()!|€£¥`'.@\\";
        String characterstr = "[]{}#%^*+=_-/:;()$@`€?!£\\|~¥.";
        if (characterstr.indexOf(str) > -1) {
            return true;
        }
        if (characterstr.contains("&")) {
            return true;
        }
        return false;
    }

    /**
     * 标点符号键盘-随机
     */
    private void randomInterpunctionkey(Keyboard mKeyboard) {
        List<Keyboard.Key> keyList = mKeyboard.getKeys();

        // 查找出标点符号的数字键
        List<Keyboard.Key> newkeyList = new ArrayList<Keyboard.Key>();
        for (int i = 0; i < keyList.size(); i++) {
            if (keyList.get(i).label != null
                    && isInterpunction(keyList.get(i).label.toString())) {
                newkeyList.add(keyList.get(i));
            }
        }
        // 数组长度
        int count = newkeyList.size();
        // 结果集
        List<KeyModel> resultList = new ArrayList<KeyModel>();
        // 用一个LinkedList作为中介
        LinkedList<KeyModel> temp = new LinkedList<KeyModel>();

        // 初始化temp
        temp.add(new KeyModel(33, "" + (char) 33));
//		temp.add(new KeyModel(34, "" + (char) 34));
        temp.add(new KeyModel(35, "" + (char) 35));
        temp.add(new KeyModel(36, "" + (char) 36));
        temp.add(new KeyModel(37, "" + (char) 37));
        temp.add(new KeyModel(38, "" + (char) 38));
//		temp.add(new KeyModel(39, "" + (char) 39));
        temp.add(new KeyModel(40, "" + (char) 40));
        temp.add(new KeyModel(41, "" + (char) 41));
        temp.add(new KeyModel(42, "" + (char) 42));
        temp.add(new KeyModel(43, "" + (char) 43));
        temp.add(new KeyModel(45, "" + (char) 45));
        temp.add(new KeyModel(46, "" + (char) 46));
        temp.add(new KeyModel(47, "" + (char) 47));
        temp.add(new KeyModel(58, "" + (char) 58));
        temp.add(new KeyModel(59, "" + (char) 59));
//		temp.add(new KeyModel(60, "" + (char) 60));
        temp.add(new KeyModel(61, "" + (char) 61));
//		temp.add(new KeyModel(62, "" + (char) 62));
        temp.add(new KeyModel(63, "" + (char) 63));
        temp.add(new KeyModel(64, "" + (char) 64));
        temp.add(new KeyModel(91, "" + (char) 91));
        temp.add(new KeyModel(92, "" + (char) 92));
        temp.add(new KeyModel(93, "" + (char) 93));
        temp.add(new KeyModel(94, "" + (char) 94));
        temp.add(new KeyModel(95, "" + (char) 95));
        temp.add(new KeyModel(96, "" + (char) 96));
        temp.add(new KeyModel(123, "" + (char) 123));
        temp.add(new KeyModel(124, "" + (char) 124));
        temp.add(new KeyModel(125, "" + (char) 125));
        temp.add(new KeyModel(126, "" + (char) 126));

        temp.add(new KeyModel(127, "" + (char) 127));
        temp.add(new KeyModel(128, "" + (char) 128));
        temp.add(new KeyModel(129, "" + (char) 129));

        temp.add(new KeyModel(163, "" + (char)163));
        temp.add(new KeyModel(165, "" + (char)165));
        temp.add(new KeyModel(8364, "" + (char)8364));
        // 取数
        Random rand = new Random();
        for (int i = 0; i < count; i++) {
            int num = rand.nextInt(count - i);
            resultList.add(new KeyModel(temp.get(num).getCode(), temp.get(num)
                    .getLable()));
            temp.remove(num);
        }
        for (int i = 0; i < newkeyList.size(); i++) {
            newkeyList.get(i).label = resultList.get(i).getLable();
            newkeyList.get(i).codes[0] = resultList.get(i).getCode();
            Log.i("resultList:", "" + resultList.get(i).getLable() + "\n" +
                    ", resultList.get(i).getCode()" + resultList.get(i).getCode());
        }

//        mKeyboardView.setKeyboard(mKeyboard);
        mLetterView.setKeyboard(mKeyboard);
    }
}
