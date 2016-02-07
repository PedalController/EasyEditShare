package br.com.srmourasilva.editshare.view;

import java.io.File;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;

public class Effect {
	private final static String IMAGES_PATCH = System.getProperty("user.dir") + File.separator + "lib" + File.separator + "images" + File.separator;
	private final static String EXTENSION = ".png";
	private int index;

	public interface OnToggleListener {
		void onToggle(int index);
	}

	private ImageView image;
	private Button button;
	private OnToggleListener listener;

	public Effect(int index, ImageView image, Button button) {
		this.index = index;

		this.image = image;
		this.button = button;
		
		button.setOnAction((event) -> {
			if (listener != null)
				listener.onToggle(index-1);
		});
	}
	
	public void active() {
		setColor(Color.RED);
	}
	
	public void disable() {
		setColor(Color.DARKRED);
	}
	
	public void setPedalName(String name) {
		Platform.runLater(() -> {
			button.setText(index + " - " + name);
			image.setImage(loadImage(name));
		});
	}
	
	public Image loadImage(String name) {
		File file = new File(IMAGES_PATCH + name + EXTENSION);

        return new Image(file.toURI().toString());
	}
	
	public void setColor(Color color) {
		Platform.runLater(() -> button.setBackground(new Background(new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY))));
	}
	
	public void setListener(OnToggleListener listener) {
		this.listener = listener;
	}
	
	@Override
	public String toString() {
		return index + " - " + button;
	}
}
