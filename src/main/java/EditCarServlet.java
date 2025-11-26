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
            // connect to database
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DBConnection.getConnection();

            // read normal form fields
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

            // -----------------------------
            // 1 get old category and old image
            // -----------------------------
            String oldCategory = "";
            PreparedStatement getPs = con.prepareStatement(
                "SELECT category, image FROM cars WHERE id=?"
            );
            getPs.setInt(1, id);
            ResultSet rs = getPs.executeQuery();
            String oldImagePath = "";

            if (rs.next()) {
                oldCategory = rs.getString("category");
                oldImagePath = rs.getString("image");
            }
            rs.close();
            getPs.close();

            // -----------------------------
            // 2 handle optional new image upload
            // -----------------------------
            Part filePart = request.getPart("image");
            String finalImagePath = oldImagePath;

            // if user uploaded a new file replace old image
            if (filePart != null && filePart.getSize() > 0) {

                // extract file name
                String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();

                // create upload folder path
                String uploadPath = getServletContext().getRealPath("/" + UPLOAD_DIR);

                // create folder if missing
                File uploadDir = new File(uploadPath);
                if (!uploadDir.exists()) uploadDir.mkdir();

                // save the uploaded file to server
                filePart.write(uploadPath + File.separator + fileName);

                // build final visible path for database
                finalImagePath = request.getContextPath() + "/" + UPLOAD_DIR + "/" + fileName;
            }

            // -----------------------------
            // 3 update main cars table
            // -----------------------------
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

            // -----------------------------
            // 4 reset and reinsert subclass table based on new category
            // -----------------------------

            // remove old subclass entries first
            con.prepareStatement("DELETE FROM sport_cars WHERE car_id=" + id).executeUpdate();
            con.prepareStatement("DELETE FROM luxury_cars WHERE car_id=" + id).executeUpdate();
            con.prepareStatement("DELETE FROM suv_cars WHERE car_id=" + id).executeUpdate();
            con.prepareStatement("DELETE FROM sedan_cars WHERE car_id=" + id).executeUpdate();

            // insert new subclass details
            if (newCategory.equals("Luxury")) {
                // luxury uses mileage
                double mileage = Double.parseDouble(request.getParameter("mileage"));
                PreparedStatement l = con.prepareStatement(
                        "INSERT INTO luxury_cars (car_id, mileage) VALUES (?,?)");
                l.setInt(1, id);
                l.setDouble(2, mileage);
                l.executeUpdate();
                l.close();
            }
            else if (newCategory.equals("Sports")) {
                // sports uses top speed
                int topSpeed = Integer.parseInt(request.getParameter("top_speed"));
                PreparedStatement s = con.prepareStatement(
                        "INSERT INTO sport_cars (car_id, top_speed) VALUES (?,?)");
                s.setInt(1, id);
                s.setInt(2, topSpeed);
                s.executeUpdate();
                s.close();
            }
            else if (newCategory.equals("SUV")) {
                // suv uses seating capacity
                int seating = Integer.parseInt(request.getParameter("seating_capacity"));
                PreparedStatement s2 = con.prepareStatement(
                        "INSERT INTO suv_cars (car_id, seating_capacity) VALUES (?,?)");
                s2.setInt(1, id);
                s2.setInt(2, seating);
                s2.executeUpdate();
                s2.close();
            }
            else if (newCategory.equals("Sedan")) {
                // sedan uses trunk space
                int trunk = Integer.parseInt(request.getParameter("trunk_space_liters"));
                PreparedStatement s3 = con.prepareStatement(
                        "INSERT INTO sedan_cars (car_id, trunk_space_liters) VALUES (?,?)");
                s3.setInt(1, id);
                s3.setInt(2, trunk);
                s3.executeUpdate();
                s3.close();
            }

            // send success response
            con.close();
            out.print("{\"success\":true}");

        
        } catch (Exception e) {
            // send error if something breaks
            e.printStackTrace();
            out.print("{\"error\":\"" + e.getMessage().replace("\"", "'") + "\"}");
        }
    }
}
