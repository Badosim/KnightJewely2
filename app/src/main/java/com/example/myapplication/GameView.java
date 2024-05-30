package com.example.myapplication;

import static com.example.myapplication.Constants.cellWidth;
import static com.example.myapplication.Constants.drawX;
import static com.example.myapplication.Constants.drawY;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Random;

public class GameView  extends SurfaceView implements SurfaceHolder.Callback {


    private DrawThread thread;
    private SpriteSheet spriteSheet;
    private Jewel jewel;
    private Jewel[][] board;
    private float oldX;
    private float oldY;
    private int poseI;
    private int poseJ;
    public String direction;
    private int newPoseI;
    private int newPoseJ;
    private boolean move = false;
    private ArrayList<ArrayList<Point>> search;


    enum GameState {
        swopping, checkSwapping, crushing, update, nathing
    }

    private int swapIndex = 8;
    private int score = 0;
    public GameState gameState;
    private boolean drop_stop;


    private int[][] level = {
            {1, 2, 3, 5, 5, 1, 5, 4, 3},
            {4, 3, 5, 2, 3, 5, 4, 5, 1},
            {4, 1, 1, 3, 2, 3, 1, 5, 3},
            {5, 2, 4, 2, 5, 1, 1, 4, 1},
            {5, 2, 4, 5, 1, 4, 3, 1, 4},
            {2, 5, 1, 4, 3, 2, 5, 1, 2},
            {1, 5, 5, 1, 2, 5, 4, 4, 5},
            {4, 2, 1, 2, 1, 3, 2, 2, 4},
            {1, 3, 1, 3, 2, 1, 5, 1, 1},
    };
    private Jewel[] topBoard;

    public GameView(Context context) {
        super(context);
        getHolder().addCallback(this);
        thread = new DrawThread(this);
        spriteSheet = new SpriteSheet(getContext());
        gameState = GameState.nathing;
        search = new ArrayList<>();
        init();


    }

    public void init() {
        board = new Jewel[level.length][level[0].length];
        for (int i = 0; i < level.length; i++) {
            for (int j = 0; j < level[0].length; j++) {
                board[i][j] = new Jewel((int) drawX + (cellWidth * j), (int) drawY + (cellWidth * i), level[i][j]);
            }
        }
        topBoard = new Jewel[board[0].length];
        for (int j = 0; j < board.length; j++) {
            topBoard[j] = new Jewel((int) (drawX + j * cellWidth), (int) (drawY - cellWidth), 0);
        }
    }

    public void update() {
        switch (gameState) {

            case swopping:
                swap();
                break;
            case checkSwapping:
                fillCrushing();
                if (search.isEmpty()) {
                    swap();
                } else gameState = GameState.crushing;
                break;
            case crushing:
                for (int i = 0; i < search.size(); i++) {
                    for (int j = 0; j < search.get(i).size(); j++) {
                        // Проверяем, что кристалл еще не был разбит
                        if (board[search.get(i).get(j).x][search.get(i).get(j).y].color != 0) {
                            score += 25; // Увеличиваем счет на 100 за каждый разбитый кристалл
                            board[search.get(i).get(j).x][search.get(i).get(j).y].color = 0;
                        }
                    }
                    // Удаляем группу разбитых кристаллов из списка после подсчета очков
                    search.remove(i);
                    i--;
                }
                if (search.isEmpty()) {
                    gameState = GameState.update;
                }
                break;
            case update:
                drop();
                fillTopBoard();
                fillCrushing();
                if (search.isEmpty()) {
                    if (!checkDrop()) {
                        gameState = GameState.nathing;
                    }
                } else {
                    gameState = GameState.crushing;
                }
                drop_stop = false;
                break;
        }
    }

    private boolean checkDrop() {
        boolean drop = false;
        for (Jewel[] jewels : board) {
            for (Jewel jewel : jewels) {
                if (jewel.color == 0) {
                    drop = true;
                    break;
                }
            }
        }
        return drop;
    }

    private void fillTopBoard() {
        for (int j = 0; j < topBoard.length; j++) {
            if (topBoard[j].color == 0) ;
            topBoard[j].color = generateNewJewels();
            if (j > 0) {
                if (topBoard[j].color == topBoard[j - 1].color) {
                    topBoard[j].color = topBoard[j].color % 5 + 1;
                }
            }
        }
    }

    private int generateNewJewels() {
        Random random = new Random();
        return random.nextInt(5) % 5 + 1;
    }

    private void drop() {
        for (int k = 0; k < topBoard.length; k++) {
            if (board[0][k].color == 0) {
                topBoard[k].poseY += cellWidth / 8;
                if ((int) drawY - topBoard[k].poseY < cellWidth / 8) {
                    board[0][k].color = topBoard[k].color;
                    topBoard[k].color = 0;
                    topBoard[k].poseY = board[0][k].poseY - cellWidth;
                    topBoard[k].poseX = (int) drawX + k * cellWidth;
                    drop_stop = true;
                }
            }
        }
        for (int i = 0; i < board.length - 1; i++) {
            for (int j = 0; j < board[0].length; j++) {
                if (board[i][j].color > 0) {
                    if (board[i + 1][j].color == 0) {
                        board[i][j].poseY += cellWidth / 8;
                        if (((int) drawY + (i + 1) * cellWidth) - board[i][j].poseY < cellWidth / 8) {
                            board[i + 1][j].color = board[i][j].color;
                            board[i][j].color = 0;
                            board[i][j].poseY = (int) (drawY) + i * cellWidth;
                            board[i][j].poseX = (int) (drawX) + j * cellWidth;
                            drop_stop = true;
                        }
                    }
                }
            }
        }
    }

    private void fillCrushing() {
        search.clear();
        int boardHeight = board.length;
        int boardWidth = board[0].length;

        // Проверяем горизонтальные ряды на наличие трех и четырех одинаковых элементов
        for (int i = 0; i < boardHeight; i++) {
            for (int j = 0; j < boardWidth; j++) {
                if (j <= boardWidth - 3 && board[i][j].color > 0) {
                    boolean matchThree = board[i][j].color == board[i][j + 1].color && board[i][j + 1].color == board[i][j + 2].color;
                    boolean matchFour = j <= boardWidth - 4 && matchThree && board[i][j + 2].color == board[i][j + 3].color;

                    if (matchThree || matchFour) {
                        ArrayList<Point> matchedPoints = new ArrayList<>();
                        matchedPoints.add(new Point(i, j));
                        matchedPoints.add(new Point(i, j + 1));
                        matchedPoints.add(new Point(i, j + 2));

                        if (matchFour) {
                            matchedPoints.add(new Point(i, j + 3));
                            j += 3;
                        } else {
                            j += 2;
                        }

                        search.add(matchedPoints);
                    }
                }
            }
        }

        // Проверяем вертикальные ряды на наличие трех и четырех одинаковых элементов
        for (int j = 0; j < boardWidth; j++) {
            for (int i = 0; i < boardHeight; i++) {
                if (i <= boardHeight - 3 && board[i][j].color > 0) {
                    boolean matchThree = board[i][j].color == board[i + 1][j].color && board[i + 1][j].color == board[i + 2][j].color;
                    boolean matchFour = i <= boardHeight - 4 && matchThree && board[i + 2][j].color == board[i + 3][j].color;

                    if (matchThree || matchFour) {
                        ArrayList<Point> matchedPoints = new ArrayList<>();
                        matchedPoints.add(new Point(i, j));
                        matchedPoints.add(new Point(i + 1, j));
                        matchedPoints.add(new Point(i + 2, j));

                        if (matchFour) {
                            matchedPoints.add(new Point(i + 3, j));
                            i += 3;
                        } else {
                            i += 2;
                        }

                        search.add(matchedPoints);
                    }
                }
            }
        }

        // Удаляем недопустимые комбинации
        for (int i = 0; i < search.size(); i++) {
            if (!allowCrushing(search.get(i))) {
                search.remove(i);
                i--;
            }
        }
    }
    private boolean allowCrushing(ArrayList<Point> points) {
        boolean allow = true;
        for (int i=0;i<points.size();i++){
            if (points.get(i).x < board.length-1){
                if (board[points.get(i).x+1][points.get(i).y].color ==0) allow=false;
            }
        }
        return allow;
    }


    private void swap() {
        if (swapIndex > 0) {
            switch (direction) {
                case "right":
                    board[poseI][poseJ + 1].poseX -= cellWidth / 8;
                    board[poseI][poseJ].poseX += cellWidth / 8;
                    break;
                case "left":
                    board[poseI][poseJ - 1].poseX += cellWidth / 8;
                    board[poseI][poseJ].poseX -= cellWidth / 8;
                    break;
                case "up":
                    board[poseI - 1][poseJ].poseY += cellWidth / 8;
                    board[poseI][poseJ].poseY -= cellWidth / 8;
                    break;
                case "down":
                    board[poseI + 1][poseJ].poseY -= cellWidth / 8;
                    board[poseI][poseJ].poseY += cellWidth / 8;
                    break;
            }
            swapIndex--;
        }else{
                Jewel jewel;
                jewel = board[poseI][poseJ];
                board[poseI][poseJ] = board[newPoseI][newPoseJ];
                board[newPoseI][newPoseJ] = jewel;

                board[poseI][poseJ].poseX = (int) (poseJ * cellWidth + drawX);
                board[poseI][poseJ].poseY = (int) (poseI * cellWidth + drawY);
                board[newPoseI][newPoseJ].poseX = (int) (newPoseJ * cellWidth + drawX);
                board[newPoseI][newPoseJ].poseY = (int) (newPoseI * cellWidth + drawY);
                swapIndex =8;
                if (gameState == GameState.swopping) {
                    gameState = GameState.checkSwapping;
                }else gameState = GameState.nathing;
            }
        }

        @Override
        public void draw (Canvas canvas){
            super.draw(canvas);
            canvas.drawColor(Color.WHITE);
            Paint p = new Paint(Paint.ANTI_ALIAS_FLAG);
            p.setColor(Color.BLACK);
            canvas.drawBitmap(spriteSheet.topBG, 0,  -cellWidth*2,  null);
            canvas.drawBitmap(spriteSheet.bottomBG, 0,  drawY+cellWidth*9,  null);
            canvas.drawBitmap(spriteSheet.bg_middle, 0,drawY,  null);
            for (int i=0;i<10;i++) {
                for (int j = 8; j < 9; j++) {
                    canvas.drawLine(0, drawY + (i * cellWidth), cellWidth * 10, drawY + (i * cellWidth), p);
                    canvas.drawLine(j * cellWidth, drawY, j * cellWidth, drawY + cellWidth * 9, p);
                }
            }
            for (Jewel[] jewels : board) {
                for (Jewel jewel : jewels) {
                    jewel.drawJewel(canvas, spriteSheet);
                    Paint paint = new Paint();
                    paint.setColor(Color.WHITE);
                    paint.setTextSize(50);
                    canvas.drawText("Счет: " + score, 50, 100, paint);


                }
            }


        }
    @Override
    public boolean onTouchEvent (MotionEvent event){
        final int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                oldX = event.getX();
                oldY = event.getY();
                poseI = (int) (oldY - drawY) / cellWidth;
                poseJ = (int) (oldX - drawX) / cellWidth;

                move = true;
                break;
            case MotionEvent.ACTION_MOVE:
                if (gameState == GameState.nathing) {
                    float newX = event.getX();
                    float newY = event.getY();
                    float deltaX = Math.abs(newX - oldX);
                    float deltaY = Math.abs(newY - oldY);
                    if (move && (deltaX > 30 || deltaY > 30)) {
                        move = false;
                        if (Math.abs(oldX - newX) > Math.abs(oldY - newY)) {
                            // if the first X touch - the end of the X touch is bigger tha
// first Y touch - the end of the touch Y so the direction is
// now se will check shich is bigger the oldX or newX for know
                            if (newX > oldX) {
                                direction = "right";
                                newPoseJ = poseJ + 1;
                            } else {
                                direction = "left";
                                newPoseJ = poseJ - 1;
                            }
                            newPoseI = poseI;
                        }
                        if (Math.abs(oldY - newY) > Math.abs(oldX - newX)) {
                            if (newY > oldY) {
                                direction = "down";
                                newPoseI = poseI + 1;
                            } else {
                                direction = "up";
                                newPoseI = poseI - 1;
                            }
                            newPoseJ = poseJ;
                        }
                        gameState = GameState.swopping;
                    }
                }
                break;
        }
        return true;
    }

        @Override
        public void surfaceCreated (@NonNull SurfaceHolder surfaceHolder){
            thread.setRunning(true);
            thread.start();
        }

        @Override
        public void surfaceChanged (@NonNull SurfaceHolder surfaceHolder,int i, int i1, int i2){}

        @Override
        public void surfaceDestroyed (@NonNull SurfaceHolder surfaceHolder){}


    }
