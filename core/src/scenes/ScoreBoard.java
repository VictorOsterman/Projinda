package scenes;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.scenes.scene2d.ui.Table;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import com.projinda.game.Boot;


public class ScoreBoard {
    public Stage stage;
    private Viewport viewport;

    private Integer worldTimer;
    private float timeCount;
    private Integer score;
    private Integer lives;

    private boolean newSecond;

    Label countdownLabel;
    Label scoreLabel;
    Label timeLabel;
    Label livesLabel;
    Label livesDescLabel;
    Label playerLabel;

    public ScoreBoard(SpriteBatch sb) {
        worldTimer = 300;
        timeCount = 0;
        score = 0;
        lives = 3;

        viewport = new FitViewport(Boot.INSTANCE.getScreenWidth(), Boot.INSTANCE.getScreenHeight(), new OrthographicCamera());
        stage = new Stage(viewport, sb);

        Table table = new Table();
        table.top();
        table.setFillParent(true);

        countdownLabel = new Label(String.format("%03d", worldTimer), new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        scoreLabel = new Label(String.format("%04d", score), new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        timeLabel = new Label("TIME", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        livesLabel = new Label(String.format("%01d", lives), new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        livesDescLabel = new Label("Lives:", new Label.LabelStyle(new BitmapFont(), Color.WHITE));
        playerLabel = new Label("Score", new Label.LabelStyle(new BitmapFont(), Color.WHITE));

        table.add(playerLabel).expandX().padTop(10);
        table.add(livesDescLabel).expandX().padTop(10);
        table.add(timeLabel).expandX().padTop(10);
        table.row();
        table.add(scoreLabel).expandX();
        table.add(livesLabel).expandX();
        table.add(countdownLabel).expandX();

        stage.addActor(table);

        newSecond = false;
    }

    public void update (float dt, int playerScore, int playerLives) {

        timeCount += dt;
        if(timeCount >= 1) {
            worldTimer --;
            countdownLabel.setText(String.format("%03d", worldTimer));
            timeCount = 0;
            newSecond = true;
        }
        else {
            newSecond = false;
        }

        if(score != playerScore) {
            score = playerScore;
            scoreLabel.setText(String.format("%04d", score));
        }

        if(lives != playerLives) {
            lives = playerLives;
            livesLabel.setText(String.format("%01d", lives));
        }

    }

    public Integer getWorldTimer() {
        return worldTimer;
    }

    public Integer getScore() {
        return score;
    }

    public Integer getLives(){
        return lives;
    }

    public boolean isNewSecond() { return newSecond; }
}
