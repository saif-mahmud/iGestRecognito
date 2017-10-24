package com.example.andrei.igestrecognito.textsign;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.andrei.igestrecognito.R;

public class ComposeActivity extends AppCompatActivity {

    EditText editText;
    ImageView sign;
    Button signButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_compose);

        editText = (EditText) findViewById(R.id.editText);
        sign = (ImageView) findViewById(R.id.signText);
        signButton = (Button) findViewById(R.id.signButton);


        signButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String text = editText.getText().toString().toLowerCase();
                Handler handler1 = new Handler();
                for (int i = 0; i < text.length(); i++) {
                    final int finalI = i;
                    handler1.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            char letter = text.charAt(finalI);
                            sign.setImageResource(MapActivity.data[letter - 'a']);

                        }
                    }, 500 * i);

                }
            }
        });
    }
}
