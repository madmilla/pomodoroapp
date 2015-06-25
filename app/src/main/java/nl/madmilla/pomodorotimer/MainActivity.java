package nl.madmilla.pomodorotimer;

import android.os.Handler;
import android.os.SystemClock;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.Date;

// TODO: Timeer system
    // Pause system
    // pomodoro count system
    // store times in filesystem or in files.
    // Make a archive?
    // Start pomo
    // stop pomo
    // start 5 min break
    // start 15 min break
    // save status when pause
    // Keep track of real life time.

public class MainActivity extends ActionBarActivity {

    public TextView timeKeeper;
    public Button  btnStart = null;
    public Button  btnPause = null;
    public Button  btnStop = null;

    private TextView started1;
    private TextView started2;
    private TextView started3;
    private TextView started4;
    private TextView ended1;
    private TextView ended2;
    private TextView ended3;
    private TextView ended4;
    private TextView breakstarted1;
    private TextView breakstarted2;
    private TextView breakstarted3;
    private TextView breakstarted4;
    private TextView breakended1;
    private TextView breakended2;
    private TextView breakended3;
    private TextView breakended4;

    private Handler myHandler = new Handler();
    long timeInMillies = 0L;
    long timeSwap = 0L;
    long finalTime = 0L;
    long startTime = 0L;
    int state = 0;
    int currentPomodoro = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        timeKeeper = (TextView) findViewById(R.id.timeKeeper);
        btnStart = (Button) findViewById(R.id.startButton);
        btnPause = (Button) findViewById(R.id.pauseButton);
        btnStop = (Button) findViewById(R.id.stopButton);

        started1 = (TextView) findViewById(R.id.started1);
        started2 = (TextView) findViewById(R.id.started2);
        started3 = (TextView) findViewById(R.id.started3);
        started4 = (TextView) findViewById(R.id.started4);
        breakstarted1 = (TextView) findViewById(R.id.breakstarted1);
        breakstarted2 = (TextView) findViewById(R.id.breakstarted2);
        breakstarted3 = (TextView) findViewById(R.id.breakstarted3);
        breakstarted4 = (TextView) findViewById(R.id.breakstarted4);

        started1 = (TextView) findViewById(R.id.started1);
        started2 = (TextView) findViewById(R.id.started2);
        started3 = (TextView) findViewById(R.id.started3);
        started4 = (TextView) findViewById(R.id.started4);
        breakstarted1 = (TextView) findViewById(R.id.breakstarted1);
        breakstarted2 = (TextView) findViewById(R.id.breakstarted2);
        breakstarted3 = (TextView) findViewById(R.id.breakstarted3);
        breakstarted4 = (TextView) findViewById(R.id.breakstarted4);
        ended1 = (TextView) findViewById(R.id.ended1);
        ended2 = (TextView) findViewById(R.id.ended2);
        ended3 = (TextView) findViewById(R.id.ended3);
        ended4 = (TextView) findViewById(R.id.ended4);
        breakended1 = (TextView) findViewById(R.id.breakended1);
        breakended2 = (TextView) findViewById(R.id.breakended2);
        breakended3 = (TextView) findViewById(R.id.breakended3);
        breakended4 = (TextView) findViewById(R.id.breakended4);



        btnPause.setVisibility(View.INVISIBLE);
        state = 0;
        currentPomodoro = 0;

        btnStart.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                stop();
                if(currentPomodoro == 0){
                    setTableText(0,false);
                }
                startTime = SystemClock.uptimeMillis();
                myHandler.postDelayed(updateTimerMethod, 0);
                btnPause.setVisibility(View.VISIBLE);
                btnStart.setVisibility(View.INVISIBLE);

            }
        });

        btnPause.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                timeKeeper.setText("PAUSED");
                timeSwap += timeInMillies;
                myHandler.removeCallbacks(updateTimerMethod);
                btnPause.setVisibility(View.INVISIBLE);
                btnStart.setVisibility(View.VISIBLE);
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                stop();
                timeKeeper.setText("STOPPED");
                myHandler.removeCallbacks(updateTimerMethod);
                btnPause.setVisibility(View.INVISIBLE);
                btnStart.setVisibility(View.VISIBLE);
            }
        });
    }

    private Runnable updateTimerMethod = new Runnable() {

        public void run() {
            timeInMillies = SystemClock.uptimeMillis() -startTime;

            finalTime = timeSwap + timeInMillies;

            switch(state){
                case 0:
                    finalTime = 1500000 - (timeSwap + timeInMillies); // 25 minutes
                    break;
                case 1:
                    finalTime = 900000 - (timeSwap + timeInMillies); // 15 minutes
                    break;
                case 2:
                    finalTime = 300000 - (timeSwap + timeInMillies); // 5 minutes
                    break;
            }


            int seconds = (int) (finalTime / 1000);
            int minutes = seconds / 60;
            seconds = seconds % 60;
            int milliseconds = (int) (finalTime % 1000);
            timeKeeper.setText("" + minutes + ":"
                    + String.format("%02d", seconds) + ":"
                    + String.format("%03d", milliseconds));

            myHandler.postDelayed(this, 0);
            if(finalTime < 0) {
                Log.w("", "FINALTIME");
                if(state == 0) {

                    if(currentPomodoro >= 3) {
                        state = 1;
                        startTime = SystemClock.uptimeMillis();
                        Log.w("","LONG BREAK");
                    }else{
                        state = 2;
                        startTime = SystemClock.uptimeMillis();
                        Log.w("","SHORT BREAK");
                    }
                    setTableText(currentPomodoro,true);
                    currentPomodoro++;

                }else if(state == 2){
                    state = 0;
                    startTime = SystemClock.uptimeMillis();
                    setTableText(currentPomodoro,false);
                    Log.w("", "Start a single POMODORO");
                }else if(state == 1){
                    setTableText(currentPomodoro,false);
                    myHandler.removeCallbacks(updateTimerMethod);

                    timeKeeper.setText("New pomodoro?");
                    btnPause.setVisibility(View.INVISIBLE);
                    btnStart.setVisibility(View.VISIBLE);

                    currentPomodoro = 0;
                    state = 0;
                    Log.w("", "END POMODORO");
                }
            }
        }
    };
    // Sets the text in the table so the user can see how his pomodoro is going.
    public void setTableText(int cPomo, boolean pause){
        if(pause){
            if(cPomo == 0){
                ended1.setText(""+getTime());
                breakstarted1.setText(""+getTime());
            }else if (cPomo == 1) {
                ended2.setText(""+getTime());
                breakstarted2.setText(""+getTime());
            }else if (cPomo == 2) {
                ended3.setText(""+getTime());
                breakstarted3.setText(""+getTime());
            }else if (cPomo == 3) {
                ended4.setText(""+getTime());
                breakstarted4.setText(""+getTime());
            }
        }else{
            if(cPomo == 0){
                started1.setText(""+getTime());
            }else if (cPomo == 1) {
                started2.setText(""+getTime());
                breakended1.setText(""+getTime());
            }else if (cPomo == 2) {
                started3.setText(""+getTime());
                breakended2.setText(""+getTime());
            }else if (cPomo == 3) {
                started4.setText(""+getTime());
                breakended3.setText(""+getTime());
            }else if (cPomo == 4) {
                breakended4.setText(""+getTime());
                //breakstarted4.setText(""+getTime());
            }

        }

    }

    public String getTime(){
        Date dt = new Date();
        int hours = dt.getHours();
        int min= dt.getMinutes();
        int sec = dt.getSeconds();
        String curTime = String.format("%02d", hours) + ":" + String.format("%02d", min) + ":" + String.format("%02d", sec);
        return curTime;
    }

    //Stop is basically a reset switch for the system.
    public void stop(){
        timeInMillies = 0L;
        timeSwap = 0L;
        finalTime = 0L;
        startTime = 0L;
        state = 0;
        currentPomodoro = 0;
        started1.setText("--:--:--");
        started2.setText("--:--:--");
        started3.setText("--:--:--");
        started4.setText("--:--:--");
        ended1.setText("--:--:--");
        ended2.setText("--:--:--");
        ended3.setText("--:--:--");
        ended4.setText("--:--:--");
        breakstarted1.setText("--:--:--");
        breakstarted2.setText("--:--:--");
        breakstarted3.setText("--:--:--");
        breakstarted4.setText("--:--:--");
        breakended1.setText("--:--:--");
        breakended2.setText("--:--:--");
        breakended3.setText("--:--:--");
        breakended4.setText("--:--:--");
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        //internal values


        savedInstanceState.putInt("cp", currentPomodoro);
        savedInstanceState.putInt("st", state);
        savedInstanceState.putLong("tim", timeInMillies);
        savedInstanceState.putLong("ft", finalTime);
        savedInstanceState.putLong("ts", timeSwap);
        savedInstanceState.putLong("stt", startTime);
      //  String strName = txtName.getText().toString();

        savedInstanceState.putString("s1", started1.getText().toString());
        savedInstanceState.putString("s2", started2.getText().toString());
        savedInstanceState.putString("s3", started3.getText().toString());
        savedInstanceState.putString("s4", started4.getText().toString());
        savedInstanceState.putString("e1", ended1.getText().toString());
        savedInstanceState.putString("e2", ended2.getText().toString());
        savedInstanceState.putString("e3", ended3.getText().toString());
        savedInstanceState.putString("e4", ended4.getText().toString());
        savedInstanceState.putString("bs1", breakstarted1.getText().toString());
        savedInstanceState.putString("bs2", breakstarted2.getText().toString());
        savedInstanceState.putString("bs3", breakstarted3.getText().toString());
        savedInstanceState.putString("bs4", breakstarted4.getText().toString());
        savedInstanceState.putString("be1", breakended1.getText().toString());
        savedInstanceState.putString("be2", breakended2.getText().toString());
        savedInstanceState.putString("be3", breakended3.getText().toString());
        savedInstanceState.putString("be4", breakended4.getText().toString());



    }
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        // Restore UI state from the savedInstanceState.
        // This bundle has also been passed to onCreate.



        currentPomodoro = savedInstanceState.getInt("cp");
        state = savedInstanceState.getInt("st");
        timeInMillies = savedInstanceState.getLong("tim");
        finalTime = savedInstanceState.getLong("ft");
        timeSwap = savedInstanceState.getLong("ts");
        startTime = savedInstanceState.getLong("stt");

        started1.setText(savedInstanceState.getString("s1"));
        started2.setText(savedInstanceState.getString("s2"));
        started3.setText(savedInstanceState.getString("s3"));
        started4.setText(savedInstanceState.getString("s4"));
        ended1.setText(savedInstanceState.getString("e1"));
        ended2.setText(savedInstanceState.getString("e2"));
        ended3.setText(savedInstanceState.getString("e3"));
        ended4.setText(savedInstanceState.getString("e4"));
        breakstarted1.setText(savedInstanceState.getString("bs1"));
        breakstarted2.setText(savedInstanceState.getString("bs2"));
        breakstarted3.setText(savedInstanceState.getString("bs3"));
        breakstarted4.setText(savedInstanceState.getString("bs4"));
        breakended1.setText(savedInstanceState.getString("be1"));
        breakended2.setText(savedInstanceState.getString("be2"));
        breakended3.setText(savedInstanceState.getString("be3"));
        breakended4.setText(savedInstanceState.getString("be4"));
        myHandler.removeCallbacks(updateTimerMethod);
        myHandler.postDelayed(updateTimerMethod, 0);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setPause(View view) {
        // SET enormous paused text
        // Safe time remaining

        timeKeeper.setText("PAUSED");
    }

    public void startPomodoroTimer(View view){
        // Set the timer on 25 minutes
        // Make somehow sure that it has 4 cycles.
        // 25 -> 5 -> 25 -> 5 -> 25 -> 5 -25 -> 15

        timeKeeper.setText("25:00:000");
    }
    public void restartPomodoroTimer(View view){
        // Set the timer on 25 minutes
        // Make somehow sure that it has 4 cycles.
        // 25 -> 5 -> 25 -> 5 -> 25 -> 5 -25 -> 15
        btnPause.setVisibility(View.VISIBLE);
        btnStart.setVisibility(View.INVISIBLE);
    }
}
