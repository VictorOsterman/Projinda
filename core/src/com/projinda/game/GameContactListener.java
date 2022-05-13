package com.projinda.game;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.physics.box2d.*;
import helper.ContactType;
import objects.MovingPlatform;
import objects.Safe;

/**
 * The game's contact listener
 * Checks if player collides with any objects
 */
public class GameContactListener implements ContactListener {

    private GameScreen gameScreen;
    public GameContactListener(GameScreen gameScreen) { this.gameScreen = gameScreen; }

    @Override
    public void beginContact(Contact contact) {
        Fixture a = contact.getFixtureA();
        Fixture b = contact.getFixtureB();

        //If any of the involved objects is the player's sensor, the player is standing
        // on something and the jump counter should be reset
        if(a.getUserData() == ContactType.PLAYERSENSOR || b.getUserData() == ContactType.PLAYERSENSOR) {
            gameScreen.getPlayer().resetJumpCounter();
            if(a.getUserData() == ContactType.SAFE || b.getUserData() == ContactType.SAFE) {
                Fixture safe = b;
                if(a.getUserData() == ContactType.SAFE) {
                    safe = a;
                }
                // Player is standing on safe, find safe and retrieve money.
                ((Safe) gameScreen.getMatchingMoneyItem(safe.getBody().getPosition().x, safe.getBody().getPosition().y)).collect();
                //gameScreen.getMatchingSafe().collect();
                Gdx.app.log("Safe in contact", "");
            }
            else if(a.getUserData() == ContactType.MOVINGPLATFORM || b.getUserData() == ContactType.MOVINGPLATFORM) {
                Fixture platform = b;
                if(a.getUserData() == ContactType.MOVINGPLATFORM) {
                    platform = a;
                }
                // Player is standing on platform, find platform and set player's platform to this platform
                MovingPlatform movingPlatform = (MovingPlatform) gameScreen.getMatchingRectangle(platform.getBody().getPosition().x, platform.getBody().getPosition().y);
                gameScreen.getPlayer().setOnPlatform(movingPlatform);
            }
        }

        if(a.getUserData() == ContactType.PLAYER || b.getUserData() == ContactType.PLAYER) {
            if(a.getUserData() == ContactType.ENEMY || b.getUserData() == ContactType.ENEMY) {
                gameScreen.getPlayer().lowerLives();
                //gameScreen.getPlayer().reset();
            }
        }

    }

    @Override
    public void endContact(Contact contact) {
        Fixture a = contact.getFixtureA();
        Fixture b = contact.getFixtureB();

        if(a.getUserData() == ContactType.PLAYERSENSOR || b.getUserData() == ContactType.PLAYERSENSOR) {
            gameScreen.getPlayer().resetJumpCounter();
            if(a.getUserData() == ContactType.MOVINGPLATFORM || b.getUserData() == ContactType.MOVINGPLATFORM) {
                // Player is no longer standing on platform, set boolean onPlatform to false
                gameScreen.getPlayer().setOnPlatform(false);
            }
        }
    }

    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {

    }

    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {

    }
}
