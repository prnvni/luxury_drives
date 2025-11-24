public abstract class Car {
    protected int id;   
    protected String brand;
    protected String model;
    protected int year;
    protected String engine;
    protected String transmission;
    protected double price;
    protected String category;
    protected String color;
    protected String interior;
    protected String drivetrain;
    protected String fuelType;
    protected String features;
    protected String image;

    public Car(int id, String brand, String model, int year, String engine,
               String transmission, double price, String category, String color,
               String interior, String drivetrain, String fuelType,
               String features, String image) {

        this.id = id;  
        this.brand = brand;
        this.model = model;
        this.year = year;
        this.engine = engine;
        this.transmission = transmission;
        this.price = price;
        this.category = category;
        this.color = color;
        this.interior = interior;
        this.drivetrain = drivetrain;
        this.fuelType = fuelType;
        this.features = features;
        this.image = image;
    }

    @Override
    public String toString() {
        return "{"
                + "\"id\":" + id + ","   
                + "\"brand\":\"" + brand + "\","
                + "\"model\":\"" + model + "\","
                + "\"year\":" + year + ","
                + "\"engine\":\"" + engine + "\","
                + "\"transmission\":\"" + transmission + "\","
                + "\"price\":" + price + ","
                + "\"category\":\"" + category + "\","
                + "\"color\":\"" + color + "\","
                + "\"interior\":\"" + interior + "\","
                + "\"drivetrain\":\"" + drivetrain + "\","
                + "\"fuelType\":\"" + fuelType + "\","
                + "\"features\":\"" + features + "\","
                + "\"image\":\"" + image + "\"";
    }
}
