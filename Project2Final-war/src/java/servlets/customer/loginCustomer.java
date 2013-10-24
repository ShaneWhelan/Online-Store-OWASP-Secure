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

public class loginCustomer extends HttpServlet {

    @EJB
    CustomerBeanLocal customerBean;
    @EJB
    AdminBeanLocal adminBean;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    /**
     * Handles the HTTP
     * <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        // Print out generic header
        printHeader(out);
        HttpSession currentSession = request.getSession(false);
        // Authenitcate and print out tags relevant to the users state
        checkAuthentication(out, currentSession);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        // Avoid injection by going through persistance API
        boolean isCustomer = customerBean.checkPassword(username, password);
        boolean isAdmin = adminBean.checkPassword(username, password);
        out.println("<html>");
        out.println("<head>");
        out.println("<title>Login Customer</title><meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n"
                + "        <link rel=\"stylesheet\" href=\"project2.css\" type=\"text/css\">");
        out.println("</head>");
        out.println("<body>");

        if (isCustomer == false && isAdmin == false) {
            out.println("Incorrect Username or password");
        } else if (isCustomer == true) {
            Customer c = customerBean.getCustomer(username);
            HttpSession newSess = request.getSession(true);
            newSess.setAttribute("ID", c.getCid());
            newSess.setAttribute("username", c.getUsername());
            newSess.setMaxInactiveInterval(360);
            // Logged in as customer redirect to home
            response.sendRedirect("/Project2Final-war/index.jsp");
        } else if (isAdmin == true) {
            Admin adminUser = adminBean.getAdmin(username);
            HttpSession newSess = request.getSession(true);
            newSess.setAttribute("ID", adminUser.getId());
            newSess.setAttribute("username", adminUser.getName());
            newSess.setMaxInactiveInterval(360);
            // Logged in as Administrator redirect to home
            response.sendRedirect("/Project2Final-war/index.jsp");
        }
        // If you get to this point havent been redirected so print end of HTML page
        out.println("</br></br><a href=\"loginCustomer\">Retry Login</a>");
        out.println("</body></html>");
    }

    private void printHeader(PrintWriter out) {
        out.println("<html>");
        out.println("<head>");
        out.println("<title>Login</title>");
        out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n"
                + "        <link rel=\"stylesheet\" href=\"project2.css\" type=\"text/css\">");
        out.println("</head>");
        out.println("<body>");
        out.println("<h1>Generic Shopping Cart Web Site</h1>");
    }

    private void checkAuthentication(PrintWriter out, HttpSession currentSession) {
        //Check Authentication
        if (currentSession.getAttribute("ID") == null) {
            out.println("<h1>Login</h1>");
            out.println("<form action=loginCustomer method=POST>");
            out.println("Username: <input type=\"text\" name=\"username\"></br>");
            out.println("Password: <input type=\"password\" name=\"password\"></br>");
            out.println("<input type=\"submit\" text=\"Submit\"></br>");
            out.println("<a href=\"index.jsp\">Home</a>");
            out.println("</body></html>");
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
