//inherit the abstract Car class
public class LuxuryCar extends Car {
    // additional feature of luxury car: mileage
    private double mileage;
    //constructor
    public LuxuryCar(int id,String brand, String model, int year, String engine, String transmission,
                     double price, String category, String color, String interior,
                     String drivetrain, String fuelType, String features, String image,
                     double mileage) {
        //call parent constructor
        super(id, brand, model, year, engine, transmission, price, category, color, interior,
                drivetrain, fuelType, features, image);
        this.mileage = mileage;
    }

    @Override
    public String toString() {
        //return json to front end and close the bracket
        return super.toString()
                + ",\"mileage\":" + mileage
                + "}";
    }
}

