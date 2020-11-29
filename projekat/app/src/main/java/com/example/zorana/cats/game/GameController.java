package com.example.zorana.cats.game;

import android.graphics.Paint;
import android.graphics.PointF;
import android.view.Menu;
import android.view.View;

import com.example.zorana.cats.R;
import com.example.zorana.cats.database.entity.CarParts;
import com.example.zorana.cats.dialogs.FinishGameDialog;
import com.example.zorana.cats.dialogs.StatsDialog;
import com.example.zorana.cats.fragments.FightFragment;
import com.example.zorana.cats.fragments.GarageFragment;
import com.example.zorana.cats.fragments.MenuFragment;

import java.util.ArrayList;

import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

public class GameController {

    private static MyCustomImageView myCustomImageView;
    public static ArrayList<CarParts> deloviNaAutu =  new ArrayList<>();

    public static ArrayList<Part> mojAuto =  new ArrayList<>();
    public static ArrayList<Part> protivnickoAuto = new ArrayList<>();

    public static Part metakLeft, metakRight;
    public static int imamLeftMetak = 0, imamRightMetak = 0;
    public static int manualFight;


    public static float[] widths = {0.23f, 0.138f, 0.138f, 0.11f, 0.055f, 0.163f, 0.048f,0.048f};
    public static float[] heights = {0.23f, 0.1f, 0.08f, 0.07f, 0.08f, 0.056f, 0.082f, 0.082f};

    public static float[] dxs = {0.01f, 0.3f, 0.7f, 0.85f, 0.65f, 0.68f, 0.168f, 0.68f};
    public static float[] dys = {0.05f, 0.3f, 0.25f, 0.55f, 0.25f, 0.3f, 0.72f, 0.72f};

    public static float[] rotx = {0f, 0.89f, 0f, 0.09f, 0f, 0f, 0.49f, 0.49f};
    public static float[] roty = {0f, 0.5f, 0f, 0.71f, 0f, 0f, 0.49f, 0.49f};

    public static Thread nit;
    private int ROTATE_TIME_MILLIS = 1000;

    private int zaustaviSasije = 0;
    private int numOfWheels = 0;

    private static ArrayList<PointF> mySasijineTackeRect = new ArrayList<>();
    private static ArrayList<PointF> enemySasijineTackeRect = new ArrayList<>();

    private static int attack, health, energy;

    private static int enemyHealth, nowEnemyHealth;

    private static int nowHealth;

    private static long mySasijaUdarena, enemySasijaUdarena;

    private static long myForklift, enemyForklift;

    private static float myAngle, enemyAngle;

    private static int gameOver = 10; // 0 -> nereseno, 1-> pobeda, -1 -> poraz


    private FightFragment fightFragment;

    private long vremeMetakLeft, vremeMetakRight;
    private static float angleMetakLeft;
    private static float angleMetakRight;

    private int metakNumWheels = 10;

    public static float pomerajLeveSasije, pomerajDesneSasije;

    public static Part bagerLeft, bagerRight;

    private static int numSecWithoutDamage = 0;



    public GameController(final MyCustomImageView myCustomImageView, FightFragment fightFragment){
        this.myCustomImageView = myCustomImageView;
        this.fightFragment = fightFragment;

        deloviNaAutu = MenuFragment.getDeloviNaAutu();
        enemyHealth = 0;
        health = 0;
        gameOver = 10;
        imamLeftMetak = 0;
        imamRightMetak = 0;
        manualFight = MenuFragment.getManualFight();

        createParts();


        numSecWithoutDamage = 0;

        bagerLeft = new Part(  0.35f, 0.38f, -1.6f,
                -0.5f, 0 ,0 ,15, myCustomImageView,1);

        bagerRight = new Part(  0.35f, 0.38f, -1.6f,
                -0.5f, 0 ,0 ,15, myCustomImageView,0);


        nowHealth = health;
        nowEnemyHealth = enemyHealth;

        myAngle = 0;
        enemyAngle = 0;

        mySasijaUdarena = 0;
        enemySasijaUdarena = 0;
        myForklift = 0;
        enemyForklift = 0;

        metakLeft = null;
        metakRight = null;

        vremeMetakLeft = 0;
        vremeMetakRight = 0;
        pomerajLeveSasije = 0;
        pomerajDesneSasije = 0;

        angleMetakLeft = angleMetakRight = 0;

        nit = new Thread(new Runnable() {
            long start = System.currentTimeMillis();
            @Override
            public void run() {

                while(!nit.isInterrupted() && gameOver == 10) {


                    float angle = (float) (System.currentTimeMillis() % ROTATE_TIME_MILLIS)
                            / ROTATE_TIME_MILLIS * 360;

                    float brojLeft = 0;
                    float brojRight = 0;
                    for (int i = 0 ; i < GameController.mojAuto.size(); i++){
                        brojLeft = GameController.mojAuto.get(i).fillMatrix(angle, zaustaviSasije, numOfWheels, myAngle);
                        brojRight = GameController.protivnickoAuto.get(i).fillMatrix(angle, zaustaviSasije, numOfWheels, enemyAngle);

                        if (zaustaviSasije == 0){
                            pomerajLeveSasije += numOfWheels * 0.5f;
                            pomerajDesneSasije += numOfWheels * 0.5f;
                        }
                        pomerajLeveSasije += brojLeft;
                        pomerajDesneSasije += brojRight;
                    }
                    pomerajLeveSasije -= brojLeft;
                    pomerajDesneSasije -= brojRight;


                    myCustomImageView.postInvalidate(); // Cause a re-draw

                    long now = System.currentTimeMillis();
                    if (now - mySasijaUdarena >= 1000){
                        mySasijaUdarena = 0;
                    }

                    if (now - enemySasijaUdarena >= 1000){
                        enemySasijaUdarena = 0;
                    }

                    if (now - myForklift >= 2000){
                        myForklift = 0;
                    }

                    if (now - enemyForklift >= 2000){
                        enemyForklift = 0;
                    }

                    proveraSudara();

                    if (imamRightMetak == 1){
                        if (metakRight != null){
                            //pomeri ga
                            metakRight.fillMatrix(0, 0, metakNumWheels, angleMetakRight);
                            if (metakRight.getCollisionRectPoints().get(0).x < 0 ||
                                    metakRight.getCollisionRectPoints().get(0).x > MyCustomImageView.screenWidth ||
                                    metakRight.getCollisionRectPoints().get(0).y < 0 ||
                                    metakRight.getCollisionRectPoints().get(0).y > MyCustomImageView.screenHeight
                            ){
                                metakRight = null;
                            }
                        }else{
                            if (now - vremeMetakRight >= 1000){
                                makeRightMetak();
                                vremeMetakRight = now;
                            }
                        }
                    }

                    if (imamLeftMetak == 1){
                        if (metakLeft != null){
                            //pomeri ga
                            metakLeft.fillMatrix(0, 0, metakNumWheels, angleMetakLeft);
                            if (metakLeft.getCollisionRectPoints().get(0).x < 0 ||
                                    metakLeft.getCollisionRectPoints().get(0).x > MyCustomImageView.screenWidth ||
                                    metakLeft.getCollisionRectPoints().get(0).y < 0 ||
                                    metakLeft.getCollisionRectPoints().get(0).y > MyCustomImageView.screenHeight
                            ){
                                metakLeft = null;
                            }

                        }else{
                            if (manualFight == 0) {
                                if (now - vremeMetakLeft >= 1000) {
                                    makeLeftMetak();
                                    vremeMetakLeft = now;
                                }
                            }
                        }
                    }

                    if (now - start >= 1000){
                        start = now;
                        numSecWithoutDamage++;
                    }


                    if (numSecWithoutDamage >= 15){
                        // nek krenu bageri

                        if (bagerLeft != null) {
                            bagerLeft.fillMatrix(0, 0, 5, 0);
                        }
                        if (bagerRight != null) {
                            bagerRight.fillMatrix(0, 0, 5, 0);
                        }

                    }

                    try {
                        Thread.sleep(30);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        break;
                    }

                }
            }
        });

    }

    private void createParts() {
        mojAuto = new ArrayList<>();
        protivnickoAuto = new ArrayList<>();
        numOfWheels = 0;


        for (int i = 0 ; i < deloviNaAutu.size(); i++){
            int idPart = (int) deloviNaAutu.get(i).getIdPart();
           // System.out.println("ID PARTA :" + idPart);

            Part part = new Part(widths[idPart], heights[idPart], dxs[idPart], dys[idPart],
                    rotx[idPart], roty[idPart], idPart, myCustomImageView,1);
            mojAuto.add(part);

            health = health + GarageFragment.healthMap[idPart];

            if (idPart == 4){
                imamLeftMetak = 1;
            }

            if (idPart >= 6)
                numOfWheels++;

            if (idPart == 1 || idPart == 2 || idPart == 4 || idPart == 5){
                idPart = nadjiId(idPart);
            }

            enemyHealth = enemyHealth + GarageFragment.healthMap[idPart];

            Part part2 = new Part(widths[idPart], heights[idPart], dxs[idPart], dys[idPart],
                    rotx[idPart], roty[idPart], idPart, myCustomImageView,0);
            protivnickoAuto.add(part2);

            if (idPart == 4){
                imamRightMetak = 1;
            }
        }

        //enemyHealth = 1;
    }

    private int nadjiId(int idPart) {
        int randNum = (int) (Math.random() * 1000);
        int first = 1000/3;
        int second  = 1000/3 * 2;

        // TODO: remove this
//        if (idPart == 4){
//            return 4;
//        }

        int rez = -1;

        if (idPart == 1){
            if (randNum < first){
                rez = 2;
            } else if(randNum < second){
                rez = 4;
            }else{
                rez = 5;
            }
        }else if( idPart == 2){
            if (randNum < first){
                rez = 1;
            } else if( randNum < second){
                rez = 4;
            }else{
                rez = 5;
            }
        }else if (idPart == 4){
            if (randNum < first){
                rez = 1;
            } else if( randNum < second){
                rez = 2;
            }else{
                rez = 5;
            }
        }else{
            if (randNum < first){
                rez = 1;
            } else if(randNum < second){
                rez = 2;
            }else{
                rez = 4;
            }
        }

        return rez;
    }

    boolean isPolygonsIntersecting(ArrayList<PointF> a, ArrayList<PointF> b)
    {
        for (int x=0; x<2; x++)
        {
            ArrayList<PointF> list = (x==0) ? a : b;

            for (int i1=0; i1<list.size(); i1++)
            {
                int   i2 = (i1 + 1) % list.size();
                PointF p1 = list.get(i1);
                PointF p2 = list.get(i2);

                PointF normal = new PointF(p2.y - p1.y, p1.x - p2.x);

                double minA = Double.POSITIVE_INFINITY;
                double maxA = Double.NEGATIVE_INFINITY;

                for (PointF p : a)
                {
                    double projected = normal.x * p.x + normal.y * p.y;

                    if (projected < minA)
                        minA = projected;
                    if (projected > maxA)
                        maxA = projected;
                }

                double minB = Double.POSITIVE_INFINITY;
                double maxB = Double.NEGATIVE_INFINITY;

                for (PointF p : b)
                {
                    double projected = normal.x * p.x + normal.y * p.y;

                    if (projected < minB)
                        minB = projected;
                    if (projected > maxB)
                        maxB = projected;
                }

                if (maxA < minB || maxB < minA)
                    return false;
            }
        }

        return true;
    }

    public void proveraSudara(){
        zaustaviSasije = 0;
        for (int i = 0 ; i < mojAuto.size(); i++){
            for (int j = 0; j < protivnickoAuto.size(); j++){
                if (isPolygonsIntersecting(mojAuto.get(i).getCollisionRectPoints(), protivnickoAuto.get(j).getCollisionRectPoints())){
                    sekuSe(mojAuto.get(i), protivnickoAuto.get(j));
                }
            }
        }

        if (metakLeft != null ){
            for (int j = 0; j < protivnickoAuto.size(); j++){
                if (isPolygonsIntersecting(metakLeft.getCollisionRectPoints(), protivnickoAuto.get(j).getCollisionRectPoints())){
                    Part metakLeftCopy = new Part(0,0,0,0,0,0, 4, myCustomImageView,1);
                    sekuSe(metakLeftCopy, protivnickoAuto.get(j));
                    if (protivnickoAuto.get(j).getIdPart() == 0){
                        metakLeft = null;
                        break;
                    }
                }
            }
        }

        if (metakRight != null ){
            for (int i = 0 ; i < mojAuto.size(); i++){
                if (isPolygonsIntersecting(mojAuto.get(i).getCollisionRectPoints(), metakRight.getCollisionRectPoints())) {
                    Part metakRightCopy = new Part(0,0,0,0,0,0, 4, myCustomImageView,0);
                    sekuSe(mojAuto.get(i), metakRightCopy);
                    if (mojAuto.get(i).getIdPart() == 0){
                        metakRight = null;
                        break;
                    }
                }
            }
        }

        if (bagerRight != null ){
            for (int i = 0 ; i < protivnickoAuto.size(); i++){
                if (isPolygonsIntersecting(protivnickoAuto.get(i).getCollisionRectPoints(), bagerRight.getCollisionRectPoints())) {
                    //Part metakRightCopy = new Part(0,0,0,0,0,0, 4, myCustomImageView,0);
                    //sekuSe(mojAuto.get(i), bagerRight);
                    if (protivnickoAuto.get(i).getIdPart() == 0){
                        bagerRight = null;
                        break;
                    }
                }
            }
        }

        if (bagerLeft != null ){
            for (int i = 0 ; i < mojAuto.size(); i++){
                if (isPolygonsIntersecting(mojAuto.get(i).getCollisionRectPoints(), bagerLeft.getCollisionRectPoints())) {
                    //Part metakRightCopy = new Part(0,0,0,0,0,0, 4, myCustomImageView,0);
                    //sekuSe(mojAuto.get(i), bagerLeft);
                    if (mojAuto.get(i).getIdPart() == 0){
                        bagerLeft = null;
                        break;
                    }
                }
            }
        }

        if (bagerLeft == null && bagerRight == null){
            gameOver = 0;
        } else if (bagerLeft == null){
            gameOver = -1;
        }else if (bagerRight == null){
            gameOver = 1;
        }

        if (gameOver != 10){
            System.out.println("over " + gameOver);

            //nit.interrupt();
//            fightFragment.getActivity().runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    nit.interrupt();
//                    NavController navController = Navigation.findNavController(myCustomImageView);
//                    navController.navigate(R.id.action_fightFragment_to_menuFragment);
//                }
//            });

            nit.interrupt();

            FinishGameDialog finishGameDialog = new FinishGameDialog(MenuFragment.getActualUser(),
                    gameOver,fightFragment,nit,myCustomImageView);
            FragmentManager fragmentManager = fightFragment.getActivity().getSupportFragmentManager();
            finishGameDialog.show(fragmentManager, "Stats");

            //gameOver = 10;
        }
    }

    private void sekuSe(Part moj, Part protivnik) {
        int idPartMe = moj.getIdPart();
        int idPartProtivnik = protivnik.getIdPart();

        //System.out.println("seku see" + "moj part :" + moj.getIdPart() + "    protivnicki : " + protivnik.getIdPart());

        if (idPartMe == 0 && idPartProtivnik == 0){ // obe sasije
            zaustaviSasije = 1;
        }

        if (idPartMe == 0 && idPartProtivnik > 0 && idPartProtivnik != 3 && idPartProtivnik < 6 ){
            //skini meni energiju
            if (idPartProtivnik == 5 || idPartProtivnik == 2){
               nowHealth = (int) (nowHealth - GarageFragment.attackMap[idPartProtivnik]/ (1000f/30));
               numSecWithoutDamage = 0;
            }else if (mySasijaUdarena == 0) {
                mySasijaUdarena = System.currentTimeMillis();
                nowHealth = nowHealth - GarageFragment.attackMap[idPartProtivnik];
                numSecWithoutDamage = 0;
            }
            if (nowHealth < 0){
                nowHealth = 0;
            }
        }

        if (idPartMe > 0 &&  idPartMe != 3 && idPartMe < 6 && idPartProtivnik == 0){
            // skini njemu enrgiju
            if (idPartMe == 5 || idPartMe == 2){
                nowEnemyHealth = (int) (nowEnemyHealth - GarageFragment.attackMap[idPartMe]/ (1000f/30));
                numSecWithoutDamage = 0;
            }else if (enemySasijaUdarena == 0) {
                enemySasijaUdarena = System.currentTimeMillis();
                nowEnemyHealth = nowEnemyHealth - GarageFragment.attackMap[idPartMe];
                numSecWithoutDamage = 0;
            }

            if (nowEnemyHealth < 0){
                nowEnemyHealth = 0;
            }
        }

        if (idPartMe == 3 && idPartProtivnik == 0){
            if (myForklift == 0) {
                if (enemyAngle < 90) {
                    enemyAngle = enemyAngle + 45;
                }
                myForklift = System.currentTimeMillis();
            }
        }

        if (idPartMe == 0 && idPartProtivnik == 3){
            if (enemyForklift == 0) {
                if (myAngle > -90) {
                    myAngle = myAngle - 45;
                }
                enemyForklift = System.currentTimeMillis();
            }
        }


        if (nowHealth == 0 && nowEnemyHealth == 0){
            gameOver = 0;
        }

        if (nowHealth == 0 && nowEnemyHealth != 0){
            gameOver = -1;
        }

        if (nowHealth > 0 && nowEnemyHealth == 0){
            gameOver = 1;
        }



//        if (idPartMe == 3 && idPartProtivnik == 0){
//            if (enemyAngle < 89){
//                enemyAngle++;
//            }
//        }
//
//        if (idPartMe == 0 && idPartProtivnik == 3){
//            if (myAngle > -89){
//                myAngle--;
//            }
//        }

        if (idPartMe > 0 && idPartProtivnik > 0){
            // nista
        }
    }


    public static void setMySasijineTackeRect(ArrayList<PointF> mySasijineTackeRect) {
        GameController.mySasijineTackeRect = mySasijineTackeRect;
    }

    public static void setEnemySasijineTackeRect(ArrayList<PointF> enemySasijineTackeRect) {
        GameController.enemySasijineTackeRect = enemySasijineTackeRect;
    }



    public static int getHealth() {
        return health;
    }


    public static int getEnemyHealth() {
        return enemyHealth;
    }


    public static int getNowEnemyHealth() {
        return nowEnemyHealth;
    }



    public static int getNowHealth() {
        return nowHealth;
    }

    public static float getMyAngle() {
        return myAngle;
    }

    public static float getEnemyAngle() {
        return enemyAngle;
    }

    public static ArrayList<PointF> getMySasijineTackeRect() {
        return mySasijineTackeRect;
    }

    public static ArrayList<PointF> getEnemySasijineTackeRect() {
        return enemySasijineTackeRect;
    }

    public static void makeLeftMetak(){
        if (metakLeft == null){
            metakLeft = new Part(0.05f, 0.05f, pomerajLeveSasije/MyCustomImageView.screenWidth + dxs[4] + widths[4] + 0.05f, dys[4]+heights[4], rotx[4], roty[4],
                    10, myCustomImageView, 1);
            metakLeft.scaleBitmap(MyCustomImageView.screenWidth, MyCustomImageView.screenHeight);
            angleMetakLeft = myAngle;
        }
    }

    public static void makeRightMetak(){
        if (metakRight == null){
            metakRight = new Part(0.05f, 0.05f, pomerajDesneSasije/MyCustomImageView.screenWidth + dxs[4] + widths[4] + 0.05f, dys[4]+heights[4], rotx[4], roty[4],
                    10, myCustomImageView, 0);
            metakRight.scaleBitmap(MyCustomImageView.screenWidth, MyCustomImageView.screenHeight);
            angleMetakRight = enemyAngle;
        }
    }
}
