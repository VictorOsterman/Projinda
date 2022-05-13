package helper;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.Shape;
import com.projinda.game.GameScreen;
import objects.Enemy;
import objects.MovingPlatform;
import objects.Player;
import objects.Safe;

import static helper.Const.PPM;

public class TiledMapHelper {

    private TiledMap tiledMap;
    private GameScreen gameScreen;
    private Vector2 tileSize = new Vector2();
    private Vector2 mapSize = new Vector2();

    public TiledMapHelper(GameScreen gameScreen){
        this.gameScreen = gameScreen;
    }

    public OrthogonalTiledMapRenderer setupMap(){
        tiledMap = new TmxMapLoader().load("maps/map0.tmx");
        parseObjects(tiledMap.getLayers().get("Objects").getObjects());
        tileSize.x = (int) tiledMap.getProperties().get("tilewidth");
        tileSize.y = (int) tiledMap.getProperties().get("tileheight");

        mapSize.x = (int) tiledMap.getProperties().get("width");
        mapSize.y = (int) tiledMap.getProperties().get("height");

        return new OrthogonalTiledMapRenderer(tiledMap);
    }

    private void parseObjects(MapObjects mapObjects){
        for(MapObject object : mapObjects){
            if(object instanceof PolygonMapObject){
                createStaticBody((PolygonMapObject) object);
            }

            //Player objects have a rectangle shape
            if(object instanceof RectangleMapObject) {
                Rectangle rectangle = ((RectangleMapObject) object).getRectangle();
                String rectangleName = object.getName();

                //Check if the map object is a player
                if(rectangleName.equals("player")) {
                    //Create a body with position and size specified in tiled map
                    Body body = BodyHelper.createBody(
                            rectangle.getX() + rectangle.getWidth() / 2,
                            rectangle.getY() + rectangle.getHeight() / 2,
                            rectangle.getWidth(),
                            rectangle.getHeight(),
                            false,
                            0,
                            gameScreen.getWorld(),
                            ContactType.PLAYER
                    );
                    gameScreen.setPlayer(new Player(rectangle.getWidth(), rectangle.getHeight(), body, gameScreen));
                }
                //Check if the map object is an enemy
                else if (rectangleName.equals("enemy")) {
                    //Create a body with position and size specified in tiled map
                    Body body = BodyHelper.createBody(
                            rectangle.getX() + rectangle.getWidth() / 2,
                            rectangle.getY() + rectangle.getHeight() / 2,
                            rectangle.getWidth(),
                            rectangle.getHeight(),
                            false,
                            0,
                            gameScreen.getWorld(),
                            ContactType.ENEMY
                    );
                    gameScreen.addMovingRectangle(new Enemy(rectangle.getWidth(), rectangle.getHeight(), body, gameScreen));
                }
                // Check if the map object is a safe
                else if (rectangleName.equals("safe")) {
                    //Create a body with correct posistion and size
                    Body body = BodyHelper.createBody(
                            rectangle.getX() + rectangle.getWidth() / 2,
                            rectangle.getY() + rectangle.getHeight() / 2,
                            rectangle.getWidth(),
                            rectangle.getHeight(),
                            true,
                            0,
                            gameScreen.getWorld(),
                            ContactType.SAFE
                    );
                    gameScreen.addMoneyItem(new Safe(rectangle.getWidth(), rectangle.getHeight(), body, gameScreen));
                }
                // Check if the map object is a moving platform
                else if (rectangleName.contains("movingplatform")) {
                    int direction = 2;
                    if (rectangleName.contains("side"))
                        direction = 0;
                    else if(rectangleName.contains("up"))
                        direction = 1;
                    //Create a body with correct posistion and size
                    Body body = BodyHelper.createBody(
                            rectangle.getX() + rectangle.getWidth() / 2,
                            rectangle.getY() + rectangle.getHeight() / 2,
                            rectangle.getWidth(),
                            rectangle.getHeight(),
                            false,
                            99999999,   // Makes the platform "immovable" in collisions
                            gameScreen.getWorld(),
                            ContactType.MOVINGPLATFORM
                    );
                    gameScreen.addMovingRectangle(new MovingPlatform(rectangle.getWidth(), rectangle.getHeight(), body, gameScreen, direction));
                }
            }
        }
    }

    private void createStaticBody(PolygonMapObject mapObject){
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.StaticBody;
        Body body = gameScreen.getWorld().createBody(bodyDef);
        Shape shape = createPolygonShape(mapObject);
        body.createFixture(shape, 1000);
        shape.dispose();

    }

    private Shape createPolygonShape(PolygonMapObject mapObject) {
        float[] vertices = mapObject.getPolygon().getTransformedVertices();
        Vector2[] worldVertices = new Vector2[vertices.length/2];

        for(int i = 0; i < vertices.length / 2; i++){
            Vector2 current = new Vector2(vertices[i * 2] / PPM, vertices[i * 2 + 1]/PPM);
            worldVertices[i] = current;
        }

        PolygonShape shape = new PolygonShape();
        shape.set(worldVertices);
        return shape;
    }

    public Vector2 getTileSize() {
        return tileSize;
    }

    public Vector2 getMapSize() {
        return mapSize;
    }
}
