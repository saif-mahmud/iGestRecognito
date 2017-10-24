package com.example.andrei.igestrecognito.keyboard;


import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputConnection;

import com.example.andrei.igestrecognito.R;

public class SignToTextKeyboard extends InputMethodService
        implements KeyboardView.OnKeyboardActionListener {

    private boolean caps = false;
    private Keyboard keyboard;
    private KeyboardView kv;

    public void onKey(int primaryCode, int[] keyCodes) {
        boolean z = true;
        InputConnection ic = getCurrentInputConnection();
        switch (primaryCode) {
            case -5:
                ic.deleteSurroundingText(1, 0);
                return;
            case -4:
                ic.sendKeyEvent(new KeyEvent(0, 66));
                return;
            case -1:
                if (this.caps) {
                    z = false;
                }
                this.caps = z;
                this.keyboard.setShifted(this.caps);
                this.kv.invalidateAllKeys();
                return;
            default:
                char code = (char) primaryCode;
                if (Character.isLetter(code) && this.caps) {
                    code = Character.toUpperCase(code);
                }
                ic.commitText(String.valueOf(code), 1);
                return;
        }
    }

    public View onCreateInputView() {
        this.kv = (KeyboardView) getLayoutInflater().inflate(R.layout.sign_to_text_keyboard, null);
        this.keyboard = new Keyboard(this, R.xml.sign_to_text_qwerty);
        this.kv.setKeyboard(this.keyboard);
        this.kv.setOnKeyboardActionListener(this);
        return this.kv;
    }

    public void onPress(int primaryCode) {
    }

    public void onRelease(int primaryCode) {
    }

    public void onText(CharSequence text) {
    }

    public void swipeDown() {
    }

    public void swipeLeft() {
    }

    public void swipeRight() {
    }

    public void swipeUp() {
    }
}