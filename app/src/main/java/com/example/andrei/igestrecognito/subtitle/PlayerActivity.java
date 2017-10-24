package com.example.andrei.igestrecognito.subtitle;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

import android.app.Activity;
import android.graphics.Typeface;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnVideoSizeChangedListener;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.example.andrei.igestrecognito.R;
import com.example.andrei.igestrecognito.subtitle.utils.Caption;
import com.example.andrei.igestrecognito.subtitle.utils.FormatSRT;
import com.example.andrei.igestrecognito.subtitle.utils.TimedTextObject;

public class PlayerActivity extends Activity implements SurfaceHolder.Callback,
        OnSeekBarChangeListener {

    private static final String TAG = "PlayerActivity";
    public static final int FADE_OUT = 0;
    public static final int SHOW_PROGRESS = 1;
    private SurfaceView sv;
    private MediaPlayer player;
    private TextView subtitleText;
    private TextView plainText;
    private SubtitleProcessingTask subsFetchTask;
    private SeekBar mSeeker;
    AudioManager audioManager;
    private MessageHandler mHandler;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        setContentView(R.layout.activity_player);

        sv = (SurfaceView) findViewById(R.id.svMain);
        sv.getHolder().addCallback(this);
        sv.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        subtitleText = (TextView) findViewById(R.id.offLine_subtitleText);
        plainText = (TextView) findViewById(R.id.normalText);
        Typeface tf = Typeface.createFromAsset(getAssets(),"fonts/GallaudetRegular.ttf");
        subtitleText.setTypeface(tf);
        mSeeker = (SeekBar) findViewById(R.id.seeker);
        mHandler = new MessageHandler();
        mSeeker.setOnSeekBarChangeListener(this);
		/*
		 * Adjust subtitles margin based on Screen dimes
		 */
        FrameLayout.LayoutParams rl2 = (FrameLayout.LayoutParams) subtitleText
                .getLayoutParams();
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        rl2.bottomMargin = (int) (dm.heightPixels * 0.08);
        subtitleText.setLayoutParams(rl2);

        FrameLayout.LayoutParams rl3 = (FrameLayout.LayoutParams) plainText
                .getLayoutParams();
        DisplayMetrics dm2 = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm2);
        rl2.bottomMargin = (int) (dm2.heightPixels * 0.08);
        plainText.setLayoutParams(rl3);
    }

    @Override
    protected void onPause() {
        if (subtitleDisplayHandler != null) {
            subtitleDisplayHandler.removeCallbacks(subtitleProcessesor);
            subtitleDisplayHandler = null;
            if (subsFetchTask != null)
                subsFetchTask.cancel(true);
        }
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {

        runOnUiThread(new Runnable() {
            public void run() {
                playVideo();
            }
        });

    }

    private void playVideo() {
        try {
            player = new MediaPlayer();
            player.setAudioStreamType(AudioManager.STREAM_MUSIC);
            player.reset();
            player.setDataSource(
                    getApplicationContext(),
                    Uri.parse("android.resource://" + getPackageName() + "/"
                            + R.raw.linux));
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null == player) {
                Toast.makeText(getApplicationContext(), "Error",
                        Toast.LENGTH_SHORT).show();
                return;
            }
        }
        player.setDisplay(sv.getHolder());
        player.setOnPreparedListener(new OnPreparedListener() {

            @Override
            public void onPrepared(MediaPlayer mp) {
                subsFetchTask = new SubtitleProcessingTask();
                subsFetchTask.execute();
                player.start();
                mHandler.sendEmptyMessage(SHOW_PROGRESS);
            }
        });
        player.setOnCompletionListener(new OnCompletionListener() {

            @Override
            public void onCompletion(MediaPlayer mp) {
                finish();
            }
        });
        player.prepareAsync();

        player.setOnVideoSizeChangedListener(new OnVideoSizeChangedListener() {

            @Override
            public void onVideoSizeChanged(MediaPlayer mp, int videoWidth,
                                           int videoHeight) {

                // Get the width of the screen
                int screenWidth = getWindowManager().getDefaultDisplay()
                        .getWidth();
                int screenHeight = getWindowManager().getDefaultDisplay()
                        .getHeight();

                // Get the SurfaceView layout parameters
                android.view.ViewGroup.LayoutParams lp = sv.getLayoutParams();

                int displayHeight = (int) (((float) videoHeight / (float) videoWidth) * (float) screenWidth);
                int displayWidth;
                if (displayHeight > screenHeight) {
                    displayHeight = screenHeight;
                    displayWidth = (int) (((float) videoWidth / (float) videoHeight) * (float) screenHeight);
                } else {
                    displayWidth = screenWidth;
                }

                lp.width = displayWidth;

			    lp.height = displayHeight;

                sv.setLayoutParams(lp);

            }
        });
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width,
                               int height) {
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
    }

    @Override
    public void finish() {
        cleanUp();
        super.finish();
    }

    private void cleanUp() {
        if (subtitleDisplayHandler != null) {
            subtitleDisplayHandler.removeCallbacks(subtitleProcessesor);
        }
        if (player != null) {
            player.stop();
            player.release();
            player = null;
        }
    }

    public TimedTextObject srt;
    private Runnable subtitleProcessesor = new Runnable() {

        @Override
        public void run() {
            if (player != null && player.isPlaying()) {
                int currentPos = player.getCurrentPosition();
                Collection<Caption> subtitles = srt.captions.values();
                for (Caption caption : subtitles) {
                    if (currentPos >= caption.start.mseconds
                            && currentPos <= caption.end.mseconds) {
                        onTimedText(caption);
                        break;
                    } else if (currentPos > caption.end.mseconds) {
                        onTimedText(null);
                    }
                }
            }
            subtitleDisplayHandler.postDelayed(this, 100);
        }
    };
    private Handler subtitleDisplayHandler = new Handler();
    private boolean mDragging;

    public class SubtitleProcessingTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            plainText.setText("Loading subtitles..");
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... params) {

            try {
                InputStream stream = getResources().openRawResource(
                        R.raw.sub);
                FormatSRT formatSRT = new FormatSRT();
                srt = formatSRT.parseFile("sample.srt", stream);

            } catch (Exception e) {
                e.printStackTrace();
                Log.e(TAG, "error in downloadinf subs");
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            if (null != srt) {
                subtitleText.setText("");
                plainText.setText("");
                Toast.makeText(getApplicationContext(), "subtitles loaded!!",
                        Toast.LENGTH_SHORT).show();
                subtitleDisplayHandler.post(subtitleProcessesor);
            }
            super.onPostExecute(result);
        }
    }

    public void onTimedText(Caption text) {
        if (text == null) {
            subtitleText.setVisibility(View.INVISIBLE);
            plainText.setVisibility(View.INVISIBLE);
            return;
        }
        subtitleText.setText(Html.fromHtml(text.content));
        subtitleText.setVisibility(View.VISIBLE);

        plainText.setText(Html.fromHtml(text.content));
        plainText.setVisibility(View.VISIBLE);
    }

    public File getExternalFile() {
        File srt = null;
        try {
            srt = new File(getApplicationContext().getExternalFilesDir(null)
                    .getPath() + "/sample.srt");
            srt.createNewFile();
            return srt;
        } catch (Exception e) {
            Log.e(TAG, "exception in file creation");
        }
        return null;
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress,
                                  boolean fromUser) {
        if (player == null) {
            return;
        }

        if (!fromUser) {
            // We're not interested in programmatically generated changes to
            // the progress bar's position.
            return;
        }

        long duration = player.getDuration();
        if (duration == -1)
            return;
        long newposition = (duration * progress) / 1000L;
        player.seekTo((int) newposition);
    }

    private int setProgress() {
        if (player == null || mDragging) {
            return 0;
        }

        int position = player.getCurrentPosition();
        if (position == -1)
            return 0;
        int duration = player.getDuration();
        if (mSeeker != null && duration > 0) {
            long pos = 1000L * position / duration;
            mSeeker.setProgress((int) pos);
        }

        return position;
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        mDragging = true;
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        mDragging = false;
        setProgress();
    }

    private class MessageHandler extends Handler {

        @Override
        public void handleMessage(Message msg) {
            if (player == null) {
                return;
            }

            int pos;
            switch (msg.what) {
                case FADE_OUT:
                    break;
                case SHOW_PROGRESS:
                    try {
                        pos = setProgress();
                    } catch (IllegalStateException ise) {
                        ise.printStackTrace();
                        break;
                    }
                    if (!mDragging && player.isPlaying()) {
                        msg = obtainMessage(SHOW_PROGRESS);
                        sendMessageDelayed(msg, 1000 - (pos % 1000));
                    }
                    break;
            }
        }
    }
}