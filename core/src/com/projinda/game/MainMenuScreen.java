package com.projinda.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

/**
 * the game's main menu
 */
public class MainMenuScreen implements Screen {

    public static final int ICON_WIDTH = 150;
    public static final int ICON_HEIGHT = 100;

    public static final int EXIT_BUTTON_WIDTH_POS = Boot.INSTANCE.getScreenWidth()/2 - ICON_WIDTH/2;
    public static final int EXIT_BUTTON_HEIGHT_POS = Boot.INSTANCE.getScreenHeight()/2 - Boot.INSTANCE.getScreenHeight()/4;
    public static final int PLAY_BUTTON_WIDTH_POS = Boot.INSTANCE.getScreenWidth()/2 - ICON_WIDTH/2;
    public static final int PLAY_BUTTON_HEIGHT_POS = Boot.INSTANCE.getScreenHeight()/2 + Boot.INSTANCE.getScreenHeight()/4;
    private SpriteBatch batch;
    private Boot boot;

    private Texture playButtonActive;
    private Texture playButtonInactive;
    private Texture exitButtonActive;
    private Texture exitButtonInactive;
    private Texture backgroundImage = new Texture("maps/background_main.png");

    /**
     * The class constructor
     * @param boot able to send the player back to main menu or the game screen
     */
    public MainMenuScreen(Boot boot){

        this.batch = new SpriteBatch();
        this.boot = boot;

        this.playButtonActive = new Texture("play_button_active.png");
        this.playButtonInactive = new Texture("play_button_inactive.png");
        this.exitButtonActive = new Texture("exit_button_active.png");
        this.exitButtonInactive = new Texture("exit_button_inactive.png");

    }

    /**
     * Called when the screen should render itself.
     *
     * @param delta The time in seconds since the last render.
     */
    @Override
    public void render(float delta) {

        Gdx.gl.glClearColor(0,0,0,1);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        batch.begin();
        batch.draw(backgroundImage,0, 0, Boot.INSTANCE.getScreenWidth(),Boot.INSTANCE.getScreenHeight());

        if(Gdx.input.getX() >= PLAY_BUTTON_WIDTH_POS && Gdx.input.getX() <= PLAY_BUTTON_WIDTH_POS + ICON_WIDTH && Boot.INSTANCE.getScreenHeight() - Gdx.input.getY() >= PLAY_BUTTON_HEIGHT_POS && Boot.INSTANCE.getScreenHeight() - Gdx.input.getY() <= PLAY_BUTTON_HEIGHT_POS + ICON_HEIGHT){
            batch.draw(playButtonActive,PLAY_BUTTON_WIDTH_POS, PLAY_BUTTON_HEIGHT_POS, ICON_WIDTH,ICON_HEIGHT);
            if(Gdx.input.isTouched()){
                try {
                    Thread.sleep(150);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                boot.setGameScreen();
                return;
            }
        }else{
            batch.draw(playButtonInactive, PLAY_BUTTON_WIDTH_POS, PLAY_BUTTON_HEIGHT_POS, ICON_WIDTH, ICON_HEIGHT);
        }

        if(Gdx.input.getX() >= EXIT_BUTTON_WIDTH_POS && Gdx.input.getX() <= EXIT_BUTTON_WIDTH_POS + ICON_WIDTH && Boot.INSTANCE.getScreenHeight() - Gdx.input.getY() >= EXIT_BUTTON_HEIGHT_POS && Boot.INSTANCE.getScreenHeight() - Gdx.input.getY() <= EXIT_BUTTON_HEIGHT_POS + ICON_HEIGHT){
            batch.draw(exitButtonActive,EXIT_BUTTON_WIDTH_POS, EXIT_BUTTON_HEIGHT_POS, ICON_WIDTH,ICON_HEIGHT);
            if(Gdx.input.isTouched()){
                try {
                    Thread.sleep(150);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Gdx.app.exit();
            }
        }else{
            batch.draw(exitButtonInactive, EXIT_BUTTON_WIDTH_POS, EXIT_BUTTON_HEIGHT_POS, ICON_WIDTH, ICON_HEIGHT);
        }


        batch.end();
    }

    @Override
    public void resize(int width, int height) {}
    @Override
    public void pause() {}
    @Override
    public void resume() {}
    @Override
    public void hide() {}
    @Override
    public void dispose() {}
    @Override
    public void show() {}

}
