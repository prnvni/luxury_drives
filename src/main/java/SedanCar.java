
//inherit abstract Car class
public class SedanCar extends Car {
        //extra feature of Sedan car:  trunk space
    private int trunkSpace;
 //constructor
    public SedanCar(int id,String brand, String model, int year, String engine, String transmission,
                    double price, String category, String color, String interior,
                    String drivetrain, String fuelType, String features, String image,
                    int trunkSpace) {
         //call parent constructor
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
            //return json format response
	public int gettrunkSpace() {
		return this.trunkSpace;
	}
}
