package me.licious.painter;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import static android.view.View.OnTouchListener;
import static me.licious.painter.Painting.Command.Line;
import static me.licious.painter.Painting.Command.Point;

public class PencilGestureDetector extends GestureDetector.SimpleOnGestureListener implements OnTouchListener {

    private final ActionHandler handler;
    private final GestureDetector detector;

    private float lastX, lastY;

    public PencilGestureDetector(ActionHandler handler, View canvasView) {
        this.handler = handler;
        detector = new GestureDetector(canvasView.getContext(), this);
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        detector.onTouchEvent(event);
        return true;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        lastX = e.getX();
        lastY = e.getY();
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        Line line = new Line();
        line.x1 = lastX;
        line.y1 = lastY;
        line.x2 = lastX = e2.getX();
        line.y2 = lastY = e2.getY();
        handler.onAction(R.id.action_paint_apply, line);
        return true;
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        Point point = new Point();
        point.x = e.getX();
        point.y = e.getY();
        handler.onAction(R.id.action_paint_apply, point);
        return true;
    }
}
