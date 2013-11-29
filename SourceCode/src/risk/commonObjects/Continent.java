package risk.commonObjects;

public class Continent {
	private String name;
	private int numberOfTerritories;
	private int bonus;
	private int numberOfBorderingTerritories;
	
	public Continent(String name, int number, int bonus, int borders) {
		this.setName(name);
		this.setNumberOfBorderingTerritories(borders);
		this.setBonus(bonus);
		this.setNumberOfTerritories(number);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getNumberOfTerritories() {
		return numberOfTerritories;
	}

	public void setNumberOfTerritories(int numberOfTerritories) {
		this.numberOfTerritories = numberOfTerritories;
	}

	public int getBonus() {
		return bonus;
	}

	public void setBonus(int bonus) {
		this.bonus = bonus;
	}

	public int getNumberOfBorderingTerritories() {
		return numberOfBorderingTerritories;
	}

	public void setNumberOfBorderingTerritories(int numberOfBorderingTerritories) {
		this.numberOfBorderingTerritories = numberOfBorderingTerritories;
	}
}
