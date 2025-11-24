

public class SportCar extends Car {
    private int topSpeed;

    public SportCar(int id,String brand, String model, int year, String engine, String transmission,
                    double price, String category, String color, String interior,
                    String drivetrain, String fuelType, String features, String image,
                    int topSpeed) {
        super(id,brand, model, year, engine, transmission, price, category, color, interior,
                drivetrain, fuelType, features, image);
        this.topSpeed = topSpeed;
    }

    @Override
    public String toString() {
        return super.toString()
                + ",\"topSpeed\":" + topSpeed
                + "}";
    }
}
