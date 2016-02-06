package br.com.srmourasilva.editshare;

import javax.sound.midi.MidiUnavailableException;

import br.com.srmourasilva.architecture.exception.DeviceNotFoundException;
import br.com.srmourasilva.domain.OnMultistompListener;
import br.com.srmourasilva.domain.message.CommonCause;
import br.com.srmourasilva.domain.message.Messages;
import br.com.srmourasilva.domain.message.Messages.Message;
import br.com.srmourasilva.domain.multistomp.Effect;
import br.com.srmourasilva.multistomp.controller.PedalController;
import br.com.srmourasilva.multistomp.controller.PedalControllerFactory;
import br.com.srmourasilva.multistomp.zoom.gseries.ZoomGSeriesMessages;

public class EasyEditSharePresenter implements OnMultistompListener {

	private View view;

	private PedalController pedal;

	public EasyEditSharePresenter(View view) {
		this.view = view;
	}
	
	public void setPedal(PedalController pedal) {
		this.pedal = pedal;
	}

	@Deprecated
	public void start() {
		try {
			this.pedal = PedalControllerFactory.searchPedal();
		} catch (DeviceNotFoundException e) {
			view.setTitle("Pedal not found! You connected any?");
			return;
		}

		pedal.addListener(this);

		try {
			pedal.on();
			
		} catch (MidiUnavailableException e) {
			view.setTitle("This Pedal has been used by other process program");
			return;
		}

		pedal.send(ZoomGSeriesMessages.REQUEST_CURRENT_PATCH_NUMBER());
	}

	@Deprecated
	public void stop() {
		if (pedal != null)
			pedal.off();
	}

	@Override
	public void onChange(Messages messages) {
		messages.getBy(CommonCause.EFFECT_ACTIVE).forEach(message -> updateEffect(message, CommonCause.EFFECT_ACTIVE));
		messages.getBy(CommonCause.EFFECT_DISABLE).forEach(message -> updateEffect(message, CommonCause.EFFECT_DISABLE));

		messages.getBy(CommonCause.TO_PATCH).forEach(message -> setPatch(message));
		messages.getBy(CommonCause.PATCH_NAME).forEach(message -> updateTitle(message.details().patch, message.details().value.toString()));
		
		messages.getBy(CommonCause.PARAM_VALUE).forEach(message -> System.out.println(message));

		messages.getBy(CommonCause.EFFECT_TYPE).forEach(message -> updateEffect(message, CommonCause.EFFECT_TYPE));
	}

	private void updateEffect(Message message, CommonCause cause) {
		int patch  = message.details().patch;
		int effect = message.details().effect;

		boolean otherPatch = patch != pedal.multistomp().getIdCurrentPatch();
		if (otherPatch)
			return;

		if (cause == CommonCause.EFFECT_ACTIVE)
			view.active(effect);
		else if (cause == CommonCause.EFFECT_DISABLE)
			view.disable(effect);

		else if (cause == CommonCause.EFFECT_TYPE)
			view.setPedalName(effect, pedal.multistomp().currentPatch().effects().get(effect).getName());
	}
	
	private void setPatch(Message message) {
		int idPatch = message.details().patch;

		pedal.send(ZoomGSeriesMessages.REQUEST_SPECIFIC_PATCH_DETAILS(idPatch));
	}

	private void updateTitle(int index, String value) {
		String patch = ((char) (65 + (index / 10))) + "" + index % 10;
		patch += " - " + value;
		view.setTitle(patch);
	}


	public void toogleEffectOf(int effect) {
		this.pedal.toogleEffect(effect);
	}

	public void updateNameOf(int indexEffect, Effect effect) {
		this.view.setPedalName(indexEffect, effect.getName());
	}
	
	/////////////////////////////////////////////////////

	public void nextPatch() {
		this.pedal.nextPatch();
	}
	
	public void beforePatch() {
		this.pedal.beforePatch();
	}
}