package com.example.radio;

import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

/**
 * 主界面
 */
public class MainActivity extends AppCompatActivity {

    private MediaPlayer mediaPlayer;
    int[] song_id = { R.raw.song1, R.raw.song2, R.raw.song3 };
    String[] song_name = { "小城故事 — 邓丽君", "Bad day - Danier Powter", "Hozier-Take Me to Church" };
    int songs = 0;
    TextView curTime, totalTime, theSong;
    Button play, pause, stop, next, previous;
    static int num = 0;
    SeekBar seekBar;
    int p = 0;
    Spinner spinner;
    ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //初始化音频播放器
        mediaPlayer = new MediaPlayer();
        //初始化控件
        play = findViewById(R.id.play);
        pause = findViewById(R.id.pause);
        stop = findViewById(R.id.stop);
        next = findViewById(R.id.next);
        previous = findViewById(R.id.previous);
        seekBar = findViewById(R.id.mSeekbar);

        curTime = findViewById(R.id.curTime);
        totalTime = findViewById(R.id.totalTime);
        theSong = findViewById(R.id.songname);
        test(num);
        initview();
        initSong();

        /**
         * 播放音频
         */
        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.start();
            }
        });

        /**
         * 暂停音乐
         */
        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.pause();
            }
        });

        /**
         * 停止播放
         */
        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                test(num);
            }
        });

        /**
         * 下一首
         */
        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (num == 2) {
                    num = 0;
                } else {
                    num++;
                }
                test(num);
                int total = mediaPlayer.getDuration() / 1000;
                int curl = mediaPlayer.getCurrentPosition() / 1000;
                curTime.setText(calculateTime(curl));
                totalTime.setText(calculateTime(total));
                mediaPlayer.start();
            }
        });

        /**
         * 上一首
         */
        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (num == 0) {
                    num = 2;
                } else {
                    num--;
                }
                test(num);
                int total = mediaPlayer.getDuration() / 1000;
                int curl = mediaPlayer.getCurrentPosition() / 1000;
                curTime.setText(calculateTime(curl));
                totalTime.setText(calculateTime(total));
                mediaPlayer.start();
            }
        });

        /**
         *  音频播放错误，点击
         */
        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                return true;
            }
        });

        /**
         * 开启一个线程，获取音频的信息（总时长、播放到多少秒、播放到什么位置）
         */
        new Thread(new Runnable() {
            @Override
            public void run() {
                int total = mediaPlayer.getDuration() / 1000;
                int curl = mediaPlayer.getCurrentPosition();
                while (true) {
                    total = mediaPlayer.getDuration() / 1000;
                    curl = mediaPlayer.getCurrentPosition() / 10;
                    curTime.setText(calculateTime(curl / 100));
                    try {
                        Thread.sleep(1000);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    /**
     * 初始化列表
     */
    public void initSong() {
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1,
                        song_name);
        ListView lv_1 = findViewById(R.id.listview);
        lv_1.setAdapter(adapter);
    }

    public void test(int i) {
        theSong = findViewById(R.id.songname);
        theSong.setText(song_name[i]);
        if (mediaPlayer != null) {
            mediaPlayer.stop();
        }
        mediaPlayer = MediaPlayer.create(this, song_id[i]);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                option();
            }
        });
    }

    public void option() {
        Toast.makeText(MainActivity.this, "finish", Toast.LENGTH_SHORT).show();
    }

    /**
     * 初始化控件监听
     */
    public void initview() {
        //获取总时长
        int total = mediaPlayer.getDuration() / 1000;
        //获取当前播放的位置
        int curl = mediaPlayer.getCurrentPosition() / 1000;
        //设置当前显示时间
        curTime.setText(calculateTime(curl));
        //设置显示总时长
        totalTime.setText(calculateTime(total));
        //进度条拖动改变监听
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int total = mediaPlayer.getDuration() / 1000;//获取音乐总时长
                int curl = mediaPlayer.getCurrentPosition() / 1000;//获取当前播放的位置
                curTime.setText(calculateTime(curl));//开始时间
                totalTime.setText(calculateTime(total));//总时长
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(
                        mediaPlayer.getDuration() * seekBar.getProgress() / 100);//在当前位置播放
                curTime.setText(calculateTime(mediaPlayer.getCurrentPosition() / 1000));
            }
        });
    }

    /**
     * 计算播放时长
     * @param time
     * @return
     */
    public String calculateTime(int time) {
        int minute;
        int second;
        if (time > 60) {
            minute = time / 60;
            second = time % 60;
            //判断秒
            if (second >= 0 && second < 10) {
                return "0" + minute + ":" + "0" + second;
            } else {
                return "0" + minute + ":" + second;
            }
        } else if (time < 60) {
            second = time;
            if (second >= 0 && second < 10) {
                return "00:" + "0" + second;
            } else {
                return "00:" + second;
            }
        } else {
            return "01:00";
        }
    }

    /**
     * 界面关闭的时候，执行该方法
     */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.stop();
            mediaPlayer.release();
        }
    }
}