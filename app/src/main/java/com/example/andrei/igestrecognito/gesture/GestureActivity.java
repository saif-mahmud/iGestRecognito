package com.example.andrei.igestrecognito.gesture;

import android.gesture.Gesture;
import android.gesture.GestureLibraries;
import android.gesture.GestureLibrary;
import android.gesture.GestureOverlayView;
import android.gesture.Prediction;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.andrei.igestrecognito.R;

import java.util.ArrayList;

public class GestureActivity extends AppCompatActivity implements GestureOverlayView.OnGesturePerformedListener {

    static String result;
    static String typedText;
    Button deleteButton, spaceButton;
    char c[] = new char[100];
    int i = 0;
    private GestureLibrary gLibrary;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gesture);
        final EditText inputTxt = (EditText) findViewById(R.id.plain_text_input);

        deleteButton = (Button) findViewById(R.id.deleteButton);
        spaceButton = (Button) findViewById(R.id.spaceButton);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                typedText = inputTxt.getText().toString();
                if (typedText.length() > 1)
                    typedText = typedText.substring(0, typedText.length() - 1);
                else
                    typedText = "";
                result = typedText;
                inputTxt.setText(result);
                inputTxt.setSelection(result.length());
            }
        });
        spaceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                typedText = inputTxt.getText().toString();
                typedText += " ";
                result = typedText;
                inputTxt.setText(result);
                inputTxt.setSelection(result.length());

            }
        });
        gLibrary = GestureLibraries.fromRawResource(this, R.raw.gesture);
        if (!gLibrary.load()) {
            finish();
        }
        GestureOverlayView gOverlay =
                (GestureOverlayView) findViewById(R.id.gOverlay);
        gOverlay.addOnGesturePerformedListener(this);

    }

    @Override
    public void onGesturePerformed(GestureOverlayView gestureOverlayView, Gesture gesture) {
        ArrayList<Prediction> predictions = gLibrary.recognize(gesture);
        EditText inputTxt = (EditText) findViewById(R.id.plain_text_input);
        if (predictions.size() > 0 && predictions.get(0).score > 1.0) {

            typedText = inputTxt.getText().toString();
            // TextView tv = (TextView)findViewById(R.id.textwrite);
            //String typedText = tv.getText().toString();

            result = typedText + predictions.get(0).name;


            inputTxt.setText(result);
            inputTxt.setSelection(result.length());

            Toast.makeText(this, result, Toast.LENGTH_SHORT).show();
            //tv.setText(result);
            //  if ("a".equalsIgnoreCase(result))
            //{
            //Toast.makeText(this, "a", Toast.LENGTH_LONG).show();
            //textView.setText(textView.getText().toString());
            //}
        }
    }
}
