import java.io.*;
import javax.servlet.*;
import javax.servlet.annotation.*;
import javax.servlet.http.*;
import java.sql.*;

@WebServlet("/dashboard")
public class DashboardServlet extends HttpServlet {
    protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            res.sendRedirect("login.html");
            return;
        }

        int userId = (int) session.getAttribute("userId");
        PrintWriter out = res.getWriter();
        res.setContentType("text/html");

        try {
            Connection con = DBConnection.getConnection();
            PreparedStatement ps = con.prepareStatement("SELECT name, balance FROM users WHERE id = ?");
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String name = rs.getString("name");
                double balance = rs.getDouble("balance");

                out.println("<html><head><title>Dashboard</title><link rel='stylesheet' href='styles.css'></head><body>");
                out.println("<h2>Welcome, " + name + "</h2>");
                out.println("<p>Current Balance: ₹" + balance + "</p>");
                out.println("<form method='post' action='transaction'>");
                out.println("<select name='type'><option value='deposit'>Deposit</option><option value='withdraw'>Withdraw</option></select><br>");
                out.println("<input type='number' name='amount' placeholder='Amount' required><br>");
                out.println("<input type='submit' value='Submit Transaction'></form>");
                out.println("</body></html>");
            } else {
                out.println("User not found.");
            }

        } catch (Exception e) {
            e.printStackTrace();
            out.println("Error loading dashboard.");
        }
    }
}
