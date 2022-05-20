package helper;

/**
 * PPM - scaling factor pixels per meter
 *
 * Contains shorts used for collision filtering
 */
public class Const {
    public static final float PPM = 32;

    public static final short PLAYER_BIT = 1;
    public static final short ENEMY_BIT = 2;
    public static final short PLATFORM_BIT = 4;
    public static final short BULLET_BIT = 8;
    public static final short COIN_BIT = 16;
    public static final short SAFE_BIT = 32;
}
