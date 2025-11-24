
public class LuxuryCar extends Car {
    private double mileage;

    public LuxuryCar(int id,String brand, String model, int year, String engine, String transmission,
                     double price, String category, String color, String interior,
                     String drivetrain, String fuelType, String features, String image,
                     double mileage) {
        super(id, brand, model, year, engine, transmission, price, category, color, interior,
                drivetrain, fuelType, features, image);
        this.mileage = mileage;
    }

    @Override
    public String toString() {
        return super.toString()
                + ",\"mileage\":" + mileage
                + "}";
    }
}
