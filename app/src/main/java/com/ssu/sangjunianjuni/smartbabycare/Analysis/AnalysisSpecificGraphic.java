package com.ssu.sangjunianjuni.smartbabycare.Analysis;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Toast;

/**
 * Created by kang on 2017-06-22.
 */

//각 분석화면에서 막대그래프를 그리기 위한 Graphic 요소
public class AnalysisSpecificGraphic extends View {

    private float framewidth;
    private float frameheight;
    private float max;
    private float min;
    private float okmax;
    private float okmin;
    private float uservalue;
    public AnalysisSpecificGraphic(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    //width:프레임 너비, height: 프레임 높이, max:데이터중 최대값, min:최소값, okmax:허용 최대값, okmin:허용 최소값, uservalue:사용자 측정값
    public void getdata(float min, float max, float okmin, float okmax, float uservalue) {

        this.min=min;
        this.max=max;
        this.okmin=okmin;
        this.okmax=okmax;
        this.uservalue=uservalue;

        invalidate();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void onDraw(Canvas canvas){
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

        //left,top,right,bottom

        //베이스

        canvas.drawRoundRect(framewidth*0.1F, frameheight/5*2, framewidth*0.9F, frameheight/5*3, framewidth*0.01F, framewidth*0.01F, red);
        //노란부분
        canvas.drawRoundRect(framewidth*(0.1F+(0.8F*(okmin-min)/(max-min)/2)), frameheight/5*2, framewidth*(0.1F+(0.8F*(okmin-min)/(max-min))), frameheight/5*3, framewidth*0.01F, framewidth*0.01F, yellow);
        canvas.drawRoundRect(framewidth*(0.1F+(0.8F*(okmax-min)/(max-min))), frameheight/5*2, (framewidth*(0.1F+(0.8F*(okmax-min)/(max-min)))+framewidth*0.9F)/2, frameheight/5*3, framewidth*0.01F, framewidth*0.01F, yellow);
        //괜찮은 부분
        canvas.drawRoundRect(framewidth*(0.1F+(0.8F*(okmin-min)/(max-min))), frameheight/5*2, framewidth*(0.1F+(0.8F*(okmax-min)/(max-min))), frameheight/5*3, framewidth*0.01F, framewidth*0.01F, green);
        //선
        canvas.drawLine(framewidth*(0.1F+(0.8F*(uservalue-min)/(max-min))), frameheight/5*1.75F, framewidth*(0.1F+(0.8F*(uservalue-min)/(max-min))), frameheight/5*3.25F, paint3);
        //하단부 밑줄
        canvas.drawLine(framewidth*0.097F, frameheight/5*3.0F, framewidth*0.903F, frameheight/5*3.0F, paint3);
        //하단부 분위 텍스트(처음, 4분의1, 중간, 4분의3, 끝지점 표시)\
        canvas.drawLine(framewidth*0.1F, frameheight/5*3.0F, framewidth*0.1F, frameheight/5*3.25F, paint3);
        canvas.drawLine(framewidth*0.3F, frameheight/5*3.0F, framewidth*0.3F, frameheight/5*3.25F, paint3);
        canvas.drawLine(framewidth*0.5F, frameheight/5*3.0F, framewidth*0.5F, frameheight/5*3.25F, paint3);
        canvas.drawLine(framewidth*0.7F, frameheight/5*3.0F, framewidth*0.7F, frameheight/5*3.25F, paint3);
        canvas.drawLine(framewidth*0.9F, frameheight/5*3.0F, framewidth*0.9F, frameheight/5*3.25F, paint3);

    }

}