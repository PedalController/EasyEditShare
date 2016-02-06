package br.com.srmourasilva.editshare;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

@Deprecated
public class EasyEditShare extends Application implements EventHandler<ActionEvent>, View {

	public static void main(String[] args) {
		launch(args);
	}

	private Stage stage;

	private Button[] buttons;

	private EasyEditSharePresenter presenter;

	@Override
	public void start(Stage stage) {
		this.stage = stage;

		GridPane grid = new GridPane();

		grid.setAlignment(Pos.CENTER);
		grid.setHgap(10);
		grid.setVgap(10);
		grid.setPadding(new Insets(25, 25, 25, 25));

		this.buttons = createStatusButtons(grid);

		createPatchButtons(grid);
		
		Scene scene = new Scene(grid, 500, 250);

		stage.setTitle("Pedals Status");
		stage.setScene(scene);
		stage.show();
		
		stage.setOnCloseRequest(windowEvent -> this.presenter.stop());
		
		this.presenter = new EasyEditSharePresenter(this);
		this.presenter.start();
	}

	private Button[] createStatusButtons(GridPane grid) {
		Button[] buttons = new Button[6];
		for (int i = 0; i < 6; i++) {
			Button button = createButton(i);
			buttons[i] = button;
			grid.add(button, i%3, i/3 < 1 ? 0 : 1);
		}
		
		return buttons;
	}

	private Button createButton(int id) {
		Button btn = new Button();
		btn.setText("Pedal " + id);
		btn.setOnAction(this);
		btn.setBackground(new Background(new BackgroundFill(Color.DARKRED, CornerRadii.EMPTY, Insets.EMPTY)));

		return btn;
	}

	private void createPatchButtons(GridPane grid) {
		Button next = new Button();
		next.setText("Next");
		next.setOnAction(event -> presenter.nextPatch());

		Button before = new Button();
		before.setText("Before");
		before.setOnAction(event -> presenter.beforePatch());

		grid.add(next, 4, 0);
		grid.add(before, 4, 1);
	}

	@Override
	public void handle(ActionEvent event) {
		Button button = (Button) event.getTarget();
		this.presenter.toogleEffectOf(idOf(button));
	}

	private int idOf(Button button) {
		for (int i = 0; i < buttons.length; i++)
			if (buttons[i] == button)
				return i;

		return -1;
	}

	public void active(int effect) {
		effect(Color.RED, effect);
	}

	public void disable(int effect) {
		effect(Color.DARKRED, effect);
	}
	
	private void effect(Color color, int effect) {
		Platform.runLater(() -> buttons[effect].setBackground(new Background(new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY))));
	}
	
	public void setPedalName(int index, String name) {
		Platform.runLater(() -> buttons[index].setText("Pedal " + index + " - " + name));
	}

	public void setTitle(String newTitle) {
		Platform.runLater(() -> stage.setTitle(newTitle));
	}
}