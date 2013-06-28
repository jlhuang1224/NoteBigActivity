package com.readboy.note.big;


import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;

import com.readboy.note.NoteFile;
import com.readboy.note.NoteLittleView.BtnOnClick;
import com.readboy.rbMinNote.R;

import android.R.string;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.os.Environment;
import android.text.InputFilter;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.AbsoluteLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
//import android.content.DialogInterface.OnClickListener;
public class NoteBigActivity extends Activity
{
	final int MENU1 = 0x111;
	final int MENU2 = 0x112;
	final int MENU3 = 0x113;
	private final int ITEM_NUM_OF_LINE = 4;//一行的icon个数
	private final int ITEM_WIDTH = 169;
	private final int ITEM_HEIGHT = 220;
	private final int ITEM_BEGIN_POS_X = 68;//起始x
	private final int ITEM_BEGIN_POS_Y = 26;//起始y
	private final int ITEM_X_INTERVAL = 66;//x方向的间隔
	private final int ITEM_Y_INTERVAL = 40;//y方向的间隔
	private final int ITEM_X_SIZE = (ITEM_WIDTH + ITEM_X_INTERVAL);
	private final int ITEM_Y_SIZE = (ITEM_HEIGHT + ITEM_Y_INTERVAL);
	
	private final int NEW = 0;//
	private final int RENAME = 1;//
	private final int DELETE = 2;//
	
	File[] files;
	//StateListDrawable[] stateDrawable = new StateListDrawable[ITEM_NUM_OF_LINE];
	BitmapDrawable[] iconBitmapsNormal = new BitmapDrawable[ITEM_NUM_OF_LINE];
	BitmapDrawable[] iconBitmapsPress = new BitmapDrawable[ITEM_NUM_OF_LINE];
	MyReceiver serviceReceiver;

	private  int flag ;
	ImageButton rename;
	ImageButton delete;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.note_big);
		InitButtonDrawable();
		//ShowList();
		serviceReceiver = new MyReceiver();
		// 创建IntentFilter
		IntentFilter filter = new IntentFilter();
		filter.addAction(com.readboy.note.NoteLittleView.CTL_UPDATE);
		registerReceiver(serviceReceiver, filter);
		
		rename = (ImageButton)findViewById(R.id.big_rename);		
		delete = (ImageButton)findViewById(R.id.big_delete);	
		
		rename.setSelected(false);
		delete.setSelected(false);
    }
	
	public void onClick(View v){
		switch(v.getId()){
		case R.id.big_new:
			Intent intent = new Intent(NoteBigActivity.this, NoteBigEditActivity.class);
			startActivity(intent);
			break;
		case R.id.big_rename:
			if(rename.isSelected()){
				rename.setSelected(false);
				flag = 0;
			}else{
				rename.setSelected(true);
				delete.setSelected(false);
				flag = RENAME;
			}
			break;
		case R.id.big_delete:
			if(delete.isSelected()){
				delete.setSelected(false);
				flag = 0;
			}else{
				rename.setSelected(false);
				delete.setSelected(true);
				flag = DELETE;
			}
		
			break;
		}
	}
	
	public class MyReceiver extends BroadcastReceiver
	{
		@Override
		public void onReceive(final Context context, Intent intent)
		{
			System.out.println("jiyh:MyReceiver-----------------------------------");
			ShowList();
		}
	}
	
    @Override
	protected void onResume()
	{
		// TODO Auto-generated method stub
    	ShowList();
    	//Toast.makeText(this, "文件列表已重新更新!", 5000).show();
		super.onResume();
	}
    
       
    @Override
	protected void onDestroy()
	{
		// TODO Auto-generated method stub
    	if(serviceReceiver != null)
    	{
    		unregisterReceiver(serviceReceiver);
    	}
		super.onDestroy();
	}



	public void ShowList()
    {
		int x, y;
		int i, j, k;
		ItemOnClick itemOnClick = new ItemOnClick();
		AbsoluteLayout lay = (AbsoluteLayout) this.findViewById(R.id.big_listContent);

		lay.removeAllViews();
		File dir = new File("/mnt/sdcard/note/");
		if (!dir.exists())
		{
			//Toast.makeText(this, "错误:文件目录不存在!", 5000).show();
			return;
		}
		files = dir.listFiles(new FilenameFilter()
		{
			public boolean accept(File dir, String filename)
			{
				if (filename.endsWith(".not"))
				{
					File file = new File(dir.getPath() + "/" + filename);
					if (file.isFile())
						return true;
				}
				return false;

			}
		});
		
		System.out.println("files:"+files);
		

		for (k = 0; k < files.length; k++)
		{
			i = k / 4;
			j = k % 4;
			Button item = new Button(this);
			x = ITEM_BEGIN_POS_X + ITEM_X_SIZE * j;
			y = ITEM_BEGIN_POS_Y + ITEM_Y_SIZE * i;
			item.setLayoutParams(new AbsoluteLayout.LayoutParams(ITEM_WIDTH, ITEM_HEIGHT, x, y));
			
			//BitmapDrawable bitmapDrawable;
			//BitmapDrawable bitmapPressed;
			StateListDrawable stateDrawable;
			//bitmapDrawable = new BitmapDrawable(iconBitmapsNormal[j]);
			//bitmapPressed = new BitmapDrawable(iconBitmapsPress[j]);
			stateDrawable = new StateListDrawable();
			stateDrawable.addState(new int[] { android.R.attr.state_pressed }, iconBitmapsPress[j]);
			stateDrawable.addState(new int[] {}, iconBitmapsNormal[j]);
			item.setBackgroundDrawable(stateDrawable);
			//item.setBackgroundDrawable(new BitmapDrawable(itemBitmaps[j]));
			//item.setBottom(150);
			item.setPadding(24, 45, 26, 130);
			item.setTextColor(0xffffffff);
			item.setTextSize(18);
			item.setText(files[k].getName().substring(0, files[k].getName().length()-4));
			item.setOnClickListener(itemOnClick);
			item.setId(k);
			registerForContextMenu(item);
			lay.addView(item);			
		}
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {

		Bitmap bitmap = Bitmap.createBitmap(
				drawable.getIntrinsicWidth(),
				drawable.getIntrinsicHeight(),
				drawable.getOpacity() != PixelFormat.OPAQUE ? Bitmap.Config.ARGB_8888 : Bitmap.Config.RGB_565);

		Canvas canvas = new Canvas(bitmap);
		drawable.setColorFilter(new PorterDuffColorFilter(0x50000000, PorterDuff.Mode.SRC_ATOP));
		drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
		drawable.draw(canvas);
		return bitmap;

	}
	
	private void InitButtonDrawable()
	{
		int i;
		Bitmap bitmap;
		
		for (i = 0; i < ITEM_NUM_OF_LINE; i++)
		{
			bitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.big_icon1 + i);		
			iconBitmapsNormal[i] = new BitmapDrawable(getResources(), bitmap);
			bitmap = drawableToBitmap(iconBitmapsNormal[i]);	
			iconBitmapsPress[i] = new BitmapDrawable(getResources(), bitmap);
		}

	}
	public class ItemOnClick implements OnClickListener
	{
		//final Builder builder = new AlertDialog.Builder(NoteBigActivity.this);
		@Override
		public void onClick(View v)
		{
			// TODO Auto-generated method stub
			switch(flag){
			case NEW:
				try
				{
					String filename = files[v.getId()].getCanonicalPath();
					System.out.println(filename);
					
					Intent intent = new Intent(NoteBigActivity.this, NoteBigEditActivity.class);
					intent.putExtra("file", filename);
					startActivity(intent);
				}
				catch (Exception e)
				{
					// TODO: handle exception
				}
				break;
			case RENAME:			
				try
				{
					String filename = files[v.getId()].getCanonicalPath();
					System.out.println(filename);
					final File file = new File(filename);
					final int index = v.getId();
					
					final Builder builder = new AlertDialog.Builder(NoteBigActivity.this);
					
				
							builder.setTitle("重命名");
							//装载界面布局
							final LinearLayout loginForm = (LinearLayout)getLayoutInflater()
								.inflate( R.layout.note_setname, null);
							EditText text = (EditText)loginForm.findViewById(R.id.getname_edit);
							//设定标题最大输入个数
							text.setFilters(new InputFilter[] { new InputFilter.LengthFilter(64) });
							
							final String oldFileName = file.getName();
							final String path = file.getPath();
							
							System.out.println(">>>>>>>>>>>"+path);
									
							text.setText(oldFileName);
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
										EditText text = (EditText)loginForm.findViewById(R.id.getname_edit);
										String newName = text.getText().toString();
										newName = NoteFile.removeBlank(newName);
										if (newName == null || newName.contentEquals(""))
										{
											Toast.makeText(NoteBigActivity.this, "重命名失败,请输入标题!", 5000).show();
											return;
										}
										if(oldFileName.compareTo(newName) == 0)
										{
											return;
										}
										if (NoteFile.isFileNameValidate(newName) == false)
										{
											Toast.makeText(NoteBigActivity.this, "重命名失败,标题不能包含以下任意字符之一:\n\\ / : * ? \" < > |", 5000).show();
											return;
										}		
										if(!newName.endsWith(".not"))
										{
											newName += ".not";
										}
										String path = null;
										try
										{
											path = Environment.getExternalStorageDirectory().getCanonicalPath() + NoteFile.NOTE_PATH + newName;
										}
										catch (IOException e)
										{
											// TODO Auto-generated catch block
											e.printStackTrace();
										}
										
										File f = new File(path);
										if(f.exists())
										{
											Toast.makeText(NoteBigActivity.this, "重命名失败,文件已存在!", 5000).show();
											return;
										}
										
										files[index].renameTo(f);
										files[index] = new File(path);
										AbsoluteLayout lay = (AbsoluteLayout) NoteBigActivity.this.findViewById(R.id.big_listContent);
										Button btn = (Button)lay.getChildAt(index);
										btn.setText(newName.substring(0, newName.length()-4));
									}
								});
							// 为对话框设置一个“取消”按钮
							builder.setNegativeButton("取消"
								,  new android.content.DialogInterface.OnClickListener()
								{
									@Override
									public void onClick(DialogInterface dialog, int which)
									{
										//取消登录，不做任何事情。
									}
								});
							//创建、并显示对话框
							builder.create().show();
							break;
					
				}
				catch (Exception e)
				{
					// TODO: handle exception
				}
				break;
			case DELETE:
				try
				{
					String filename = files[v.getId()].getCanonicalPath();
					System.out.println(filename);
					
					File file = new File(filename);
					if(file.delete()){
						Toast.makeText(NoteBigActivity.this, filename+"已删除", Toast.LENGTH_SHORT).show();
					}else{
						Toast.makeText(NoteBigActivity.this, filename+"删除失败", Toast.LENGTH_SHORT).show();
					}
					
				}
				catch (Exception e)
				{
					// TODO: handle exception
				}
				ShowList();
				break;
			}

		}
	}
	// 每次创建上下文菜单时都会触发该方法
	@Override
	public void onCreateContextMenu(ContextMenu menu, View source,
		ContextMenu.ContextMenuInfo menuInfo)
	{
		menu.add(source.getId(), MENU1, 0, "编辑");
		menu.add(source.getId(), MENU2, 0, "删除");
		menu.add(source.getId(), MENU3, 0, "重命名");
		//System.out.println("source:"+source.toString());
		// 将三个菜单项设为单选菜单项
		//menu.setGroupCheckable(0, true, true);
		//设置上下文菜单的标题、图标
		//menu.setHeaderIcon(R.drawable.tools);
		//menu.setHeaderTitle("选择背景色");
	}

	// 菜单项被单击时触发该方法。
	@Override
	public boolean onContextItemSelected(MenuItem mi)
	{
		final Builder builder = new AlertDialog.Builder(NoteBigActivity.this);
		View v = mi.getActionView();
		String filename = null;
		final int index = mi.getGroupId();
		try
		{
			//System.out.println('');
			filename = files[index].getCanonicalPath();
		}
		catch (Exception e) 
		{
			// TODO: handle exception
		}
		
		switch (mi.getItemId())
		{
			case MENU1://编辑
				Intent intent = new Intent(NoteBigActivity.this, NoteBigEditActivity.class);
				//intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				intent.putExtra("file", filename);
				startActivity(intent);
				break;
			case MENU2://删除
				builder.setMessage("是否删除文件:"+filename);
				//为列表项的单击事件设置监听器
				builder.setPositiveButton("确定", new android.content.DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						files[index].delete();
						ShowList();
					}
				});
				// 为对话框设置一个“取消”按钮
				builder.setNegativeButton("取消", new android.content.DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
					}
				});
				//创建、并显示对话框
				builder.create().show();
				break;
			case MENU3://重命名
				// 设置对话框的图标
				//builder.setIcon(R.drawable.tools);
				// 设置对话框的标题
				builder.setTitle("重命名");
				//装载界面布局
				final LinearLayout loginForm = (LinearLayout)getLayoutInflater()
					.inflate( R.layout.note_setname, null);
				EditText text = (EditText)loginForm.findViewById(R.id.getname_edit);
				//设定标题最大输入个数
				text.setFilters(new InputFilter[] { new InputFilter.LengthFilter(64) });
				final String oldName = files[index].getName().substring(0, files[index].getName().length()-4);
				text.setText(oldName);
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
							EditText text = (EditText)loginForm.findViewById(R.id.getname_edit);
							String newName = text.getText().toString();
							newName = NoteFile.removeBlank(newName);
							if (newName == null || newName.contentEquals(""))
							{
								Toast.makeText(NoteBigActivity.this, "重命名失败,请输入标题!", 5000).show();
								return;
							}
							if(oldName.compareTo(newName) == 0)
							{
								return;
							}
							if (NoteFile.isFileNameValidate(newName) == false)
							{
								Toast.makeText(NoteBigActivity.this, "重命名失败,标题不能包含以下任意字符之一:\n\\ / : * ? \" < > |", 5000).show();
								return;
							}		
							if(!newName.endsWith(".not"))
							{
								newName += ".not";
							}
							String path = null;
							try
							{
								path = Environment.getExternalStorageDirectory().getCanonicalPath() + NoteFile.NOTE_PATH + newName;
							}
							catch (IOException e)
							{
								// TODO Auto-generated catch block
								e.printStackTrace();
							}							
							File f = new File(path);
							if(f.exists())
							{
								Toast.makeText(NoteBigActivity.this, "重命名失败,文件已存在!", 5000).show();
								return;
							}
							files[index].renameTo(f);
							files[index] = new File(path);
							AbsoluteLayout lay = (AbsoluteLayout) NoteBigActivity.this.findViewById(R.id.big_listContent);
							Button btn = (Button)lay.getChildAt(index);
							btn.setText(newName.substring(0, newName.length()-4));
						}
					});
				// 为对话框设置一个“取消”按钮
				builder.setNegativeButton("取消"
					,  new android.content.DialogInterface.OnClickListener()
					{
						@Override
						public void onClick(DialogInterface dialog, int which)
						{
							//取消登录，不做任何事情。
						}
					});
				//创建、并显示对话框
				builder.create().show();
				break;
		}
		return true;
	}
}
