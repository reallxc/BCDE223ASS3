package nz.ac.arastudent.xil0393.bcde223helloworld;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;
import org.w3c.dom.Text;

public class GameActivity extends AppCompatActivity {
    private static final String TAG = "GameActivity";
    private int[][] mazeMap;
    private GameModel game;
    private ImageView playerLogo;
    private int boardSize, startCellID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        Intent intent = getIntent();
        int gameDifficulty = intent.getIntExtra(MainActivity.DIFFICULTY,0) + 1;

        TextView textView = findViewById(R.id.textStatus);
        textView.setText("Difficulty: " + gameDifficulty);

        this.game = new GameModel(gameDifficulty);
        this.mazeMap = this.game.board.board;
        this.boardSize = this.game.board.getWidth();
        this.playerLogo = findViewById(R.id.imagePlayer);

        drawBoard();
    }

    @Override
    protected void onStart() {
        super.onStart();
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
                tv.setBackgroundColor(Color.GRAY);
                int finalI = i;
                int finalJ = j;
                tv.setOnClickListener(v -> clickPosition(finalI, finalJ, posNumber, tvID));
                tbr.addView(tv);
                if (i==0&&j==0) {
                    this.startCellID = tvID;
                }
            }
            gameTable.addView(tbr);
        }
        //Put player logo on start cell
        final TextView startCell = findViewById(startCellID);
        startCell.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                startCell.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                movePlayerLogo(startCellID);
            }
        });
    }

    private void clickPosition(int x, int y, int number, int cellID) {
        Log.i(TAG, "Clicked " + x + ":" + y +", Number: " + number);
        if (this.game.board.getPositionNumber(this.game.humanPlayer.currentPosition)!=0) {
            if (game.humanPlayer.movePlayer(x,y)) {
                movePlayerLogo(cellID);
                updateStepLabel();
                if (this.game.board.isGoalPosition(this.game.humanPlayer.currentPosition)) {
                    Log.i(TAG, "WIN!");
                    String dialogMsg = "You reached the goal position in " + this.game.humanPlayer.myPath.getSteps();
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
        this.playerLogo.setX(x);
        this.playerLogo.setY(y);
    }

    public void restartGame(View view) {
        this.game.restartGame();
        movePlayerLogo(startCellID);
        updateStepLabel();
    }

    private void updateStepLabel() {
        int step = this.game.humanPlayer.myPath.getSteps();
        TextView textStepCount = findViewById(R.id.textStepCount);
        textStepCount.setText("Steps: " + step);
    }

    public static float convertSpToPixels(float sp, Context context) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, context.getResources().getDisplayMetrics());
    }


}
