import java.io.*;
import java.sql.*;
import jakarta.servlet.*;
import jakarta.servlet.annotation.*;
import jakarta.servlet.http.*;

@WebServlet("/CarServlet")
public class CarServlet extends HttpServlet {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        resp.setContentType("application/json");
        PrintWriter out = resp.getWriter();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/luxury_drives",
                    "root",
                    "pranav123"
            );

            String query =
                "SELECT c.*, l.mileage, s.top_speed, d.trunk_space_liters, u.seating_capacity " +
                "FROM cars c " +
                "LEFT JOIN luxury_cars l ON c.id = l.car_id " +
                "LEFT JOIN sport_cars s ON c.id = s.car_id " +
                "LEFT JOIN sedan_cars d ON c.id = d.car_id " +
                "LEFT JOIN suv_cars u ON c.id = u.car_id";

            PreparedStatement ps = con.prepareStatement(query);
            ResultSet rs = ps.executeQuery();

            out.print("[");
            boolean first = true;

            while (rs.next()) {

                if (!first) out.print(",");
                first = false;

                int id = rs.getInt("id");
                String brand = rs.getString("brand");
                String model = rs.getString("model");
                int year = rs.getInt("year");
                String engine = rs.getString("engine");
                String transmission = rs.getString("transmission");
                double price = rs.getDouble("price");
                String category = rs.getString("category");
                String color = rs.getString("color");
                String interior = rs.getString("interior");
                String drivetrain = rs.getString("drivetrain");
                String fuelType = rs.getString("fuel_type");
                String features = rs.getString("features");
                String image = rs.getString("image");

                Car car = null;

                if (rs.getObject("mileage") != null) {
                    car = new LuxuryCar(id, brand, model, year, engine, transmission, price,
                            category, color, interior, drivetrain, fuelType,
                            features, image, rs.getDouble("mileage"));
                }
                else if (rs.getObject("top_speed") != null) {
                    car = new SportCar(id, brand, model, year, engine, transmission, price,
                            category, color, interior, drivetrain, fuelType,
                            features, image, rs.getInt("top_speed"));
                }
                else if (rs.getObject("trunk_space_liters") != null) {
                    car = new SedanCar(id, brand, model, year, engine, transmission, price,
                            category, color, interior, drivetrain, fuelType,
                            features, image, rs.getInt("trunk_space_liters"));
                }
                else if (rs.getObject("seating_capacity") != null) {
                    car = new SUVCar(id, brand, model, year, engine, transmission, price,
                            category, color, interior, drivetrain, fuelType,
                            features, image, rs.getInt("seating_capacity"));
                }
                

                out.print(car.toString());
            }

            out.print("]");
            
            rs.close();
            ps.close();
            con.close();

        } catch (Exception e) {
            e.printStackTrace();
            out.print("[]");
        }
    }
}