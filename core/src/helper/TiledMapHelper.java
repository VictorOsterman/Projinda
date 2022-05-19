package helper;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.objects.PolygonMapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import com.projinda.game.GameScreen;
import objects.Enemy;
import objects.MovingPlatform;
import objects.Player;
import objects.Safe;

import static helper.Const.PPM;

/**
 * TiledMapHelper helps the tiled map file to be placed on the game screen as well as making objects where there
 * should be object
 */
public class TiledMapHelper {

    private TiledMap tiledMap;
    private GameScreen gameScreen;
    private Vector2 tileSize = new Vector2();
    private Vector2 mapSize = new Vector2();

    public TiledMapHelper(GameScreen gameScreen){
        this.gameScreen = gameScreen;
    }

    /**
     * sets up the map
     * @return orthogonalTiledMapRenderer object, containing the map
     */
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
                            (float) 0,
                            gameScreen.getWorld(),
                            ContactType.PLAYER,
                            Const.PLAYER_BIT,
                            (short) (Const.PLAYER_BIT | Const.ENEMY_BIT | Const.PLATFORM_BIT | Const.COIN_BIT | Const.SAFE_BIT),
                            (short) 1
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
                            99999999,
                            gameScreen.getWorld(),
                            ContactType.ENEMY,
                            Const.ENEMY_BIT,
                            (short) (Const.PLAYER_BIT | Const.PLATFORM_BIT | Const.SAFE_BIT | Const.BULLET_BIT),
                            (short) -1
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
                            ContactType.SAFE,
                            Const.SAFE_BIT,
                            (short) (Const.PLAYER_BIT | Const.ENEMY_BIT | Const.BULLET_BIT | Const.COIN_BIT),
                            (short) 0
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
                            ContactType.MOVINGPLATFORM,
                            Const.PLATFORM_BIT,
                            (short) (Const.PLAYER_BIT | Const.ENEMY_BIT | Const.BULLET_BIT | Const.COIN_BIT),
                            (short) 0
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

    /**
     * x - width
     * y - height
     * @return A vector2 containing the tile size.
     */
    public Vector2 getTileSize() {
        return tileSize;
    }

    /**
     * x - map width
     * y- map height
     * @return the map size
     */
    public Vector2 getMapSize() {
        return mapSize;
    }
}
