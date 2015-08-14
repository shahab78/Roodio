package com.codecanyon.streamradio.v2;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import com.example.flashlight.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

@SuppressLint("ClickableViewAccessibility") public class MainActivity extends FragmentActivity {

    private static DataManager dataManager;
    private static TableLayout UIRadioList;
    private static ArrayList<String> userRadios = new ArrayList<String>();
    private static ViewPager viewPager;
    private static ImageView bufferingIndicator, speaker, startOrStopBtn;
    private static LoadingAnimation bufferingAnimation;
    private static AudioManager audioManager;
    private static TextView radioListLocation, radioListName, radioTitle;
    private ImageView screenChaneButton, plus;
    private boolean runOnce = true;
    private LinearLayout volumeLayout, volumeButton;
    private int volumeStore;
    private ImageView previousBtn, nextBtn;
    private AdView adView;
    private Typeface fontRegular;

    public static void radioListRefresh() {
        dataManager.createRadioListForRadioScreen(UIRadioList, userRadios, radioListName, radioListLocation);
    }

    public static void setViewPagerSwitch() {
        viewPager.setCurrentItem(0, true);
    }

    public static void startBufferingAnimation() {
        bufferingIndicator = MainScreen.getLoadingImage();
        bufferingAnimation = new LoadingAnimation(bufferingIndicator);
        bufferingAnimation.startAnimation();
    }

    public static void stopBufferingAnimation() {
        bufferingIndicator = MainScreen.getLoadingImage();
        bufferingAnimation.clearAnimation();
    }

    public static DataManager getDataManager() {
        return dataManager;
    }

    public static ArrayList<String> getUserRadios() {
        return userRadios;
    }

    public static TextView getRadioListLocation() {
        return radioListLocation;
    }

    public static ImageView getStartOrStopBtn() {
        return startOrStopBtn;
    }

    public static void nextPage() {
        viewPager.setCurrentItem(1, true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        
        if (!io.vov.vitamio.LibsChecker.checkVitamioLibs(this))
            return;

        dataManager = new DataManager(this, "user_radio");
        fontRegular = Typeface.createFromAsset(getAssets(), "fonts/font.otf");
        radioTitle = (TextView) findViewById(R.id.radioTitle);
        radioTitle.setTypeface(fontRegular);

        plus = (ImageView) findViewById(R.id.plus);
        plus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(viewPager.getCurrentItem()==1){
                    final Dialog dialog = new Dialog(MainActivity.this);
                    dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                    dialog.setContentView(R.layout.add_radio_dialog);
                    FrameLayout mainLayout = (FrameLayout) findViewById(R.id.mainLayout);
                    dialog.getWindow().setLayout((int) (mainLayout.getWidth() * 0.8), (int) (mainLayout.getHeight() * 0.4));
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                    final EditText radioListName = (EditText) dialog.getWindow().findViewById(R.id.dialogradioListName);
                    final EditText radioUrl = (EditText) dialog.getWindow().findViewById(R.id.dialogRadioUrl);
                    TextView dialogTitle = (TextView) dialog.getWindow().findViewById(R.id.dialogTitle);
                    Button confirmBtn = (Button) dialog.getWindow().findViewById(R.id.confirm);
                    Button cancelBtn = (Button) dialog.getWindow().findViewById(R.id.cancel);

                    dialogTitle.setTypeface(fontRegular);
                    radioListName.setTypeface(fontRegular);
                    radioUrl.setTypeface(fontRegular);
                    confirmBtn.setTypeface(fontRegular);
                    cancelBtn.setTypeface(fontRegular);

                    radioUrl.addTextChangedListener(new TextWatcher() {
                        private boolean isInAfterTextChanged;
                        @Override
                        public void beforeTextChanged(CharSequence charSequence, int i, int i2, int i3) {

                        }

                        @Override
                        public void onTextChanged(CharSequence charSequence, int i, int i2, int i3) {

                        }

                        @Override
                        public void afterTextChanged(Editable editable) {
                            if (!isInAfterTextChanged) {
                                isInAfterTextChanged = true;
                                if(radioUrl.getText().toString().length()==1){
                                    radioUrl.setText("http://"+editable.toString());
                                    radioUrl.setSelection(radioUrl.getText().length());
                                }else if(radioUrl.getText().toString().length()<7){
                                    radioUrl.setText("http://");
                                    radioUrl.setSelection(radioUrl.getText().length());
                                }
                                else if(!radioUrl.getText().toString().contains("http://")){
                                    radioUrl.setText("http://"+editable.toString());
                                    radioUrl.setSelection(radioUrl.getText().length());
                                }else if(radioUrl.getText().toString().contains("http://")){
                                    String split[]=radioUrl.getText().toString().split("http://");
                                    if(split.length>2){
                                        radioUrl.setText("http://"+radioUrl.getText().toString().replace("http://", ""));
                                        radioUrl.setSelection(radioUrl.getText().length());
                                    }
                                }
                                if(radioUrl.getCurrentTextColor()==Color.RED){
                                    radioUrl.setTextColor(Color.parseColor("#FFAAAAAA"));
                                }
                                isInAfterTextChanged = false;
                            }
                        }
                    });

                    confirmBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (radioListName.getText().length() != 0 && radioUrl.getText().length() != 0) {
                                if(radioUrl.getText().toString().endsWith(".pls") || radioUrl.getText().toString().endsWith(".m3u")){
                                    try {
                                        String url=new FileToUrl().execute(radioUrl.getText().toString()).get();
                                        if(url.toString()=="empty")
                                            radioUrl.setTextColor(Color.RED);
                                        else{
                                            dataManager.addNewRadio(radioListName.getText().toString(), "Own station", url);
                                            radioListRefresh();
                                            dialog.dismiss();
                                        }
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }else if(radioUrl.getText().toString().endsWith(".asx"))
                                    radioUrl.setTextColor(Color.RED);
                                else{
                                    dataManager.addNewRadio(radioListName.getText().toString(), "Own station", radioUrl.getText().toString());
                                    radioListRefresh();
                                    dialog.dismiss();
                                }
                            }
                        }
                    });

                    cancelBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                }
                else System.exit(0);
            }
        });


        TabPagerAdapter tabPageAdapter = new TabPagerAdapter(getSupportFragmentManager());
        viewPager = (ViewPager) findViewById(R.id.pager);
        viewPager.setAdapter(tabPageAdapter);

        viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {
            }

            @Override
            public void onPageSelected(int i) {
                if (viewPager.getCurrentItem() == 0) {
                    radioTitle.setText("Stream Radio");
                    radioTitle.setTextColor(Color.parseColor("#ffee9d53"));
                    plus.setImageResource(R.drawable.exit);
                    screenChaneButton.setImageResource(R.drawable.switch_page);
                    adView.setVisibility(View.INVISIBLE);
                } else {
                    radioTitle.setText("All Stations");
                    radioTitle.setTextColor(Color.parseColor("#ffe51998"));
                    plus.setImageResource(R.drawable.plus);
                    screenChaneButton.setImageResource(R.drawable.back);
                    if(Boolean.parseBoolean(getResources().getString(R.string.admob_true_or_false)))
                        adView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onPageScrollStateChanged(int i) {
            }
        });

        screenChaneButton = (ImageView) findViewById(R.id.nextScreen);
        screenChaneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (viewPager.getCurrentItem() == 0) viewPager.setCurrentItem(1, true);
                else viewPager.setCurrentItem(0, true);
            }
        });
        speaker = (ImageView) findViewById(R.id.speaker);
        speaker.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (audioManager.getStreamVolume(AudioManager.STREAM_MUSIC) == 0) {
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volumeStore, volumeStore);
                    defaultVolumeBarPosition(audioManager, volumeLayout, volumeButton);
                } else {
                    volumeStore = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
                    defaultVolumeBarPosition(audioManager, volumeLayout, volumeButton);
                }
                return false;
            }
        });
        volumeLayout = (LinearLayout) findViewById(R.id.linearLayout_t);
        volumeButton = (LinearLayout) findViewById(R.id.button_t);
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);

        registerReceiver(new HeadsetReceiver(getApplicationContext()), new IntentFilter(Intent.ACTION_HEADSET_PLUG));
        TelephonyManager telephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        telephonyManager.listen(new PhoneCallListener(), PhoneStateListener.LISTEN_CALL_STATE);

        previousBtn = (ImageView) findViewById(R.id.previous_btn);
        nextBtn = (ImageView) findViewById(R.id.next_btn);
        startOrStopBtn = (ImageView) findViewById(R.id.start_or_stop_btn);


        adView = (AdView) this.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
        adView.setVisibility(View.INVISIBLE);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)
            defaultVolumeBarPosition(audioManager, volumeLayout, volumeButton);
        else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP)
            defaultVolumeBarPosition(audioManager, volumeLayout, volumeButton);
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)
            defaultVolumeBarPosition(audioManager, volumeLayout, volumeButton);
        else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP)
            defaultVolumeBarPosition(audioManager, volumeLayout, volumeButton);
        else if (keyCode == KeyEvent.KEYCODE_BACK) {
            moveTaskToBack(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        // TODO Auto-generated method stub
        super.onWindowFocusChanged(hasFocus);
        if (runOnce) {
            UIRadioList = (TableLayout) findViewById(R.id.radioListUi);
            radioListName = (TextView) findViewById(R.id.mainRadioName);
            radioListLocation = (TextView) findViewById(R.id.mainRadioLocation);
            startWallpaperAnimation();
            radioListRefresh();
            volumeBarReaction(volumeLayout, volumeButton, audioManager);
            new MusicPlayerControl(previousBtn, startOrStopBtn, nextBtn, radioListLocation, radioListName).setOnTouchListeners();
            connectionDialog(isOnline());
            FrameLayout mainLayout = (FrameLayout) findViewById(R.id.mainLayout);
            RadioList.declarateDialog(this, (int) (mainLayout.getWidth() * 0.8), (int) (mainLayout.getHeight() * 0.3));
            runOnce = false;

        }
        defaultVolumeBarPosition(audioManager, volumeLayout, volumeButton);
    }

    private void startWallpaperAnimation() {
        Point size = new Point();
        Display display = getWindowManager().getDefaultDisplay();
        display.getSize(size);
        LinearLayout picture = (LinearLayout) findViewById(R.id.wallpaper);
        TranslateAnimation animation = new TranslateAnimation(0, 0 - (picture.getWidth() - size.x), 0, 0);
        animation.setInterpolator(new LinearInterpolator());
        animation.setDuration(200000);
        animation.setFillAfter(true);
        animation.setRepeatMode(Animation.REVERSE);
        animation.setRepeatCount(Animation.INFINITE);
        picture.startAnimation(animation);
    }

    public void defaultVolumeBarPosition(AudioManager audioManager, LinearLayout volumeLayout, LinearLayout volumeButton) {
        float actual = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        float max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        float endPoint = volumeLayout.getWidth() - volumeButton.getWidth();
        volumeButton.setX((endPoint / max * actual));
        if (volumeButton.getX() == 0)
            speaker.setImageResource(R.drawable.volume_muted);
        else speaker.setImageResource(R.drawable.volume_on);
    }

    @SuppressLint("ClickableViewAccessibility") public void volumeBarReaction(final LinearLayout volumeLayout, final LinearLayout volumeButton, final AudioManager audioManager) {

        volumeLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                float max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
                float endPoint = volumeLayout.getWidth() - volumeButton.getWidth();
                volumeButton.setX(motionEvent.getX()-volumeButton.getWidth()/2);

                if (volumeButton.getX() >= 0) {
                    float pos = volumeButton.getX() / (endPoint / max);
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, (int) pos, 0);
                }
                if (volumeButton.getX() >= endPoint) {
                    volumeButton.setX(endPoint);
                }
                if (volumeButton.getX() <= 0) {
                    volumeButton.setX(0);
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 0);
                    speaker.setImageResource(R.drawable.volume_muted);
                }
                else speaker.setImageResource(R.drawable.volume_on);
                return true;
            }
        });
    }

    public boolean isOnline() {
        ConnectivityManager cm =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        else return false;
    }

    public void connectionDialog(boolean isOnline){
        if(!isOnline){
            final Dialog dialog = new Dialog(MainActivity.this);
            dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
            dialog.setContentView(R.layout.without_internet);
            FrameLayout mainLayout = (FrameLayout) findViewById(R.id.mainLayout);
            dialog.getWindow().setLayout((int) (mainLayout.getWidth() * 0.8), (int) (mainLayout.getHeight() * 0.45));
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
            Button retryBtn = (Button) dialog.getWindow().findViewById(R.id.retry);
            retryBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(!isOnline()){
                        dialog.dismiss();
                        dialog.show();
                    }else{
                        dialog.dismiss();
                        radioListRefresh();
                    }
                }
            });
            dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                @Override
                public void onCancel(DialogInterface dialogInterface) {
                    if(!isOnline()){
                        dialog.dismiss();
                        dialog.show();
                    }
                }
            });
            dialog.show();
    }
    }

    public class FileToUrl extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... strings) {
            String radioURL = "empty";
            try {
                URL url = new URL(strings[0]);
                URLConnection uc = url.openConnection();
                BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream()));
                String inputLine;
                while ((inputLine = in.readLine()) != null) {
                    System.out.println(inputLine);
                    if (inputLine.contains("http://")) {
                        String[] fields = inputLine.split("http://");
                        radioURL = "http://" + fields[1];
                        in.close();
                        break;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return radioURL;
        }
    }
}


