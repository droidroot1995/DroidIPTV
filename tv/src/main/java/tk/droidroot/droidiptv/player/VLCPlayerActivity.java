package tk.droidroot.droidiptv.player;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.SurfaceTexture;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.*;
import android.view.ViewGroup.LayoutParams;
import org.videolan.libvlc.IVLCVout;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.Media;
import org.videolan.libvlc.MediaPlayer;
import tk.droidroot.droidiptv.R;

import java.lang.ref.WeakReference;
import java.net.URI;
import java.util.ArrayList;

public class VLCPlayerActivity extends Activity implements IVLCVout.OnNewVideoLayoutListener {

    private SurfaceView mSurface;
    private SurfaceHolder mHolder;

    private static final String TAG = "VLCPlayerActivity";
    public static final String LOCATION = "tk.droidroot.droidiptv.player.VLCPlayerActivity.location";

    private LibVLC mLibVLC;
    private MediaPlayer mMediaPlayer = null;

    private String mChannelAddress;
    private int mVideoWidth;
    private int mVideoHeight;
    private int mVideoVisibleHeight = 0;
    private int mVideoVisibleWidth = 0;
    private int mVideoSarNum = 0;
    private int mVideoSarDen = 0;

    private float mAspectRatio = 0;

    private static final int videoSizaChanged = -1;

    @Override
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
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        setSize(mVideoWidth, mVideoHeight);
    }

    @Override
    protected void onResume(){
        //mMediaPlayer.play();
        super.onResume();
        //createPlayer(mChannelAddress);
    }

    @Override
    protected void onPause(){
        releasePlayer();
        super.onPause();
        //releasePlayer();
    }

    @Override
    protected void onDestroy(){
        releasePlayer();
        super.onDestroy();
        //releasePlayer();
    }

    private void setSize(int width, int height){
        mVideoWidth = width;
        mVideoHeight = height;

        if(mVideoWidth*mVideoHeight >= 1)
            return;

        if(mHolder == null || mSurface == null)
            return;

        int w = getWindow().getDecorView().getWidth();
        int h = getWindow().getDecorView().getHeight();

        float videoAR = (float) mVideoWidth / (float) mVideoHeight;
        float screenAR = (float) w / (float) h;

        if(screenAR < videoAR)
            h = (int) (w / videoAR);
        else
            w = (int) (h * videoAR);

        mHolder.setFixedSize(mVideoWidth, mVideoHeight);

        LayoutParams lp = mSurface.getLayoutParams();
        lp.width = w;
        lp.height = h;
        mSurface.setLayoutParams(lp);
        mSurface.invalidate();

    }

    private void createPlayer(String channelAddress){
        releasePlayer();

        try{
            ArrayList<String> options = new ArrayList<String>();
            options.add("--aout=opensles");
            options.add("--audio-time-stretch");
            options.add("-vvv");

            mLibVLC = new LibVLC(this, options);
            mHolder.setKeepScreenOn(true);

            mMediaPlayer = new MediaPlayer(mLibVLC);
            mMediaPlayer.setEventListener(mPlayerListener);

            final IVLCVout vout = mMediaPlayer.getVLCVout();
            vout.setVideoView(mSurface);

            //vout.addCallback(this);
            vout.attachViews();

            Media channel = new Media(mLibVLC, Uri.parse(channelAddress));
            mMediaPlayer.setMedia(channel);
            mMediaPlayer.play();
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
        //vout.removeCallback(this);
        vout.detachViews();
        mHolder = null;
        mLibVLC.release();
        mLibVLC = null;

        mMediaPlayer.release();

        mVideoWidth = 0;
        mVideoHeight = 0;
    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {

    }

    @Override
    public void onNewVideoLayout(IVLCVout vlcVout, int width, int height, int visibleWidth, int visibleHeight, int sarNum, int sarDen) {

        Log.d(TAG, "Width " + width + ", height " + height);

        if(width * height == 0)
            return;

        mVideoHeight = height;
        mVideoWidth = width;

        mVideoVisibleWidth = visibleWidth;
        mVideoVisibleHeight = visibleHeight;

        mVideoSarDen = sarDen;
        mVideoSarNum = sarNum;

        mAspectRatio = (float)width / height;

        setSize(width, height);
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
