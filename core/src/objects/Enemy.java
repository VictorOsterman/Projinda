package objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.projinda.game.Boot;
import com.projinda.game.GameScreen;

import java.util.Random;

/**
 * Enemy class
 * Chases after player
 */
public class Enemy extends MovingRectangle{
    /**
     * Constructor for moving rectangle
     *
     * @param width  width of the players body
     * @param height height of the players body
     * @param body   body to be used by player
     */
    public Enemy(float width, float height, Body body, GameScreen gameScreen) {
        super(width, height, body, gameScreen);
        this.speedLevel = 0.5F;

        //TANKE
        // Dumt att ändra speed variabeln
        // Borde: låt moving rectangle ha en variabel velocity.
        // I update: sätt velocity*velX och velocity*velY istället.cd
        this.texture = new Texture("cop.png");
        this.lives = 3;
        this.className = "Enemy";

        addSensor();
    }

    @Override
    public void update() {

        if(lives <= 0 && !destroyed) {
            removeMovingRectangle();
            generateCoin();
            return;
        }
        else if (!destroyed) {
            Gdx.app.log(String.valueOf(lives), "");
            super.update();
            moveEnemy();
            body.setLinearVelocity(directionX*speedLevel*speed, body.getLinearVelocity().y);
        }
    }

    /**
     * Move the enemy
     * If the enemy is close to the player, the enemy chases the player
     * If the enemy is too far away from the player, the enemy just wanders around
     */
    private void moveEnemy() {
        Player player = gameScreen.getPlayer();
        //Check if player is too far away in x-coordinates
        if(player.getX() < x - Boot.INSTANCE.getScreenWidth() / 2 || player.getX() > x + Boot.INSTANCE.getScreenWidth() / 2) {
            wanderAround();
        }
        //Check if player is too far away in y-coordinates
        else if(player.getY() < y - Boot.INSTANCE.getScreenHeight() / 2 || player.getY() > y + Boot.INSTANCE.getScreenHeight() / 2) {
            wanderAround();
        }
        else {
            moveAccordingToPlayer();
        }
    }

    /**
     * Make the enemy "wander around"
     * Moves slower and every other second it might change direction
     */
    private void wanderAround() {
        // Lower the speed of the enemy
        speedLevel = 0.25F;
        // Every other second set the directionX randomly to either 1 or -1
        if((gameScreen.getScoreBoard().isNewSecond()) && gameScreen.getScoreBoard().getWorldTimer() % 2 == 0) {
            Random rng = new Random();
            directionX = rng.nextInt(2) == 0 ? -1 : 1;
        }
    }
    /**
     * Chases the player
     * Gets the correct directionX
     * If the player is higher up than the enemy the enemy jumps
     */
    private void moveAccordingToPlayer() {
        speedLevel = 0.5F;
        Player player = gameScreen.getPlayer();
        // Change the direction to point to the player
        if(player.getX() > x + 3)
            directionX = 1;
        else if (player.getX() < x - 3) {
            directionX = -1;
        }

        // Every other second
        if(gameScreen.getScoreBoard().isNewSecond() && gameScreen.getScoreBoard().getWorldTimer() % 2 == 0) {
            // If the player is higher up than the enemy, -1 is there to make sure enemy doesn't jump when they are
            // equall high up
            if(player.getY() - 1> y) {
                body.setLinearVelocity(body.getLinearVelocity().x, 15);
            }
        }

    }


}
