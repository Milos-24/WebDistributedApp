package model;

public class Product {
    private String name;
    private int id;
    private int volume;
    private String description;

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getVolume() {
        return volume;
    }

    public void setVolume(int volume) {
        this.volume = volume;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Override
    public String toString() {
        return "Product{" +
                "name='" + name + '\'' +
                ", id=" + id +
                ", volume=" + volume +
                ", description='" + description + '\'' +
                '}';
    }

    public Product(String name, int id, int volume, String description) {
        this.id = id;
        this.name=name;
        this.description=description;
        this.volume=volume;
    }
}
