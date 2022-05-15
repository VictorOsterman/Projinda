package objects;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.physics.box2d.Body;
import com.projinda.game.GameScreen;

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
        velX = 0;
        speed = 5;
        //TANKE
        // Dumt att ändra speed variabeln
        // Borde: låt moving rectangle ha en variabel velocity.
        // I update: sätt velocity*velX och velocity*velY istället.cd 
        this.texture = new Texture("cop.png");
    }

    @Override
    public void update() {
        super.update();
        velX = 0;

        moveAccordingToPlayer();

        body.setLinearVelocity(velX*speed, body.getLinearVelocity().y);
    }

    private void moveAccordingToPlayer() {

        Player player = gameScreen.getPlayer();
        if(player.getX() > x + 3)
            velX = 1;
        else if (player.getX() < x - 3) {
            velX = -1;
        }
    }
}
