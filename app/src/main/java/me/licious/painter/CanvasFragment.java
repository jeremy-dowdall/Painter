package me.licious.painter;

import android.app.Fragment;
import android.content.res.Resources;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import static me.licious.painter.Painting.Command;

public class CanvasFragment extends Fragment implements ActionHandler {

    private static final String BACKGROUND_COLOR = "background.color";

    private Painting painting;
    private ImageView canvasView;
    private int backgroundId;

    @Override
    public boolean onAction(int id, Object data) {
        switch(id) {
            case R.id.action_paint_apply: add(data); return true;
            case R.id.action_redo:        redo();    return true;
            case R.id.action_undo:        undo();    return true;
            case R.id.action_set_bg_white:
            case R.id.action_set_bg_pink:
            case R.id.action_set_bg_blue:
            case R.id.action_set_bg_green:
            case R.id.action_set_bg_purple:
            case R.id.action_set_bg_pattern: setBackground(id); return true;
        }
        return false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        if(savedInstanceState == null) {
            backgroundId = R.id.action_set_bg_white;
        } else {
            backgroundId = savedInstanceState.getInt(BACKGROUND_COLOR, R.id.action_set_bg_white);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(painting == null) {
            painting = new Painting(getActivity());
        }
        canvasView = (ImageView) getActivity().findViewById(R.id.canvas_view);
        setBackground(backgroundId);
        painting.attach(canvasView);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        painting.detach();
    }


    private void add(Object data) {
        if(data instanceof Command) {
            painting.add((Command) data);
        } else {
            Log.wtf("painter", "cannot apply: " + data);
        }
    }

    private int getColor(int id) {
        switch(id) {
            case R.id.action_set_bg_white:  return Color.WHITE;
            case R.id.action_set_bg_pink:   return getResources().getColor(R.color.pink);
            case R.id.action_set_bg_blue:   return getResources().getColor(R.color.blue);
            case R.id.action_set_bg_green:  return getResources().getColor(R.color.green);
            case R.id.action_set_bg_purple: return getResources().getColor(R.color.purple);
            default:
                return Color.WHITE;
        }
    }

    private void setBackground(int id) {
        backgroundId = id;
        if(backgroundId == R.id.action_set_bg_pattern) {
            Resources res = getActivity().getResources();
            BitmapDrawable bmd = new BitmapDrawable(res, BitmapFactory.decodeResource(res, R.drawable.pattern));
            bmd.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
            canvasView.setBackground(bmd);
        } else {
            canvasView.setBackgroundColor(getColor(id));
        }
    }

    private void redo() {
        painting.redo();
    }

    private void undo() {
        painting.undo();
    }
}
