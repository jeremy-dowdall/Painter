package me.licious.painter;

import android.content.Context;
import android.graphics.Matrix;
import android.graphics.PointF;
import android.graphics.drawable.Drawable;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import com.almeros.android.multitouch.MoveGestureDetector;
import com.almeros.android.multitouch.RotateGestureDetector;

import static me.licious.painter.Painting.Command.Sticker;

public class StickerGestureDetector implements View.OnTouchListener {

    private final View canvasView;
    private final ImageView toolView;
    private final ActionHandler handler;
    private final GestureDetector      detector;
    private final ScaleGestureDetector scaleDetector;
    private final RotateGestureDetector rotateDetector;
    private final MoveGestureDetector moveDetector;

    private final Matrix matrix = new Matrix();
    private float scaleFactor = .4f;
    private float rotationDegrees = 0.f;
    private float focusX = 0.f;
    private float focusY = 0.f;
    private int imageHeight, imageWidth;

    public StickerGestureDetector(ActionHandler handler, View canvasView, ImageView toolView) {
        this.handler = handler;
        this.canvasView = canvasView;
        this.toolView = toolView;

        Context context = toolView.getContext();

        View parent = (View) toolView.getParent();
        int parentHeight = parent.getHeight();
        int parentWidth = parent.getWidth();

        Drawable d = toolView.getDrawable();
        imageHeight = d.getIntrinsicHeight();
        imageWidth = d.getIntrinsicWidth();

        float imageCenterX = (imageWidth * scaleFactor)/2;
        float imageCenterY = (imageHeight * scaleFactor)/2;

        focusX = (parentWidth / 2);
        focusY = (parentHeight / 2);

        matrix.postScale(scaleFactor, scaleFactor);
        matrix.postTranslate(focusX - imageCenterX, focusY - imageCenterY);
        toolView.setImageMatrix(matrix);

        detector = new GestureDetector(context, new GestureListener());
        scaleDetector = new ScaleGestureDetector(context, new ScaleListener());
        rotateDetector = new RotateGestureDetector(context, new RotateListener());
        moveDetector = new MoveGestureDetector(context, new MoveListener());
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        detector.onTouchEvent(event);
        scaleDetector.onTouchEvent(event);
        rotateDetector.onTouchEvent(event);
        moveDetector.onTouchEvent(event);

        float scaledImageCenterX = (imageWidth * scaleFactor) / 2;
        float scaledImageCenterY = (imageHeight * scaleFactor) / 2;

        matrix.reset();
        matrix.postScale(scaleFactor, scaleFactor);
        matrix.postRotate(rotationDegrees, scaledImageCenterX, scaledImageCenterY);
        matrix.postTranslate(focusX - scaledImageCenterX, focusY - scaledImageCenterY);

        toolView.setImageMatrix(matrix);

        return true;
    }


    private class GestureListener extends GestureDetector.SimpleOnGestureListener {
        public boolean onSingleTapUp(MotionEvent e) {
            ScaleAnimation anim = new ScaleAnimation(1, 1.20f, 1f, 1.20f, focusX, focusY);
            anim.setDuration(100);
            anim.setRepeatCount(1);
            anim.setRepeatMode(Animation.REVERSE);
            toolView.startAnimation(anim);
            Sticker cmd = new Sticker();
            cmd.resId = R.drawable.friends_sticker;
            cmd.matrix = new Matrix(matrix);
            cmd.matrix.postTranslate(
                    toolView.getLeft() - canvasView.getLeft(),
                    toolView.getTop() - canvasView.getTop()
            );
            handler.onAction(R.id.action_paint_apply, cmd);
            return true;
        }
    }

    private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
        public boolean onScale(ScaleGestureDetector detector) {
            scaleFactor *= detector.getScaleFactor();
            scaleFactor = Math.max(0.1f, Math.min(scaleFactor, 10.0f));
            return true;
        }
    }

    private class RotateListener extends RotateGestureDetector.SimpleOnRotateGestureListener {
        public boolean onRotate(RotateGestureDetector detector) {
            rotationDegrees -= detector.getRotationDegreesDelta();
            return true;
        }
    }

    private class MoveListener extends MoveGestureDetector.SimpleOnMoveGestureListener {
        public boolean onMove(MoveGestureDetector detector) {
            PointF d = detector.getFocusDelta();
            focusX += d.x;
            focusY += d.y;
            return true;
        }
    }
}
