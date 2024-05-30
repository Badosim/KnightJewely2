package com.example.myapplication;

import static com.example.myapplication.Constants.cellWidth;
import static com.example.myapplication.Constants.screenWidth;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.InputStream;

public class SpriteSheet {
    public Bitmap topBG;
    public Bitmap bottomBG;
    public Bitmap bg_middle;
    private Bitmap jewelsBMP;
public Bitmap red;
public Bitmap yellow;
public  Bitmap green;
public  Bitmap blue;
public Bitmap purple;



    public SpriteSheet(Context context){
        AssetManager assetManager = context.getAssets();
        try {
            InputStream istr=assetManager.open("Без именпавпви.jpeg");
            topBG = BitmapFactory.decodeStream(istr);
            topBG = Bitmap.createBitmap(topBG,0,0,topBG.getWidth(),topBG.getHeight());
            topBG = Bitmap.createScaledBitmap(topBG,screenWidth,cellWidth*5,false );
            istr = assetManager.open("1660609857_2-kartinkin-net-p-oboi-dark-souls-krasivo-2.jpg");
            bottomBG= BitmapFactory.decodeStream(istr);
            bottomBG= Bitmap.createBitmap(bottomBG, 0,0,bottomBG.getWidth(),bottomBG.getHeight());
            bottomBG = Bitmap.createScaledBitmap(bottomBG,screenWidth,bottomBG.getHeight(),false);
            istr = assetManager.open("fdsfsd.jpeg");
            bg_middle= BitmapFactory.decodeStream(istr);
            bg_middle= Bitmap.createBitmap(bg_middle, 0,0,bg_middle.getWidth(),bg_middle.getHeight());
            bg_middle = Bitmap.createScaledBitmap(bg_middle,screenWidth, cellWidth*9, false);
istr = assetManager.open("jewels..png");
jewelsBMP = BitmapFactory.decodeStream(istr);
jewelsBMP = Bitmap.createBitmap(jewelsBMP,0,0, jewelsBMP.getWidth(),jewelsBMP.getHeight());
red = Bitmap.createBitmap(jewelsBMP,510,260,119,119);
            purple = Bitmap.createBitmap(jewelsBMP,500,150,119,119);
            yellow = Bitmap.createBitmap(jewelsBMP,500,30,119,119);
            green= Bitmap.createBitmap(jewelsBMP,510,380,119,119);
            blue = Bitmap.createBitmap(jewelsBMP,510,510,119,119);
//0,0 этро координаты для битмэпа, 51 это высота и ширина для драгоценностей
            red = Bitmap.createScaledBitmap(red,cellWidth,cellWidth, false);
            red = Bitmap.createBitmap(jewelsBMP,510,260,119,119);
//0,0 этро координаты для битмэпа, 51 это высота и ширина для драгоценностей
            purple = Bitmap.createScaledBitmap(purple,cellWidth,cellWidth, false);
            purple = Bitmap.createBitmap(jewelsBMP,500,150,119,119);
//0,0 этро координаты для битмэпа, 51 это высота и ширина для драгоценностей
            yellow = Bitmap.createScaledBitmap(yellow,cellWidth,cellWidth, false);
           yellow = Bitmap.createBitmap(jewelsBMP,501,35,119,119);
           green= Bitmap.createScaledBitmap(green,cellWidth,cellWidth, false);
            green= Bitmap.createBitmap(jewelsBMP,510,380,119,119);
            blue = Bitmap.createScaledBitmap(blue,cellWidth,cellWidth, false);
            blue = Bitmap.createBitmap(jewelsBMP,510,510,119,119);
        }catch (Exception e){
System.out.println(e.getMessage());
        }
    }
}
