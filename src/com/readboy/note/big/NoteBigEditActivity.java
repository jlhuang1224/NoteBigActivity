package com.readboy.note.big;

import java.io.File;
import java.io.IOException;

import com.readboy.note.DrawEdit;
import com.readboy.note.DrawView;
import com.readboy.note.NoteFile;
import com.readboy.note.NoteLittleView.BtnOnClick;
import com.readboy.rbMinNote.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputFilter;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.AbsoluteLayout;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.Toast;

public class NoteBigEditActivity extends Activity
{
	BtnOnClick btnOnClick;
	NoteFile noteFile;
	String filename;
	AbsoluteLayout rootLayoutayout;

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.note_big_edit);
		btnOnClick = new BtnOnClick();
		noteFile = new NoteFile(this);
		rootLayoutayout = (AbsoluteLayout) findViewById(R.id.big_edit_root);

		findViewById(R.id.big_ok).setOnClickListener(btnOnClick);
		findViewById(R.id.big_cancle).setOnClickListener(btnOnClick);

		findViewById(R.id.big_pen).setOnClickListener(btnOnClick);
		findViewById(R.id.big_font).setOnClickListener(btnOnClick);
		findViewById(R.id.big_eraser).setOnClickListener(btnOnClick);
		findViewById(R.id.big_undo).setOnClickListener(btnOnClick);
		findViewById(R.id.big_redo).setOnClickListener(btnOnClick);

		//限制1w字
		((EditText) findViewById(R.id.big_edit))
				.setFilters(new InputFilter[] { new InputFilter.LengthFilter(10 * 1024) });

		Intent intent = getIntent();
		filename = intent.getStringExtra("file");
		System.out.println("filename:" + filename);
		readNote();

		((DrawView) findViewById(R.id.big_draw)).setEdit(false);
		((DrawEdit) findViewById(R.id.big_edit)).setFocusable(false);
		((DrawEdit) findViewById(R.id.big_edit)).setEnabled(true);
		/*
		//选中画笔
		setBtnSelect((ImageButton) findViewById(R.id.big_pen), R.drawable.big_pen2);
		((DrawView) findViewById(R.id.big_draw)).setPen();
		((DrawEdit) findViewById(R.id.big_edit)).setEnabled(false);*/

	}

	private void setBtnSelect(ImageButton button, int resId)
	{
		ImageButton seletctImageButton;

		seletctImageButton = (ImageButton) findViewById(R.id.big_btn_select2);

		AbsoluteLayout.LayoutParams lp = (AbsoluteLayout.LayoutParams) button.getLayoutParams();

		seletctImageButton.setBackgroundColor(Color.TRANSPARENT);
		seletctImageButton.setImageResource(resId);

		seletctImageButton.setLayoutParams(lp);
		seletctImageButton.setVisibility(View.VISIBLE);
		seletctImageButton.setOnClickListener(btnOnClick);
	}

	public class BtnOnClick implements OnClickListener
	{
		//final Builder builder = new AlertDialog.Builder(NoteBigEditActivity.this);

		@Override
		public void onClick(View v)
		{
			// TODO Auto-generated method stub
			switch (v.getId())
			{
			case R.id.big_cancle:
				finish();
				break;
			case R.id.big_ok:
				if (filename != null)
				{
					saveNote(filename, true);
				}
				else
				{
					Builder builder = new AlertDialog.Builder(NoteBigEditActivity.this);
					builder.setTitle("保存");
					//装载界面布局
					final LinearLayout loginForm = (LinearLayout) getLayoutInflater().inflate(R.layout.note_setname,
							null);
					//设定标题最大输入个数
					((EditText) loginForm.findViewById(R.id.getname_edit))
							.setFilters(new InputFilter[] { new InputFilter.LengthFilter(64) });
					// 设置对话框显示的View对象
					builder.setView(loginForm);
					// 为对话框设置一个“确定”按钮
					builder.setPositiveButton("确认"
					// 为按钮设置监听器
							, new android.content.DialogInterface.OnClickListener()
							{
								@Override
								public void onClick(DialogInterface dialog, int which)
								{
									//此处可执行登录处理
									EditText text = (EditText) loginForm.findViewById(R.id.getname_edit);
									String newName = text.getText().toString();
									if (saveNote(newName, false))
									{
										filename = noteFile.saveName;
									}
								}
							});
					// 为对话框设置一个“取消”按钮
					builder.setNegativeButton("取消", new android.content.DialogInterface.OnClickListener()
					{
						@Override
						public void onClick(DialogInterface dialog, int which)
						{
							//取消登录，不做任何事情。
						}
					});
					//创建、并显示对话框
					builder.create().show();
				}
				break;
			case R.id.big_pen:
				setBtnSelect((ImageButton) v, R.drawable.big_pen2);

				((DrawView) findViewById(R.id.big_draw)).setPen();
				((DrawEdit) findViewById(R.id.big_edit)).setEnabled(false);
				findViewById(R.id.big_edit).setFocusable(false);

				break;
			case R.id.big_font:
				setBtnSelect((ImageButton) v, R.drawable.big_font2);

				findViewById(R.id.big_edit).setEnabled(true);
				findViewById(R.id.big_edit).setFocusable(true);
				findViewById(R.id.big_edit).setFocusableInTouchMode(true);

				break;
			case R.id.big_eraser:
				setBtnSelect((ImageButton) v, R.drawable.big_eraser2);

				((DrawView) findViewById(R.id.big_draw)).setEraser();
				((DrawEdit) findViewById(R.id.big_edit)).setEnabled(false);
				findViewById(R.id.big_edit).setFocusable(false);
				break;
			case R.id.big_btn_select2:
				/*final SetColorLayout layout = (SetColorLayout) getLayoutInflater().inflate(R.layout.note_big_set_pen,
						null);
				final PopupWindow popup = new PopupWindow(layout, ViewGroup.LayoutParams.WRAP_CONTENT,
						ViewGroup.LayoutParams.WRAP_CONTENT);
				popup.setOutsideTouchable(true);
				popup.setFocusable(true);
				popup.showAtLocation(rootLayoutayout, Gravity.LEFT | Gravity.TOP, 0, 77);
				ImageButton btn = (ImageButton)layout.findViewById(R.id.big_setpen_close);
				btn.setOnClickListener(new OnClickListener()
				{
					@Override
					public void onClick(View v)
					{
						// TODO Auto-generated method stub
						popup.dismiss();
					}
				});
*/
				v.setVisibility(View.INVISIBLE);
				((DrawEdit) findViewById(R.id.big_edit)).setFocusable(false);
				((DrawView) findViewById(R.id.big_draw)).setEdit(false);
				((DrawEdit) findViewById(R.id.big_edit)).setEnabled(true);
				break;
			case R.id.big_undo:
				findViewById(R.id.big_undo).setVisibility(View.INVISIBLE);
				break;
			case R.id.big_redo:
				findViewById(R.id.big_redo).setVisibility(View.INVISIBLE);
				break;
			default:
				break;
			}
		}
	}

	private void readNote()
	{
		if (filename != null && !filename.isEmpty())
		{
			DrawView drawView = (DrawView) findViewById(R.id.big_draw);
			DrawEdit drawEdit = (DrawEdit) findViewById(R.id.big_edit);
			if(noteFile.readNote(filename, drawView, drawEdit) == false)
			{
				filename = null;
			}
		}
	}

	public boolean saveNote(String filename, boolean isAllPath)
	{
		DrawView drawView = (DrawView) findViewById(R.id.big_draw);
		DrawEdit drawEdit = (DrawEdit) findViewById(R.id.big_edit);

		if (this.filename == null)
		{
			return noteFile.saveNote(filename, drawView, drawEdit, true, isAllPath);
		}
		else
		{
			return noteFile.saveNote(filename, drawView, drawEdit, false, isAllPath);
		}
	}
}
