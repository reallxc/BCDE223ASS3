package nz.ac.arastudent.xil0393.bcde223ass3;

import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import static android.widget.SeekBar.*;

public class MainActivity extends AppCompatActivity {

    public static final String DIFFICULTY = "nz.ac.arastudent.xil0393.bcde223.ass3.DIFFICULTY_TEXT";
    private static final String TAG = "MainActivity";
    private static final int INITIAL_DIFF = 2;
    private TextView textDiff;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textDiff = findViewById(R.id.textDiff);
        SeekBar diffSelector = findViewById(R.id.diffSelector);

        diffSelector.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                Log.i(TAG, "onProgressChanged: " + progress);
                int mazeSize = GameModel.getMazeSize(progress+1);
                textDiff.setText(progress + 1 + " - " + mazeSize + " x " + mazeSize + " Maze");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });
        diffSelector.setProgress(INITIAL_DIFF);
    }

    /** Call when the users tap the Start button */
    public void startGame(View view) {
        //Do something
        Intent intent = new Intent(this, GameActivity.class);
        SeekBar diffSelect = findViewById(R.id.diffSelector);
        int gameDifficulty = diffSelect.getProgress();
        intent.putExtra(DIFFICULTY, gameDifficulty);
        startActivity(intent);
    }
}