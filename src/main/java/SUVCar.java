
//inherit abstract Car class  

public class SUVCar extends Car {
    //extra feature of SUVCar : seating capacity
    private int seatingCapacity;
    //constructor
    public SUVCar(int id,String brand, String model, int year, String engine, String transmission,
                  double price, String category, String color, String interior,
                  String drivetrain, String fuelType, String features, String image,
                  int seatingCapacity) {
        //call parent constructor
        super(id,brand, model, year, engine, transmission, price,
                category, color, interior, drivetrain, fuelType, features, image);

        this.seatingCapacity = seatingCapacity;
    }
            //return json format 
    public int getseatingCapacity() {
    	return this.seatingCapacity;
    }
    @Override
    public String toString() {
        return super.toString()
                + ",\"seatingCapacity\":" + seatingCapacity
                + "}";
    }
}
