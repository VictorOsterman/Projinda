package com.projinda.game;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Preferences;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Align;

public class GameOverScreen implements Screen {

    //Game Over sprite
    public static final int PADDING = 15;
    public static final int GAME_OVER_WIDTH = 350;
    public static final int GAME_OVER_HEIGHT = 100;
    public static final int GAME_OVER_X_POS = Boot.INSTANCE.getScreenWidth()/2 - GAME_OVER_WIDTH/2;
    public static final int GAME_OVER_Y_POS = Boot.INSTANCE.getScreenHeight() - GAME_OVER_HEIGHT - PADDING;


    private SpriteBatch batch;
    private Boot boot;
    private int score, highScore;

    private Texture gameOverLabel;
    private BitmapFont scoreFont;

    public GameOverScreen(Boot boot, int score){
        batch = new SpriteBatch();
        this.boot = boot;
        this.score = score;

        Preferences preferences = Gdx.app.getPreferences("game");
        this.highScore = preferences.getInteger("highscore",0);

        if (score > highScore){
            preferences.putInteger("highscore", score);
            preferences.flush();
        }

        gameOverLabel = new Texture("game_over.png");
        scoreFont = new BitmapFont();

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

        batch.draw(gameOverLabel, GAME_OVER_X_POS, GAME_OVER_Y_POS, GAME_OVER_WIDTH, GAME_OVER_HEIGHT);
        GlyphLayout scoreLayout = new GlyphLayout(scoreFont,"Your Score: \n" + score, Color.WHITE,0, Align.left,false);
        GlyphLayout highScoreLayout = new GlyphLayout(scoreFont,"Highscore: \n" + highScore, Color.WHITE,0, Align.left,false);
        scoreFont.draw(batch, scoreLayout, Boot.INSTANCE.getScreenWidth()/2 - scoreLayout.width/2, Boot.INSTANCE.getScreenHeight() - GAME_OVER_HEIGHT - PADDING * 2);
        scoreFont.draw(batch, highScoreLayout, Boot.INSTANCE.getScreenWidth()/2 - scoreLayout.width/2, Boot.INSTANCE.getScreenHeight() - GAME_OVER_HEIGHT - scoreLayout.height - PADDING * 3);

        GlyphLayout tryAgain = new GlyphLayout(scoreFont, "Try Again");
        GlyphLayout returnToMenu = new GlyphLayout(scoreFont, "Return to main menu");
        float tryAgainXPos = Boot.INSTANCE.getScreenWidth() / 2 - tryAgain.width/2 ;
        float tryAgainYPos = Boot.INSTANCE.getScreenHeight() / 2 - tryAgain.height/2;
        float returnToMenuXPos = Boot.INSTANCE.getScreenWidth() / 2 - returnToMenu.width/2;
        float returnToMenuYPos = Boot.INSTANCE.getScreenHeight() / 2 - returnToMenu.height/2 - tryAgain.height - PADDING ;

        float mouseX = Gdx.input.getX(), mouseY = Boot.INSTANCE.getScreenHeight() - Gdx.input.getY();

        if(Gdx.input.isTouched()){
            //Try again
            if(mouseX >= tryAgainXPos && mouseX <= tryAgainXPos + tryAgain.width && mouseY >= tryAgainYPos - tryAgain.height && mouseY <= tryAgainYPos){
                boot.setGameScreen();
                return;
            }
            //Go back to main menu
            if(mouseX >= returnToMenuXPos && mouseX <= returnToMenuXPos + returnToMenu.width && mouseY >= returnToMenuYPos - returnToMenu.height && mouseY <= returnToMenuYPos){
                boot.setMainMenuScreen();
                return;
            }
        }
        if(Gdx.input.isKeyPressed(Input.Keys.ESCAPE))
            Gdx.app.exit();

        scoreFont.draw(batch, tryAgain, tryAgainXPos, tryAgainYPos);
        scoreFont.draw(batch, returnToMenu, returnToMenuXPos, returnToMenuYPos);

        batch.end();

    }

    @Override
    public void show() {}
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
}