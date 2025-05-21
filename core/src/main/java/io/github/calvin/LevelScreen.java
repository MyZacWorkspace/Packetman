package io.github.calvin;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;

import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.math.Vector2;

import com.badlogic.gdx.graphics.Pixmap;

import com.badlogic.gdx.controllers.*;

//TILED MAP Stuff
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;

public class LevelScreen implements Screen, ControllerListener, ContactListener {
    Calvin game;
    World world;
    Box2DDebugRenderer b2ddr;
    OrthographicCamera orthoCamera;
    float totalElapsedTime = 0.0f;

    //Sprites
    PlayerSprite player;

    //Sample Player World-Object
    BodyDef playerBodyDef;
    Body playerBody;
    PolygonShape playerShape;
    FixtureDef playerFixtureDef;
    Fixture playerFixture;

    //Sample Ball
    /*
    BodyDef bodyDef;
    Body body;
    CircleShape circle;
    FixtureDef fixtureDef;
    Fixture fixture;
    */
    
    //Sample Ground
    BodyDef groundBodyDef;
    Body groundBody;
    PolygonShape groundBox;
    
    //Input
    Controller firstController;
    
    //Tiled Map
    TiledMap tiledMap;
    TiledMapRenderer tiledMapRenderer;
    
    public LevelScreen(final Calvin game) {
        //As usual set a reference to the original Calvin object
        this.game = game;
    
        orthoCamera = new OrthographicCamera();
        orthoCamera.setToOrtho(false, game.viewport.getWorldWidth(), game.viewport.getWorldHeight());
    
        firstController = Controllers.getCurrent();
        try {
            firstController.addListener(this);
        } catch (NullPointerException npe) {
        }
    
        generateSprites();
        generateWorld();
    
        tiledMap = new TmxMapLoader().load("myFirstTileMap.tmx");
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap, 1.0f/(game.PIXELS_IN_METERS  - 40));
    }
    
    public void generateWorld() {
        world = new World(new Vector2(0, -10), true);
    
        //Sample Ground
        groundBodyDef = new BodyDef();
        groundBodyDef.position.set(new Vector2(0, 0));
        groundBody = world.createBody(groundBodyDef);
    
        groundBox = new PolygonShape();
        groundBox.setAsBox(orthoCamera.viewportWidth, 10.0f / game.PIXELS_IN_METERS);
        groundBody.createFixture(groundBox, 0.0f);
    
        //Sample World-Object
        /*
        bodyDef = new BodyDef();
        bodyDef.type = BodyType.DynamicBody;
        bodyDef.position.set(1, 5);
        body = world.createBody(bodyDef);
    
        CircleShape circle = new CircleShape();
        circle.setRadius(15f / (game.PIXELS_IN_METERS / 2));
    
        fixtureDef = new FixtureDef();
        fixtureDef.shape = circle;
        fixtureDef.density = 0.5f;
        fixtureDef.friction = 0.4f;
        fixtureDef.restitution = 0.6f;
        fixture = body.createFixture(fixtureDef);
        circle.dispose();
    
        body.applyForceToCenter(3f, 0.0f, true);
        */

        //Sample Player World-Object
        playerBodyDef = new BodyDef();
        playerBodyDef.type = BodyType.DynamicBody;
        playerBodyDef.position.set(new Vector2(player.getX(), player.getY()));
        playerBody = world.createBody(playerBodyDef);
        playerBody.setFixedRotation(true);

        playerShape = new PolygonShape();
        //This just works -- dividing by half the in game PIXELS-to-METERS Ratio
        playerShape.setAsBox(player.getWidth() / (game.PIXELS_IN_METERS / 2),
                player.getHeight() / (game.PIXELS_IN_METERS / 2));

        playerFixtureDef = new FixtureDef();
        playerFixtureDef.shape = playerShape;
        playerFixtureDef.density = 0.2f;
        playerFixtureDef.friction = 0.4f;
        playerFixtureDef.restitution = 0.0f;
        playerFixture = playerBody.createFixture(playerFixtureDef);
        playerShape.dispose();

        world.setContactListener(this);
        b2ddr = new Box2DDebugRenderer();

    }

    public void generateSprites() {
        player = new PlayerSprite(5.0f, 2.0f);
    }

    @Override
    public void render(float delta) {
        totalElapsedTime += delta;

        ScreenUtils.clear(Color.GRAY);

        renderWorld(delta);
        orthoCamera.update();
        tiledMapRenderer.setView(orthoCamera);
        tiledMapRenderer.render();

        game.viewport.apply();
        game.batch.setProjectionMatrix(game.viewport.getCamera().combined);

        game.batch.begin();

        game.font.draw(game.batch, "Gameplay shown here", 100 / game.PIXELS_IN_METERS, 150 / game.PIXELS_IN_METERS);

        player.draw(game.batch);

        game.batch.end();

        updateEntities(totalElapsedTime, delta);
    }

    //FIXME implement keylistener, to listen for keyboard events just like a game controller
    //Change character state variables in render and use that to update character state

    private void renderWorld(float delta) {
        world.step(1 / 60f, 6, 2);

        player.setPosition(playerBody.getPosition().x - (player.getWidth() / 25 / 2),
                playerBody.getPosition().y - (player.getHeight() / 25 / 2));

        b2ddr.render(world, orthoCamera.combined); //Matrix4 debug matrix

        //BALL
        /*
        if (Gdx.input.isKeyPressed(Input.Keys.LEFT)) {
            body.applyForceToCenter(-2f, 0.0f, true);
        } else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT)) {
            body.applyForceToCenter(2f, 0.0f, true);
        }
        */
    }

    private void updateEntities(float totalElapsedTime, float delta) {
        //GRAPHICALLY
        player.update(totalElapsedTime, delta);
        //PHYSICALLY
        if(player.isJumping)
        {
            playerBody.applyForceToCenter(0.0f, 80.0f, true);
            player.isJumping = false;
            player.isAirborne = true;
        }

        if (!player.isAirborne)
        {
            if (player.isRight) {
                playerBody.setLinearVelocity(2.0f, playerBody.getLinearVelocity().y);
            } else if (player.isLeft) {
                playerBody.setLinearVelocity(-2.0f, playerBody.getLinearVelocity().y);
            }
        }
        
    }

    @Override
    public void resize(int width, int height) {
        game.viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        groundBox.dispose();
    }

    @Override
    public void connected(Controller controller) {

        //FIXME allow program to work with a controller connected during this session
        //throw new UnsupportedOperationException("Unimplemented method 'connected'");
    }

    @Override
    public void disconnected(Controller controller) {

        //throw new UnsupportedOperationException("Unimplemented method 'disconnected'");
    }

    @Override
    public boolean buttonDown(Controller controller, int buttonCode) {

        controller = firstController;
        if (!player.isAirborne)
        {
            if (controller.getButton(controller.getMapping().buttonA)) {
                player.isJumping = true;
            }
        }
        
        if (controller.getButton(controller.getMapping().buttonDpadRight)) {
            player.isRight = true;
        } else if (controller.getButton(controller.getMapping().buttonDpadLeft)) {
            player.isLeft = true;
        }
      
        return true;
    }

@Override
    public boolean buttonUp(Controller controller, int buttonCode) {

        controller = firstController;

        if (buttonCode == controller.getMapping().buttonDpadRight)
        {
            player.isRight = false;
        }
        else if ( buttonCode == controller.getMapping().buttonDpadLeft) 
        {
            player.isLeft = false;
        }
        
        //throw new UnsupportedOperationException("Unimplemented method 'buttonUp'");
        return false;
    }

    @Override
    public boolean axisMoved(Controller controller, int axisCode, float value) {
        
        //throw new UnsupportedOperationException("Unimplemented method 'axisMoved'");
        return true;
    }

    //Unused
    @Override
    public void show() {
    }
    @Override
    public void hide() {
    }
    @Override
    public void pause() {
    }
    @Override
    public void resume() {
    }


    @Override
    public void beginContact(Contact contact) {
        
        if(contact.getFixtureA().equals(playerFixture) || contact.getFixtureB().equals(playerFixture))
        {
            player.isAirborne = false;
            //System.out.println("HELLO!");
        }
        //throw new UnsupportedOperationException("Unimplemented method 'beginContact'");
    }


    @Override
    public void endContact(Contact contact) {
        // TODO Auto-generated method stub
        //throw new UnsupportedOperationException("Unimplemented method 'endContact'");
    }


    @Override
    public void preSolve(Contact contact, Manifold oldManifold) {
        // TODO Auto-generated method stub
        //throw new UnsupportedOperationException("Unimplemented method 'preSolve'");
    }


    @Override
    public void postSolve(Contact contact, ContactImpulse impulse) {
        // TODO Auto-generated method stub
        //throw new UnsupportedOperationException("Unimplemented method 'postSolve'");
    }
}
