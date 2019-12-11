package com.artack2;

public class CB implements WebSocketGolos.Callback {

    @Override
    public void callingBack(String[] data) {
        Main.ws = data;
    }
}
