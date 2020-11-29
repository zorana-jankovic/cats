package com.example.zorana.cats.game;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.graphics.PointF;

import com.example.zorana.cats.R;
import com.example.zorana.cats.fragments.GarageFragment;

import java.util.ArrayList;

public class Part {
    private float width;
    private float height;
    private float dx;
    private float dy;
    private float rotatex;
    private float rotatey;
    private float left;
    private float top;
    private Bitmap bitmap;
    private int idPart;
    private Matrix matrix;
    private int leftCar;
    private float cnt;
    private int screenWidth;
    private int screemHeight;
    private ArrayList<PointF> collisionRectPoints = new ArrayList<>(); // leftTop, rightTop, rightBottom, leftBottom

    public  int numOfMySasijaRotation = 0, numOfEnemySasijaRotation = 0;


    public Part(float width, float height, float dx, float dy, float rotatex, float rotatey, int idPart, MyCustomImageView myCustomImageView, int leftCar) {
        this.width = width;
        this.height = height;
        this.dx = dx;
        this.dy = dy;
        this.rotatex = rotatex;
        this.rotatey = rotatey;
        this.idPart = idPart;
        this.leftCar = leftCar;
        cnt = 1;

        if (idPart == 10){
            bitmap = BitmapFactory.decodeResource(myCustomImageView.getResources(), R.drawable.rocketfire);
        }else if (idPart == 15) {
            bitmap = BitmapFactory.decodeResource(myCustomImageView.getResources(), R.drawable.bager);
        }else {
            bitmap = BitmapFactory.decodeResource(myCustomImageView.getResources(), GarageFragment.mappingImagesIds[idPart]);
        }
        matrix = new Matrix();
    }

    public float fillMatrix(float angle, int zaustavljen, int numOfWheels, float rotateAngle) {
        matrix.reset();

        float ret = 0;
        if (idPart == 0 || idPart == 2 || idPart == 4 || idPart == 5) {
            angle = 0;
        }

        if (idPart == 3) {
//            if (rotateAngle != 0) {
//                angle = 0;
//            }
            angle = -angle;
        }
        matrix.preRotate(angle, rotatex * bitmap.getWidth(), rotatey * bitmap.getHeight());


        if (zaustavljen == 0) {
            if (numOfWheels == 0) {
                cnt = 0;
            }
            if (numOfWheels == 1) {
                cnt = 0.5f;
            }
            if (numOfWheels == 2) {
                cnt = 1;
            }
        } else {
            cnt = 0;
        }

        if (idPart == 10 || idPart == 15){
            cnt = 0.5f * numOfWheels;
        }

        int broj = (int) ((GarageFragment.width[0] * screenWidth)/3);

        if (idPart == 10 || idPart == 15){
            broj = 0;
        }

        if (leftCar == 0) {
            matrix.postScale(-1, 1, bitmap.getWidth() / 2, bitmap.getHeight() / 2);
            left = left - cnt;

            if (numOfEnemySasijaRotation == 0){
                if (rotateAngle == 45){
                    left -= broj;
                    ret = broj;
                    numOfEnemySasijaRotation++;
                }
            }

            if (numOfEnemySasijaRotation == 1){
                if (rotateAngle == 90){
                    left -= broj + broj/2;
                    ret = broj + broj/2;
                    numOfEnemySasijaRotation++;
                }
            }



        } else {
            left = left + cnt;

            if (numOfMySasijaRotation == 0){
                if (rotateAngle == -45){
                    left += broj;
                    ret = broj;
                    numOfMySasijaRotation++;
                }
            }

            if (numOfMySasijaRotation == 1){
                if (rotateAngle == -90){
                    left += broj + broj/2;
                    ret = broj + broj/2;
                    numOfMySasijaRotation++;
                }
            }


        }

        //cnt = 0;

        matrix.postTranslate(left, top);

        resetPoints();
        for (int i = 0; i < collisionRectPoints.size(); i++) {
            PointF r = applyMatrix(collisionRectPoints.get(i).x, collisionRectPoints.get(i).y, matrix);
            collisionRectPoints.get(i).x = r.x;
            collisionRectPoints.get(i).y = r.y;
        }

        if (leftCar == 1) {
            matrix.postRotate(rotateAngle,
                    GameController.getMySasijineTackeRect().get(3).x,
                    GameController.getMySasijineTackeRect().get(3).y);

           // System.out.println(GameController.getMySasijineTackeRect().get(3).x + "   " + GameController.getMySasijineTackeRect().get(3).y);
        }else{
            matrix.postRotate(rotateAngle,
                    GameController.getEnemySasijineTackeRect().get(3).x ,
                    GameController.getEnemySasijineTackeRect().get(3).y);
        }

        resetPoints();
        for (int i = 0; i < collisionRectPoints.size(); i++) {
            PointF r = applyMatrix(collisionRectPoints.get(i).x, collisionRectPoints.get(i).y, matrix);
            collisionRectPoints.get(i).x = r.x;
            collisionRectPoints.get(i).y = r.y;
        }

        return ret;
    }


    public void scaleBitmap(int w, int h) {
        screenWidth = w;
        screemHeight = h;
        bitmap = Bitmap.createScaledBitmap(bitmap, (int) (width * MyCustomImageView.screenWidth),
                (int) (height * MyCustomImageView.screenHeight), false);

        if (idPart == 0) { // sasija
            if (leftCar == 1) {
                left = dx * w; // 0.01f * screenWidth;
                top = h / 2 + dy * h; // screenHeight/2 + 0.05f * screenHeight;
            } else {
                left = w - dx * w - width * w + 1; // screenWidth - sasijaX - sasijaWidth
                top = h / 2 + dy * h; // screenHeight/2 + 0.05f * screenHeight;
            }
        } else {
            if (leftCar == 1) {
                left = GameController.dxs[0] * w + dx * (GameController.widths[0] * w); //  sasijaX + (0.832f * sasijaWidth)
                top = h / 2 + GameController.dys[0] * h + dy * (GameController.heights[0] * h); // sasijaY + (0.72f * sasijaHeight)
            } else {
                left = w - (GameController.dxs[0] * w) -
                        (GameController.widths[0] * w) + (1 - dx) * (GameController.widths[0] * w) - width * w ; // screenWidth - sasijaX - sasijaWidth + 0.832f * sasijaWidth - deoWidth

                top = h / 2 + GameController.dys[0] * h + dy * (GameController.heights[0] * h); // sasijaY + (0.72f * sasijaHeight)
            }
        }

        resetPoints();
    }

    public float getLeft() {
        return left;
    }

    public void setLeft(float left) {
        this.left = left;
    }

    public float getTop() {
        return top;
    }

    public void setTop(float top) {
        this.top = top;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public int getIdPart() {
        return idPart;
    }

    public void setIdPart(int idPart) {
        this.idPart = idPart;
    }

    public Matrix getMatrix() {
        return matrix;
    }

    public void setMatrix(Matrix matrix) {
        this.matrix = matrix;
    }

    public int getLeftCar() {
        return leftCar;
    }

    public void setLeftCar(int leftCar) {
        this.leftCar = leftCar;
    }


    public float getCnt() {
        return cnt;
    }

    public void setCnt(float cnt) {
        this.cnt = cnt;
    }

    private PointF applyMatrix(float x1, float y1, Matrix matrix) {
        float[] niz = {x1, y1, 1};
        float[] rez = {0, 0, 1};
        float[] vals = new float[9];
        matrix.getValues(vals);

//        for (int i = 0 ; i < vals.length; i++){
//            System.out.println("val mat :" + vals[i]);
//        }

        rez[0] = niz[0] * vals[0] + niz[1] * vals[1] + niz[2] * vals[2];
        rez[1] = niz[0] * vals[3] + niz[1] * vals[4] + niz[2] * vals[5];
        rez[2] = niz[0] * vals[6] + niz[1] * vals[7] + niz[2] * vals[8];


        PointF pointF = new PointF(rez[0], rez[1]);

        return pointF;
    }

    public void resetPoints() {
        collisionRectPoints.clear();

        if (leftCar == 1) {
            collisionRectPoints.add(new PointF(0.05f * bitmap.getWidth(), 0.05f * bitmap.getHeight()));
            collisionRectPoints.add(new PointF(bitmap.getWidth() * 0.95f, 0.05f * bitmap.getHeight()));
            collisionRectPoints.add(new PointF(bitmap.getWidth() * 0.95f, bitmap.getHeight() * 0.95f));
            collisionRectPoints.add(new PointF(0.05f * bitmap.getWidth(), bitmap.getHeight() * 0.95f));
        } else {
            collisionRectPoints.add(new PointF(0.05f * bitmap.getWidth(), 0.05f * bitmap.getHeight()));
            collisionRectPoints.add(new PointF(bitmap.getWidth() * 0.95f, 0.05f * bitmap.getHeight()));
            collisionRectPoints.add(new PointF(bitmap.getWidth() * 0.95f, bitmap.getHeight() * 0.95f));
            collisionRectPoints.add(new PointF(0.05f * bitmap.getWidth(), bitmap.getHeight() * 0.95f));
//
//            collisionRectPoints.add(new PointF(bitmap.getWidth() * 0.95f, 0.05f * bitmap.getHeight()));
//            collisionRectPoints.add(new PointF(0.05f * bitmap.getWidth(), 0.05f * bitmap.getHeight()));
//            collisionRectPoints.add(new PointF(0.05f * bitmap.getWidth(), bitmap.getHeight() * 0.95f));
//            collisionRectPoints.add(new PointF(bitmap.getWidth() * 0.95f, bitmap.getHeight() * 0.95f));
        }


        if (idPart == 0) {
            if (leftCar == 0) {
                GameController.setEnemySasijineTackeRect(collisionRectPoints);
            } else {
                GameController.setMySasijineTackeRect(collisionRectPoints);
            }
        }
    }

    public ArrayList<PointF> getCollisionRectPoints() {
        return collisionRectPoints;
    }

    public void setCollisionRectPoints(ArrayList<PointF> collisionRectPoints) {
        this.collisionRectPoints = collisionRectPoints;
    }
}
