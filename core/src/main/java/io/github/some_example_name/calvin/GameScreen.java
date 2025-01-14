package com.badlogic.calvin;

//All imports
import com.badlogic.entity.*;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Input.Keys;
import com.badlogic.gdx.InputProcessor;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.MapObjects;
import com.badlogic.gdx.maps.MapProperties;
import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TiledMapRenderer;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector3;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.audio.Sound;
import java.util.Iterator;
import com.badlogic.gdx.physics.box2d.*;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;

public class GameScreen implements Screen, InputProcessor {

    final Calvin game;
    MainMenuScreen mainMenu;

    //Game mechanics
    float elapsedTime;
    int coinsCollected = 0;
    float lastLaser = 0.0f;
    boolean goalReached;

    //Tiled Map
    TiledMap platformMap;
    OrthogonalTiledMapRenderer platformMapRenderer;
    MapObjects staticObjects;
    MapObject staticObject;
    float moveIncr = 1.5f;
    float mapWidth;
    float mapHeight;
    MapLayer layer;
    float factor;
    float tileSize = 0.32f;

    Texture backgroundImage;
    Texture ground;

    OrthographicCamera camera;
    float PIXELS_TO_METERS = 100.0f;
    float viewportWidth;
    float viewportHeight;

    //Music and SFX
    Music backgroundMusic;
    Sound coinGet = Gdx.audio.newSound(Gdx.files.internal("audio/sfx/calvin/retro-coin-3-236679.mp3"));

    Sound laserHit = Gdx.audio.newSound(Gdx.files.internal("audio/sfx/enemy/laser-45816.mp3"));
    Sound waterHit = Gdx.audio.newSound(Gdx.files.internal("audio/sfx/calvin/water-splash-80537.mp3"));
    Sound calvinHit = Gdx.audio.newSound(Gdx.files.internal("audio/vine-boom.mp3"));
    Sound enemyHit = Gdx.audio.newSound(Gdx.files.internal("audio/sfx/enemy/metal-sound-fighting-game-87507.mp3"));

    Sound nullifySound = Gdx.audio.newSound(Gdx.files.internal("audio/sfx/negate-impact.mp3"));

    Sound victoryFanfare = Gdx.audio.newSound(Gdx.files.internal("audio/sfx/goodresult-82807.mp3"));

    //Physics World
    World world;
    Body bodyEdgeScreen;
    // Entity Bodies
    Body playerBody;
    Array<Body> enemyBodies = new Array<Body>();

    Texture duckTexture = new Texture(Gdx.files.internal("character/sirDuck.png"));
    Sprite duckSprite = new Sprite(duckTexture);

    boolean loadEnemy = true;

    //Game Entities
    PlayerSprite player;

    String coinPath = "character/calvin/cappy_coin.atlas";
    Array<AnimatedSprite> coins = new Array<AnimatedSprite>();
    String waterPath = "character/calvin/water.atlas";
    Array<AnimatedSprite> waters = new Array<AnimatedSprite>();
    String laserPath = "character/enemy/laser.atlas";
    Array<AnimatedSprite> lasers = new Array<AnimatedSprite>();
    String enemyPath = "character/enemy/lizard.atlas";

    //enemies
    Array<EnemySprite> enemies = new Array<EnemySprite>();

    //Other
    Box2DDebugRenderer debugRenderer;
    Matrix4 debugMatrix;

    // Vector3 clickCoordinates;
    // Vector3 position;

    public GameScreen(final Calvin gam, MainMenuScreen pmainMenu) {
        this.game = gam;
        this.mainMenu = pmainMenu;

        //Scaling to meters
        viewportWidth = Gdx.graphics.getWidth() / PIXELS_TO_METERS;
        viewportHeight = Gdx.graphics.getHeight() / PIXELS_TO_METERS;

        backgroundImage = new Texture(Gdx.files.internal("scene/background1.png"));
        ground = new Texture(Gdx.files.internal("scene/ground1.png"));

        //Music in the background
        backgroundMusic = Gdx.audio.newMusic(Gdx.files.internal("audio/music/chemPlantZone.mp3"));
        backgroundMusic.setLooping(true);
        backgroundMusic.setVolume(0.50f);
        //FIXME level victory sound!

        camera = new OrthographicCamera();
        camera.setToOrtho(false, viewportWidth, viewportHeight);

        //SPAWN PLAYER and ENABLE INPUT
        player = new PlayerSprite();
        player.setSize(46 / PIXELS_TO_METERS, 72 / PIXELS_TO_METERS);
        Gdx.input.setInputProcessor(this);

        //TILE MAP LOADING
        platformMap = new TmxMapLoader().load("TechnicalPrototype.tmx");
        platformMapRenderer = new OrthogonalTiledMapRenderer(platformMap, 1 / PIXELS_TO_METERS);
        Gdx.input.setInputProcessor(this);
        MapProperties mapProperties = platformMap.getProperties();

        int tileWidth = mapProperties.get("tilewidth", Integer.class); // See tileset_map_1.tmx
        int tileHeight = mapProperties.get("tileheight", Integer.class);
        int mapWidthInTiles = mapProperties.get("width", Integer.class);
        int mapHeightInTiles = mapProperties.get("height", Integer.class);

        mapHeight = mapHeightInTiles * tileHeight;
        mapWidth = mapWidthInTiles * tileWidth;

        //Add Entities to game
        addCoin(12.4f, 2.0f);
        addCoin(0.25f, 2.0f);
        
        addCoin(7.449f, 3.20f);
        addCoin(19f, 2.6f);

        addEnemy(tileSize * 25, tileSize * 10);
        addEnemy(22.8f, 1.3f);
        addEnemy(0.5f, 3f);
       
        player.setPosition(4f, tileSize * 6);

        duckSprite.setPosition(24.5f, 1.3f);
        duckSprite.setSize(0.75f, 0.89f);

        //Call generate world!
        generateWorld();
    }

    //Generate world
    public void generateWorld() {
        world = new World(new Vector2(0, -10f), true);

        //Define the standard edge to be used for world
        BodyDef bodyDef2 = new BodyDef();
        bodyDef2.type = BodyDef.BodyType.StaticBody;
        bodyDef2.position.set(0, 0);
        FixtureDef fixtureDef2 = new FixtureDef();
        EdgeShape edgeShape = new EdgeShape();
        fixtureDef2.friction = 0.3f;

        createBody(edgeShape, fixtureDef2, bodyDef2, 0, 4, 12, 4);

        createBody(edgeShape, fixtureDef2, bodyDef2, 12, 4, 14, 6);
        createBody(edgeShape, fixtureDef2, bodyDef2, 14, 6, 16, 6);
        createBody(edgeShape, fixtureDef2, bodyDef2, 16, 6, 18, 8);
        createBody(edgeShape, fixtureDef2, bodyDef2, 18, 8, 20, 8);
        createBody(edgeShape, fixtureDef2, bodyDef2, 20, 8, 22, 10);
        createBody(edgeShape, fixtureDef2, bodyDef2, 22, 10, 29, 10);
        createBody(edgeShape, fixtureDef2, bodyDef2, 29, 10, 31, 8);
        createBody(edgeShape, fixtureDef2, bodyDef2, 31, 8, 33, 8);
        createBody(edgeShape, fixtureDef2, bodyDef2, 33, 8, 35, 6);
        createBody(edgeShape, fixtureDef2, bodyDef2, 35, 6, 37, 6);
        createBody(edgeShape, fixtureDef2, bodyDef2, 37, 6, 39, 4);

        createBody(edgeShape, fixtureDef2, bodyDef2, 39, 4, 54, 4);

        createBody(edgeShape, fixtureDef2, bodyDef2, 54, 4, 56, 6);
        createBody(edgeShape, fixtureDef2, bodyDef2, 56, 6, 57, 6);
        createBody(edgeShape, fixtureDef2, bodyDef2, 57, 6, 58, 7);
        createBody(edgeShape, fixtureDef2, bodyDef2, 58, 7, 60, 7);
        createBody(edgeShape, fixtureDef2, bodyDef2, 60, 7, 60, 0);

        createBody(edgeShape, fixtureDef2, bodyDef2, 63, 0, 63, 7);
        createBody(edgeShape, fixtureDef2, bodyDef2, 63, 7, 65, 7);
        createBody(edgeShape, fixtureDef2, bodyDef2, 65, 7, 66, 6);
        createBody(edgeShape, fixtureDef2, bodyDef2, 66, 6, 67, 6);
        createBody(edgeShape, fixtureDef2, bodyDef2, 67, 6, 69, 4);
        createBody(edgeShape, fixtureDef2, bodyDef2, 69, 4, 80, 4);

        createBody(edgeShape, fixtureDef2, bodyDef2, 80, 4, 80, 20);

        edgeShape.dispose();

        // Define box2d body for the sprite -- Project 2 -- player body
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyDef.BodyType.DynamicBody;
        // The body's position in meters is set using the middle of the body,
        // while the sprite's position is set using the lower left corner of the sprite.
        bodyDef.position.set((player.getX() + player.getWidth() / 2),
                (player.getY() + player.getHeight() / 2));

        playerBody = world.createBody(bodyDef);

        PolygonShape shape = new PolygonShape();

        shape.setAsBox(player.getWidth() / 2 / 2,
                player.getHeight() / 2);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = shape;
        fixtureDef.density = 0.01f;

        playerBody.createFixture(fixtureDef);
        shape.dispose();

        BodyDef enemyDef;
        PolygonShape enemyShape;
        FixtureDef enemyFixtureDef;

        //FOR ALL ENEMIES, GENERATE A BODY (FUTURE)
        //The index in the list of bodies corresponds with the list of the enemy sprites
        for (int i = 0; i < enemies.size; i++) {
            EnemySprite enemy = enemies.get(i);
            enemyDef = new BodyDef();
            enemyDef.type = BodyDef.BodyType.DynamicBody;
            enemyDef.position.set((enemy.getX() + enemy.getWidth() / 2), (enemy.getY() + enemy.getHeight() / 2));
            enemyBodies.add(world.createBody(enemyDef));
            enemyShape = new PolygonShape();
            shape.setAsBox(enemy.getWidth() / 2, enemy.getHeight() / 2);
            enemyFixtureDef = new FixtureDef();
            enemyFixtureDef.shape = enemyShape;
            enemyFixtureDef.density = 0.015f;
            enemyBodies.get(i).createFixture(enemyFixtureDef);
            enemyShape.dispose();
        }

        debugRenderer = new Box2DDebugRenderer();
    }

    @Override
    public void render(float delta) {
        Gdx.gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        world.step(1f / 60f, 6, 2);
        player.setPosition((playerBody.getPosition().x) - player.getWidth() / 2,
                (playerBody.getPosition().y) - player.getHeight() / 2);

        player.setRotation((float) Math.toDegrees(playerBody.getAngle()));

        //FIXME iteration needed
        //update enemy sprite position for world step
        for (int e = 0; e < enemies.size; e++) {
            enemies.get(e).setPosition((enemyBodies.get(e).getPosition().x) - enemies.get(e).getWidth() / 2,
                    (enemyBodies.get(e).getPosition().y) - enemies.get(e).getHeight() / 2);

            enemies.get(e).setRotation((float) Math.toDegrees(enemyBodies.get(e).getAngle()));
        }

        game.batch.setProjectionMatrix(camera.combined);
        debugMatrix = game.batch.getProjectionMatrix().cpy().scale(PIXELS_TO_METERS, PIXELS_TO_METERS, 0);

        game.batch.enableBlending();

        game.batch.begin();

        //Parallaxing
        factor = 0.2f;
        for (int i = 1; i < 4; i++) {
            layer = platformMap.getLayers().get(i);
            layer.setParallaxX(factor);
            factor += 0.2f;
        }
        platformMapRenderer.setView(camera);
        platformMapRenderer.render();

        //Camera Scrolling
        updateScreen();
        camera.update();

        //DRAW all entities --------

        //Draw player
        player.draw(game.batch);

        //Draw all coinSprites
        for (AnimatedSprite coin : coins) {
            coin.draw(game.batch);
        }
        //Draw all laserSprites
        for (AnimatedSprite laser : lasers) {
            laser.draw(game.batch);
        }
        //Draw all waterSprites
        for (AnimatedSprite water : waters) {
            water.draw(game.batch);
        }
        //Draw all enemies
        for (EnemySprite enemy : enemies) {
            enemy.draw(game.batch);
        }
        duckSprite.draw(game.batch);

        game.batch.end();

        //Despawn when falling out of level
        if (player.getY() < -5) {
            //PLAYER HAS DIED, GAME OVER AND GO BACK TO MAIN MENU
            //System.out.println("PLAYER IS DEFEATED");
            //FIXME game over screen
            game.setScreen(new MainMenuScreen(game));
            dispose();
        } 
        for(int e = 0; e  < enemies.size ; e++)
        {
            if (enemies.get(e).getY() < -5)
            {
                enemies.removeIndex(e);
                world.destroyBody(enemyBodies.removeIndex(e));
                e--;
            }
        }

        Rectangle playerRectangle = player.getBoundingRectangle();
        Rectangle coinRectangle = null;
        Rectangle waterRectangle = null;
        Rectangle laserRectangle = null;
        Rectangle enemyRectangle = null;

        //FIXME
        //Victory Condition, reach the shop and all enemies are defeated!
        if(playerRectangle.overlaps(duckSprite.getBoundingRectangle()) && enemies.size == 0)
        {
            //FIXME Set equal to the level complete screen and then shop screen 
            dispose();
            game.setScreen(new MainMenuScreen(game));
            victoryFanfare.play();
        }

        // Update player-coin behavior
        Iterator<AnimatedSprite> coinIterator = coins.iterator();
        while (coinIterator.hasNext()) {
            AnimatedSprite coin = coinIterator.next();
            coinRectangle = coin.getBoundingRectangle();
            if (playerRectangle.overlaps(coinRectangle)) {
                coinGet.play();
                coinsCollected += 1;
                coinIterator.remove();
            }
        }

        // Update player-laser behavior
        Iterator<AnimatedSprite> laserIterator = lasers.iterator();
        while (laserIterator.hasNext()) {
            AnimatedSprite laser = laserIterator.next();
            laserRectangle = laser.getBoundingRectangle();
            //On player contact
            if (playerRectangle.overlaps(laserRectangle)) {
                if(player.currentMoveState.equals("hi_punch") && (player.currentMoveFrame == 3 || player.currentMoveFrame == 4))
                {
                    nullifySound.play();
                }
                else
                {
                    laserHit.play();
                    if (laser.isRight()) {
                        playerBody.applyForceToCenter(0.15f, 0.1f, true);
                    } else
                        playerBody.applyForceToCenter(-0.15f, 0.1f, true);

                    player.hitPoints -= 1;
                    if (player.hitPoints == 0) {
                        //PLAYER HAS DIED, GAME OVER AND GO BACK TO MAIN MENU
                        //System.out.println("PLAYER IS DEFEATED");
                        game.setScreen(new MainMenuScreen(game));
                        dispose();
                    }
                }
                
                laserIterator.remove();
            }
            //Unload Projectiles if offscreen
            if (laser.getX() < (camera.position.x - (viewportWidth / 2))
                    || laser.getX() > (camera.position.x + (viewportWidth / 2))) {
                laserIterator.remove();
            }
            //Move the projectile forward
            if (laser.isRight())
                laser.setX(laser.getX() + 1.5f * Gdx.graphics.getDeltaTime());
            else
                laser.setX(laser.getX() - 1.5f * Gdx.graphics.getDeltaTime());

        }

        //Update enemy-water behavior
        Iterator<AnimatedSprite> waterIterator = waters.iterator();

        while (waterIterator.hasNext()) {
            AnimatedSprite water = waterIterator.next();
            waterRectangle = water.getBoundingRectangle();
            // On player contact
            for (int e = 0; e < enemies.size; e++) {
                EnemySprite enemy = enemies.get(e);
                enemyRectangle = enemy.getBoundingRectangle();
                if (enemyRectangle.overlaps(waterRectangle)) {
                    if(enemy.currentMoveState.equals("hi_bite") && enemy.currentMoveFrame >= 5 && enemy.currentMoveFrame <= 7)
                    {
                        nullifySound.play();
                    }
                    else
                    {
                        // water play hit sound
                        waterHit.play();
                        // playerBody.applyForceToCenter(0,0.00005f, true);
                        enemy.hitPoints -= 1;
                        //Apply force to enemy?
                        if(water.isRight())
                        {
                            enemyBodies.get(e).applyForceToCenter(0.6f, 0.2f, true);
                        }
                        else
                            enemyBodies.get(e).applyForceToCenter(-0.6f, 0.2f, true);
                        
                        if (enemy.hitPoints == 0) {
                            enemies.removeIndex(e);
                            world.destroyBody(enemyBodies.removeIndex(e));
                            e--;
                        }
                          
                    } 
                    waterIterator.remove();
                }
            }
            //Unload Projectiles if offscreen
            if (water.getX() < (camera.position.x - (viewportWidth / 2))
                    || water.getX() > (camera.position.x + (viewportWidth / 2))) {
                waterIterator.remove();
            }
            //Move the projectile forward
            if (water.isRight())
                water.setX(water.getX() + 1.5f * Gdx.graphics.getDeltaTime());
            else
                water.setX(water.getX() - 1.5f * Gdx.graphics.getDeltaTime());
        }

        // Update laser-water behavior They disappear on impact!
        waterIterator = waters.iterator();
        laserIterator = lasers.iterator();
        while (waterIterator.hasNext()) {
            AnimatedSprite water = waterIterator.next();
            waterRectangle = water.getBoundingRectangle();
            while (laserIterator.hasNext()) {
                AnimatedSprite laser = laserIterator.next();
                laserRectangle = laser.getBoundingRectangle();

                if (laserRectangle.overlaps(waterRectangle)) {
                    //play nullify
                    nullifySound.play();
                    laserIterator.remove();
                    waterIterator.remove();
                }
            }
        }

        boolean nullifiedImpact = false;
        //System.out.println(player.hitPoints);
        //FIXME
        //remove laser or water from Array if it hits the ground or goes offscreen
        //Go through player-enemy interactions
        boolean enemyAlive = true;
        for (int e = 0; e < enemies.size; e++) {
            EnemySprite enemy = enemies.get(e);
            enemyRectangle = enemy.getBoundingRectangle();
            enemyAlive = true;
            nullifiedImpact = false;
            if (!enemy.isHurt) {
                if (playerRectangle.overlaps(enemyRectangle)) {

                    if (player.currentMoveState.equals("hi_punch")) {
                        if (player.currentMoveFrame == 3 || player.currentMoveFrame == 4) {

                            if(enemy.currentMoveState.equals("hi_bite") && enemy.currentMoveFrame >= 5 && enemy.currentMoveFrame <= 7)
                            {
                                nullifiedImpact = true;
                                nullifySound.play();
                                if(player.facingRight)
                                    playerBody.applyForceToCenter(-0.1f, 0.0f, true);
                                else
                                    playerBody.applyForceToCenter(0.1f, 0.0f, true);

                                if(enemy.facingRight)
                                    enemyBodies.get(e).applyForceToCenter(-1f, 0.0f, true);
                                else
                                    enemyBodies.get(e).applyForceToCenter(1f, 0.0f, true);
                            }
                            else
                            {
                                    //System.out.println("Punch landed");
                                enemy.isHurt = true;
                                enemy.hurtTimer += Gdx.graphics.getDeltaTime();
                                enemy.hitPoints -= 2;

                                calvinHit.play();

                                enemy.setColor(Color.PINK);
                                if(player.getX() < enemy.getX())
                                {
                                    enemyBodies.get(e).applyForceToCenter(1f + playerBody.getLinearVelocity().x, 0f, true);
                                }
                                else
                                    enemyBodies.get(e).applyForceToCenter(-1f - playerBody.getLinearVelocity().x, 0f, true);


                                if (enemy.hitPoints <= 0) {
                                    enemies.removeIndex(e);
                                    world.destroyBody(enemyBodies.removeIndex(e));
                                    e--;
                                    enemyAlive = false;
                                }
                            }
                        }
                            
                    }

                    if (enemyAlive && !nullifiedImpact && !player.isHurt && enemy.currentMoveState.equals("hi_bite")) {
                        if (enemy.currentMoveFrame >= 5 && enemy.currentMoveFrame <= 7) {
                           
                            player.isHurt = true;
                            player.hitPoints -= 2;
                            enemyHit.play();
                            if (player.hitPoints <= 0) {
                                //System.out.println("PLAYER IS DEFEATED");
                                game.setScreen(new MainMenuScreen(game));
                                dispose();
                            }
                            if (enemy.getX() > player.getX())
                            {
                                playerBody.applyForceToCenter(-0.2f, 0f, true);
                            }
                            else {
                                playerBody.applyForceToCenter(0.2f, 0f, true);
                            }
                        
                            player.setColor(Color.PINK);
                            
                            
                        }
                    }

                }
            } else if (enemy.hurtTimer >= 1) {
                //System.out.println("Enemy can now be hit again");
                enemy.hurtTimer = 0;
                enemy.isHurt = false;
                // Default coloration is always white
                enemy.setColor(Color.WHITE);
            } else if (enemy.hurtTimer < 1) {
                //System.out.println("Enemy still hurt");
                enemy.hurtTimer += Gdx.graphics.getDeltaTime();
            }
        }

        if (player.isHurt && player.hurtTimer >= 1) {
            System.out.println("Player can now be hurt again");
            player.hurtTimer = 0.0f;
            player.isHurt = false;
            player.setColor(Color.WHITE);
            System.out.println(player.getColor());
        } else if (player.isHurt && player.hurtTimer < 1) {
            // System.out.println("Player still hurt");
            player.hurtTimer += Gdx.graphics.getDeltaTime();
        }

        updateSprite();

        //Prevent entity bodies from rotating
        playerBody.setFixedRotation(true);
        for (Body enemyBody : enemyBodies) {
            enemyBody.setFixedRotation(true);
        }

        //System.out.println("Player location: " + player.getX() + " " + player.getY());
        debugRenderer.render(world, debugMatrix);
    }

    //Adapted from project 2
    //Update all animations
    private void updateSprite() {
        // Get current y velocity
        float vy = playerBody.getLinearVelocity().y;
        // Debug:
        // System.out.println("vy: " + vy);
        // If not currently jumping ...
        float notJumpingThreshold = .01f;

        //FIXME prevent player from moving while attacking
        if (Math.abs(vy) < notJumpingThreshold) {

            if (player.walkRight.isActive() && player.walkLeft.isActive()) {

            } else if (player.walkRight.isActive()) {
                playerBody.setLinearVelocity(2f + coinsCollected / 20f, 0);
            } else if (player.walkLeft.isActive()) {
                playerBody.setLinearVelocity(-2f - coinsCollected / 20f, 0);
            }
            if (player.jump.isActive()) {
                //Project 2

                playerBody.applyForceToCenter(0f, 0.40f + coinsCollected / 30f, true);
            }
        }
        //Project 2
        //Get elapsed time and send it to the sprite update method. Call the update method from AnimatedSprite class
        //Use a variable for airborne state to prevent mid-air turnaround
        elapsedTime += Gdx.graphics.getDeltaTime();
        boolean airborne = Math.abs(vy) > notJumpingThreshold;

        //Update the player sprite
        player.update(elapsedTime, airborne);
        //parameters: elapsed time, enemy aiborne, and player position
        for (int e = 0; e < enemies.size; e++) {
            enemies.get(e).update(elapsedTime, Math.abs(enemyBodies.get(e).getLinearVelocity().y) > notJumpingThreshold,
                    player.getX(),
                    player.getY());
        }
        //Update all coin sprite animations
        for (AnimatedSprite coin : coins) {
            coin.update(elapsedTime);
        }
        //Update all laser sprite animations
        for (AnimatedSprite laser : lasers) {
            laser.update(elapsedTime);
        }
        //Update all water sprite animations
        for (AnimatedSprite water : waters) {
            water.update(elapsedTime);
        }

        for (int e = 0; e < enemies.size; e++) {
            if (enemies.get(e).lastLaser <= 2.5f) {
                //System.out.println("BEFORE: " + lastLaser);
                enemies.get(e).lastLaser += Gdx.graphics.getDeltaTime();
                //System.out.println("AFTER: " + lastLaser);

            } else {
                enemies.get(e).lastLaser = 0.0f;
                addLaser(enemies.get(e).getX(), enemies.get(e).getY() + 0.6f, enemies.get(e).facingRight);
                if(enemies.get(e).facingRight)
                    enemies.get(e).laserAtkRight.setActive(true);
                else
                    enemies.get(e).laserAtkLeft.setActive(true);
            }

        }

    }

    private void updateScreen() {

        if (player.getX() < 4) {
            camera.position.x = 4;
        } else
            camera.position.x = Math.min(player.getX(), 21.6f);

        if (player.getY() < 2) {
            camera.position.y = 2;
        } else
            camera.position.y = Math.min(player.getY(), 4.35f);

    }

    //METHODS TO ADD OTHER GAME OBJECTS
    //Add a coin to the gamescreen given a position
    public void addCoin(float x, float y)
    {
        AnimatedSprite coin = new AnimatedSprite(coinPath, true);
        coin.setPosition(x,y);
        coin.setSize(0.6f , 0.6f);
        coins.add(coin);
    }
    //Add a laser to the gamescreen given a position
    public void addLaser(float x, float y, boolean facingRight)
    {
        AnimatedSprite laser = new AnimatedSprite(laserPath, facingRight);
        laser.setPosition(x, y);
        laser.setSize(0.45f, 0.18f);
        lasers.add(laser);
    }
    //Add a water to the gamescreen given a position
    public void addWater(float x, float y, boolean direction)
    {
        AnimatedSprite water = new AnimatedSprite(waterPath, player.facingRight);
        water.setPosition(x, y);
        water.setSize(0.45f, 0.18f);

        waters.add(water);
    }

    public void addEnemy(float x, float y) {
        
        EnemySprite enemy = new EnemySprite(enemyPath);
        enemy.setPosition(x, y);
        enemy.setSize(0.6f, 1.05f);
        enemies.add(enemy);

    }
    
    public void createBody(EdgeShape edgeShape, FixtureDef fixtureDef, BodyDef bodyDef,
                           float x1, float y1, float x2, float y2){
        edgeShape.set(tileSize*x1, tileSize*y1, tileSize*x2, tileSize*y2);
        fixtureDef.shape = edgeShape;
        bodyEdgeScreen = world.createBody(bodyDef);
        bodyEdgeScreen.createFixture(fixtureDef);
    }
    
    @Override
    public void resize(int width, int height) {
    
    }
    @Override
    public void show() {
    
        backgroundMusic.play();
    
    }
    @Override
    public void hide() {
    
    }
    @Override
    public void resume() {
    
    }
    @Override
    public void pause() {
    
    }
    
    @Override
    public void dispose() {
    
        backgroundMusic.dispose();
        player.dispose();
    }
    
    //NO CROUCHING YET
    @Override
    public boolean keyDown(int keycode) {
        if (keycode == Input.Keys.LEFT) player.walkLeft.setActive(true);
        if (keycode == Input.Keys.RIGHT) player.walkRight.setActive(true);
        if (keycode == Input.Keys.UP) player.jump.setActive(true);
       
    
        //Player Projectile Attack Command
        if(keycode == Input.Keys.SPACE)
        {
            if (player.timeSinceWater >= 1.5f)
            {
                player.timeSinceWater = 0.0f;
                if(player.facingRight)
                    player.waterAtkRight.setActive(true);
                else
                    player.waterAtkLeft.setActive(true);
                //System.out.println("water attack activated");
                
                addWater(player.getX(), player.getY() + 0.5f, player.facingRight);
            }
     
        }
    
        //Player Punch Attack
        if(keycode == Input.Keys.S)
        {
            if(player.facingRight)
            {
                player.hiPunchRight.setActive(true);
            }
            else
            {
                player.hiPunchLeft.setActive(true);
            }
        }
        return false;
    }
    
    @Override
    public boolean keyUp(int keycode) {
        if (keycode == Input.Keys.LEFT) player.walkLeft.setActive(false);
        if (keycode == Input.Keys.RIGHT) player.walkRight.setActive(false);
        if (keycode == Input.Keys.UP) player.jump.setActive(false);
      
    
        if (keycode == Keys.ESCAPE) {
    
            game.setScreen((mainMenu));
            dispose();
    
        }
        return false;
    }
    
    @Override
    public boolean keyTyped(char character) {return false;}
    
    @Override
    public boolean touchDown(int screenX, int screenY, int pointer, int button) {
    
        //Debugger for location on screen
        /*
        clickCoordinates = new Vector3(screenX, screenY, 0);
        position = camera.unproject(clickCoordinates);
    
        System.out.println("Pointer x: " + position.x + ", y: " + position.y);
    
        */
    return true;

    }

    @Override
    public boolean touchUp(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchCancelled(int screenX, int screenY, int pointer, int button) {
        return false;
    }

    @Override
    public boolean touchDragged(int screenX, int screenY, int pointer) {
        return false;
    }

    @Override
    public boolean mouseMoved(int screenX, int screenY) {
        return false;
    }

    @Override
    public boolean scrolled(float amountX, float amountY) {
        return false;
    }
}
