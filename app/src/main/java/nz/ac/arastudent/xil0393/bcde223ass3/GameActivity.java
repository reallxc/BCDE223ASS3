package nz.ac.arastudent.xil0393.bcde223ass3;

import android.animation.ObjectAnimator;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.SystemClock;
import android.provider.MediaStore;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class GameActivity extends AppCompatActivity {
    private static final String TAG = "GameActivity";
    private int[][] mazeMap;
    private GameModel game;
    private ImageView playerLogo;
    private int boardSize, startCellID, goalReached = 0, gameRunning = 0;
    private long timeWhenPaused;
    private List<Integer> tvIDPath = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Intent intent = getIntent();
        int gameDifficulty = intent.getIntExtra(MainActivity.DIFFICULTY,0) + 1;

        this.game = new GameModel(gameDifficulty);
        this.mazeMap = this.game.board.board;
        this.boardSize = this.game.board.getWidth();
        this.playerLogo = findViewById(R.id.imagePlayer);

        String datetimeSuffix = "";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
            LocalDateTime now = LocalDateTime.now();
            datetimeSuffix = dtf.format(now);
        } else {
            datetimeSuffix = "unavailable";
        }
        TextView textStatus = findViewById(R.id.textStatus);
        textStatus.setText("Level: d" + gameDifficulty + "s" + boardSize + "d" + datetimeSuffix);
        TextView textBestStep = findViewById(R.id.textBestStep);
        textBestStep.setText("Best Solution: " + this.game.cPlayer.successPath.getSteps() + " steps");
        TextView textNumberOfGoal = findViewById(R.id.textNumberOfGoal);
        textNumberOfGoal.setText("0 of " + this.game.board.numberOfGoal + " goal(s) reached");

        drawBoard();
    }

    @Override
    protected void onStart() {
        super.onStart();
        this.createTimer();
        this.gameRunning = 1;
    }

    private void drawBoard() {
        TableLayout gameTable = findViewById(R.id.tableGame);
        int screenWidth = Resources.getSystem().getDisplayMetrics().widthPixels;
        int cellSize = screenWidth/this.boardSize;
        Log.i(TAG, "Cell size:" + screenWidth + ":" + cellSize + ":" + this.boardSize);
        for (int i=0;i<this.mazeMap.length;i++) {
            TableRow tbr = new TableRow(this);
            for (int j=0;j<this.mazeMap[0].length;j++) {
                TextView tv = new TextView(this);
                int tvID = View.generateViewId();
                int posNumber = this.mazeMap[i][j];
                tv.setId(tvID);
                tv.setText("" + posNumber);
                //Set cell size
                tv.setWidth(cellSize);
                tv.setHeight(cellSize);
                //Set cell font size
                int textSizeInSp = (int) getResources().getDimension(R.dimen.cell_font_size);
                tv.setTextSize(convertSpToPixels(textSizeInSp , getApplicationContext()));
                //Set alignment
                tv.setGravity(Gravity.CENTER);
                //Set background color
                if (posNumber==0) {
                    tv.setBackgroundColor(Color.RED);
                } else {
                    tv.setBackgroundColor(Color.GRAY);
                }

                int finalI = i;
                int finalJ = j;
                tv.setOnClickListener(v -> {
                    try {
                        clickPosition(finalI, finalJ, posNumber, tvID);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
                tbr.addView(tv);
                if (i==0&&j==0) {
                    this.startCellID = tvID;
                }
            }
            gameTable.addView(tbr);
        }
        //Put player logo on start cell
        this.tvIDPath.add(this.startCellID);
        final TextView startCell = findViewById(startCellID);
        startCell.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                startCell.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                movePlayerLogo(startCellID);
            }
        });
    }

    private void clickPosition(int x, int y, int number, int cellID) throws IOException {
        Log.i(TAG, "Clicked " + x + ":" + y +", Number: " + number);
        if (this.game.board.getPositionNumber(this.game.humanPlayer.currentPosition)!=0) {
            if (game.humanPlayer.movePlayer(x,y)) {
                movePlayerLogo(cellID);
                updateStepLabel();
                this.tvIDPath.add(cellID);
                if (this.game.board.isGoalPosition(this.game.humanPlayer.currentPosition)) {
                    this.goalReached += 1;
                    this.playWinAudio();
                    this.stopTimer();
                    Button btnPlayback = (Button) findViewById(R.id.btnPlayback);
                    btnPlayback.setVisibility(View.VISIBLE);
                    Log.i(TAG, "WIN!");
                    TextView textNumberOfGoal = findViewById(R.id.textNumberOfGoal);
                    textNumberOfGoal.setText(this.goalReached + " of " + this.game.board.numberOfGoal + " goal(s) reached");
                    String dialogMsg = "You reached the goal position in " + this.game.humanPlayer.myPath.getSteps() + " steps";
                    if (this.game.checkBestPath()) {
                        dialogMsg += ", you had the best solution!";
                    } else {
                        dialogMsg += ", but your solution is not the best solution(" + this.game.cPlayer.successPath.getSteps() + " steps).";
                    }
                    new AlertDialog.Builder(this)
                            .setTitle("You WIN!")
                            .setMessage(dialogMsg)
                            .setNegativeButton("OK", null)
                            .show();
                }
                Log.i(TAG, "Moved to new position");
            } else {
                this.playErrorAudio();
                Log.i(TAG, "Game rule fail");
                Snackbar.make(findViewById(R.id.tableGame), "You can only move exact number of squares horizontally or vertically in a straight line.", Snackbar.LENGTH_LONG).show();
            }
        } else {
            Log.i(TAG, "On goal position, can not move");
        }
    }

    private void movePlayerLogo(int tvID) {
        TextView tv = findViewById(tvID);
        TableRow tbr = (TableRow) tv.getParent();
        TableLayout tbl = findViewById(R.id.tableGame);
        float y = tbl.getY() + tbr.getY();
        float x = tv.getX();
        Log.i(TAG, "tv size: " + tv.getWidth() + ":" + tv.getHeight());
        x = x + tv.getWidth()/2;
        y = y + tv.getHeight()/2;
        Log.i(TAG, "tv Position: " + x + ":" + y);
        ObjectAnimator animatorX = ObjectAnimator.ofFloat(this.playerLogo, View.X, x);
        animatorX.setDuration(250);
        ObjectAnimator animatorY = ObjectAnimator.ofFloat(this.playerLogo, View.Y, y);
        animatorY.setDuration(250);
// set all the animation-related stuff you want (interpolator etc.)
        animatorX.start();
        animatorY.start();
//        this.playerLogo.setX(x);
//        this.playerLogo.setY(y);
    }

    public void playbackGame(View view) {
        for (int i=0;i<=this.game.humanPlayer.myPath.getSteps();i++) {
            this.movePlayerLogo(this.tvIDPath.get(i));
        }
    }

    public void restartGame(View view) {
        Chronometer timer = (Chronometer) findViewById(R.id.textTimer);
        timer.setBase(SystemClock.elapsedRealtime());
        this.game.restartGame();
        this.timeWhenPaused = 0;
        this.gameRunning = 1;
        movePlayerLogo(startCellID);
        this.tvIDPath.clear();
        this.tvIDPath.add(this.startCellID);
        updateStepLabel();
        timer.start();
    }

    public void pauseGame(View view) {
        TableLayout gameTable = findViewById(R.id.tableGame);
        Chronometer timer = (Chronometer) findViewById(R.id.textTimer);
        Button btn = (Button) findViewById(R.id.btnPause);
        if (this.gameRunning == 1) {
            gameTable.setVisibility(View.INVISIBLE);
            btn.setText("Resume");
            this.timeWhenPaused = timer.getBase() - SystemClock.elapsedRealtime();
            timer.stop();
            this.gameRunning = 0;
        } else {
            gameTable.setVisibility(View.VISIBLE);
            btn.setText("Pause");
            timer.setBase(SystemClock.elapsedRealtime() + this.timeWhenPaused);
            timer.start();
            this.gameRunning = 1;
        }
    }

    public void undoStep(View view) {
        if (this.game.humanPlayer.myPath.getSteps()>0) {
            this.movePlayerLogo(this.tvIDPath.remove(this.game.humanPlayer.myPath.getSteps()-1));
            this.game.humanPlayer.currentPosition = this.game.humanPlayer.myPath.goBackward();
            updateStepLabel();
        }
    }

    private void updateStepLabel() {
        int step = this.game.humanPlayer.myPath.getSteps();
        TextView textStepCount = findViewById(R.id.textStepCount);
        textStepCount.setText("Steps: " + step);
    }

    public static float convertSpToPixels(float sp, Context context) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics());
    }

    private void createTimer() {
        Chronometer timer = (Chronometer) findViewById(R.id.textTimer);
        timer.start();
    }

    private void stopTimer() {
        Chronometer timer = (Chronometer) findViewById(R.id.textTimer);
        timer.stop();
    }

    private void playWinAudio() throws IOException {
        MediaPlayer mp = MediaPlayer.create(this, R.raw.win);
        mp.start();
    }

    private void playErrorAudio() throws IOException {
        MediaPlayer mp = MediaPlayer.create(this, R.raw.error);
        mp.start();
    }


}
