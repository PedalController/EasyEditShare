package br.com.srmourasilva.editshare;

public interface View {
	void setTitle(String newTitle);	
	void setPedalName(int index, String name);

	void active(int effect);
	void disable(int effect);
}
