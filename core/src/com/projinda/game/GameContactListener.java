package com.projinda.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.*;
import helper.ContactType;
import objects.*;

/**
 * The game's contact listener
 * Checks if player collides with any objects
 */
public class GameContactListener implements ContactListener {

    private GameScreen gameScreen;
    public GameContactListener(GameScreen gameScreen) { this.gameScreen = gameScreen; }

    @Override
    public void beginContact(Contact contact) {
        Gdx.app.log("Contact begun", "");

        Fixture a = contact.getFixtureA();
        Fixture b = contact.getFixtureB();

        Gdx.app.log(String.valueOf(a.getUserData()), String.valueOf(b.getUserData()));


        //If any of the involved objects is the player's sensor, the player is standing
        // on something and the jump counter should be reset
        if(a.getUserData() == ContactType.PLAYERSENSOR || b.getUserData() == ContactType.PLAYERSENSOR) {
            gameScreen.getPlayer().resetJumpCounter();

            Fixture notPlayer = b;
            if(b.getUserData() == ContactType.PLAYERSENSOR) {
                notPlayer = a;
            }

            if(a.getUserData() == ContactType.SAFE || b.getUserData() == ContactType.SAFE) {
                // Player is standing on safe, find safe and retrieve money.
                ((Safe) gameScreen.getMatchingMoneyItem(notPlayer.getBody().getPosition().x, notPlayer.getBody().getPosition().y)).collect();
            }
            else if(a.getUserData() == ContactType.MOVINGPLATFORM || b.getUserData() == ContactType.MOVINGPLATFORM) {
                // Player is standing on platform, find platform and set player's platform to this platform
                MovingPlatform movingPlatform = (MovingPlatform) gameScreen.getMatchingRectangle(notPlayer.getBody().getPosition().x, notPlayer.getBody().getPosition().y);
                gameScreen.getPlayer().setOnRectangle(movingPlatform);
            }
            else if(a.getUserData() == ContactType.ENEMY || b.getUserData() == ContactType.ENEMY) {
                // Player is standing on platform, find platform and set player's platform to this platform
                Enemy enemy = (Enemy) gameScreen.getMatchingRectangle(notPlayer.getBody().getPosition().x, notPlayer.getBody().getPosition().y);
                gameScreen.getPlayer().setOnRectangle(enemy);
            }
        }

        if(a.getUserData() == ContactType.PLAYER || b.getUserData() == ContactType.PLAYER) {
            if(a.getUserData() == ContactType.PLAYERBULLET || b.getUserData() == ContactType.PLAYERBULLET) {
                return;
            }
            Gdx.app.log("2Collision with player and bullet", "");
            Fixture notPlayer = b;
            if(b.getUserData() == ContactType.PLAYER) {
                notPlayer = a;
            }
            if(a.getUserData() == ContactType.ENEMY || b.getUserData() == ContactType.ENEMY) {
                // Player is in contact with enemy.
                // If the player is not standing on the enemy it should die.
                // Meaning, if the players lowest y is below enemy's highest y
                // Get the matching enemy
                Enemy enemy = (Enemy) gameScreen.getMatchingRectangle(notPlayer.getBody().getPosition().x, notPlayer.getBody().getPosition().y);

                // Fixes bug when player is below enemy outside camera's scope
                if(enemy == null)
                    return;

                //If the player's bottom y is below enemy's bottom y (+30) and the player is not dashing downards, the player is killed by the enemy
                if(gameScreen.getPlayer().getY() < enemy.getY() + 30 && gameScreen.getPlayer().getBody().getLinearVelocity().y > -100) {
                    gameScreen.getPlayer().setIsDead(true);
                }
            }
            if(a.getUserData() == ContactType.COIN || b.getUserData() == ContactType.COIN) {
                // Player is in contact with coin
                Coin coin = ((Coin) gameScreen.getMatchingMoneyItem(notPlayer.getBody().getPosition().x, notPlayer.getBody().getPosition().y));
                if(coin != null) {
                    coin.collect();
                    Gdx.app.log(String.valueOf(coin.getValue()), "");
                    Gdx.app.log("Collected coin", "");
                }
                else {
                    Gdx.app.log("Coin is null", "");
                }
            }
        }

        if(a.getUserData() == ContactType.PLAYERBULLET || b.getUserData() == ContactType.PLAYERBULLET) {
            Fixture notBullet = b;
            Fixture bulletFix = a;
            if(b.getUserData() == ContactType.PLAYERBULLET) {
                notBullet = a;
                bulletFix = b;
            }

            // If the bullet hit an enemy, the enemy's lives should be lowered
            if(a.getUserData() == ContactType.ENEMY || b.getUserData() == ContactType.ENEMY) {
                Enemy enemy = (Enemy) gameScreen.getMatchingRectangle(notBullet.getBody().getPosition().x, notBullet.getBody().getPosition().y);
                // Fixes bug when player is below enemy outside camera's scope
                if(enemy == null)
                    return;
                enemy.lowerLives();
            }

            // If the bullet hit the safe, the safe should crack
            if(a.getUserData() == ContactType.SAFE || b.getUserData() == ContactType.SAFE) {
                // Player is standing on safe, find safe and retrieve money.
                ((Safe) gameScreen.getMatchingMoneyItem(notBullet.getBody().getPosition().x, notBullet.getBody().getPosition().y)).collect();
            }

            //After the bullet has hit anything it should be removed, do this by lowering its lives
            Bullet bullet = (Bullet) gameScreen.getMatchingRectangle(bulletFix.getBody().getPosition().x, bulletFix.getBody().getPosition().y);
            // Fixes bug
            if(bullet == null)
                return;
            bullet.lowerLives();

        }
    }

    @Override
    public void endContact(Contact contact) {
        Gdx.app.log("Contact ended", "");
        Fixture a = contact.getFixtureA();
        Fixture b = contact.getFixtureB();

        if(a.getUserData() == ContactType.PLAYERSENSOR || b.getUserData() == ContactType.PLAYERSENSOR) {
            gameScreen.getPlayer().resetJumpCounter();
            if(a.getUserData() == ContactType.MOVINGPLATFORM || b.getUserData() == ContactType.MOVINGPLATFORM) {
                // Player is no longer standing on platform, set boolean onPlatform to false
                gameScreen.getPlayer().setOnRectangle(false);
            }
            else if(a.getUserData() == ContactType.ENEMY || b.getUserData() == ContactType.ENEMY) {
                gameScreen.getPlayer().setOnRectangle(false);
            }
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
        Gdx.app.log("presolve", "");
        Fixture a = contact.getFixtureA();
        Fixture b = contact.getFixtureB();
        Gdx.app.log(String.valueOf(a.getUserData()), String.valueOf(b.getUserData()));
    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
        Gdx.app.log("postsolve", "");
    }
}
