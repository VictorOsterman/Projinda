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
 * Player class
 * Creates a player object and listens to user input.
 */
public class Player {
    private Body body;
    private GameScreen gameScreen;

    private float startX, startY;
    private float x, y, velX, velY, speed;
    private float width, height;

    private int jumpCounter;

    private Texture texture;

    /**
     * Constructor for player
     * @param width width of the players body
     * @param height height of the players body
     * @param body body to be used by player
     */
    public Player(float width, float height, Body body) {
        this.startX = body.getPosition().x;
        this.startY = body.getPosition().y;

        this.x = body.getPosition().x;
        this.y = body.getPosition().y;

        this.speed = 10;
        this.velX = 0;
        this.velY = 0;

        this.texture = new Texture("badlogic.jpg");
        this.width = width;
        this.height = height;

        this.jumpCounter = 0;

        this.body = body;

        // Skapar sensor med följande form
        PolygonShape shape = new PolygonShape();
        shape.setAsBox((width / 2 - 2) / Const.PPM  , height / 16 / Const.PPM, new Vector2(0, -height/2 / Const.PPM), 0);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 0;
        fixtureDef.isSensor = true;
        this.body.createFixture(fixtureDef).setUserData(ContactType.SENSOR);
    }

    /**
     * Sets the players position to its start position
     */
    public void reset() {
        this.body.setTransform(startX, startY, 0);
    }

    /**
     * Update method of player
     * Updates x and y positions
     * Listens to user input
     */
    public void update() {
        x = body.getPosition().x * Const.PPM - (width / 2);
        y = body.getPosition().y * Const.PPM - (height / 2);

        // Reset velX, when user stops moving the player instantly stops
        // Removing this will result in player "gliding"
        velX = 0;

        manageUserInput();

        body.setLinearVelocity(velX*speed, body.getLinearVelocity().y);
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
    private void manageUserInput() {
        //Walk right
        if(Gdx.input.isKeyPressed(Input.Keys.RIGHT))
            velX = 1;
        //Walk left
        if(Gdx.input.isKeyPressed(Input.Keys.LEFT))
            velX = -1;
        //Jump up
        if(Gdx.input.isKeyJustPressed(Input.Keys.UP) && jumpCounter < 2) {
            body.setLinearVelocity(body.getLinearVelocity().x, 0);
            body.applyLinearImpulse(new Vector2(0, 15), body.getPosition(), true);
            jumpCounter ++;
        }

        if(body.getLinearVelocity().y == 0) {
            jumpCounter = 0;
        }
        //Dash down
        if(Gdx.input.isKeyPressed(Input.Keys.DOWN))
            body.applyForceToCenter(0, -1500, true);
        //Reset
        if(Gdx.input.isKeyPressed(Input.Keys.R))
            reset();
        //Run
        if(Gdx.input.isKeyPressed(Input.Keys.SPACE))
            velX *= 1.5;
        //Dash to side
        if(Gdx.input.isKeyJustPressed(Input.Keys.D))
            velX *= 8;
    }

    public void render(SpriteBatch batch) {
        batch.draw(texture, x, y, width, height);
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public Body getBody() {
        return body;
    }
}
