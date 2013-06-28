package com.readboy.note;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Date;

import android.R.string;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.text.InputFilter;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.readboy.rbMinNote.R;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.AlertDialog.Builder;

import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.os.Bundle;

public class NoteLittleView extends AbsoluteLayout
{
	BtnOnClick btnOnClick;
	ImageButton button;
	NoteFile noteFile;
	SharedPreferences preferences;
	SharedPreferences.Editor editor;
	boolean isSaved = false;
	public static final String CTL_UPDATE 
	= "com.readboy.note.CTL_UPDATE";


	void Logji(String string)
	{
		Log.v("jiyh", string);
	}

	private void init()
	{
		preferences = getContext().getSharedPreferences("notepad", Context.MODE_WORLD_READABLE);
		editor = preferences.edit();
		
		//------------------------------------
		btnOnClick = new BtnOnClick();
	}

	public NoteLittleView(Context context, AttributeSet attrs, int defStyle)
	{
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		init();
	}

	public NoteLittleView(Context context, AttributeSet attrs)
	{
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init();
	}

	public NoteLittleView(Context context)
	{
		super(context);
		// TODO Auto-generated constructor stub
		//setBackgroundResource(R.drawable.main1);
		init();
	}

	@Override
	protected void onFinishInflate()
	{
		// TODO Auto-generated method stub
		super.onFinishInflate();
		
		noteFile = new NoteFile(getContext());

		findViewById(R.id.note_pen).setOnClickListener(btnOnClick);
		findViewById(R.id.note_font).setOnClickListener(btnOnClick);
		findViewById(R.id.note_eraser).setOnClickListener(btnOnClick);
		findViewById(R.id.note_undo).setOnClickListener(btnOnClick);
		findViewById(R.id.note_redo).setOnClickListener(btnOnClick);
		
		//设定标题最大输入个数
		((EditText)findViewById(R.id.note_title)).setFilters(new InputFilter[] { new InputFilter.LengthFilter(64) });
		//限制1w字
		((EditText)findViewById(R.id.note_edit)).setFilters(new InputFilter[] { new InputFilter.LengthFilter(10*1024) });


		//设定选中画笔
		setBtnSelect((ImageButton)findViewById(R.id.note_pen), R.drawable.note_pen2);
		((DrawView) findViewById(R.id.note_draw)).setPen();
		((DrawEdit) findViewById(R.id.note_edit)).setEnabled(false);
		//保存下来原来的名字,暂时去掉...
		//readNote();
	}

	private void setBtnSelect(ImageButton button, int resId)
	{
		ImageButton seletctImageButton;

		seletctImageButton = (ImageButton) findViewById(R.id.note_btn_select2);

		AbsoluteLayout.LayoutParams lp = (AbsoluteLayout.LayoutParams) button.getLayoutParams();
		Logji("x:" + lp.x + ",y:" + lp.y + ",w:" + lp.width + ",h:" + lp.height);

		//Drawable  drawable = button.getDrawable();
		//Logji("drawable:"+drawable);
		seletctImageButton.setBackgroundColor(Color.TRANSPARENT);
		seletctImageButton.setImageResource(resId);

		seletctImageButton.setLayoutParams(lp);
		seletctImageButton.setVisibility(VISIBLE);
		seletctImageButton.setOnClickListener(btnOnClick);
	}

	public class BtnOnClick implements OnClickListener
	{

		@Override
		public void onClick(View v)
		{
			// TODO Auto-generated method stub
			switch (v.getId())
			{
			case R.id.note_pen:
				setBtnSelect((ImageButton) v, R.drawable.note_pen2);

				((DrawView) findViewById(R.id.note_draw)).setPen();
				((DrawEdit) findViewById(R.id.note_edit)).setEnabled(false);

				break;
			case R.id.note_font:
				setBtnSelect((ImageButton) v, R.drawable.note_font2);

				findViewById(R.id.note_edit).setEnabled(true);
				//findViewById(R.id.note_edit).setFocusable(true);
				//findViewById(R.id.note_edit).setFocusableInTouchMode(true);

				break;
			case R.id.note_eraser:
				setBtnSelect((ImageButton) v, R.drawable.note_eraser2);

				((DrawView) findViewById(R.id.note_draw)).setEraser();
				((DrawEdit) findViewById(R.id.note_edit)).setEnabled(false);
				break;
			case R.id.note_btn_select2:
				/*
				v.setVisibility(INVISIBLE);
				((DrawEdit) findViewById(R.id.note_edit)).setEnabled(false);
				((DrawView) findViewById(R.id.note_draw)).setEdit(false);
				*/
				break;
			case R.id.note_undo:
				findViewById(R.id.note_undo).setVisibility(INVISIBLE);
				break;
			case R.id.note_redo:
				findViewById(R.id.note_redo).setVisibility(INVISIBLE);
				break;
			default:
				break;
			}
		}
	}

	private void readNote()
	{
		String fileName = preferences.getString("lastname", null);
		Logji("filename:"+fileName);
		if(fileName != null)
		{
			DrawView drawView = (DrawView) findViewById(R.id.note_draw);
			DrawEdit drawEdit = (DrawEdit) findViewById(R.id.note_edit);
			if(noteFile.readNote(fileName, drawView, drawEdit))
			{
				EditText title = (EditText) findViewById(R.id.note_title);
				title.setText(fileName);
			}			
		}
	}

	public void saveNote(boolean isSwitch)
	{
		String file = ((EditText) findViewById(R.id.note_title)).getText().toString();
		
		DrawView drawView = (DrawView) findViewById(R.id.note_draw);
		DrawEdit drawEdit = (DrawEdit) findViewById(R.id.note_edit);
		
		if(isSwitch)
		{
			if(file.isEmpty())
			{
				if(drawView.isDraw() == false && drawEdit.getText().toString().isEmpty())
				{
					return;
				}
				else 
				{
					//file = "";
				}
			}
			if(drawView.isSaved() == true && drawEdit.isSaved() == true)
			{
				return;
			}
		}
		if(noteFile.saveNote(file, drawView, drawEdit, !isSaved, false))
		{	
			isSaved = true;
			editor.putString("lastname", file);
			editor.commit();
			
			Intent intent = new Intent(CTL_UPDATE);
			// 发送广播 ，将被Service组件中的BroadcastReceiver接收到
			getContext().sendBroadcast(intent);		
			
		}
	}
}
