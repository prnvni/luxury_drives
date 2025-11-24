
public class SedanCar extends Car {
    private int trunkSpace;

    public SedanCar(int id,String brand, String model, int year, String engine, String transmission,
                    double price, String category, String color, String interior,
                    String drivetrain, String fuelType, String features, String image,
                    int trunkSpace) {
        super(id, brand, model, year, engine, transmission, price, category, color, interior,
                drivetrain, fuelType, features, image);
        this.trunkSpace = trunkSpace;
    }

    @Override
    public String toString() {
        return super.toString()
                + ",\"trunkSpaceLiters\":" + trunkSpace
                + "}";
    }
}
