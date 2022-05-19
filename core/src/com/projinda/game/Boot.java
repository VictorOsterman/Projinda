package com.projinda.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.OrthographicCamera;

/**
 * Boot class, initiates the main menu. Gives the game the possibility to create different windows.
 */
public class Boot extends Game {

    public static Boot INSTANCE;

    private int screenWidth, screenHeight;
    private OrthographicCamera orthographicCamera;

    /**
     * Used to get access to the screen size
     */
    public Boot(){
        INSTANCE = this;
    }

    /**
     *Create orthographic camera and sets screen to MainMenuScree
     */
    @Override
    public void create() {
        screenWidth = Gdx.graphics.getWidth();
        screenHeight= Gdx.graphics.getHeight();
        orthographicCamera = new OrthographicCamera();
        orthographicCamera.setToOrtho(false, screenWidth, screenHeight);
        setScreen(new MainMenuScreen(this));
    }

    /**
     * Sets the screen to the actual game
     */
    public void setGameScreen(){
        setScreen(new GameScreen(orthographicCamera, this));
    }

    /**
     * Sets the screen to GameOverScreen, takes the parameters {@param score} and {@param died} with it to present
     * different backgrounds and the score.
     * @param score the score of this round.
     * @param died to determine if the player died or ran out of time.
     */
    public void setGameOverScreen(int score, boolean died){
        setScreen(new GameOverScreen(this, score, died));
    }

    /**
     * Sets the screen to the main menu
     */
    public void setMainMenuScreen(){
        setScreen(new MainMenuScreen(this));
    }

    /**
     *
     * @return the width of the screen
     */
    public int getScreenWidth() {
        return screenWidth;
    }

    /**
     *
     * @return the height of the screen
     */
    public int getScreenHeight() {
        return screenHeight;
    }
}
