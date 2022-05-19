package helper;
import com.badlogic.gdx.physics.box2d.*;

/**
 * Helper class used to create bodies.
 * Used to create players and enemies.
 */
public class BodyHelper {

    public static Body createBody(float x, float y, float width, float height, boolean isStatic, float density,
                                  World world, ContactType type, short cBits, short mBits, short gIndex){
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

        fixtureDef.filter.categoryBits = cBits; // Is a
        fixtureDef.filter.maskBits = mBits;     // Collides with
        fixtureDef.filter.groupIndex = gIndex;      //gIndex; // 0 - ignore, same - number -> never collide, same + number -> always collide

        body.createFixture(fixtureDef).setUserData(type);

        shape.dispose();
        return body;


    }
}
