/**
 * 0144266, Chris O'Brien 09005763, Shane Whelan Distributed Systems Project 2
*
 */
package servlets.product;

import admin.Admin;
import admin.AdminBeanLocal;
import customer.Customer;
import customer.CustomerBeanLocal;
import java.io.IOException;
import java.io.PrintWriter;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import product.Product;
import product.ProductBeanLocal;

public class searchByID extends HttpServlet {

    @EJB
    ProductBeanLocal productBean;
    @EJB
    CustomerBeanLocal customerBean;
    @EJB
    AdminBeanLocal adminBean;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        // Print Generic header then check authenitcation and print HTML
        printHeader(out);
        HttpSession currentSession = request.getSession(false);
        checkAuthentication(out, currentSession);

        // Print off form to search
        out.println("<form action=searchByID method=POST>");
        out.println("Enter Product ID: <input type=\"text\" name=\"id\"></br>");
        out.println("<input type=\"submit\" text=\"Submit\"></br>");
        out.println("<a href=\"index.jsp\">Home</a>");
        out.println("</body></html>");
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        PrintWriter out = response.getWriter();

        // Print Generic header then check authenitcation and print HTML
        printHeader(out);
        HttpSession currentSession = request.getSession(false);
        checkAuthentication(out, currentSession);

        // Get product object for searching
        try {
            Product p = productBean.getProduct(Integer.parseInt(request.getParameter("id")));
            if (p == null) {
                out.println("</br></br>No product with this ID");
            } else {
                // Print out result
                out.println("</br>Result:</br>");
                out.println(p.getId() + " " + "<a href=\"productDisplayPage?productName=" + p.getName() + "\" >" + p.getName() + "</a>" + " - " + p.getDescription() + " - " + p.getQty() + "</br></br>");
            }
            out.println("<a href=\"index.jsp\">Home</a>");
            out.println("</body></html>");

        } catch (Exception e) {
            // catches any exception (such as letter for quantity or product name too long) and prints out error
            out.print("<html><head></head><body><h1>Please enter an number. A real number. </h1></body></html>");
        }
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
        out.println("<title>Search By ID</title>");
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
