import jakarta.servlet.*;
import jakarta.servlet.annotation.*;
import jakarta.servlet.http.*;
import java.io.*;
import java.sql.*;
import java.nio.file.*;

@WebServlet("/AddCarServlet")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024,   // file kept in memory up to 1 mb
    maxFileSize = 1024 * 1024 * 5,     // max single file allowed is 5 mb
    maxRequestSize = 1024 * 1024 * 10  // max total request size is 10 mb
)
public class AddCarServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private static final String UPLOAD_DIR = "uploads"; // folder where images will be stored

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        Connection con = null;

        try {
            // connect to database
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DBConnection.getConnection();

            // read all fields from form
            int id = Integer.parseInt(request.getParameter("id"));
            String brand = request.getParameter("brand");
            String model = request.getParameter("model");
            int year = Integer.parseInt(request.getParameter("year"));
            String category = request.getParameter("category");
            double price = Double.parseDouble(request.getParameter("price"));
            String engine = request.getParameter("engine");
            String transmission = request.getParameter("transmission");
            String drivetrain = request.getParameter("drivetrain");
            String fuelType = request.getParameter("fuelType");
            String color = request.getParameter("color");
            String interior = request.getParameter("interior");
            String features = request.getParameter("features");

            // read subclass fields if present
            String mileage = request.getParameter("mileage");
            String topSpeed = request.getParameter("top_speed");
            String seating = request.getParameter("seating_capacity");
            String trunk = request.getParameter("trunk_space_liters");

            // create correct car object based on category
            Car car = null;

            if (category.equals("Luxury")) {
                // make luxury car object
                car = new LuxuryCar(
                    id, brand, model, year, engine, transmission, price,
                    category, color, interior, drivetrain, fuelType,
                    features, null,
                    Double.parseDouble(mileage)
                );
            }
            else if (category.equals("Sports")) {
                // make sports car object
                car = new SportCar(
                    id, brand, model, year, engine, transmission, price,
                    category, color, interior, drivetrain, fuelType,
                    features, null,
                    Integer.parseInt(topSpeed)
                );
            }
            else if (category.equals("SUV")) {
                // make suv object
                car = new SUVCar(
                    id, brand, model, year, engine, transmission, price,
                    category, color, interior, drivetrain, fuelType,
                    features, null,
                    Integer.parseInt(seating)
                );
            }
            else if (category.equals("Sedan")) {
                // make sedan object
                car = new SedanCar(
                    id, brand, model, year, engine, transmission, price,
                    category, color, interior, drivetrain, fuelType,
                    features, null,
                    Integer.parseInt(trunk)
                );
            }

            // handle image upload and create final path
            String imagePath = "";
            Part filePart = request.getPart("image");

            if (filePart != null && filePart.getSize() > 0) {

                // get file name from uploaded file
                String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();

                // build absolute upload path
                String uploadPath = getServletContext().getRealPath("/" + UPLOAD_DIR);

                // create upload folder if missing
                File uploadDir = new File(uploadPath);
                if (!uploadDir.exists()) uploadDir.mkdir();

                // save file to disk
                filePart.write(uploadPath + File.separator + fileName);

                // create relative path for database
                imagePath = request.getContextPath() + "/" + UPLOAD_DIR + "/" + fileName;
            }

            // assign the uploaded image to the car object
            car.image = imagePath;

            // insert main car data into parent table using parent fields only
            PreparedStatement ps = con.prepareStatement(
                "INSERT INTO cars (id, brand, model, year, category, price, engine, " +
                "transmission, drivetrain, fuel_type, color, interior, features, image) " +
                "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)"
            );

            // use inherited fields for insertion
            ps.setInt(1, car.id);
            ps.setString(2, car.brand);
            ps.setString(3, car.model);
            ps.setInt(4, car.year);
            ps.setString(5, car.category);
            ps.setDouble(6, car.price);
            ps.setString(7, car.engine);
            ps.setString(8, car.transmission);
            ps.setString(9, car.drivetrain);
            ps.setString(10, car.fuelType);
            ps.setString(11, car.color);
            ps.setString(12, car.interior);
            ps.setString(13, car.features);
            ps.setString(14, car.image);
            ps.executeUpdate();
            ps.close();

            // insert subclass info based on actual type of car object
            if (car instanceof LuxuryCar lux) {
                // insert luxury car specific field
                PreparedStatement luxPS = con.prepareStatement(
                    "INSERT INTO luxury_cars (car_id, mileage) VALUES (?,?)"
                );
                luxPS.setInt(1, car.id);
                luxPS.setDouble(2, lux.getMileage());
                luxPS.executeUpdate();
                luxPS.close();
            }
            else if (car instanceof SportCar sport) {
                // insert sport car field
                PreparedStatement sportPS = con.prepareStatement(
                    "INSERT INTO sport_cars (car_id, top_speed) VALUES (?,?)"
                );
                sportPS.setInt(1, car.id);
                sportPS.setInt(2, sport.gettopSpeed());
                sportPS.executeUpdate();
                sportPS.close();
            }
            else if (car instanceof SUVCar suv) {
                // insert suv field
                PreparedStatement suvPS = con.prepareStatement(
                    "INSERT INTO suv_cars (car_id, seating_capacity) VALUES (?,?)"
                );
                suvPS.setInt(1, car.id);
                suvPS.setInt(2, suv.getseatingCapacity());
                suvPS.executeUpdate();
                suvPS.close();
            }
            else if (car instanceof SedanCar sedan) {
                // insert sedan field
                PreparedStatement sedPS = con.prepareStatement(
                    "INSERT INTO sedan_cars (car_id, trunk_space_liters) VALUES (?,?)"
                );
                sedPS.setInt(1, car.id);
                sedPS.setInt(2, sedan.gettrunkSpace());
                sedPS.executeUpdate();
                sedPS.close();
            }

            // close db and send success response
            con.close();
            out.print("{\"success\":true}");

        } catch (Exception e) {
            // catch any error and return in json
            e.printStackTrace();
            out.print("{\"error\":\"" + e.getMessage().replace("\"","'") + "\"}");
        }
    }
}
