package com.example.andrei.igestrecognito.menu;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.andrei.igestrecognito.gesture.GestureActivity;
import com.example.andrei.igestrecognito.subtitle.PlayerActivity;
import com.example.andrei.igestrecognito.textsign.ComposeActivity;
import com.example.andrei.igestrecognito.R;
import com.example.andrei.igestrecognito.tutorial.TutorialActivity;
import com.example.andrei.igestrecognito.keyboard.SignToTextActivity;
import com.example.andrei.igestrecognito.voice.SpeechToTextActivity;

/**
 * Created by saif_m_dhrubo on 26/09/17.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {

    private String[] titles = {"iGest Keyboard", "iVoice Recognition", "Text to Sign", "Sign Tutorial", "Sign Subtitle", "iGesture"};

    private String[] details = {"Keyboard details", "Voice details", "Text to Sign details", "Tutorial details", "Subtitle details", "Getsure Character Recognition"};

    private int[] images = {R.drawable.keyboard_image,
            R.drawable.voice_image,
            R.drawable.text_to_sign,
            R.drawable.tutorial,
            R.drawable.subtitle,
            R.drawable.gesture};

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.menu_cardview, viewGroup, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {
        viewHolder.itemTitle.setText(titles[i]);
        viewHolder.itemDetail.setText(details[i]);
        viewHolder.itemImage.setImageResource(images[i]);
    }

    @Override
    public int getItemCount() {
        return titles.length;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final Context context;
        public ImageView itemImage;
        public TextView itemTitle;
        public TextView itemDetail;

        public ViewHolder(View itemView) {
            super(itemView);
            context = itemView.getContext();
            itemImage = itemView.findViewById(R.id.icon);
            itemTitle = itemView.findViewById(R.id.title);
            itemDetail = itemView.findViewById(R.id.details);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();

                    final Intent intent;

                    switch (getAdapterPosition()) {
                        case 0:
                            intent = new Intent(context, SignToTextActivity.class);
                            break;
                        case 1:
                            intent = new Intent(context, SpeechToTextActivity.class);
                            break;
                        case 2:
                            intent = new Intent(context, ComposeActivity.class);
                            break;
                        case 3:
                            intent = new Intent(context, TutorialActivity.class);
                            break;
                        case 4:
                            intent = new Intent(context, PlayerActivity.class);
                            break;
                        case 5:
                            intent = new Intent(context, GestureActivity.class);
                            break;
                        default:
                            intent = new Intent(context, TutorialActivity.class);
                            break;
                    }
                    context.startActivity(intent);
                    Snackbar.make(v, "Click detected on item " + position,
                            Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();

                }
            });
        }
    }
}