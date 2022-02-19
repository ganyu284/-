package cn.com.alex.imusic.util;

import android.widget.TextView;
import android.content.Context;
import android.util.AttributeSet;

public class AlwaysMarqueeTextView extends TextView {
	public AlwaysMarqueeTextView(Context context) {
		super(context);
	}

	public AlwaysMarqueeTextView(Context context, AttributeSet attrs) {

		super(context, attrs);

	}

	public AlwaysMarqueeTextView(Context context, AttributeSet attrs,
			int defStyle) {

		super(context, attrs, defStyle);

	}

	@Override
	public boolean isFocused() {

		return true;

	}
}
