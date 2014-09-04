package me.licious.painter;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import android.view.View.MeasureSpec;
import android.view.ViewGroup.LayoutParams;
import android.widget.PopupWindow;

public class CustomPopupWindow extends PopupWindow {

    public CustomPopupWindow(Context context) {
        super(context, null);
        setBackgroundDrawable(context.getResources().getDrawable(R.drawable.popup_bg));
        setWindowLayoutMode(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        setOutsideTouchable(true);
        setFocusable(true);
    }

    @Override
    public void showAsDropDown(View anchor) {
        Rect padding = new Rect();
        getBackground().getPadding(padding);
        View view = getContentView();
        int wspec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        int hspec = MeasureSpec.makeMeasureSpec(0, MeasureSpec.UNSPECIFIED);
        view.measure(wspec, hspec);
        setHeight(view.getMeasuredHeight());
        super.showAsDropDown(anchor, 0, -padding.top);
    }
}
