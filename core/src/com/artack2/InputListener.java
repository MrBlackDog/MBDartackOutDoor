package com.artack2;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputProcessor;

final class InputListener implements InputProcessor {

    private Cam camera;

    InputListener(Cam camera){
        this.camera=camera;
    }

    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    public boolean keyDown(int keycode) {
        return keycode == Input.Keys.BACK;
    }

    public boolean keyUp(int keycode) {
        return false;
    }


    public boolean keyTyped(char character) {
        return false;
    }


    public boolean touchUp(int screenX, int screenY, int pointer, int button){
        return false;
    }


    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }


    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    public boolean scrolled(int amount) {
        return false;
    }
}
