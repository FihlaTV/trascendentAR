package org.glud.ar;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.assets.AssetManager;
import com.badlogic.gdx.audio.Music;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.PerspectiveCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g3d.Environment;
import com.badlogic.gdx.graphics.g3d.Model;
import com.badlogic.gdx.graphics.g3d.ModelBatch;
import com.badlogic.gdx.graphics.g3d.ModelInstance;
import com.badlogic.gdx.graphics.g3d.attributes.ColorAttribute;
import com.badlogic.gdx.graphics.g3d.environment.DirectionalLight;
import com.badlogic.gdx.graphics.g3d.utils.CameraInputController;
import com.badlogic.gdx.math.*;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.Label;
import com.badlogic.gdx.utils.Array;
import com.badlogic.gdx.utils.FloatArray;
import com.badlogic.gdx.utils.StringBuilder;
import com.badlogic.gdx.utils.Timer;
import com.badlogic.gdx.utils.viewport.StretchViewport;

import static com.badlogic.gdx.Gdx.gl;

public class main extends ApplicationAdapter {
	final static String TAG = "AR Application";
	SpriteBatch batch;
	Texture img;
	ARToolKitManager arToolKitManager;
	Music musica;
	float volumen;
	float delta;
	int marcadorId, marcadorId2;
	Vector2 posicion;

	AR_Camera camera;
	ModelBatch batch_3d;
	Model model;
	ModelInstance modelInstance;
	Environment environment;
	Array<ModelInstance> instanceArray;
	Array<ModelInstance> tmpArray;
	AssetManager manager;
	String model_name = "koko_relax.g3db";
	//String model_name = "nature.g3dj";
	boolean loading = true;
	Matrix4 matriz_transformacion = new Matrix4();
	Matrix4 matriz_proyeccion = new Matrix4();
	Stage stage;
	Label label;
	Image music_img;
	Timer timer;
	Vector3 tmp = new Vector3();

	Vector3 object_position = new Vector3();
	Vector3 object_scale = new Vector3();
	Quaternion object_rotation = new Quaternion();

	StringBuilder stringBuilder;

	boolean update_label= false;

	public main(ARToolKitManager arToolKitManager){
		this.arToolKitManager = arToolKitManager;
	}
	
	@Override
	public void create () {
		Gdx.app.setLogLevel(Application.LOG_DEBUG);
		batch = new SpriteBatch();
		img = new Texture("musica.png");
		musica = Gdx.audio.newMusic(Gdx.files.internal("musica.ogg"));
		musica.setLooping(true);
		posicion = new Vector2(Gdx.graphics.getWidth()*0.5f - img.getWidth()*0.5f ,
				Gdx.graphics.getHeight()*0.5f - img.getHeight()*0.5f);

		//3D

		camera = new AR_Camera(67,Gdx.graphics.getWidth(),Gdx.graphics.getHeight());
		camera.position.set(0f,0f,1f);
		camera.lookAt(0,0,0);
		camera.near = 0;
		camera.far = 1000f;
		camera.update();
		manager = new AssetManager();
		manager.load(model_name,Model.class);
		manager.load("adventurer.g3db",Model.class);

		instanceArray = new Array<ModelInstance>();
		tmpArray = new Array<ModelInstance>();
		batch_3d = new ModelBatch();

		//Adding lights
		environment = new Environment();
		environment.set(new ColorAttribute(ColorAttribute.AmbientLight, 0.4f, 0.4f, 0.4f, 1f));
		environment.add(new DirectionalLight().set(0.8f, 0.8f, 0.8f, -1f, -0.8f, -0.2f));

		//UI
		stage = new Stage(new StretchViewport(640,360),batch);
		Label.LabelStyle labelStyle = new Label.LabelStyle(new BitmapFont(), Color.GREEN);
		label = new Label("BIENVENIDO A TRASCENDENTAR",labelStyle);
		label.setPosition(0,50);
		label.setWrap(true);
		label.setVisible(false);

		music_img = new Image(img);
		music_img.setSize(50,50);
		music_img.setPosition(0,stage.getHeight()-music_img.getHeight());
		music_img.setVisible(false);
		stage.addActor(label);
		stage.addActor(music_img);

		stringBuilder = new StringBuilder();

		timer = new Timer();
		timer.scheduleTask(new Timer.Task() {
			@Override
			public void run() {
				update_label = true;
			}
		},0,0.5f,10000000);
		//cargar macardor
//		marcadorId = arToolKitManager.cargarMarcador("single;Data/hiro.patt;80");
//		Gdx.app.debug(TAG,"Marcador ID = "+marcadorId);
//		if(marcadorId < 0){
//			Gdx.app.error(TAG,"marcador no cargado");
//		}else {
//			Gdx.app.debug(TAG,"marcador cargado");
//		}
	}

	@Override
	public void render () {
		marcadorId = arToolKitManager.obtenerMarcador();
		if(loading && manager.update()){
			done_loading();
		}
		delta = Gdx.graphics.getDeltaTime();
		gl.glClearColor(0, 0, 0, 0);
		gl.glClear(GL20.GL_COLOR_BUFFER_BIT | GL20.GL_DEPTH_BUFFER_BIT);

		if(arToolKitManager.marcadorVisible(marcadorId)){
			matriz_transformacion.set(arToolKitManager.getTransformMatrix(marcadorId));
			matriz_proyeccion.set(arToolKitManager.getProjectionMatrix());
//			matriz_transformacion.row_switch();
			if(!musica.isPlaying()) {
				musica.play();
			}
			if(volumen < 0.99) {
				volumen += 0.5*delta;
				musica.setVolume(volumen);
			}
			//Render
			matriz_transformacion.getTranslation(tmp);
			tmp.scl(-1);
			camera.projection.set(matriz_proyeccion);
			if(Gdx.input.isTouched())tmp.add(1,0,0);
			camera.position.set(tmp);
			camera.update();
			matriz_transformacion.rotate(1,0,0,90);
			for(ModelInstance instance : instanceArray){
				instance.transform.set(matriz_transformacion);
//				instance.transform.setTranslation(0,0,z);
//				instance.calculateTransforms();
			}
			batch_3d.begin(camera);
			batch_3d.render(instanceArray,environment);
			batch_3d.end();

			if(!music_img.isVisible()) music_img.setVisible(true);
		}else{
			if(music_img.isVisible()) music_img.setVisible(false);
			if(musica.isPlaying()) {
				volumen -= 0.5*delta;
				musica.setVolume(volumen);
				if(volumen < 0.001) {
					musica.pause();
				}
			}
		}
		renderMan();
		print_info();
		stage.act();
		stage.draw();
	}

	private void renderMan() {
		marcadorId2 = arToolKitManager.obtenerMarcador2();
		if (arToolKitManager.marcadorVisible(marcadorId2)) {
			matriz_transformacion.set(arToolKitManager.getTransformMatrix(marcadorId2));
			matriz_proyeccion.set(arToolKitManager.getProjectionMatrix());
			//Render
			matriz_transformacion.getTranslation(tmp);
			tmp.scl(-1);
			camera.projection.set(matriz_proyeccion);
			if (Gdx.input.isTouched()) tmp.add(1, 0, 0);
			camera.position.set(tmp);
			camera.update();
			matriz_transformacion.rotate(1, 0, 0, 0);
			for (ModelInstance instance : tmpArray) {
				instance.transform.set(matriz_transformacion);
			}
			batch_3d.begin(camera);
			batch_3d.render(tmpArray, environment);
			batch_3d.end();

			if (!music_img.isVisible()) music_img.setVisible(true);
		}
	}

	private void done_loading(){
		model = manager.get(model_name);
		modelInstance = new ModelInstance(model);
		instanceArray.add(modelInstance);
		modelInstance = new ModelInstance(manager.get("adventurer.g3db",Model.class));
		tmpArray.add(modelInstance);
		loading=false;
	}
	
	@Override
	public void dispose () {
		batch.dispose();
		img.dispose();
	}

	private void print_info(){
		if(!update_label)return;
		update_label = false;
		//label.setText(matriz_transformacion.toString());

		matriz_transformacion.getTranslation(object_position);
		matriz_transformacion.getScale(object_scale);
		matriz_transformacion.getRotation(object_rotation);

		stringBuilder.setLength(0);
		stringBuilder.append("\nFPS: ");
		stringBuilder.append(Gdx.graphics.getFramesPerSecond());
		if(arToolKitManager.marcadorVisible(marcadorId)){
			stringBuilder.append("\nMarcador1 visible");
		}else{
			stringBuilder.append("\nMarcador1 no visible");
		}
		if(arToolKitManager.marcadorVisible(marcadorId2)){
			stringBuilder.append("\nMarcador2 visible");
		}else{
			stringBuilder.append("\nMarcador2 no visible");
		}
//		stringBuilder.append("DETALLES DEL MARCADOR:");
//		stringBuilder.append("\nPosicion: "+object_position);
//		stringBuilder.append("\nEscala: "+object_scale);
//		stringBuilder.append("\nRotacion: "+object_rotation);

		label.setText(stringBuilder);
	}

}
