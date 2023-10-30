package model;

public class Product {
	private String name;
	private int id;
	private int volume;
	private String description;
	
	@Override
	public String toString() {
		return name + "|" + id + "|" + volume + "|" + description;
	}
	
	public Product()
	{
		
	}

	public Product(String name, int id, int volume, String description) {
		super();
		this.name = name;
		this.id = id;
		this.volume = volume;
		this.description = description;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public int getId()
	{
		return this.id;
	}

	public int getVolume() {
		return volume;
	}

	public void setVolume(int volume) {
		this.volume = volume;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public void setId(int parseInt) {
		this.id=parseInt;
		// TODO Auto-generated method stub
		
	}
	
}
