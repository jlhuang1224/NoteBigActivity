package com.readboy.note;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;
import android.util.AttributeSet;
import android.widget.Button;

public class StateButton extends Button {

	public StateButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		notifyBackgroundChanged();
	}

	public StateButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		notifyBackgroundChanged();
	}

	public StateButton(Context context) {
		super(context);
		notifyBackgroundChanged();
	}

	private StateListDrawable mBackground = new StateListDrawable();

	public void notifyBackgroundChanged() {
		try {
			Drawable drawable = this.getBackground();
			if (drawable != null) {
				Bitmap bitmap = drawableToBitmap(drawable);

				BitmapDrawable bitmapPressed = new BitmapDrawable(this.getResources(), bitmap);

				mBackground = new StateListDrawable();
				mBackground.addState(new int[] { android.R.attr.state_pressed }, bitmapPressed);

				mBackground.addState(new int[] {}, drawable);

				setBackgroundDrawable(mBackground);

			}
		} catch (Exception e) {

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

}
