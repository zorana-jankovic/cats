package com.example.zorana.cats.game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;

import com.example.zorana.cats.R;
import com.example.zorana.cats.fragments.MenuFragment;

import androidx.annotation.DrawableRes;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

public class MyCustomImageView extends AppCompatImageView {

    public static int screenWidth, screenHeight;

    public MyCustomImageView(Context context) {
        super(context);
        init();
    }

    public MyCustomImageView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyCustomImageView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }


    private Bitmap sasijaLeft, sasijaRight;
    private Paint sasijaPaint;

    private Bitmap deoLeft, deoRight, forkl;

    private Thread nit;
    private Matrix matrixL = new Matrix();
    private Matrix matrixR = new Matrix();

    private Bitmap fight, srce, metak, bager;
    private Paint fightPaint, srcePaint;

    private Paint rectPaintBellow, rectPaintAbove, textPaint;

    private float myPercent, enemyPercent;


    // 1000 = 1 sec (rotate 1 circle pre 1 sec)
    private int ROTATE_TIME_MILLIS = 1800;

    private int idDeo = 1;

    private void init() {
        sasijaPaint = new Paint();

        sasijaLeft = BitmapFactory.decodeResource(getResources(), R.drawable.rocketfire);
        sasijaRight = BitmapFactory.decodeResource(getResources(), R.drawable.sasija3);

        deoLeft = BitmapFactory.decodeResource(getResources(), R.drawable.wheel);
        forkl = BitmapFactory.decodeResource(getResources(), R.drawable.forklift);
        deoRight = BitmapFactory.decodeResource(getResources(), R.drawable.chainsaw);


        fightPaint = new Paint();
        fight = BitmapFactory.decodeResource(getResources(), R.drawable.fight);
        fight = Bitmap.createScaledBitmap(fight,60,60,false);
        fightPaint.setColor(Color.RED);

        rectPaintBellow = new Paint();
        rectPaintBellow.setColor(getResources().getColor(R.color.backgroundTransparent));

        rectPaintAbove = new Paint();
        rectPaintAbove.setColor(getResources().getColor(R.color.blueNoTransparent));


        srcePaint = new Paint();
        srce=BitmapFactory.decodeResource(getResources(), R.drawable.srce);
        srcePaint.setColor(Color.RED);
        srce = Bitmap.createScaledBitmap(srce,50,50,false);

        metak = BitmapFactory.decodeResource(getResources(), R.drawable.rocketfire);
        //metak = Bitmap.createScaledBitmap(metak, (int)(screenWidth * 0.05),  (int)(screenHeight * 0.025), false);

        bager = BitmapFactory.decodeResource(getResources(), R.drawable.bager);
        //bager = Bitmap.createScaledBitmap(bager, (int)(screenWidth * 0.05),  (int)(screenHeight * 0.025), false);


        textPaint = new Paint();
        textPaint.setColor(Color.BLACK);
        textPaint.setTextSize(25);
        textPaint.setFakeBoldText(true);

        myPercent = 1f;
        enemyPercent = 1f;



        if (MenuFragment.getManualFight() == 1){
            this.setOnTouchListener(new OnTouchListener() {
                @Override
                public boolean onTouch(View view, MotionEvent motionEvent) {
                    int x = (int) motionEvent.getX();
                    int y = (int) motionEvent.getY();

                    switch (motionEvent.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            if (x >= screenWidth * 0.35f && x <= screenWidth * 0.35f + fight.getWidth() &&
                                    y >= screenHeight * 0.86f && y <= screenHeight * 0.86f + fight.getHeight()){
                                // ispali metak
                                if (GameController.imamLeftMetak  == 1) {
                                    GameController.makeLeftMetak();
                                }

                            }
                            break;
                    }

                    return true;
                }
            });
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        screenWidth = w;
        screenHeight = h;


        for (int i = 0; i < GameController.mojAuto.size(); i++) {
            GameController.mojAuto.get(i).scaleBitmap(screenWidth, screenHeight);
            GameController.protivnickoAuto.get(i).scaleBitmap(screenWidth, screenHeight);
        }

        GameController.nit.start();

        metak = Bitmap.createScaledBitmap(metak, (int)(screenWidth * 0.05),  (int)(screenHeight * 0.05), false);

        bager = Bitmap.createScaledBitmap(bager, (int)(screenWidth * 0.35),  (int)(screenHeight * 0.38), false);


        GameController.bagerLeft.scaleBitmap(screenWidth, screenHeight);
        GameController.bagerLeft.fillMatrix(0,0,0,0);

        GameController.bagerRight.scaleBitmap(screenWidth, screenHeight);
        GameController.bagerRight.fillMatrix(0,0,0,0);

    }

    private BitmapDrawable flip(BitmapDrawable d) {
        Matrix m = new Matrix();
        m.preScale(-1, 1);
        Bitmap src = d.getBitmap();
        Bitmap dst = Bitmap.createBitmap(src, 0, 0, src.getWidth(), src.getHeight(), m, false);
        dst.setDensity(DisplayMetrics.DENSITY_DEFAULT);
        return new BitmapDrawable(dst);
    }


    private float x, y;



    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);


        for (int i = 0; i < GameController.mojAuto.size(); i++) {
            if (GameController.mojAuto.get(i).getIdPart() == 0) {
                canvas.drawBitmap(GameController.mojAuto.get(i).getBitmap(), GameController.mojAuto.get(i).getMatrix(), null);
//                Matrix matrix = GameController.mojAuto.get(i).getMatrix();
//                applyMatrix(GameController.mojAuto.get(i).getBitmap().getWidth(), GameController.mojAuto.get(i).getBitmap().getHeight(), matrix);
//                Paint paint = new Paint();
//                paint.setColor(Color.GREEN);
//                canvas.drawCircle(x, y, 5, paint);
//
//                for(int j = 0; j < GameController.mojAuto.get(i).getCollisionRectPoints().size();j++){
//                    canvas.drawCircle(GameController.mojAuto.get(i).getCollisionRectPoints().get(j).x,
//                            GameController.mojAuto.get(i).getCollisionRectPoints().get(j).y,10,srcePaint);
//                }
            }
        }

        for (int i = 0; i < GameController.protivnickoAuto.size(); i++) {
            if (GameController.protivnickoAuto.get(i).getIdPart() == 0) {
                canvas.drawBitmap(GameController.protivnickoAuto.get(i).getBitmap(), GameController.protivnickoAuto.get(i).getMatrix(), null);

//                for(int j = 0; j < GameController.protivnickoAuto.get(i).getCollisionRectPoints().size();j++){
//                    canvas.drawCircle(GameController.protivnickoAuto.get(i).getCollisionRectPoints().get(j).x,
//                            GameController.protivnickoAuto.get(i).getCollisionRectPoints().get(j).y,10,srcePaint);
//                }
            }
        }

        for (int i = 0; i < GameController.mojAuto.size(); i++) {
            if (GameController.mojAuto.get(i).getIdPart() != 3 && GameController.mojAuto.get(i).getIdPart() != 0
                    && GameController.mojAuto.get(i).getIdPart() != 1) {
                canvas.drawBitmap(GameController.mojAuto.get(i).getBitmap(), GameController.mojAuto.get(i).getMatrix(), null);
//                for(int j = 0; j < GameController.mojAuto.get(i).getCollisionRectPoints().size();j++){
//                    canvas.drawCircle(GameController.mojAuto.get(i).getCollisionRectPoints().get(j).x,
//                            GameController.mojAuto.get(i).getCollisionRectPoints().get(j).y,10,srcePaint);
//                }
            }
        }

        for (int i = 0; i < GameController.protivnickoAuto.size(); i++) {
            if (GameController.protivnickoAuto.get(i).getIdPart() != 3 && GameController.protivnickoAuto.get(i).getIdPart() != 0
                    && GameController.protivnickoAuto.get(i).getIdPart() != 1) {
                canvas.drawBitmap(GameController.protivnickoAuto.get(i).getBitmap(), GameController.protivnickoAuto.get(i).getMatrix(), null);


//                for(int j = 0; j < GameController.protivnickoAuto.get(i).getCollisionRectPoints().size();j++){
//                    canvas.drawCircle(GameController.protivnickoAuto.get(i).getCollisionRectPoints().get(j).x,
//                            GameController.protivnickoAuto.get(i).getCollisionRectPoints().get(j).y,10,srcePaint);
//                }
            }
        }

        for (int i = 0; i < GameController.mojAuto.size(); i++) {
            if (GameController.mojAuto.get(i).getIdPart() == 3 || GameController.mojAuto.get(i).getIdPart() == 1) {
                canvas.drawBitmap(GameController.mojAuto.get(i).getBitmap(), GameController.mojAuto.get(i).getMatrix(), null);
//                for(int j = 0; j < GameController.mojAuto.get(i).getCollisionRectPoints().size();j++){
//                    canvas.drawCircle(GameController.mojAuto.get(i).getCollisionRectPoints().get(j).x,
//                            GameController.mojAuto.get(i).getCollisionRectPoints().get(j).y,10,srcePaint);
//                }
            }
        }


        for (int i = 0; i < GameController.protivnickoAuto.size(); i++) {
            if (GameController.protivnickoAuto.get(i).getIdPart() == 3 || GameController.protivnickoAuto.get(i).getIdPart() == 1) {
                canvas.drawBitmap(GameController.protivnickoAuto.get(i).getBitmap(), GameController.protivnickoAuto.get(i).getMatrix(), null);

//                for(int j = 0; j < GameController.protivnickoAuto.get(i).getCollisionRectPoints().size();j++){
//                    canvas.drawCircle(GameController.protivnickoAuto.get(i).getCollisionRectPoints().get(j).x,
//                            GameController.protivnickoAuto.get(i).getCollisionRectPoints().get(j).y,10,srcePaint);
//                }
            }
        }



        if (MenuFragment.getManualFight() == 1 && GameController.imamLeftMetak == 1) {
            canvas.drawBitmap(fight, screenWidth * 0.35f, screenHeight * 0.86f, fightPaint);
        }

//        if (MenuFragment.getManualFight() == 1 && GameController.imamLeftMetak == 0) {
//
//        }

        canvas.drawRect(screenWidth * 0.009f,screenHeight * 0.03f,
                (screenWidth/2) - screenWidth * 0.05f, screenHeight * 0.13f, rectPaintBellow);

        canvas.drawRect((screenWidth/2) + screenWidth * 0.05f,screenHeight * 0.03f,
                (screenWidth - screenWidth * 0.009f), screenHeight * 0.13f, rectPaintBellow);



        float rectWidth =(screenWidth/2) - screenWidth * 0.05f - screenWidth * 0.009f;


        myPercent = ((float)GameController.getNowHealth())/GameController.getHealth();
        enemyPercent = ((float)GameController.getNowEnemyHealth())/GameController.getEnemyHealth();

        canvas.drawRect(screenWidth * 0.009f,screenHeight * 0.03f,
                ((rectWidth) + screenWidth * 0.009f) * myPercent, screenHeight * 0.13f, rectPaintAbove);

        canvas.drawRect(((screenWidth/2) + screenWidth * 0.05f) + (rectWidth - rectWidth * enemyPercent),screenHeight * 0.03f,
                (screenWidth - screenWidth * 0.009f), screenHeight * 0.13f, rectPaintAbove);

        //percent = percent - 0.001f;


        // lifes photos canvas

        canvas.drawBitmap(srce, screenWidth * 0.01f, screenHeight * 0.028f, srcePaint);


        canvas.drawText(GameController.getNowHealth() + "/" + GameController.getHealth(), screenWidth * 0.01f + 60,screenHeight * 0.1f, textPaint);


        canvas.drawBitmap(srce, screenWidth - 50 - screenWidth * 0.01f, screenHeight * 0.028f, srcePaint);


        canvas.drawText(GameController.getNowEnemyHealth() + "/" + GameController.getEnemyHealth(), screenWidth - 50 - screenWidth * 0.01f - 120,screenHeight * 0.1f, textPaint);

        if (GameController.metakRight != null && GameController.metakRight.getLeft() != 0 ){
            canvas.drawBitmap(GameController.metakRight.getBitmap(),
                    GameController.metakRight.getMatrix(), null);
        }

        if (GameController.metakLeft != null && GameController.metakLeft.getLeft() != 0 ){
            canvas.drawBitmap(GameController.metakLeft.getBitmap(),
                    GameController.metakLeft.getMatrix(), null);
        }

        //canvas.drawBitmap(bager, screenWidth * 0.5f, screenHeight * 0.45f, null);

        if (GameController.bagerLeft != null) {
            canvas.drawBitmap(GameController.bagerLeft.getBitmap(),
                    GameController.bagerLeft.getMatrix(), null);
        }

        if (GameController.bagerRight != null) {
            canvas.drawBitmap(GameController.bagerRight.getBitmap(),
                    GameController.bagerRight.getMatrix(), null);
        }
    }


    private void applyMatrix(int x1, int y1, Matrix matrix) {
        float[] niz = {x1, y1, 1};
        float[] rez = {0, 0, 1};
        float[] vals = new float[9];
        matrix.getValues(vals);

        for (int i = 0 ; i < vals.length; i++){
            System.out.println("val mat :" + vals[i]);
        }

        rez[0] = niz[0] * vals[0] + niz[1] * vals[1] + niz[2] * vals[2];
        rez[1] = niz[0] * vals[3] + niz[1] * vals[4] + niz[2] * vals[5];
        rez[2] = niz[0] * vals[6] + niz[1] * vals[7] + niz[2] * vals[8];


        x = rez[0];
        y = rez[1];
    }

}
