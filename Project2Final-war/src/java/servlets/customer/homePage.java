/**
 * 0144266, Chris O'Brien 09005763, Shane Whelan Distributed Systems Project 2
*
 */
package servlets.customer;

import admin.*;
import customer.*;
import java.io.IOException;
import java.io.PrintWriter;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 *
 * @author Shane
 */
public class homePage extends HttpServlet {

    @EJB
    CustomerBeanLocal customerBean;
    @EJB
    AdminBeanLocal adminBean;

    /**
     * Processes requests for both HTTP
     * <code>GET</code> and
     * <code>POST</code> methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
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
        // Print Generic header then check authenitcation and print HTML
        printHeader(out);
        HttpSession currentSession = request.getSession(false);
        checkAuthentication(out, currentSession);
        out.println("</body></html>");
    }

    /**
     * Handles the HTTP
     * <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }

    private void printHeader(PrintWriter out) {
        out.println("<html>");
        out.println("<head>");
        out.println("<title>Home</title>");
        out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n"
                + "        <link rel=\"stylesheet\" href=\"project2.css\" type=\"text/css\">");
        out.println("</head>");
        out.println("<body>");
        out.println("<h1>Generic Shopping Cart Web Site</h1>");
    }

    private void checkAuthentication(PrintWriter out, HttpSession currentSession) {
        //Check Authentication
        if (currentSession.getAttribute("ID") == null) {
            out.println("</br></br><a href=registerCustomer>Register</a>&nbsp;&nbsp;");
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
                out.println("</br></br>Welcome Customer:</br>" + currentCustomer.getName() + "\n\n");
                out.println("</br></br><a href=shoppingCart>Cart</a>&nbsp;&nbsp;");
                out.println("<a href=logout>Logout</a>&nbsp;&nbsp;");
            } else if (adminBean.checkAdminId(intID) == true && adminBean.checkAdminUsername(username) == true) {
                // Print what admin should see
                out.println("<a href=searchProduct>Search Products by Name</a>&nbsp;&nbsp;");
                out.println("<a href=searchByID>Search Products by ID</a>&nbsp;&nbsp;");
                out.println("<a href=listAllProducts>Show All Products</a>&nbsp;&nbsp;");
                Admin currentAdmin = adminBean.getAdmin(intID);
                out.println("</br></br>Welcome Administrator:</br>" + currentAdmin.getName());
                out.println("</br><a href=createProduct>Create Product</a>&nbsp;&nbsp;");
                out.println("<a href=deleteProduct>Remove Product</a>&nbsp;&nbsp;");
                out.println("<a href=updateQty>Update Product Qty</a>&nbsp;&nbsp;");
                out.println("</br></br><a href=logout>Logout</a>&nbsp;&nbsp;" + "</br></br>");
            }
        }
    }
}
