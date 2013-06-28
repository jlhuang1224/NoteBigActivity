package com.readboy.note;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.EditText;

public class DrawEdit extends EditText
{
	private boolean isValidate = true;
	private boolean isSaved = true;
	
	public DrawEdit(Context context)
	{
		super(context);
		// TODO Auto-generated constructor stub
	}

	public DrawEdit(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	public DrawEdit(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean onTouchEvent(MotionEvent event)
	{
		if(isValidate == false)
		{
			return false;
		}
		// TODO Auto-generated method stub
		return super.onTouchEvent(event);
	}

	public boolean isValidate()
	{
		return isValidate;
	}

	public void setValidate(boolean isValidate)
	{
		this.isValidate = isValidate;
	}

	@Override
	public void setEnabled(boolean enabled)
	{
		setValidate(enabled);
		// TODO Auto-generated method stub
		super.setEnabled(enabled);
	}

	@Override
	protected void onTextChanged(CharSequence text, int start, int lengthBefore, int lengthAfter)
	{
		// TODO Auto-generated method stub
		isSaved = false;
		super.onTextChanged(text, start, lengthBefore, lengthAfter);
	}

	public boolean isSaved()
	{
		return isSaved;
	}

	public void setSaved(boolean isSaved)
	{
		this.isSaved = isSaved;
	}	
}
