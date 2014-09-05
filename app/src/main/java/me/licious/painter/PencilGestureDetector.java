package me.licious.painter;

import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

import static android.view.View.OnTouchListener;
import static me.licious.painter.Painting.Command.Line;
import static me.licious.painter.Painting.Command.Point;

public class PencilGestureDetector extends GestureDetector.SimpleOnGestureListener implements OnTouchListener {

    private final ActionHandler handler;
    private final GestureDetector detector;
    private final boolean erase;

    private Line line;

    public PencilGestureDetector(ActionHandler handler, View canvasView) {
        this(handler, canvasView, false);
    }
    public PencilGestureDetector(ActionHandler handler, View canvasView, boolean erase) {
        this.handler = handler;
        this.detector = new GestureDetector(canvasView.getContext(), this);
        this.erase = erase;
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        detector.onTouchEvent(event);
        return true;
    }

    @Override
    public boolean onDown(MotionEvent e) {
        line = new Line();
        line.points = new ArrayList<Line.Point>();
        line.points.add(point(e));
        line.erase = erase;
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        line.points.add(point(e2));
        handler.onAction(R.id.action_paint_apply, line);
        return true;
    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        Point point = new Point();
        point.x = e.getX();
        point.y = e.getY();
        point.erase = erase;
        handler.onAction(R.id.action_paint_apply, point);
        return true;
    }

    private Line.Point point(MotionEvent e) {
        Line.Point point = new Line.Point();
        point.x = e.getX();
        point.y = e.getY();
        return point;
    }

}
