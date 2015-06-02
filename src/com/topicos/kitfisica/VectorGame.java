package com.topicos.kitfisica;

import org.andengine.engine.camera.Camera;
import org.andengine.engine.handler.IUpdateHandler;
import org.andengine.engine.options.EngineOptions;
import org.andengine.engine.options.ScreenOrientation;
import org.andengine.engine.options.resolutionpolicy.RatioResolutionPolicy;
import org.andengine.entity.modifier.MoveModifier;
import org.andengine.entity.primitive.Rectangle;
import org.andengine.entity.scene.Scene;
import org.andengine.entity.scene.background.Background;
import org.andengine.entity.sprite.AnimatedSprite;
import org.andengine.entity.sprite.Sprite;
import org.andengine.entity.text.Text;
import org.andengine.entity.text.TextOptions;
import org.andengine.entity.util.FPSLogger;
import org.andengine.input.touch.TouchEvent;
import org.andengine.input.touch.controller.MultiTouch;
import org.andengine.opengl.font.Font;
import org.andengine.opengl.font.FontFactory;
import org.andengine.opengl.texture.ITexture;
//import org.andengine.input.touch.controller.MultiTouch;
import org.andengine.opengl.texture.TextureOptions;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.BitmapTextureAtlasTextureRegionFactory;
import org.andengine.opengl.texture.atlas.bitmap.BuildableBitmapTextureAtlas;
import org.andengine.opengl.texture.atlas.bitmap.source.IBitmapTextureAtlasSource;
import org.andengine.opengl.texture.atlas.buildable.builder.BlackPawnTextureAtlasBuilder;
import org.andengine.opengl.texture.atlas.buildable.builder.ITextureAtlasBuilder.TextureAtlasBuilderException;
import org.andengine.opengl.texture.region.ITextureRegion;
import org.andengine.opengl.texture.region.TiledTextureRegion;
import org.andengine.ui.activity.SimpleBaseGameActivity;
import org.andengine.util.HorizontalAlign;
import org.andengine.util.color.Color;
import org.andengine.util.debug.Debug;

import android.content.Intent;
import android.opengl.GLES20;
import android.widget.Toast;

//import android.widget.Toast;

/**
 * (c) 2010 Nicolas Gramlich
 * (c) 2011 Zynga
 *
 * @author Nicolas Gramlich
 * @since 11:54:51 - 03.04.2010
 */
public class VectorGame extends SimpleBaseGameActivity {
	// ===========================================================
	// Constants
	// ===========================================================

	private static final int CAMERA_WIDTH = 480;
	private static final int CAMERA_HEIGHT = 320;
	private static final int INVALID_POINTER_ID = -1;
	private float fX, fY, sX, sY, focalX, focalY;
	private int ptrID1, ptrID2;
	private float angle_inicial, angle_actual;
	private boolean firstTouch;
	
	private int gano=0;

	// ===========================================================
	// Fields
	// ===========================================================

	private BuildableBitmapTextureAtlas mBitmapTextureAtlas;

	private ITextureRegion mArrow1TextureRegion;
	private ITextureRegion mArrowC1TextureRegion;
	private TiledTextureRegion mHelicopterTextureRegion;
	private TiledTextureRegion mBananaTextureRegion;
	
	Camera camera;
	private Font mFont;
	Text centerText;
	
	
	AnimatedSprite helicopter;
	AnimatedSprite banana;
	@Override
	public EngineOptions onCreateEngineOptions() {
		camera = new Camera(0, 0, CAMERA_WIDTH, CAMERA_HEIGHT);
		
		EngineOptions engineOptions= new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), camera);
		engineOptions.getTouchOptions().setNeedsMultiTouch(true);
		/*
        if(MultiTouch.isSupported(this)) {
            if(MultiTouch.isSupportedDistinct(this)) {
                Toast.makeText(this, "MultiTouch detected --> Both controls will work properly!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "MultiTouch detected, but your device has problems distinguishing between fingers.\n\nControls are placed at different vertical locations.", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(this, "Sorry your device does NOT support MultiTouch!\n\n(Falling back to SingleTouch.)\n\nControls are placed at different vertical locations.", Toast.LENGTH_LONG).show();
        }*/
		Toast.makeText(this, "Coloca los vectores dentro de los cuadros.", Toast.LENGTH_LONG).show();
        return engineOptions; 
		
		//return new EngineOptions(true, ScreenOrientation.LANDSCAPE_FIXED, new RatioResolutionPolicy(CAMERA_WIDTH, CAMERA_HEIGHT), camera);
	}

	@Override
	public void onCreateResources() {
		
		FontFactory.setAssetBasePath("font/");

		final ITexture fontTexture = new BitmapTextureAtlas(this.getTextureManager(), 256, 256, TextureOptions.BILINEAR);
		this.mFont = FontFactory.createFromAsset(this.getFontManager(), fontTexture, this.getAssets(), "Plok.ttf", 16, true, android.graphics.Color.WHITE);
		this.mFont.load();
		
		BitmapTextureAtlasTextureRegionFactory.setAssetBasePath("gfx/");

		this.mBitmapTextureAtlas = new BuildableBitmapTextureAtlas(this.getTextureManager(), 512, 256, TextureOptions.NEAREST);
//		this.mBitmapTextureAtlas = new BuildableBitmapTextureAtlas(this.getTextureManager(), 512, 256, TextureOptions.BILINEAR);
		this.mHelicopterTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBitmapTextureAtlas, this, "helicopter_tiled.png", 2, 2);
		this.mBananaTextureRegion = BitmapTextureAtlasTextureRegionFactory.createTiledFromAsset(this.mBitmapTextureAtlas, this, "banana_tiled.png", 4, 2);
		this.mArrow1TextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBitmapTextureAtlas, this, "flecha.png");
		this.mArrowC1TextureRegion = BitmapTextureAtlasTextureRegionFactory.createFromAsset(this.mBitmapTextureAtlas, this, "contorno.png");
		
		
		try {
			this.mBitmapTextureAtlas.build(new BlackPawnTextureAtlasBuilder<IBitmapTextureAtlasSource, BitmapTextureAtlas>(0, 0, 1));
			this.mBitmapTextureAtlas.load();
		} catch (TextureAtlasBuilderException e) {
			Debug.e(e);
		}
	}

	@Override
	public Scene onCreateScene() {
		this.mEngine.registerUpdateHandler(new FPSLogger());

		final Scene scene = new Scene();
		scene.setBackground(new Background(0.09804f, 0.6274f, 0.8784f));
		
		
		final Sprite arrow1 = new Sprite(300, 220, this.mArrow1TextureRegion, this.getVertexBufferObjectManager()){
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				this.setPosition(pSceneTouchEvent.getX() - this.getWidth() / 2, pSceneTouchEvent.getY() - this.getHeight() / 2);
				/*
				float pValueX = this.getX();
	            float pValueY = CAMERA_HEIGHT-this.getY();

	            float  dx = pValueX -  pSceneTouchEvent.getX();
	            float  dy = pValueY -  pSceneTouchEvent.getY();
	            double  Radius = Math.atan2(dy,dx);
	            double Angle = Radius * 360 ;
	            switch(pSceneTouchEvent.getAction()) {
	            	case TouchEvent.ACTION_UP:
	            		this.setRotation((float)Math.toDegrees(Angle));
	            		break;
	            }*/
	            
				return true;
			}
		};
		
		scene.attachChild(arrow1);
		scene.registerTouchArea(arrow1);
		
		
		final Sprite arrow2 = new Sprite(300, 120, this.mArrow1TextureRegion, this.getVertexBufferObjectManager()){
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				
				switch(pSceneTouchEvent.getAction()) {
				case TouchEvent.ACTION_DOWN:
					//this.setScale(1.25f);
					break;
				case TouchEvent.ACTION_MOVE:
					this.setPosition(pSceneTouchEvent.getX() - this.getWidth() / 2, pSceneTouchEvent.getY() - this.getHeight() / 2);
					break;
				case TouchEvent.ACTION_UP:
					//this.setScale(1.0f);
					//this.setRotation((float) Math.toDegrees(angle_actual));
					break;
			}
				return true;
			}
		};		
		//arrow1.setRotation(30);
		scene.attachChild(arrow2);
		scene.registerTouchArea(arrow2);
		arrow2.setRotation(270);
		
		final Sprite arrow3 = new Sprite(400, 220, this.mArrow1TextureRegion, this.getVertexBufferObjectManager()){
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				this.setPosition(pSceneTouchEvent.getX() - this.getWidth() / 2, pSceneTouchEvent.getY() - this.getHeight() / 2);
				return true;
			}
		};
		
		scene.attachChild(arrow3);
		scene.registerTouchArea(arrow3);
		arrow3.setRotation(210);
		
		final Sprite arrow4 = new Sprite(400, 120, this.mArrow1TextureRegion, this.getVertexBufferObjectManager()){
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				this.setPosition(pSceneTouchEvent.getX() - this.getWidth() / 2, pSceneTouchEvent.getY() - this.getHeight() / 2);
				return true;
			}
		};
		
		scene.attachChild(arrow4);
		scene.registerTouchArea(arrow4);
		arrow4.setRotation(135);
		
		final Sprite arrow5 = new Sprite(350, 170, this.mArrow1TextureRegion, this.getVertexBufferObjectManager()){
			@Override
			public boolean onAreaTouched(final TouchEvent pSceneTouchEvent, final float pTouchAreaLocalX, final float pTouchAreaLocalY) {
				this.setPosition(pSceneTouchEvent.getX() - this.getWidth() / 2, pSceneTouchEvent.getY() - this.getHeight() / 2);
				return true;
			}
		};
		
		scene.attachChild(arrow5);
		scene.registerTouchArea(arrow5);
		arrow5.setRotation(43);
		
		final Sprite arrowC1 = new Sprite(155, 200, this.mArrowC1TextureRegion, this.getVertexBufferObjectManager());
		scene.attachChild(arrowC1);
			
		final Sprite arrowC2 = new Sprite(225, 145, this.mArrowC1TextureRegion, this.getVertexBufferObjectManager());
		scene.attachChild(arrowC2);
		arrowC2.setRotation(90);
				
		final Sprite arrowC3 = new Sprite(160, 65, this.mArrowC1TextureRegion, this.getVertexBufferObjectManager());
		scene.attachChild(arrowC3);
		arrowC3.setRotation(30);
		
		final Sprite arrowC4 = new Sprite(60, 80, this.mArrowC1TextureRegion, this.getVertexBufferObjectManager());
		scene.attachChild(arrowC4);
		arrowC4.setRotation(135);
		
		final Sprite arrowC5 = new Sprite(50, 170, this.mArrowC1TextureRegion, this.getVertexBufferObjectManager());
		scene.attachChild(arrowC5);
		arrowC5.setRotation(43);

		String mensaje = "Aprendimos...\n\nLa suma de los vectores\nque forman una figura cerrada\nes CERO.";
		centerText = new Text(10
				, 50, mFont, mensaje, new TextOptions(HorizontalAlign.LEFT),getVertexBufferObjectManager());
		centerText.setColor(1f,1f,1f,0f);
		scene.attachChild(centerText);
		
		helicopter = new AnimatedSprite(50, 200, mHelicopterTextureRegion, getVertexBufferObjectManager());
		helicopter.animate(new long[] { 100, 100 }, 1, 2, true);
		
		scene.attachChild(helicopter);
		helicopter.setScale(2.0f);
		helicopter.setVisible(false);
		
		banana = new AnimatedSprite(400, 220, mBananaTextureRegion, getVertexBufferObjectManager());
		banana.animate(100);
		scene.attachChild(banana);
		banana.setScale(1.5f);
		banana.setVisible(false);
		
		scene.setTouchAreaBindingOnActionDownEnabled(true);
		
		//verificar si hay colision
		scene.registerUpdateHandler(new IUpdateHandler() {
			@Override
			public void reset() { }

			@Override
			public void onUpdate(final float pSecondsElapsed) {
				/*if(Math.abs(arrowC1.getX()-arrow1.getX())<5 && Math.abs(arrowC1.getY()-(int)arrow1.getY())<5 ){
					Rectangle centerRectangle = new Rectangle(100,50, 32, 32, getVertexBufferObjectManager());
					scene.attachChild(centerRectangle);
					centerRectangle.setColor(1, 0, 0);
				}	*/
				if( Math.abs(arrowC1.getX()-arrow1.getX())<5 && Math.abs(arrowC1.getY()-(int)arrow1.getY())<5 && arrow2.collidesWith(arrowC2)&&arrow3.collidesWith(arrowC3) && arrow4.collidesWith(arrowC4)&&arrow5.collidesWith(arrowC5)){
					scene.detachChild(arrow1);
					scene.detachChild(arrow2);
					scene.detachChild(arrow3);
					scene.detachChild(arrow4);
					scene.detachChild(arrow5);
					scene.detachChild(arrowC1);
					scene.detachChild(arrowC2);
					scene.detachChild(arrowC3);
					scene.detachChild(arrowC4);
					scene.detachChild(arrowC5);
					centerText.setColor(0.9804f, 0.8274f, 0.8f,0.7f);
					helicopter.registerEntityModifier(new MoveModifier(30, 0, CAMERA_WIDTH - helicopter.getWidth(), 0, CAMERA_HEIGHT - helicopter.getHeight()));
					banana.setVisible(true);
					helicopter.setVisible(true);
				}
			}
		});

		return scene;
	}
	
}
