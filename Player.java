package hki2;

public class Player {
	private String Playername;
	private final String filename="player.txt";

	public String getPlayername() {
		return Playername;
	}
	public void setPlayername(String playername) {
		Playername = playername;
	}
	public Player(String Playername) {
		this.Playername=Playername;
	}
	public void loadname(){
		
	}
	public void savename(String Playername) {
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}

}
