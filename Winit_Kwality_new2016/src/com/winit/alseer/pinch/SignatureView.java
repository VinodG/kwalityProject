package com.winit.alseer.pinch;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Path.Direction;
import android.view.Display;
import android.view.MotionEvent;
import android.view.View;

public class SignatureView extends View{

    private Bitmap  mBitmap;
    private Canvas  mCanvas;
    private Path    mPath;
    private Paint   mBitmapPaint;
    private float x,y;
    private int width = 480, height = 800;
    private boolean isSigned=false;
    private Paint mPaint;
    @SuppressWarnings("deprecation")
	public SignatureView(Context c)
    {
        super(c);
        Display display = 	((Activity)c).getWindowManager().getDefaultDisplay(); 
        width 			= 	display.getWidth();
        height 			= 	display.getHeight();
        if(mBitmap != null)
        	mBitmap.recycle();
        
        mBitmap 		= 	Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
        mCanvas 		= 	new Canvas(mBitmap);
        mPath 			= 	new Path();
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setDither(true);
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setStrokeJoin(Paint.Join.ROUND);
        mPaint.setStrokeCap(Paint.Cap.ROUND);
        mPaint.setStrokeWidth(2);
        
        mBitmapPaint	= 	new Paint(Paint.DITHER_FLAG);
       
        mBitmapPaint.setAntiAlias(true);
        mBitmapPaint.setDither(true);
        mBitmapPaint.setColor(Color.BLACK);
        mBitmapPaint.setStyle(Paint.Style.STROKE);
        mBitmapPaint.setStrokeJoin(Paint.Join.ROUND);
        mBitmapPaint.setStrokeCap(Paint.Cap.ROUND);
        mBitmapPaint.setStrokeWidth(2);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);
    }
    
    @Override
    protected void onDraw(Canvas canvas) 
    {
        canvas.drawColor(Color.WHITE);
        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
        canvas.drawPath(mPath, mPaint);
    }
    private float mX, mY;
    private static final float TOUCH_TOLERANCE = 4;
    
    private void touch_start(float x, float y)
    {
        mPath.reset();
        mPath.moveTo(x, y);
        mPath.addCircle(x, y,(.3f),Direction.CW);
        mX = x;
        mY = y;
    }
    private void touch_move(float x, float y) 
    {
      float dx = Math.abs(x - mX);
      float dy = Math.abs(y - mY);
      if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) 
      {
       mPath.quadTo(mX, mY, (x + mX)/2, (y + mY)/2);
       mX = x;
       mY = y;
      }
    }
    private void touch_up()
    {
         mPath.lineTo(mX, mY);
         mCanvas.drawPath(mPath, mPaint);
         mPath.reset();
     }
    
    private void clearCanvas()
    {
    	mBitmap 		= 	Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_4444);
        mCanvas 		= 	new Canvas(mBitmap);
    	invalidate();
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) 
    {
         x = event.getX();
         y = event.getY();
        
        switch (event.getAction()) 
        {
            case MotionEvent.ACTION_DOWN:
                touch_start(x, y);
                invalidate();
                break;
                
            case MotionEvent.ACTION_MOVE:
            	isSigned=true;
                touch_move(x, y);
                invalidate();
                break;
                
            case MotionEvent.ACTION_UP:
                touch_up();
                invalidate();
                break;
        }
    	return true;
   }

    public boolean isSigned(){
    	return this.isSigned;
    }
    public void resetSign(){
    	isSigned=false;
    	clearCanvas();
    }
    public Bitmap getBitmap()
	{
		return this.getDrawingCache(true);
	}
}
