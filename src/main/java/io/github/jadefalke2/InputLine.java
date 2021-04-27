package io.github.jadefalke2;

import io.github.jadefalke2.stickRelatedClasses.StickPosition;
import io.github.jadefalke2.util.CorruptedScriptException;
import io.github.jadefalke2.util.Input;

import java.util.ArrayList;

public class InputLine {

	private int frame;
	public ArrayList<String> buttonsEncoded = new ArrayList<>();
	private StickPosition stickL, stickR;

	public InputLine(String full) {
		try {
			splitIntoComponents(full);
		} catch (CorruptedScriptException e) {
			e.printStackTrace();
		}
	}


	private void splitIntoComponents(String full) throws CorruptedScriptException {

		if (full.equals("")){
			throw new CorruptedScriptException("empty script");
		}

		String[] components = full.split(" ");

		frame = Integer.parseInt(components[0]);
		String buttons = components[1];
		String[] buttonsPressed = buttons.split(";");

		for (String s : buttonsPressed) {
			if (Input.getEncodeInputMap().containsValue(s)) {
				buttonsEncoded.add(Input.getDecodeInputMap().get(s));
			}
			else if(!s.equals("NONE")){
				throw new CorruptedScriptException("Unknown button encountered: "+s);
			}
		}

		stickL = new StickPosition(components[2]);
		stickR = new StickPosition(components[3]);
	}


	public int getFrame() {
		return frame;
	}

	public ArrayList<String> getButtonsEncoded() {
		return buttonsEncoded;
	}

	public StickPosition getStickL() {
		return stickL;
	}

	public StickPosition getStickR() {
		return stickR;
	}

	public void setStickL(StickPosition stickL) {
		this.stickL = stickL;
	}

	public void setStickR(StickPosition stickR) {
		this.stickR = stickR;
	}

	public void setFrame(int frame) {
		this.frame = frame;
	}

	public String getFull() {
		StringBuilder tmpString = new StringBuilder();

		tmpString.append(frame).append(" ");

		boolean first = true;

		if (buttonsEncoded.isEmpty()) {
			tmpString.append("NONE");
		} else {
			for (String button : buttonsEncoded) {

				if (!first) {
					tmpString.append(";");
				} else {
					first = false;
				}

				tmpString.append(Input.getEncodeInputMap().get(button));
			}
		}

		tmpString
			.append(" ").append(getStickL().toCartString())
			.append(" ").append(getStickR().toCartString());


		return tmpString.toString();
	}

	public Object[] getArray (){

		String[] buttonNames = {
			"A",
			"B",
			"X",
			"Y",
			"ZR",
			"ZL",
			"R",
			"L",
			"DP-R",
			"DP-L",
			"DP-U",
			"DP-D"
		};

		ArrayList<Object> tmp = new ArrayList<>();
		tmp.add(frame);
		tmp.add(stickL.toCartString());
		tmp.add(stickR.toCartString());

		for (int i = 3; i < buttonNames.length + 3; i++){
			tmp.add(buttonsEncoded.contains(buttonNames[i - 3]) ? buttonNames[i - 3] : " ");
		}

		return tmp.toArray();
	}

	public boolean isEmpty (){
		return buttonsEncoded.isEmpty() && stickR.isZeroZero() && stickL.isZeroZero();
	}

	public static InputLine getEmpty (int line){
		return new InputLine(line + " NONE 0;0 0;0");
	}
}
