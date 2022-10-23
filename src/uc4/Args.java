package uc4;

public enum Args {
	START("start");

	private final String text;
	
	Args(String text) {
		this.text = text;
	}
	
	@Override
	public String toString() {
		return this.text;
	}
}
