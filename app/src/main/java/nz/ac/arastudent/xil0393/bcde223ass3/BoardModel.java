package nz.ac.arastudent.xil0393.bcde223ass3;

public class BoardModel {
    static final int numberOfGoal = 1;
    int[][] board;
    int[] goalPos = new int[2];
    private int width,height;

    public BoardModel(int height, int width) {
        this.height = height;
        this.width = width;
        //set goal position
        this.goalPos[0] = height - 1;
        this.goalPos[1] = width - 1;
    }

    int[][] generateBoard() {
        this.board = new int[height][width];
        int posNumber;
        for (int i=0;i<height;i++){
            for (int j=0;j<width;j++){
                //position number should be the maximum of rmax - r, r - rmin, cmax - c, and c - cmin
                do {
                    posNumber = (int)(Math.random()*10);
                } while(posNumber==0||(posNumber>(height-i-1)&&posNumber>(i)&&posNumber>(width-j-1)&&posNumber>j));
                this.board[i][j] = posNumber;
            }
        }
        this.board[goalPos[0]][goalPos[1]] = 0;
        return this.board;
    }

    int[][] outputBoard() {
        return this.board;
    }

    int getPositionNumber(int[] pos) {
        return this.board[pos[0]][pos[1]];
    }

    int getWidth() {
        return this.width;
    }

    int getHeight() {
        return this.height;
    }

    boolean isGoalPosition(int[] pos) {
        if (pos[0]==this.goalPos[0]&&pos[1]==this.goalPos[1]) {
            return true;
        } else {
            return false;
        }
    }
}
