import jakarta.servlet.*;
import jakarta.servlet.annotation.*;
import jakarta.servlet.http.*;
import java.io.*;
import java.sql.*;
import java.nio.file.*;

@WebServlet("/EditCarServlet")
@MultipartConfig(
    fileSizeThreshold = 1024 * 1024,
    maxFileSize = 1024 * 1024 * 5,
    maxRequestSize = 1024 * 1024 * 10
)
public class EditCarServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
	private static final String URL = "jdbc:mysql://localhost:3306/luxury_drives";
    private static final String USER = "root";
    private static final String PASSWORD = "pranav123";
    private static final String UPLOAD_DIR = "uploads";

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        PrintWriter out = response.getWriter();

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection(URL, USER, PASSWORD);

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

            // ============================
            // 1. GET OLD CATEGORY
            // ============================
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

            // ============================
            // 2. HANDLE IMAGE UPLOAD (OPTIONAL) gpt
            // ============================
            Part filePart = request.getPart("image");
            String finalImagePath = oldImagePath;

            if (filePart != null && filePart.getSize() > 0) {
                String fileName = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
                String uploadPath = getServletContext().getRealPath("/" + UPLOAD_DIR);


                File uploadDir = new File(uploadPath);
                if (!uploadDir.exists()) uploadDir.mkdir();

                filePart.write(uploadPath + File.separator + fileName);

                finalImagePath = request.getContextPath() + "/" + UPLOAD_DIR + "/" + fileName;
            }

            // ============================
            // 3. UPDATE MAIN CAR TABLE
            // ============================
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

            // ============================
            // 4. IF CATEGORY CHANGED → UPDATE SUBCLASS TABLES
            // ============================
         // ALWAYS DELETE AND RE-INSERT SUBCLASS DATA (even if category is same)
            con.prepareStatement("DELETE FROM sport_cars WHERE car_id=" + id).executeUpdate();
            con.prepareStatement("DELETE FROM luxury_cars WHERE car_id=" + id).executeUpdate();
            con.prepareStatement("DELETE FROM suv_cars WHERE car_id=" + id).executeUpdate();
            con.prepareStatement("DELETE FROM sedan_cars WHERE car_id=" + id).executeUpdate();

            // INSERT INTO CORRECT TABLE
            if (newCategory.equals("Luxury")) {
                double mileage = Double.parseDouble(request.getParameter("mileage"));
                PreparedStatement l = con.prepareStatement(
                        "INSERT INTO luxury_cars (car_id, mileage) VALUES (?,?)");
                l.setInt(1, id);
                l.setDouble(2, mileage);
                l.executeUpdate();
                l.close();
            }
            else if (newCategory.equals("Sports")) {
                int topSpeed = Integer.parseInt(request.getParameter("top_speed"));
                PreparedStatement s = con.prepareStatement(
                        "INSERT INTO sport_cars (car_id, top_speed) VALUES (?,?)");
                s.setInt(1, id);
                s.setInt(2, topSpeed);
                s.executeUpdate();
                s.close();
            }
            else if (newCategory.equals("SUV")) {
                int seating = Integer.parseInt(request.getParameter("seating_capacity"));
                PreparedStatement s2 = con.prepareStatement(
                        "INSERT INTO suv_cars (car_id, seating_capacity) VALUES (?,?)");
                s2.setInt(1, id);
                s2.setInt(2, seating);
                s2.executeUpdate();
                s2.close();
            }
            else if (newCategory.equals("Sedan")) {
                int trunk = Integer.parseInt(request.getParameter("trunk_space_liters"));
                PreparedStatement s3 = con.prepareStatement(
                        "INSERT INTO sedan_cars (car_id, trunk_space_liters) VALUES (?,?)");
                s3.setInt(1, id);
                s3.setInt(2, trunk);
                s3.executeUpdate();
                s3.close();
            }



            con.close();
            out.print("{\"success\":true}");

        
            } catch (Exception e) {
            e.printStackTrace();
            out.print("{\"error\":\"" + e.getMessage().replace("\"", "'") + "\"}");
        }
    }
}
