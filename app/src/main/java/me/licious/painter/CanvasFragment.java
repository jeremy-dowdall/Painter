package me.licious.painter;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import static me.licious.painter.Painting.Command;

public class CanvasFragment extends Fragment implements ActionHandler {

    private static final String BACKGROUND_COLOR = "background.color";

    private Painting painting;
    private ImageView canvasView;
    private int backgroundColor;

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
            case R.id.action_set_bg_purple:  setBackground(id); return true;
        }
        return false;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setRetainInstance(true);

        if(savedInstanceState == null) {
            backgroundColor = Color.WHITE;
        } else {
            backgroundColor = savedInstanceState.getInt(BACKGROUND_COLOR, Color.WHITE);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(painting == null) {
            painting = new Painting(getActivity());
        }
        canvasView = (ImageView) getActivity().findViewById(R.id.canvas_view);
        canvasView.setBackgroundColor(backgroundColor);
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
        backgroundColor = getColor(id);
        canvasView.setBackgroundColor(backgroundColor);
    }

    private void redo() {
        painting.redo();
    }

    private void undo() {
        painting.undo();
    }
}
