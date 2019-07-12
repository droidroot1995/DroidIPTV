package tk.droidroot.droidiptv.feature.player;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.*;
import android.view.ViewGroup.LayoutParams;
import android.widget.CheckedTextView;
import android.widget.Toast;
import org.videolan.libvlc.IVLCVout;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;
import tk.droidroot.droidiptv.feature.R;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class VLCPlayerActivity extends Fragment implements IVLCVout.OnNewVideoLayoutListener, View.OnClickListener {

    private SurfaceView mSurface;
    private SurfaceHolder mHolder;
    private CheckedTextView mPlayPauseButton;
    private CheckedTextView mFavoritesButton;
    private CheckedTextView mFullscreenButton;

    private static final String TAG = "VLCPlayerActivity";
    public static final String LOCATION = "tk.droidroot.droidiptv.player.VLCPlayerActivity.location";

    private LibVLC mLibVLC;
    private MediaPlayer mMediaPlayer = null;

    private boolean isPaused = false;

    private String mChannelAddress;
    private int mVideoWidth;
    private int mVideoHeight;
    private int mVideoVisibleHeight = 0;
    private int mVideoVisibleWidth = 0;
    private int mVideoSarNum = 0;
    private int mVideoSarDen = 0;

    private int mChangedVideoHeight = 0;
    private int mChangedVideoWidth = 0;

    private float mAspectRatio = 0;

    private static final int videoSizaChanged = -1;

    private static final int SURFACE_AUTO = 0;
    private static final int SURFACE_16_9 = 1;
    private static final int SURFACE_4_3 = 2;
    private static final int SURFACE_FILL = 4;
    private static final int SURFACE_ORIGINAL = 5;

    private int mCurrentSize = SURFACE_AUTO;

    private Context context;

    public static Intent newIntent(Context context, String location) {
        Intent intent = new Intent(context, VLCPlayerActivity.class);
        intent.putExtra(LOCATION, location);
        return intent;
    }

   /* @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player_actvity);

        Intent intent = getIntent();
        mChannelAddress = intent.getExtras().getString(LOCATION);

        Log.d(TAG, "Channel address: " + mChannelAddress);

        mSurface = (SurfaceView) findViewById(R.id.VLCView);
        mSurface.setVisibility(View.VISIBLE);
        mHolder = mSurface.getHolder();

        createPlayer(mChannelAddress);
    }*/

   @Override
   public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
       context = container.getContext();

       View view = inflater.inflate(R.layout.player_actvity, container, false);

       mSurface = (SurfaceView) view.findViewById(R.id.VLCView);
       mSurface.setVisibility(View.VISIBLE);
       mHolder = mSurface.getHolder();

       mPlayPauseButton = (CheckedTextView) view.findViewById(R.id.play_pause);
       mPlayPauseButton.setOnClickListener(this);

       mFullscreenButton = (CheckedTextView) view.findViewById(R.id.fullscreenMode);
       mFullscreenButton.setOnClickListener(this);

       mFavoritesButton = (CheckedTextView) view.findViewById(R.id.addToFavorites);
       mFavoritesButton.setOnClickListener(this);

       //mChannelAddress = savedInstanceState.getString(LOCATION);
       return view;
   }

   @Override
   public void onViewCreated(View view, Bundle savedInstanceState){
       super.onViewCreated(view, savedInstanceState);

       Bundle arguments = this.getArguments();
       if(arguments != null)
           mChannelAddress = arguments.getString(LOCATION);

       /*if(savedInstanceState != null){

       }*/

       //mChannelAddress = savedInstanceState.getString(LOCATION);

       /*mSurface = (SurfaceView) view.findViewById(R.id.VLCView);
       mSurface.setVisibility(View.VISIBLE);
       mHolder = mSurface.getHolder();

       mPlayPauseButton = (CheckedTextView) view.findViewById(R.id.play_pause);
       mPlayPauseButton.setOnClickListener(this);

       mFullscreenButton = (CheckedTextView) view.findViewById(R.id.fullscreenMode);
       mFullscreenButton.setOnClickListener(this);

       mFavoritesButton = (CheckedTextView) view.findViewById(R.id.addToFavorites);
       mFavoritesButton.setOnClickListener(this);*/

       createPlayer(mChannelAddress);

   }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.d(TAG, "mVideoWidth: " + mVideoWidth + ", mVideoHeight: " + mVideoHeight);
        setSize(mVideoWidth, mVideoHeight);
    }

    @Override
    public void onResume(){
        //mMediaPlayer.play();
        super.onResume();
        //createPlayer(mChannelAddress);
    }

    @Override
    public void onPause(){
        releasePlayer();
        super.onPause();
        //releasePlayer();
    }

    @Override
    public void onDestroy(){
        releasePlayer();
        super.onDestroy();
        //releasePlayer();
    }

    @Override
    public void onClick(View v){
       int v_id = v.getId();

       if(v_id == R.id.play_pause){
           /*if (v.getBackground().getConstantState().equals(v.getResources().getDrawable(R.drawable.play))) {
               if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                   v.setBackgroundDrawable(v.getResources().getDrawable(R.drawable.pause));
               } else {
                   v.setBackground(v.getResources().getDrawable(R.drawable.pause));
               }
               play();
           } else if (v.getBackground().getConstantState().equals(v.getResources().getDrawable(R.drawable.pause))) {
               if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                   v.setBackgroundDrawable(v.getResources().getDrawable(R.drawable.play));
               } else {
                   v.setBackground(v.getResources().getDrawable(R.drawable.play));
               }
               pause();
           }*/

           if(isPaused){
               play();
               if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                   v.setBackgroundDrawable(v.getResources().getDrawable(R.drawable.pause));
               } else {
                   v.setBackground(v.getResources().getDrawable(R.drawable.pause));
               }
           }
           else {
               if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN) {
                   v.setBackgroundDrawable(v.getResources().getDrawable(R.drawable.play));
               } else {
                   v.setBackground(v.getResources().getDrawable(R.drawable.play));
               }
               pause();
           }

           Toast.makeText(this.getContext(), "PlayPause clicked", Toast.LENGTH_SHORT).show();

       }
       else if(v_id == R.id.addToFavorites){
           Toast.makeText(this.getContext(), "AddToFavorites clicked", Toast.LENGTH_SHORT).show();
       }
       else if(v_id == R.id.fullscreenMode){
           Toast.makeText(this.getContext(), "FullscreenMode clicked", Toast.LENGTH_SHORT).show();
       }
       else {

       }
    }

    private void pause(){
       if(mMediaPlayer != null) {
           mMediaPlayer.pause();
           isPaused = true;
       }
    }

    private void play(){
       if(mMediaPlayer != null) {

           Media channel = new Media(mLibVLC, Uri.parse(mChannelAddress));
           channel.setHWDecoderEnabled(true, true);
           mMediaPlayer.setMedia(channel);

           mMediaPlayer.play();
           mMediaPlayer.setVideoTrackEnabled((true));

           isPaused = false;

           /*mMediaPlayer.play();
           isPaused = false;*/
       }
    }

    private void setSize(int width, int height){
        switch(mCurrentSize){
            case SURFACE_AUTO:
                mMediaPlayer.setAspectRatio(null);
                mMediaPlayer.setScale(0);
                break;
            case SURFACE_FILL:
                break;
            case SURFACE_ORIGINAL:
                mMediaPlayer.setAspectRatio(null);
                mMediaPlayer.setScale(0);
                break;
            case SURFACE_16_9:
                mMediaPlayer.setAspectRatio("16:9");
                mMediaPlayer.setScale(0);
                break;
            case SURFACE_4_3:
                mMediaPlayer.setAspectRatio("4:3");
                mMediaPlayer.setScale(0);
                break;
        }
    }

    private void setSurfaceSize(int width, int height, float aspectRatio){
        mVideoWidth = width;
        mVideoHeight = height;
        mAspectRatio = aspectRatio;

        this.changeSurfaceSize();
    }

    private void changeSurfaceSize(){
        int sw = mChangedVideoWidth;
        int sh = mChangedVideoHeight;

        //int sw = getWindow().getDecorView().getWidth();
        //int sh = getWindow().getDecorView().getHeight();

        if(sw * sh == 0){
            Log.e(TAG, "Invalid surface size");
            return;
        }

        mMediaPlayer.getVLCVout().setWindowSize(sw, sh);

        LayoutParams lp = mSurface.getLayoutParams();

        if(mVideoWidth * mVideoHeight == 0){
            lp.width = LayoutParams.MATCH_PARENT;
            lp.height = LayoutParams.MATCH_PARENT;

            mSurface.setLayoutParams(lp);

            setSize(sw, sh);
            return;
        }

        double dw = sw;
        double dh = sh;

        final boolean isPortrait = getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT;

        if(sw > sh && isPortrait || sw < sh && !isPortrait){
            dw = sh;
            dh = sw;
        }

        double ar = 0, vw = 0;

        if(mVideoSarDen == mVideoSarNum){
            vw = mVideoVisibleWidth;
            ar = (double)mVideoVisibleWidth/(double)mVideoVisibleHeight;
        }
        else {
            vw = mVideoVisibleWidth * (double)mVideoSarNum / mVideoSarDen;
            ar = vw / mVideoVisibleHeight;
        }

        double dar = dw/dh;

        switch(mCurrentSize){
            case SURFACE_AUTO:
                if(dar < ar)
                    dh = dw / ar;
                else
                    dw = dh*ar;
                break;

            /*case SURFACE_FIT_HORIZONTAL:
                dh = (int) (dw / ar);
                break;

            case SURFACE_FIT_VERTICAL:
                dw = (int) (dh * ar);
                break;*/

            case SURFACE_FILL:
                break;

            case SURFACE_16_9:
                ar =16.0 / 9.0;

                if(dar < ar)
                    dh = dw / ar;
                else
                    dw = dh * ar;
                break;

            case SURFACE_4_3:
                ar = 4.0 / 3.0;
                if(dar < ar)
                    dh = dw / ar;
                else
                    dw = dh * ar;
                break;

            case SURFACE_ORIGINAL:
                dh = mVideoVisibleHeight;
                dw = vw;
                break;
        }
        lp.width = (int)Math.ceil(dw * mVideoWidth / mVideoVisibleWidth);
        lp.height = (int) Math.ceil(dh * mVideoHeight / mVideoVisibleHeight);

        mSurface.setLayoutParams(lp);

        mSurface.invalidate();
    }

    private void createPlayer(String channelAddress){
        releasePlayer();

        try{
            ArrayList<String> options = new ArrayList<String>();
            //options.add("--aout=opensles");
            //options.add("--audio-time-stretch");
            options.add("--android-display");
            options.add("-vvv");

            mLibVLC = new LibVLC(context, options); //this, options);
            //mHolder.setKeepScreenOn(true);
            mSurface.getHolder().setKeepScreenOn(true);

            mMediaPlayer = new MediaPlayer(mLibVLC);
            mMediaPlayer.setEventListener(mPlayerListener);

            final IVLCVout vout = mMediaPlayer.getVLCVout();
            vout.setVideoView(mSurface);

            vout.attachViews();

            Media channel = new Media(mLibVLC, Uri.parse(channelAddress));
            channel.setHWDecoderEnabled(true, true);
            mMediaPlayer.setMedia(channel);

            if(mSurface.getWidth() != 0 && mSurface.getHeight() != 0){
                vout.setWindowSize(mSurface.getWidth(), mSurface.getHeight());
            }

            mMediaPlayer.play();
            mMediaPlayer.setVideoTrackEnabled((true));

            isPaused = false;
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }

    private void releasePlayer(){
        if(mLibVLC == null)
            return;

        mMediaPlayer.stop();
        final IVLCVout vout = mMediaPlayer.getVLCVout();
        vout.detachViews();
        mHolder = null;
        mLibVLC.release();
        mLibVLC = null;

        mMediaPlayer.release();

        mVideoWidth = 0;
        mVideoHeight = 0;
    }

    public void setVideoFormat(int format){
        mCurrentSize = format;
        setSize(mVideoWidth, mVideoHeight);
    }

    /*@Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }*/

    @Override
    public void onNewVideoLayout(IVLCVout vlcVout, int width, int height, int visibleWidth, int visibleHeight, int sarNum, int sarDen) {

        Toast.makeText(this.getContext(), "OnNewVideoLayout", Toast.LENGTH_SHORT).show();

        Log.d("VLCPlayerActivity", "onNewVideoLayout");

        Log.d(TAG, "Width " + width + ", height " + height);

        if(width * height == 0)
            return;

        mVideoHeight = height;
        mVideoWidth = width;

        mChangedVideoHeight = height;
        mChangedVideoWidth = width;

        mVideoVisibleWidth = visibleWidth;
        mVideoVisibleHeight = visibleHeight;

        mVideoSarDen = sarDen;
        mVideoSarNum = sarNum;

        mAspectRatio = (float)width / height;

        changeSurfaceSize();
    }


    private VLCPlayerListener mPlayerListener = new VLCPlayerListener(this);

    private static class VLCPlayerListener implements MediaPlayer.EventListener {
        private WeakReference<VLCPlayerActivity> mOwner;

        public VLCPlayerListener(VLCPlayerActivity owner) {
            mOwner = new WeakReference<VLCPlayerActivity>(owner);
        }

        @Override
        public void onEvent(MediaPlayer.Event event) {
            VLCPlayerActivity player = mOwner.get();
            Log.d(TAG, "Player EVENT");
            switch(event.type) {
                case MediaPlayer.Event.EndReached:
                    Log.d(TAG, "MediaPlayerEndReached");
                    player.releasePlayer();
                    break;
                case MediaPlayer.Event.EncounteredError:
                    Log.d(TAG, "Media Player Error, re-try");
                    //player.releasePlayer();
                    break;
                case MediaPlayer.Event.Playing:
                case MediaPlayer.Event.Paused:
                case MediaPlayer.Event.Stopped:
                default:
                    break;
            }
        }
    }
}
