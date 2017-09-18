package com.ssu.sangjunianjuni.smartbabycare.Analysis;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

/**
 * Created by kang on 2017-09-18.
 */

public class AnalysisHWText extends View {
    private float framewidth;
    private float frameheight;
    private String height, weight;
    private String babyname;

    public AnalysisHWText(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }

    public void getdata(String height, String weight, String babyname) {

        this.height=height;
        this.weight=weight;
        this.babyname=babyname;
        invalidate();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void onDraw(Canvas canvas) {
        Paint paint=new Paint();
        Paint paint2=new Paint();
        Paint paint3=new Paint();
        Paint red=new Paint();
        Paint yellow=new Paint();
        Paint green=new Paint();
        paint.setColor(Color.rgb(240, 215, 210));
        paint2.setColor(Color.rgb(255, 64, 129));
        paint3.setColor(Color.rgb(0,0,0));
        paint3.setStrokeWidth(6);
        red.setColor(Color.rgb(255,223,223));
        yellow.setColor(Color.rgb(251,233,167));
        green.setColor(Color.rgb(197,255,188));
        framewidth=canvas.getWidth();
        frameheight=canvas.getHeight();

        paint3.setTextSize(25);
        red.setTextSize(120);
        green.setTextSize(120);
        green.setStrokeWidth(14);
        canvas.drawText(babyname+" 아기의", framewidth*0.05F, frameheight*0.15F, paint3);
        paint3.setTextSize(60);
        paint3.setStrokeWidth(8);
        canvas.drawText("키", framewidth*0.25F, frameheight*0.35F, paint3);
        canvas.drawText("몸무게", framewidth*0.75F, frameheight*0.35F, paint3);
        canvas.drawText(height+"CM", framewidth*0.15F, frameheight*0.65F, green);
        canvas.drawText(weight+"KG", framewidth*0.65F, frameheight*0.65F, green);

    }
}