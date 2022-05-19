package helper;
import com.badlogic.gdx.physics.box2d.*;

/**
 * Helper class used to create bodies.
 * Used to create players and enemies.
 */
public class BodyHelper {

    /**
     * Creates bodies
     * @param x the x position to place the the body
     * @param y the y position to place the the body
     * @param width the width of the body
     * @param height the height of the body
     * @param isStatic whether or not the body is static
     * @param density the density of the body
     * @param world the world object
     * @param type which contact type
     * @return a body
     */
    public static Body createBody(float x, float y, float width, float height, boolean isStatic, float density, World world, ContactType type){
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = !isStatic ? BodyDef.BodyType.DynamicBody: BodyDef.BodyType.StaticBody;
        bodyDef.position.set(x/Const.PPM, y/Const.PPM);
        Body body = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();
        shape.setAsBox(width / 2 / Const.PPM, height / 2 / Const.PPM);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = density;
        fixtureDef.friction = 0;
        body.createFixture(fixtureDef).setUserData(type);

        shape.dispose();
        return body;

    }
}
