package br.com.srmourasilva.editshare;

import br.com.srmourasilva.domain.message.CommonCause;
import br.com.srmourasilva.domain.message.Details;
import br.com.srmourasilva.domain.message.Messages;
import br.com.srmourasilva.domain.message.multistomp.MultistompDetails;
import br.com.srmourasilva.domain.message.Message;
import br.com.srmourasilva.domain.multistomp.Effect;
import br.com.srmourasilva.domain.multistomp.OnMultistompListener;
import br.com.srmourasilva.editshare.view.View;
import br.com.srmourasilva.multistomp.controller.PedalController;
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

	@Override
	public void onChange(Messages messages) {
		messages.getBy(CommonCause.EFFECT_ACTIVE).forEach(message -> updateEffect(message, CommonCause.EFFECT_ACTIVE));
		messages.getBy(CommonCause.EFFECT_DISABLE).forEach(message -> updateEffect(message, CommonCause.EFFECT_DISABLE));

		messages.getBy(CommonCause.TO_PATCH).forEach(message -> setPatch(message));
		messages.getBy(CommonCause.PATCH_NAME).forEach(message -> updateTitle(message.details()));
		
		messages.getBy(CommonCause.PARAM_VALUE).forEach(message -> System.out.println(message));

		messages.getBy(CommonCause.EFFECT_TYPE).forEach(message -> updateEffect(message, CommonCause.EFFECT_TYPE));
	}

	private void updateEffect(Message message, CommonCause cause) {
		MultistompDetails details = (MultistompDetails) message.details();
		
		int patch  = details.patch;
		int effect = details.effect;

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
		MultistompDetails details = (MultistompDetails) message.details();
		int idPatch = details.patch;

		pedal.send(ZoomGSeriesMessages.REQUEST_SPECIFIC_PATCH_DETAILS(idPatch));
	}

	private void updateTitle(Details details) {
		MultistompDetails d = (MultistompDetails) details;
		
		updateTitle(d.patch, d.value.toString());
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