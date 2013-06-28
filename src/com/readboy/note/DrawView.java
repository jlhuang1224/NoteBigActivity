package com.readboy.note;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.util.Log;

import com.readboy.rbMinNote.R;


public class DrawView extends View
{
	float preX;
	float preY;
	private Path path;
	public Paint paint = null;
	Resources res = getResources();
	public final int VIEW_WIDTH = 1280;//(int)res.getDimension(R.dimen.drawview_width);//496;
	public final int VIEW_HEIGHT = 676;//(int)res.getDimension(R.dimen.drawview_height);//348;
	private boolean isSaved = false;
	private boolean isDraw = false;

	// 定义一个内存中的图片，该图片将作为缓冲区
	Bitmap cacheBitmap = null;
	// 定义cacheBitmap上的Canvas对象
	Canvas cacheCanvas = null;
	//是否可以编辑
	private boolean isEdit = false;
	public boolean isEdit()
	{
		return isEdit;
	}
	public void setEdit(boolean isEdit)
	{
		this.isEdit = isEdit;
	}
	public DrawView(Context context, AttributeSet set)
	{
		super(context, set);
		//Log.e("jiyh", "VIEW_WIDTH:"+VIEW_WIDTH);
		//Log.e("jiyh", "VIEW_HEIGHT:"+VIEW_HEIGHT);
		// 创建一个与该View相同大小的缓存区
		cacheBitmap = Bitmap.createBitmap(VIEW_WIDTH
			, VIEW_HEIGHT , Config.ARGB_8888);
		cacheCanvas = new Canvas();
		path = new Path();
		// 设置cacheCanvas将会绘制到内存中的cacheBitmap上
		cacheCanvas.setBitmap(cacheBitmap);
		//cacheCanvas.drawColor(Color.TRANSPARENT);
		cacheCanvas.drawColor(Color.rgb(239, 227, 195));		
		//设置画笔的颜色
		paint = new Paint(Paint.DITHER_FLAG);
		paint.setColor(Color.RED);
		//设置画笔风格
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(5);
		//反锯齿
		paint.setAntiAlias(true);
		paint.setDither(true);	
		
		//设定平滑绘制效果
		//paint.setPathEffect(new CornerPathEffect(10));
		
	}
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		if(isEdit == false)
		{
			return false;
		}
		//获取拖动事件的发生位置
		float x = event.getX();
		float y = event.getY();
		switch (event.getAction())
		{
			case MotionEvent.ACTION_DOWN:
				path.moveTo(x, y);
				preX = x;
				preY = y;				
				break;
			case MotionEvent.ACTION_MOVE:
				path.quadTo(preX , preY , x, y);
				//cacheCanvas.drawPath(path, paint);     //①
				preX = x;
				preY = y;
				break;
			case MotionEvent.ACTION_UP:
				cacheCanvas.drawPath(path, paint);     //①
				path.reset();
				isSaved = false;
				isDraw = true;
				break;
		}
		invalidate();
		// 返回true表明处理方法已经处理该事件
		return true;
	}	
	@Override
	public void onDraw(Canvas canvas)
	{
		Paint bmpPaint = new Paint();
		// 将cacheBitmap绘制到该View组件上
		canvas.drawBitmap(cacheBitmap , 0 , 0 , bmpPaint);    //②
		// 沿着path绘制
		canvas.drawPath(path, paint);
	}
	
	public void setPen()
	{
		setEdit(true);
		paint.setColor(Color.RED);
		//paint.setXfermode(null);
		paint.setStrokeWidth(5);
	}
	public void setEraser()
	{
		setEdit(true);
		paint.setColor(Color.rgb(239, 227, 195));
		//paint.setColor(Color.BLACK);
		//paint.setXfermode(new PorterDuffXfermode(Mode.DST_OUT));
		paint.setStrokeWidth(16);
	}
	public Bitmap getCacheBitmap()
	{
		return cacheBitmap;
	}
	
	public void setCacheBitmap(Bitmap bitmap)
	{
		cacheCanvas.drawBitmap(bitmap, 0 , 0 , new Paint());
		invalidate();
		isSaved = true;
	}
	public boolean isSaved()
	{
		return isSaved;
	}
	public boolean isDraw()
	{
		return isDraw;
	}
	public void setSaved(boolean isSaved)
	{
		this.isSaved = isSaved;
	}	
	
}
