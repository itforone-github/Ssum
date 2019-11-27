package kr.foryou.ssum;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class RecordActivity extends AppCompatActivity {
    LinearLayout btnLayout;
    Button successBtn,listenBtn;
    ImageView playImg;
    TextView timeTxt;
    int milliSecond=0;
    String secondStr="";
    String minStr="";
    int second=0;
    int min=0;
    boolean recordBoolean=false;
    MediaRecorder recorder=null;
    MediaPlayer player;
    String recordPath;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_record);
        successBtn=(Button)findViewById(R.id.successBtn);
        listenBtn=(Button)findViewById(R.id.listenBtn);
        timeTxt=(TextView)findViewById(R.id.timeTxt);
        playImg=(ImageView)findViewById(R.id.playImg);
        btnLayout=(LinearLayout)findViewById(R.id.btnLayout);
        playImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(recordBoolean==false) {
                    playImg.setSelected(true);
                    playImg.setImageResource(R.drawable.btn_stop);
                    recordBoolean=true;

                    if(recorder!=null){
                        recorder.stop();
                        recorder.release();
                        recorder=null;
                    }
                    recorder=new MediaRecorder();
                    recordPath= Environment.getExternalStorageDirectory().getAbsolutePath()+"/voice.mp4";
                    recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                    recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                    recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
                    recorder.setAudioSamplingRate(44100);
                    recorder.setAudioEncodingBitRate(96000);
                    recorder.setOutputFile(recordPath);
                    btnLayout.setVisibility(View.GONE);
                    try {

                        recorder.prepare();
                        recorder.start();
                    }catch (Exception e){
                        Log.d("error",e.toString());
                        e.printStackTrace();
                    }


                    mHandler.sendEmptyMessage(0);
                }else{
                    playImg.setSelected(false);
                    playImg.setImageResource(R.drawable.record_selector);
                    recordBoolean=false;
                    min=0;
                    second=0;
                    milliSecond=0;
                    if(recorder==null)
                        return;
                    //녹음정지
                    recorder.stop();
                    recorder.release();
                    recorder=null;

                    mHandler.removeMessages(0);
                    btnLayout.setVisibility(View.VISIBLE);
                }
            }
        });
        successBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //여기에 값을 넘기기
                Intent intent=new Intent();
                intent.putExtra("filePath",recordPath);
                setResult(RESULT_OK,intent);
                finish();
            }
        });
        listenBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (player != null) {
                    player.stop();
                    player.release();
                    player = null;
                }
                try {

                    // 오디오를 플레이 하기위해 MediaPlayer 객체 player를 생성한다.
                    player = new MediaPlayer ();

                    // 재생할 오디오 파일 저장위치를 설정
                    player.setDataSource(recordPath);
                    // 웹상에 있는 오디오 파일을 재생할때
                    // player.setDataSource(Audio_Url);

                    // 오디오 재생준비,시작
                    player.prepare();
                    player.start();
                } catch (Exception e) {
                    Log.e("SampleAudioRecorder", "Audio play failed.", e);
                }
            }
        });

        /*
        success.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(recordBoolean==false) {
                    recordBoolean=true;

                    if(recorder!=null){
                        recorder.stop();
                        recorder.release();
                        recorder=null;
                    }
                    recorder=new MediaRecorder();
                    recordPath= Environment.getExternalStorageDirectory().getAbsolutePath()+"/voice.mp4";
                    recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
                    recorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
                    recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
                    recorder.setOutputFile(recordPath);

                    try {

                        recorder.prepare();
                        recorder.start();
                    }catch (Exception e){
                        Log.d("error",e.toString());
                        e.printStackTrace();
                    }
                    playBtn.setText("녹음정지");
                    mHandler.sendEmptyMessage(0);
                }else{
                    recordBoolean=false;
                    min=0;
                    second=0;
                    milliSecond=0;
                    if(recorder==null)
                        return;
                    //녹음정지
                    recorder.stop();
                    recorder.release();
                    recorder=null;
                    playBtn.setText("녹음하기");
                    mHandler.removeMessages(0);
                }
            }
        });*/
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        recordBoolean=false;
        min=0;
        second=0;
        milliSecond=0;
        if(recorder==null)
            return;
        //녹음정지
        recorder.stop();
        recorder.release();
        recorder=null;
        mHandler.removeMessages(0);
    }

    Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            milliSecond++;
            Log.d("time-mill-second",milliSecond%1000+"");
            if(milliSecond==100){
                second++;
                milliSecond=0;
            }
            if(second==60){
                min++;
                second=0;
            }
            if(second<10){
                secondStr="0"+second;
            }else{
                secondStr=second+"";
            }
            if(min<10){
                minStr="0"+min;
            }else{
                minStr=min+"";
            }
            timeTxt.setText(minStr+":"+secondStr+"."+milliSecond);

            mHandler.sendEmptyMessageDelayed(0,1);
        }
    };

}
