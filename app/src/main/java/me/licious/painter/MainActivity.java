package me.licious.painter;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;

public class MainActivity extends Activity implements ActionHandler {

    private static final String CANVAS = "canvas_fragment";


    private View canvasView;
    private ImageView toolView;
    private View toolPencil;
    private View toolSticker;
    private CanvasFragment canvas;
    private CustomPopupWindow popup;

    @Override
    public boolean onAction(int id, Object data) {
        if(popup != null) {
            popup.dismiss();
        }
        switch(id) {
            case R.id.action_paint_end:       stopPaint();    return true;
            case R.id.action_paint_sticker:
            case R.id.action_paint_pencil:    startPaint(id); return true;
            case R.id.action_select_bg:       onBackgroundSelectorClicked((View) data); return true;
            case R.id.action_select_stickers: onStickerSelectorClicked((View) data);    return true;
        }
        return canvas.onAction(id, data);
    }

    // used as click listener from xml
    public void onAction(View v) {
        onAction(v.getId(), v);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_activity);

        canvasView = findViewById(R.id.canvas_view);
        toolView = (ImageView) findViewById(R.id.tool_view);
        toolPencil = findViewById(R.id.action_paint_pencil);
        toolSticker = findViewById(R.id.action_select_stickers);

        FragmentManager manager = getFragmentManager();
        canvas = (CanvasFragment) manager.findFragmentByTag(CANVAS);
        if(canvas == null) {
            manager.beginTransaction().add(canvas = new CanvasFragment(), CANVAS).commit();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(popup != null) {
            popup.dismiss();
        }
    }

    private void onBackgroundSelectorClicked(View v) {
        View contentView = getLayoutInflater().inflate(R.layout.main_select_bg_popup, null);

        popup = new CustomPopupWindow(this);
        popup.setContentView(contentView);
        popup.setOnDismissListener(new PopupWindow.OnDismissListener() {
            public void onDismiss() {
                popup = null;
            }
        });
        popup.showAsDropDown(v);
    }

    private void onStickerSelectorClicked(View v) {
        if(toolSticker.isSelected()) {
            stopPaint();
        } else {
            View contentView = getLayoutInflater().inflate(R.layout.main_select_stickers_popup, null);

            popup = new CustomPopupWindow(this);
            popup.setContentView(contentView);
            popup.setOnDismissListener(new PopupWindow.OnDismissListener() {
                public void onDismiss() {
                    popup = null;
                }
            });
            popup.showAsDropDown(v);
        }
    }

    private void startPaint(int id) {
        switch(id) {
            case R.id.action_paint_sticker:
                if(toolSticker.isSelected()) {
                    stopPaint();
                } else {
                    stopPaint();
                    toolView.setVisibility(View.VISIBLE);
                    toolView.setOnTouchListener(new StickerGestureDetector(this, canvasView, toolView));
                    toolSticker.setSelected(true);
                }
                break;
            case R.id.action_paint_pencil:
                if(toolPencil.isSelected()) {
                    stopPaint();
                } else {
                    stopPaint();
                    canvasView.setOnTouchListener(new PencilGestureDetector(this, canvasView));
                    toolPencil.setSelected(true);
                }
                break;
        }
    }



    private void stopPaint() {
        toolView.setVisibility(View.GONE);
        toolView.setOnTouchListener(null);
        canvasView.setOnTouchListener(null);
        toolPencil.setSelected(false);
        toolSticker.setSelected(false);
    }

}
