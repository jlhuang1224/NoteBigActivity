package com.readboy.note;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.widget.EditText;
import android.widget.Toast;

import com.readboy.rbMinNote.R;

public class NoteFile
{
	Context context;
	public static final String NOTE_PATH = "/note/";
	public String saveName = null;
	final Toast toast_haveSaved;
	final Toast toast_noTitle;
	final Toast toast_invalidaeTitle;
	final Toast toast_noSpace;
	
	public NoteFile(Context context)
	{
		// TODO Auto-generated constructor stub
		this.context = context;
		toast_haveSaved = Toast.makeText(context, "文件已保存!",  Toast.LENGTH_SHORT);
		toast_noTitle = Toast.makeText(context, "保存失败,标题不存在!", Toast.LENGTH_SHORT);
		toast_noSpace = Toast.makeText(context, "磁盘空间不足1M,保存文件失败!", Toast.LENGTH_SHORT);
		toast_invalidaeTitle = Toast.makeText(context, "保存失败,标题不能包含以下任意字符之一:\n\\ / : * ? \" < > |", 5000);
	}
	static public String removeBlank(String str)
	{
		int i;
		int a = -1, b = -1;;
		
		for(i = 0; i < str.length(); i++)
		{
			if(' ' != str.charAt(i))
			{
				a = i;
				break;
			}
		}
		for(i = str.length()-1; i >= 0; i--)
		{
			if(' ' != str.charAt(i))
			{
				b = i+1;
				break;
			}
		}
		if(a != -1 && b != -1 && a < b)
		{
			return str.substring(a, b);
		}
		
		return null;
	}
	private String readText(FileInputStream fis, int count)
	{
		int readSize;
		try
		{
			// 打开文件输入流
			byte[] buff = new byte[10240];
			int hasRead = 0;
			StringBuilder sb = new StringBuilder("");
			while (count > 0)
			{
				readSize = Math.min(count, 10240);
				hasRead = fis.read(buff, 0, readSize);
				sb.append(new String(buff, 0, hasRead, "GB2312"));
				count -= readSize;
			}
			return sb.toString();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
	public boolean readNote(String fileName, DrawView drawView, DrawEdit drawEdit)
	{
		Bitmap bitmap = null;
		String text = null;
		FileInputStream fis;
		String path;

		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) == false)
		{
			//Toast.makeText(getContext() ,"未发现存储器!" , 5000).show();
			return false;
		}
		try
		{
			int textSize;
			
			if(true)
			{
				path = fileName;
			}
			else 
			{
				if(fileName.length()<=4 || !fileName.endsWith(".not"))
				{
					fileName += ".not";
				}
				path = Environment.getExternalStorageDirectory().getCanonicalPath() + NOTE_PATH + fileName;
			}
			//System.out.println("path:"+path);
			//Toast.makeText(context, "文件:" + path, 5000).show();
			
			fis = new FileInputStream(path);

			if (!(fis.read() == 0x6e && fis.read() == 0x6f && fis.read() == 0x74 && fis.read() == 0x65))
			{
				Toast.makeText(context, "格式错误,载入文件失败!", 5000).show();
				return false;
			}
			fis.skip(12);
			textSize = fis.read() + (fis.read() << 8) + (fis.read() << 16) + (fis.read() << 24);
			fis.skip(12);
			text = readText(fis, textSize);
			bitmap = BitmapFactory.decodeStream(fis);
		}
		catch (Exception e)
		{
			// TODO: handle exception
			e.printStackTrace();
		}

		if (bitmap == null)
		{
			return false;
		}
		drawView.setCacheBitmap(bitmap);
		bitmap.recycle();
		bitmap = null;

		drawEdit.setText(text);
		drawEdit.setSaved(true);
		saveName = new String(fileName);
		
		return true;
	}
	
	public static boolean isFileNameValidate(String name)
	{
		final String invalid = "\\/:*?\"<>|";
		
		for(int i = 0; i < invalid.length(); i++)
		{
			if(name.contains(invalid.subSequence(i, i+1)))
			{
				return false;
			}
		}
		return true;
	}
	
	public boolean saveNote(String file, DrawView drawView, DrawEdit drawEdit, boolean isJugeExistFlie, boolean isAllPath)
	{
		String path;
		Bitmap bitmap;
		String fileName = null;
		
		//		int []pixels;
		//		RandomAccessFile fp;
		//判断手机是否插入sd卡
		file = NoteFile.removeBlank(file);
		if (file == null || file.contentEquals(""))
		{
			toast_noTitle.show();
			return false;
		}
		

		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED) == false)
		{
			Toast.makeText(context, "未发现存储器!", 5000).show();
			return false;
		}
		try
		{
			File sdCard = Environment.getExternalStorageDirectory();
			long freeSpace = sdCard.getFreeSpace();
			System.out.println("freeSpace:"+freeSpace);
			if(freeSpace < 1024*1024)
			{
				toast_noSpace.show();
				return false;
			}
			
			if(isAllPath == false)
			{
				if (isFileNameValidate(file) == false)
				{
					toast_invalidaeTitle.show();
					return false;
				}
				path = sdCard.getCanonicalPath();
				path += NOTE_PATH;
				File f = new File(path);
				if (f.exists() == false)
				{
					if (false == f.mkdir())
					{
						Toast.makeText(context, "创建目录失败,数据无法保存!", 5000).show();
						return false;
					}
				}
				if (file.contentEquals(""))
				{
					Toast.makeText(context, "保存失败,请输入标题!", 5000).show();
					return false;
				}
				fileName = path + file;
				if(fileName.substring(fileName.length()-4).compareToIgnoreCase(".not") != 0)
				{
					fileName += ".not";
				}
			}
			else
			{
				fileName = file;
			}
		}
		catch (IOException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if(saveName != null)
		{
			if(saveName.compareTo(fileName) == 0 && drawEdit.isSaved() && drawView.isSaved())
			{
				//toast.cancel();
				toast_haveSaved.show();
				return false;
			}
		}

		bitmap = drawView.getCacheBitmap();
		String text = drawEdit.getText().toString();

		try
		{
			int i = 0;
			byte[] bs = text.getBytes("GB2312");
			File f = new File(fileName);
			//if(f.exists() && isJugeExistFlie)
			if(f.exists() && (saveName == null || saveName.compareTo(fileName) != 0))
			{
				Toast.makeText(context, "保存失败,文件已存在!", 5000).show();
				return false;
			}
			FileOutputStream fOut = new FileOutputStream(f);
			fOut.write("note".getBytes());
			for (i = 0; i < 12; i++)
			{
				fOut.write(0);
			}
			fOut.write(bs.length);
			fOut.write(bs.length >> 8);
			fOut.write(bs.length >> 16);
			fOut.write(bs.length >> 24);
			for (i = 0; i < 12; i++)
			{
				fOut.write(0);
			}
			fOut.write(bs);
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);

			fOut.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		Toast.makeText(context, "文件保存成功:" + fileName, 5000).show();
		drawEdit.setSaved(true);
		drawView.setSaved(true);
		saveName = new String(fileName);
		return true;
	}
}
