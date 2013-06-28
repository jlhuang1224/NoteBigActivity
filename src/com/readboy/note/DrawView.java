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

	// ����һ���ڴ��е�ͼƬ����ͼƬ����Ϊ������
	Bitmap cacheBitmap = null;
	// ����cacheBitmap�ϵ�Canvas����
	Canvas cacheCanvas = null;
	//�Ƿ���Ա༭
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
		// ����һ�����View��ͬ��С�Ļ�����
		cacheBitmap = Bitmap.createBitmap(VIEW_WIDTH
			, VIEW_HEIGHT , Config.ARGB_8888);
		cacheCanvas = new Canvas();
		path = new Path();
		// ����cacheCanvas������Ƶ��ڴ��е�cacheBitmap��
		cacheCanvas.setBitmap(cacheBitmap);
		//cacheCanvas.drawColor(Color.TRANSPARENT);
		cacheCanvas.drawColor(Color.rgb(239, 227, 195));		
		//���û��ʵ���ɫ
		paint = new Paint(Paint.DITHER_FLAG);
		paint.setColor(Color.RED);
		//���û��ʷ��
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(5);
		//�����
		paint.setAntiAlias(true);
		paint.setDither(true);	
		
		//�趨ƽ������Ч��
		//paint.setPathEffect(new CornerPathEffect(10));
		
	}
	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		if(isEdit == false)
		{
			return false;
		}
		//��ȡ�϶��¼��ķ���λ��
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
				//cacheCanvas.drawPath(path, paint);     //��
				preX = x;
				preY = y;
				break;
			case MotionEvent.ACTION_UP:
				cacheCanvas.drawPath(path, paint);     //��
				path.reset();
				isSaved = false;
				isDraw = true;
				break;
		}
		invalidate();
		// ����true�����������Ѿ�������¼�
		return true;
	}	
	@Override
	public void onDraw(Canvas canvas)
	{
		Paint bmpPaint = new Paint();
		// ��cacheBitmap���Ƶ���View�����
		canvas.drawBitmap(cacheBitmap , 0 , 0 , bmpPaint);    //��
		// ����path����
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
