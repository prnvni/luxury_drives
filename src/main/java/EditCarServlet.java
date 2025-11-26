import jakarta.servlet.*;
import jakarta.servlet.annotation.*;
import jakarta.servlet.http.*;
import java.io.*;
import java.sql.*;
import java.nio.file.*;

@WebServlet("/EditCarServlet")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024,   // file stored in memory until 1 mb
    maxFileSize = 1024 * 1024 * 5,     // max single uploaded file is 5 mb
    maxRequestSize = 1024 * 1024 * 10  // full multipart request cannot exceed 10 mb
)
public class EditCarServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    private static final String UPLOAD_DIR = "uploads"; // folder used for saving images

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        Connection con = null;

        try {
            // open database connection
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DBConnection.getConnection();

            // read common fields from form
            int id = Integer.parseInt(request.getParameter("id"));
            String brand = request.getParameter("brand");
            String model = request.getParameter("model");
            int year = Integer.parseInt(request.getParameter("year"));
            String newCategory = request.getParameter("category");
            double price = Double.parseDouble(request.getParameter("price"));
            String engine = request.getParameter("engine");
            String transmission = request.getParameter("transmission");
            String drivetrain = request.getParameter("drivetrain");
            String fuelType = request.getParameter("fuelType");
            String color = request.getParameter("color");
            String interior = request.getParameter("interior");
            String features = request.getParameter("features");

            // get previous category and image for this car
            String oldCategory = "";
            String oldImagePath = "";
            PreparedStatement getPs = con.prepareStatement(
                "SELECT category, image FROM cars WHERE id=?"
            );
            getPs.setInt(1, id);
            ResultSet rs = getPs.executeQuery();

            if (rs.next()) {
                oldCategory = rs.getString("category");
                oldImagePath = rs.getString("image");
            }
            rs.close();
            getPs.close();

            // check if a new image was uploaded
            Part filePart = request.getPart("image");
            String finalImagePath = oldImagePath;

            if (filePart != null && filePart.getSize() > 0) {

                // extract file name of uploaded file
                String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();

                // get internal folder path to store image
                String uploadPath = getServletContext().getRealPath("/" + UPLOAD_DIR);

                // create folder if missing
                File uploadDir = new File(uploadPath);
                if (!uploadDir.exists()) uploadDir.mkdir();

                // write file to local folder
                filePart.write(uploadPath + File.separator + fileName);

                // build web accessible url to store in db
                finalImagePath = request.getContextPath() + "/" + UPLOAD_DIR + "/" + fileName;
            }

            // update car record stored in parent table
            PreparedStatement ps = con.prepareStatement(
                "UPDATE cars SET brand=?, model=?, year=?, category=?, price=?, engine=?, "
                        + "transmission=?, drivetrain=?, fuel_type=?, color=?, interior=?, features=?, image=? "
                        + "WHERE id=?"
            );

            ps.setString(1, brand);
            ps.setString(2, model);
            ps.setInt(3, year);
            ps.setString(4, newCategory);
            ps.setDouble(5, price);
            ps.setString(6, engine);
            ps.setString(7, transmission);
            ps.setString(8, drivetrain);
            ps.setString(9, fuelType);
            ps.setString(10, color);
            ps.setString(11, interior);
            ps.setString(12, features);
            ps.setString(13, finalImagePath);
            ps.setInt(14, id);

            ps.executeUpdate();
            ps.close();

            // delete old subclass data because car type may have changed
            con.prepareStatement("DELETE FROM luxury_cars WHERE car_id=" + id).executeUpdate();
            con.prepareStatement("DELETE FROM sport_cars WHERE car_id=" + id).executeUpdate();
            con.prepareStatement("DELETE FROM suv_cars WHERE car_id=" + id).executeUpdate();
            con.prepareStatement("DELETE FROM sedan_cars WHERE car_id=" + id).executeUpdate();

            // insert new subclass values based on new category
            if (newCategory.equals("Luxury")) {

                double mileage = Double.parseDouble(request.getParameter("mileage"));
                PreparedStatement lux = con.prepareStatement(
                        "INSERT INTO luxury_cars (car_id, mileage) VALUES (?,?)");
                lux.setInt(1, id);
                lux.setDouble(2, mileage);
                lux.executeUpdate();
                lux.close();
            }
            else if (newCategory.equals("Sports")) {

                int topSpeed = Integer.parseInt(request.getParameter("top_speed"));
                PreparedStatement sp = con.prepareStatement(
                        "INSERT INTO sport_cars (car_id, top_speed) VALUES (?,?)");
                sp.setInt(1, id);
                sp.setInt(2, topSpeed);
                sp.executeUpdate();
                sp.close();
            }
            else if (newCategory.equals("SUV")) {

                int seating = Integer.parseInt(request.getParameter("seating_capacity"));
                PreparedStatement suv = con.prepareStatement(
                        "INSERT INTO suv_cars (car_id, seating_capacity) VALUES (?,?)");
                suv.setInt(1, id);
                suv.setInt(2, seating);
                suv.executeUpdate();
                suv.close();
            }
            else if (newCategory.equals("Sedan")) {

                int trunk = Integer.parseInt(request.getParameter("trunk_space_liters"));
                PreparedStatement sed = con.prepareStatement(
                        "INSERT INTO sedan_cars (car_id, trunk_space_liters) VALUES (?,?)");
                sed.setInt(1, id);
                sed.setInt(2, trunk);
                sed.executeUpdate();
                sed.close();
            }

            con.close();
            out.print("{\"success\":true}");

        } catch (Exception e) {

            // send error json if something goes wrong
            e.printStackTrace();
            out.print("{\"error\":\"" + e.getMessage().replace("\"", "'") + "\"}");
        }
    }
}
