package br.com.srmourasilva.editshare;

import java.util.ArrayList;
import java.util.List;

import br.com.srmourasilva.editshare.view.Effect;
import br.com.srmourasilva.editshare.view.View;
import br.com.srmourasilva.editshare.view.Effect.OnToggleListener;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class EasyEditShareView implements View {

	public interface OnSetPatchListener {
		void next();
		void before();
	}

	private Stage stage;
	
	private Text title;
	
	private Button next;
	private Button before;
	
	private OnToggleListener toggleListener;
	private OnSetPatchListener setPatchListener;

	private List<Effect> effects;

	public EasyEditShareView(Stage stage, OnToggleListener toggleListener, OnSetPatchListener setPatchListener) {
		this.stage = stage;
		this.toggleListener = toggleListener;
		this.effects = initEffects(stage.getScene().getRoot());

		this.title = (Text) stage.getScene().getRoot().lookup("#bank");
		
		this.next = (Button) stage.getScene().getRoot().lookup("#next");
		this.before = (Button) stage.getScene().getRoot().lookup("#before");
		
		next.setOnAction(event -> setPatchListener.next());
		before.setOnAction(event -> setPatchListener.before());
	}

	private List<Effect> initEffects(Parent parent) {
		ArrayList<Effect> effects = new ArrayList<>();
		
		for (int i = 1; i <= 6; i++) {
			ImageView image = (ImageView) parent.lookup("#image_"+i);
			Button button = (Button) parent.lookup("#state_"+i);
			
			Effect effect = new Effect(i, image, button);
			effect.setPedalName("None");
			effect.setListener(toggleListener);
			effects.add(effect);
		}
		
		return effects;
	}

	@Override
	public void setTitle(String newTitle) {
		Platform.runLater(() -> title.setText(newTitle));
	}

	@Override
	public void setPedalName(int index, String name) {
		effects.get(index).setPedalName(name);
	}

	@Override
	public void active(int effect) {
		effects.get(effect).active();
	}

	@Override
	public void disable(int effect) {
		effects.get(effect).disable();
	}
}
