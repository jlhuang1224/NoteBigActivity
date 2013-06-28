package com.readboy.rbMinNote;

import com.readboy.note.NoteLittleView;
import com.readboy.note.big.NoteBigActivity;
import com.readboy.rbMinNote.rbWindow;
import com.readboy.rbMinNote.R;
import com.readboy.rbMinNote.rbWindow.OnDismissListener;
import com.readboy.rbpopupservice.IrbPopupManager;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageButton;

public class rbMinNote extends Service{
	static final String TAG = "rbMinNote";
	
	private IrbPopupManager managerService;
	private boolean FloatNoteWindow;
	
	private ServiceConnection conn = new ServiceConnection() {
		
		@Override
		public void onServiceDisconnected(ComponentName name) {
			managerService = null;
			Log.i(TAG, "onServiceDisconnected: managerService="+managerService);
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service) {
			managerService = IrbPopupManager.Stub.asInterface(service);
			Log.i(TAG, "onServiceConnected: managerService="+managerService);
			
			showNoteWindow();
		}
	};
	

	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	public int onStartCommand(Intent intent, int flags, int startId){
		//int ret = super.onStartCommand(intent, flags, startId);
		Log.i(TAG, "rbMiniCalc is running");
		
		if(managerService==null){
			Intent in = new Intent("com.readboy.rbpopupservice.rbPopupManager");
			Log.i(TAG, Boolean.toString(
    			bindService(in, conn, Service.BIND_AUTO_CREATE)));
		}else{
			showNoteWindow();
		}
		
		return START_NOT_STICKY;
	}
	
	public void onDestroy(){
		if(managerService!=null){
			unbindService(conn);
		}
		Log.i(TAG, "onDestroy");
	}
	
	public void showNoteWindow(){
    	if(FloatNoteWindow == false)
    	{
	    	Context context = getApplicationContext();
	    	LayoutInflater layoutInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	    	final NoteLittleView mainView = (NoteLittleView)layoutInflater.inflate(R.layout.note_little, null);			
			View titleView = (View)layoutInflater.inflate(R.layout.note_title, null);
			if(managerService == null)
			{
				Log.e("jiyh", "error:managerService == null!!!!!!!!!!!!!!!!");
				Log.e("jiyh", "error:managerService == null!!!!!!!!!!!!!!!!");
				Log.e("jiyh", "error:managerService == null!!!!!!!!!!!!!!!!");
				return;
			}
			rbWindow win = new rbWindow(context, managerService, "rbMinNote", mainView, titleView, 498, 520);
			win.setOnDismissListener(new OnDismissListener() {
				@Override
				public void onDismiss(rbWindow win) {
					FloatNoteWindow = false;
					stopSelf();
				}
			});
	    	
			ImageButton colse = (ImageButton)titleView.findViewById(R.id.note_close);
			colse.setTag(win);
			colse.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					rbWindow pop = (rbWindow)v.getTag();
					pop.dismiss();
				}
			});
	        
			ImageButton ok = (ImageButton)titleView.findViewById(R.id.note_ok);
			ok.setTag(mainView);
			ok.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					NoteLittleView pop = (NoteLittleView)v.getTag();
					pop.saveNote(false);	
					//pop.closeNote();
				}
			});
			
			ImageButton arrow = (ImageButton)titleView.findViewById(R.id.note_arror);
			arrow.setTag(win);
			arrow.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					rbWindow pop = (rbWindow)v.getTag();
					Context context = getApplicationContext();
					
					mainView.saveNote(true);
					//进入大图界面Activity
					Intent intent = new Intent(context, NoteBigActivity.class);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startActivity(intent);
					pop.dismiss();
				}
			});
    		FloatNoteWindow = true;
    	}
    	else 
    	{
    		Log.w(TAG, "mini FloatNoteWindow is already existed");
		}
    }
}
