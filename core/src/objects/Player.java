package objects;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.projinda.game.Boot;
import com.projinda.game.GameScreen;
import helper.Const;
import helper.BodyHelper;
import helper.ContactType;

/**
 * Player class, extends MovingRectangle class
 * Creates a player object and listens to user input.
 */
public class Player extends MovingRectangle{


    /**
     * Constructor for player
     * @param width width of the players body
     * @param height height of the players body
     * @param body body to be used by player
     */

    private int score;
    private boolean onRectangle;
    private MovingRectangle movingRectangle;
    public Player(float width, float height, Body body, GameScreen gameScreen) {
        super(width, height, body, gameScreen);
        this.score = 0;
        this.onRectangle = false;
        this.texture = new Texture("player.png");
        addSensor();
        this.lives = 3;
    }

    @Override
    public void addSensor() {
        // Skapar sensor med följande form
        PolygonShape shape = new PolygonShape();
        //Narrower width than movingRectangle's sensor
        shape.setAsBox((width / 2 - 10) / Const.PPM  , height / 16 / Const.PPM, new Vector2(0, -height/2 / Const.PPM), 0);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 0;
        fixtureDef.isSensor = true;
        this.body.createFixture(fixtureDef).setUserData(ContactType.PLAYERSENSOR);
    }

    /**
     * Update method of player
     * Updates x and y positions
     * Listens to user input
     */
    @Override
    public void update(){
        directionX = 0;
        super.update();
        float velocityX = directionX*speedLevel;
        // If the player is standing on a platform, the platforms velocity is the player's "base velocity"
        if(onRectangle) {
            Gdx.app.log("player is still on rectangle", "");
            velocityX = directionX*speedLevel + movingRectangle.directionX* movingRectangle.speedLevel;
        }
        body.setLinearVelocity(velocityX*speed, body.getLinearVelocity().y);
    }

    /**
     * Listens to user input
     *
     * KEY - ACTION
     * RIGHT - WALKS RIGHT
     * LEFT - WALKS LEFT
     * UP - JUMPS UP
     * DOWN - DASHES DOWNWARDS
     * R - RESETS PLAYER
     * SPACE - RUNS
     * D - DASHES
     */
    @Override
    public void manageUserInput() {
        super.manageUserInput();
        //Temporary reset button
        if(Gdx.input.isKeyJustPressed(Input.Keys.G))
            reset();
        //Walk right
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT))
            directionX = 1;
        //Walk left
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT))
            directionX = -1;
        //Jump up
        if(Gdx.input.isKeyJustPressed(Input.Keys.UP) && jumpCounter < 2) {
            body.setLinearVelocity(body.getLinearVelocity().x, 0);
            body.applyLinearImpulse(new Vector2(0, 15), body.getPosition(), true);
            jumpCounter ++;
        }
        //Dash down
        if(Gdx.input.isKeyPressed(Input.Keys.DOWN))
            body.applyForceToCenter(0, -1500, true);
        //Run
        if(Gdx.input.isKeyPressed(Input.Keys.SPACE))
            directionX *= 1.5;
        //Dash to side
        if(Gdx.input.isKeyJustPressed(Input.Keys.D))
            directionX *= 8;
    }

    public int getScore() { return score; }
    public void increaseScore(int newScore) { score = newScore; }

    public void setOnRectangle(MovingRectangle movingRectangle) {
        this.movingRectangle = movingRectangle;
        this.onRectangle = true;
    }

    public void setOnRectangle(boolean onRectangle) {
        this.onRectangle = onRectangle;
    }

}
