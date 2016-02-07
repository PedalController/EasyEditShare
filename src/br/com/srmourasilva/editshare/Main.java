package br.com.srmourasilva.editshare;

import java.io.IOException;
import java.net.URL;

import javax.sound.midi.MidiUnavailableException;

import br.com.srmourasilva.architecture.exception.DeviceNotFoundException;
import br.com.srmourasilva.editshare.EasyEditShareView.OnSetPatchListener;
import br.com.srmourasilva.editshare.view.View;
import br.com.srmourasilva.editshare.view.Effect.OnToggleListener;
import br.com.srmourasilva.multistomp.controller.PedalController;
import br.com.srmourasilva.multistomp.controller.PedalControllerFactory;
import br.com.srmourasilva.multistomp.zoom.gseries.ZoomGSeriesMessages;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class Main extends Application implements OnToggleListener, OnSetPatchListener {
	
	public static void main(String[] args) {
		launch(args);
	}

	private View view;
	private PedalController pedal;

	@Override
	public void start(Stage stage) throws Exception {
		stage = initView(stage);
		
		this.view = new EasyEditShareView(stage, this, this);
		EasyEditSharePresenter presenter = new EasyEditSharePresenter(view);

		this.pedal = initPedal(presenter);
		presenter.setPedal(pedal);
		
		
		stage.show();
	}

	private Stage initView(Stage stage) throws IOException {
		stage.setTitle("EasyEditShare");

		URL paneUrl = getClass().getResource("pane.fxml");
		Pane pane = (Pane) FXMLLoader.load(paneUrl);		

		Scene scene = new Scene(pane);
		stage.setScene(scene);
		
		stage.setOnCloseRequest((event) -> {
			if (pedal != null)
				pedal.off();
		});

		return stage;
	}
	
	public PedalController initPedal(EasyEditSharePresenter presenter) {
		PedalController pedal;
		try {
			pedal = PedalControllerFactory.searchPedal();
		} catch (DeviceNotFoundException e) {
			view.setTitle("Pedal not found! You connected any?");
			return null;
		}

		pedal.addListener(presenter);

		try {
			pedal.on();
			
		} catch (MidiUnavailableException e) {
			view.setTitle("This Pedal has been used by other process program");
			return null;
		}

		pedal.send(ZoomGSeriesMessages.REQUEST_CURRENT_PATCH_NUMBER());
		
		return pedal;
	}

	@Override
	public void next() {
		if (pedal != null)
			pedal.nextPatch();
	}

	@Override
	public void before() {
		if (pedal != null)
			pedal.beforePatch();
	}

	@Override
	public void onToggle(int idEffect) {
		if (pedal != null)
			pedal.toogleEffect(idEffect);
	}
}
