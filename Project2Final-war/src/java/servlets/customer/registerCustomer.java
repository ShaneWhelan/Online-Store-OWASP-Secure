/**
 * 0144266, Chris O'Brien 09005763, Shane Whelan Distributed Systems Project 2
*
 */
package servlets.customer;

import admin.*;
import customer.Customer;
import customer.CustomerBeanLocal;
import java.io.IOException;
import java.io.PrintWriter;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class registerCustomer extends HttpServlet {

    @EJB
    CustomerBeanLocal customerBean;
    @EJB
    AdminBeanLocal adminBean;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        // Print out generic header of HTML file
        printHeader(out);
        HttpSession currentSession = request.getSession(false);
        // Authenitcate and print out tags relevant to the users state
        checkAuthentication(out, currentSession);

        out.println("<form action=registerCustomer method=POST>");
        out.println("Username: <input type=\"text\" name=\"username\"></br>");
        out.println("Name: <input type=\"text\" name=\"name\"></br>");
        out.println("Address: <input type=\"text\" name=\"address\"></br>");
        out.println("Password: <input type=\"password\" name=\"password\"></br>");
        out.println("<input type=\"submit\" text=\"Submit\"></br>");
        out.println("<a href=\"index.jsp\">Home</a>");
        out.println("</body></html>");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Check if customer exists with username
        Customer cust = customerBean.getCustomer(request.getParameter("username"));
        Admin admin = adminBean.getAdmin(request.getParameter("username"));
        PrintWriter out = response.getWriter();
        printHeader(out);

        if (cust == null && admin == null) {
            // Username does not exist
            customerBean.createCustomer(request.getParameter("username"), request.getParameter("name"), request.getParameter("password"), request.getParameter("address"));
            out.println("<h1>You have successfully Registered.</h1>");
        } else {
            out.println("<h1>Username already exists</h1>");
        }
        out.println("<br><br><a href=index.jsp>Home</a>");
        out.println("</body></html>");
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }

    private void printHeader(PrintWriter out) {
        out.println("<html>");
        out.println("<head>");
        out.println("<title>Registration</title>");
        out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n"
                + "        <link rel=\"stylesheet\" href=\"project2.css\" type=\"text/css\">");
        out.println("</head>");
        out.println("<body>");
        out.println("<h1>Generic Shopping Cart Web Site</h1>");
    }

    private void checkAuthentication(PrintWriter out, HttpSession currentSession) {
        //Check Authentication
        if (currentSession.getAttribute("ID") == null) {
            out.println("<a href=loginCustomer>Login</a>&nbsp;&nbsp;");
        } else {
            // Use ID and Username to identify users. At this point we know there is a session with an ID, verify it is a valid session
            int intID = (Integer) currentSession.getAttribute("ID");
            String username = (String) currentSession.getAttribute("username");
            if (customerBean.checkCustomerId(intID) == true && customerBean.checkCustomerUsername(username) == true) {
                // Print what customer should see
                out.println("<a href=searchProduct>Search Products by Name</a>&nbsp;&nbsp;");
                out.println("<a href=searchByID>Search Products by ID</a>&nbsp;&nbsp;");
                out.println("<a href=listAllProducts>Show All Products</a>&nbsp;&nbsp;");
                Customer currentCustomer = customerBean.getCustomer(intID);
                out.println("</br></br>Welcome Customer" + currentCustomer.getName() + "\n\n");
                out.println("</br></br><a href=shoppingCart>Cart</a>&nbsp;&nbsp;");
                out.println("<a href=logout>Logout</a>&nbsp;&nbsp;");
            } else if (adminBean.checkAdminId(intID) == true && adminBean.checkAdminUsername(username) == true) {
                // Print what admin should see
                out.println("<a href=searchProduct>Search Products by Name</a>&nbsp;&nbsp;");
                out.println("<a href=searchByID>Search Products by ID</a>&nbsp;&nbsp;");
                out.println("<a href=listAllProducts>Show All Products</a>&nbsp;&nbsp;");
                Admin currentAdmin = adminBean.getAdmin(intID);
                out.println("</br></br>Welcome Administrator:" + currentAdmin.getName());
                out.println("</br><a href=createProduct>Create Product</a>&nbsp;&nbsp;");
                out.println("<a href=deleteProduct>Remove Product</a>&nbsp;&nbsp;");
                out.println("<a href=updateQty>Update Product Qty</a>&nbsp;&nbsp;");
                out.println("</br><a href=logout>Logout</a>&nbsp;&nbsp;" + "</br></br>");
            }
        }
    }
}
