

public class SUVCar extends Car {

    private int seatingCapacity;

    public SUVCar(int id,String brand, String model, int year, String engine, String transmission,
                  double price, String category, String color, String interior,
                  String drivetrain, String fuelType, String features, String image,
                  int seatingCapacity) {

        super(id,brand, model, year, engine, transmission, price,
                category, color, interior, drivetrain, fuelType, features, image);

        this.seatingCapacity = seatingCapacity;
    }

    @Override
    public String toString() {
        return super.toString()
                + ",\"seatingCapacity\":" + seatingCapacity
                + "}";
    }
}
