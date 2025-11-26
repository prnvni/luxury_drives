
//inherits abstract Car class
public class SportCar extends Car {
        //extra feature of sports car: topspeed
    private int topSpeed;
    //constructor
    public SportCar(int id,String brand, String model, int year, String engine, String transmission,
                    double price, String category, String color, String interior,
                    String drivetrain, String fuelType, String features, String image,
                    int topSpeed) {
        //call parent constructor
        super(id,brand, model, year, engine, transmission, price, category, color, interior,
                drivetrain, fuelType, features, image);
        this.topSpeed = topSpeed;
    }
      //return json format response
    public int gettopSpeed() {
    	return this.topSpeed;
    }
    @Override
    public String toString() {
        return super.toString()
                + ",\"topSpeed\":" + topSpeed
                + "}";
    }
}
