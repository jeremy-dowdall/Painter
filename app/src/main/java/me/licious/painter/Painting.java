package me.licious.painter;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import static me.licious.painter.Painting.Command.Line;
import static me.licious.painter.Painting.Command.Point;
import static me.licious.painter.Painting.Command.Sticker;

public class Painting implements View.OnLayoutChangeListener {

    private final List<Command> commands;

    private final Paint drawBitmapPaint;
    private final Paint drawPointPaint;
    private final Paint drawLinePaint;
    private final Paint erasePointPaint;
    private final Paint eraseLinePaint;

    private int pos;
    private Bitmap bm;
    private ImageView view;

    public Painting(Context context) {
        commands = new ArrayList<Command>();

        Resources res = context.getResources();
        drawBitmapPaint = new Paint();
        drawPointPaint = new Paint();
        drawPointPaint.setStrokeCap(Paint.Cap.ROUND);
        drawPointPaint.setStrokeWidth(res.getDimension(R.dimen.point_size));
        drawLinePaint = new Paint();
        drawLinePaint.setStrokeWidth(res.getDimension(R.dimen.line_width));
        erasePointPaint = new Paint(drawPointPaint);
        erasePointPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        eraseLinePaint = new Paint(drawLinePaint);
        eraseLinePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
    }

    @Override
    public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
        if(view != null) {
            view.post(new Runnable() {
                public void run() {
                    attach(view);
                }
            });
        }
    }


    public void add(Command cmd) {
        if(pos < commands.size()) {
            commands.subList(pos, commands.size()).clear();
        }
        commands.add(cmd);
        apply(commands.size() - 1, commands.size());
    }

    private void apply(int start, int end) {
        if(start < 0) {
            bm = Bitmap.createBitmap(view.getWidth(), view.getHeight(), Bitmap.Config.ARGB_8888);
            view.setImageBitmap(bm);
            start = 0;
        }
        Canvas canvas = new Canvas(bm);
        for(pos = start; pos < end && pos < commands.size(); pos++) {
            Command cmd = commands.get(pos);
            if(cmd instanceof Sticker) {
                Sticker sticker = (Sticker) cmd;
                Resources resources = view.getContext().getResources();
                Bitmap b = BitmapFactory.decodeResource(resources, sticker.resId);
                canvas.drawBitmap(b, sticker.matrix, drawBitmapPaint);
            }
            if(cmd instanceof Point) {
                Point point = (Point) cmd;
                canvas.drawPoint(point.x, point.y, point.erase ? erasePointPaint : drawPointPaint);
            }
            if(cmd instanceof Line) {
                Line line = (Line) cmd;
                canvas.drawLine(line.x1, line.y1, line.x2, line.y2, line.erase ? eraseLinePaint : drawLinePaint);
            }
        }
        view.postInvalidate();
    }

    public void attach(ImageView view) {
        if(this.view != null) {
            this.view.removeOnLayoutChangeListener(this);
        }
        this.view = view;
        if(this.view != null) {
            view.addOnLayoutChangeListener(this);
            int w = view.getWidth();
            int h = view.getHeight();
            if(w > 0 && h > 0) {
                if(bm == null || bm.getWidth() != w || bm.getHeight() != h) {
                    apply(-1, pos);
                }
            }
        }
    }

    public void detach() {
        if(this.view != null) {
            this.view.removeOnLayoutChangeListener(this);
        }
        this.view = null;
    }

    public void redo() {
        if(pos < commands.size()) {
            apply(pos, pos + 1);
        }
    }

    public void undo() {
        if(pos > 0) {
            apply(-1, pos - 1);
        }
    }



    public interface Command {
        public static class Sticker implements Command {
            public int resId;
            public Matrix matrix;
        }
        public static class Point implements Command {
            public float x, y;
            public boolean erase;
        }
        public static class Line implements Command {
            public float x1, y1, x2, y2;
            public boolean erase;
        }
    }

}
