package nz.ac.arastudent.xil0393.bcde223ass3;

import android.util.Log;

public class HumanPlayer extends Player {
    private static final String TAG = "HumanPlayerClass";

    public HumanPlayer(BoardModel board) {
        super(board);
    }

    boolean nextMove(int direction) {
        return this.movePlayer(direction);
    }
    boolean movePlayer(int x, int y) {
        //Use coordinates to move
        int step = this.myBoard.getPositionNumber(this.currentPosition);
        if ((Math.abs(this.currentPosition[0]-x)==step&&Math.abs(this.currentPosition[1]-y)==0)||(Math.abs(this.currentPosition[1]-y)==step&&Math.abs(this.currentPosition[0]-x)==0)) {
            int[] newPosition = {x,y};
            if (this.myPath.pushStep(newPosition)) {
                this.currentPosition[0] = x;
                this.currentPosition[1] = y;
                Log.i(TAG, "Step ok");
                return true;
            } else {
                Log.i(TAG, "Step push fail, already been here?");
                return false;
            }
        } else {
            return false;
        }
    }
}
