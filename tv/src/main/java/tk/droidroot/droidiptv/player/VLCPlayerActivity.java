package tk.droidroot.droidiptv.player;

import android.app.Activity;
import android.graphics.SurfaceTexture;
import android.os.Bundle;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.TextureView;
import org.videolan.libvlc.IVLCVout;
import org.videolan.libvlc.LibVLC;
import org.videolan.libvlc.MediaPlayer;
import tk.droidroot.droidiptv.R;

public class VLCPlayerActivity extends Activity implements IVLCVout {

    private SurfaceView mSurface;
    private SurfaceHolder mHolder;

    private LibVLC mLibVLC;
    private MediaPlayer mMediaPlayer = null;
    private int mVideoWidth;
    private int mVideoHeight;

    private static final int videoSizaChanged = -1;

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.player_actvity);

        mSurface = (SurfaceView) findViewById(R.id.VLCView);
        mHolder = mSurface.getHolder();
    }

    @Override
    protected void onResume(){
        super.onResume();
    }

    @Override
    protected void onPause(){
        super.onPause();
    }

    @Override
    protected void onDestroy(){
        super.onDestroy();
    }

    private void createPlayer(){

    }

    private void releasePlayer(){

    }

    @Override
    public void setVideoView(SurfaceView videoSurfaceView) {

    }

    @Override
    public void setVideoView(TextureView videoTextureView) {

    }

    @Override
    public void setVideoSurface(Surface videoSurface, SurfaceHolder surfaceHolder) {

    }

    @Override
    public void setVideoSurface(SurfaceTexture videoSurfaceTexture) {

    }

    @Override
    public void setSubtitlesView(SurfaceView subtitlesSurfaceView) {

    }

    @Override
    public void setSubtitlesView(TextureView subtitlesTextureView) {

    }

    @Override
    public void setSubtitlesSurface(Surface subtitlesSurface, SurfaceHolder surfaceHolder) {

    }

    @Override
    public void setSubtitlesSurface(SurfaceTexture subtitlesSurfaceTexture) {

    }

    @Override
    public void attachViews(OnNewVideoLayoutListener onNewVideoLayoutListener) {

    }

    @Override
    public void attachViews() {

    }

    @Override
    public void detachViews() {

    }

    @Override
    public boolean areViewsAttached() {
        return false;
    }

    @Override
    public void addCallback(Callback callback) {

    }

    @Override
    public void removeCallback(Callback callback) {

    }

    @Override
    public void sendMouseEvent(int action, int button, int x, int y) {

    }

    @Override
    public void setWindowSize(int width, int height) {

    }
}
