package com.projinda.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;

public class Boot extends Game {

    public static Boot INSTANCE;

    private int screenWidth, screenHeight;
    private OrthographicCamera orthographicCamera;

    public Boot(){
        INSTANCE = this;
    }

    @Override
    public void create() {
        screenWidth = Gdx.graphics.getWidth();
        screenHeight= Gdx.graphics.getHeight();
        orthographicCamera = new OrthographicCamera();
        orthographicCamera.setToOrtho(false, screenWidth, screenHeight);
        setScreen(new MainMenuScreen(this));
        //setScreen(new GameScreen(orthographicCamera));
    }

    public void setGameScreen(){
        setScreen(new GameScreen(orthographicCamera, this));
    }

    public void setGameOverScreen(int score){
        setScreen(new GameOverScreen(this, score));
    }

    public void setMainMenuScreen(){
        setScreen(new MainMenuScreen(this));
    }

    public int getScreenWidth() {
        return screenWidth;
    }

    public int getScreenHeight() {
        return screenHeight;
    }
}
