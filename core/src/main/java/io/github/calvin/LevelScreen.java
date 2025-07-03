package io.github.calvin;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.utils.ScreenUtils;
import java.util.Iterator;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.OrthographicCamera;

import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.math.Rectangle;

import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.graphics.Pixmap;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.controllers.*;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
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
    Array<AnimatedSprite> coins;
    String COIN_PATH = "sprites/cappyCoin.atlas";
    int coinsCollected = 0;
    long score;

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

    //Sample Game World-Objects (Physics)
    Array<BodyDef> gameObjectBodyDefs;
    Array<Body> gameBodies;
    Array<PolygonShape> gameShapes;

    //Input
    Controller firstController;

    //Tiled Map
    TiledMap tiledMap;
    TiledMapRenderer tiledMapRenderer;
    float MAP_SCALE_MODIFIER = 60.0f;

    float tiledMapScale;

    Sprite hitBox;
    Sprite hurtBox;
    Sprite duckShop;

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

        tiledMapScale = game.PIXELS_IN_METERS - MAP_SCALE_MODIFIER;

        tiledMap = new TmxMapLoader().load("myFirstTileMap.tmx");
        tiledMapRenderer = new OrthogonalTiledMapRenderer(tiledMap,
                1.0f / (tiledMapScale));

        generateSprites();
        generateWorld();

        
        Rectangle hit = new Rectangle(1.0f, 1.0f,
                10.0f, 5.0f);
        hitBox = new Sprite(new Texture(Gdx.files.internal("Hit.png")));
        hitBox.setAlpha(0.50f);
        hitBox.setBounds(hit.x, hit.y, hit.width, hit.height);
        Rectangle hurt = new Rectangle(10.0f, 1.0f,
                10.0f, 5.0f);
        hurtBox = new Sprite(new Texture(Gdx.files.internal("Hurt.png")));
        hurtBox.setAlpha(0.50f);
        hurtBox.setBounds(hurt.x, hurt.y, hurt.width, hurt.height);

        

    }

    public void generateSprites() {
        player = new PlayerSprite(5.0f, 5.0f);
        coins = new Array<AnimatedSprite>();
        coins.add( new AnimatedSprite(COIN_PATH, 0.0f, 0.0f, 0.1f, 20.0f, 8.0f, 4.0f));
        coins.add(new AnimatedSprite(COIN_PATH, 0.0f, 0.0f, 0.1f, 20.0f, 12.0f, 4.0f));
        coins.add(new AnimatedSprite(COIN_PATH, 0.0f, 0.0f, 0.1f, 20.0f, 16.0f, 4.0f));

        //System.out.println(orthoCamera.position.x);
        //System.out.println(orthoCamera.position.y);
        //System.out.println(orthoCamera.position.z);
    }

    public void generateWorld() {
        world = new World(new Vector2(0, -10), true);

        MapProperties globalMapProperties = tiledMap.getProperties();

        MapLayer objectLayer = tiledMap.getLayers().get("Object Layer 1");
        MapObjects gameMapObjects = objectLayer.getObjects();
        MapObject currentObject;
        MapProperties currentObjectProps;
    
        gameObjectBodyDefs = new Array<BodyDef>();
        gameBodies = new Array<Body>();
        gameShapes = new Array<PolygonShape>();
        //CONSTRUCT ALL RECTANGULAR OBJECTS from the OBJECT LAYER
        //Because Tiled Map is like a big sprite, divide by 2 for proper adjustment
        for(int o = 0; o < gameMapObjects.getCount() ; o++)
        {
            currentObject = gameMapObjects.get(o);
            currentObjectProps = currentObject.getProperties();

            Iterator iteration = currentObjectProps.getValues();
            while (iteration.hasNext())
            {
              System.out.println(iteration.next());
            }
            gameObjectBodyDefs.add(new BodyDef());
            gameObjectBodyDefs.get(o).position.set(
                    (float) currentObjectProps.get("x") / (tiledMapScale) + ( (float) currentObjectProps.get("width") / 2/ (tiledMapScale)),
                    (float) currentObjectProps.get("y") / (tiledMapScale) + ((float) currentObjectProps.get("height")/ 2 / (tiledMapScale)));
            gameBodies.add(world.createBody(gameObjectBodyDefs.get(o)));

            gameShapes.add(new PolygonShape());

            gameShapes.get(o).setAsBox(
                    ((float) currentObjectProps.get("width") /2) / (tiledMapScale),
                    ((float) currentObjectProps.get("height") /2) / (tiledMapScale));
            gameBodies.get(o).createFixture(gameShapes.get(o), 0.0f);    
        }


       
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
        playerBodyDef.position.set(player.getX(), player.getY());
        playerBody = world.createBody(playerBodyDef);
        playerBody.setFixedRotation(true);

        playerShape = new PolygonShape();
        //This just works -- dividing by half the in game PIXELS-to-METERS Ratio
        playerShape.setAsBox(player.getWidth() / 2 / (game.PIXELS_IN_METERS / 2),
                player.getHeight() / (game.PIXELS_IN_METERS / 2 ));

        playerFixtureDef = new FixtureDef();
        playerFixtureDef.shape = playerShape;
        playerFixtureDef.density = 0.33f;
        playerFixtureDef.friction = 0.4f;
        playerFixtureDef.restitution = 0.0f;
        playerFixture = playerBody.createFixture(playerFixtureDef);
        playerShape.dispose();

        world.setContactListener(this);
        b2ddr = new Box2DDebugRenderer();

    }

  
    @Override
    public void render(float delta) {
        totalElapsedTime += delta;

        ScreenUtils.clear(Color.GRAY);

        renderWorld(delta);

        scrollCamera();
        orthoCamera.update();
        tiledMapRenderer.setView(orthoCamera);
        tiledMapRenderer.render();

        game.batch.begin();

        game.viewport.apply();

        game.batch.setProjectionMatrix(orthoCamera.combined);

        player.draw(game.batch);
        for (AnimatedSprite coin : coins)
        {
            coin.draw(game.batch);
        }

        // CHECK ATTACKS -- HITBOX VISUALIZATION
        //Remember Indexing starts at frame zero!
        //Have a mutable list of hitbox sprites, and update these according to the current index in the literal and graphical hitbox, then draw. 
        //Do for all.
        if (player.isStandPunchActive && player.currentFrameNumber == 4) {
            for (Rectangle thisBox : player.punch.getHitboxes(4))
            {
                hitBox.setBounds(thisBox.x + playerBody.getWorldCenter().x, thisBox.y + playerBody.getWorldCenter().y,
                        thisBox.width, thisBox.height);
                hitBox.draw(game.batch);
            }
        }
        //hitBox.draw(game.batch);
        //hurtBox.draw(game.batch);

        game.hud_viewport.apply();
        game.batch.setProjectionMatrix(game.hud_viewport.getCamera().combined);
        game.font.draw(game.batch, "Calvin the Capybara \nCoins " + coinsCollected + "\nScore " + score,
            0.0f, 1.5f);
        

        game.batch.end();

      
        updateEntities(totalElapsedTime, delta);
        
    }

    private void renderWorld(float delta) {
        world.step(1 / 60f, 6, 2);

        player.setPosition(playerBody.getPosition().x - player.getWidth() / 2 / 25,
                playerBody.getPosition().y - player.getHeight() / 2 / 25);
        b2ddr.render(world, orthoCamera.combined); // Matrix4 debug matrix
    }
    private void scrollCamera()
    {
        float horizontalDisplacement = 0.0f;
        //On first frame

        if (Gdx.input.isKeyPressed(Input.Keys.DOWN))
        {
            orthoCamera.translate(0.0f, -0.1f, 0.0f);
        }
        else if (Gdx.input.isKeyPressed(Input.Keys.UP)) {
            orthoCamera.translate(0.0f, 0.1f, 0.0f);
        }
        else if (Gdx.input.isKeyPressed(Input.Keys.RIGHT))
        {
            orthoCamera.translate(0.1f, 0.0f, 0.0f);
        }
        else if (Gdx.input.isKeyPressed(Input.Keys.LEFT))
        {
            orthoCamera.translate(-0.1f, 0.0f, 0.0f);
        }
        
        if(player.initialPosition == null)
        {
            player.setInitialPosition(new Vector2(playerBody.getPosition().x, playerBody.getPosition().y));
        }
        else
        {
            player.setRecentPosition(new Vector2(playerBody.getPosition().x, playerBody.getPosition().y));
            orthoCamera.translate(horizontalDisplacement, 0.0f, 0.0f);
            
            horizontalDisplacement = player.getRecentPosition().x - player.getInitialPosition().x;

            orthoCamera.translate(horizontalDisplacement, 0.0f, 0.0f);
            player.setInitialPosition(player.getRecentPosition());
            player.setRecentPosition(null);
        }
    }

private void updateEntities(float totalElapsedTime, float delta) {
      
        Rectangle playerRect = player.getBoundingRectangle();
        Rectangle coinRect;
        //Remove coins that come into contact with the player
        //Updated so that the punch move collects coins
        //In the future, only need to check active hitboxes.
        for (int c = 0; c < coins.size; c++)
        {
            coinRect = coins.get(c).getBoundingRectangle();
            Rectangle newHitbox = player.punch.getHitboxes(4).get(0);
            Rectangle newnewHitbox = new Rectangle(newHitbox.x + playerBody.getWorldCenter().x, 
                    newHitbox.y + playerBody.getWorldCenter().y, newHitbox.width, newHitbox.height);
            //newHitbox.x += playerBody.getWorldCenter().x;
            //newHitbox.y += playerBody.getWorldCenter().y;
            if(player.isStandPunchActive && player.currentFrameNumber == 5 && coinRect.overlaps(newnewHitbox))
            {
                coins.removeIndex(c);
                c--;
                coinsCollected++;
                score += 100L;
            }
            /*
            if(playerRect.overlaps(coinRect))
            {
                coins.removeIndex(c);
                c--;
                coinsCollected++;
                score += 100L;
            }
            */
        }
        for (AnimatedSprite coin : coins)
        {
            coin.update(totalElapsedTime);
        }
        player.update(totalElapsedTime, delta);
        

        if (player.isJumping) {
            playerBody.applyForceToCenter(0.0f, 110.0f, true);
            player.isJumping = false;
            player.isAirborne = true;
        }

        if (!player.isAirborne) {
            if (player.isRight) {
                playerBody.setLinearVelocity(2.0f, playerBody.getLinearVelocity().y);
            } else if (player.isLeft) {
                playerBody.setLinearVelocity(-2.0f, playerBody.getLinearVelocity().y);
            }
        }

    }

    //Touching Ground Logic!
    @Override
    public void beginContact(Contact contact) {

        if (contact.getFixtureA().equals(playerFixture) || contact.getFixtureB().equals(playerFixture)) {
            player.isAirborne = false;
            // System.out.println("HELLO!");
        }
        // throw new UnsupportedOperationException("Unimplemented method
        // 'beginContact'");
    }

    //Controller Support
    @Override
    public boolean buttonDown(Controller controller, int buttonCode) {

        controller = firstController;

        if (controller.getButton(controller.getMapping().buttonX))
        {
            player.isStandPunchActive = true;
        }

        if (!player.isAirborne) {
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

        if (buttonCode == controller.getMapping().buttonDpadRight) {
            player.isRight = false;
        } else if (buttonCode == controller.getMapping().buttonDpadLeft) {
            player.isLeft = false;
        }

        // throw new UnsupportedOperationException("Unimplemented method 'buttonUp'");
        return false;
    }

    @Override
    public void resize(int width, int height) {
        game.viewport.update(width, height, true);
        game.hud_viewport.update(width, height, true);
    }

    @Override
    public void dispose() {
        for (int d = 0; d < gameShapes.size; d += 0)
        {
            gameShapes.get(d).dispose();
        }
        
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
