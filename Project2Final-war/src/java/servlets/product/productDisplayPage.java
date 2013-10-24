/**
 * 0144266, Chris O'Brien 09005763, Shane Whelan Distributed Systems Project 2
*
 */
package servlets.product;

import admin.*;
import customer.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import product.*;

public class productDisplayPage extends HttpServlet {

    @EJB
    ProductBeanLocal productBean;
    @EJB
    CustomerBeanLocal customerBean;
    @EJB
    AdminBeanLocal adminBean;
    @EJB
    CommentBeanLocal commentBean;

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
        String productName = request.getParameter("productName");
        if (productName != null) {
            // Product exists so begin to work with a product object.
            Product productDisplay = productBean.getProduct(productName);
            out.println("<html>");
            out.println("<head>");
            // Print dynamic web page title.
            out.println("<title>" + productDisplay.getName() + "</title>");
            out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n"
                    + "        <link rel=\"stylesheet\" href=\"project2.css\" type=\"text/css\">");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Generic Shopping Cart Web Site</h1>");
            HttpSession currentSession = request.getSession(false);

            if (checkAuthentication(out, currentSession) == true) {
                out.println("<h1>Product Name: </h1>" + productDisplay.getName() + "</br>");
                out.println("<h1>Description: </h1>" + productDisplay.getDescription() + "</br>");
                // Get stock details
                int qty = productDisplay.getQty();
                if (qty > 0) {
                    out.println("<h3>Instock:</h3>" + qty + " left</br></br>");
                } else {
                    out.println("<h3>Not in stock.</h3>");
                }

                // User is logged in so print shopping cart link
                out.println("<a href=\"shoppingCart?productId=" + productDisplay.getId() + "\">Add to cart</a></br></br>");
                out.println("Add a comment: </br><form action=productDisplayPage method=POST><input type=\"text\" name=\"comment\" value=\"\" ><input type=\"hidden\" value =\"" + productDisplay.getName() + "\" name=\"productName\"><input type=\"submit\" name=\"submit\"></form>");
                // Get list of comments and print them off if they exist
                List<Comment> cList = commentBean.getAllProductComments(productDisplay.getId());
                if (!cList.isEmpty()) {
                    for (int i = 0; i < cList.size(); i++) {
                        out.println("<p>");
                        // Print actual comment with username
                        out.println("Username: </br>" + cList.get(i).getUsername() + "</br>Comment: </br> " + cList.get(i).getComment() + "</br>");
                        out.println("</p></br>");
                    }
                }
            }
            out.println("</br></br><a href=\"index.jsp\">Home</a>");
            out.println("</body></html>");
        }
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
        // If you get a post request it means a comment was entered.
        PrintWriter out = response.getWriter();
        // Print Generic header then check authenitcation and print HTML
        printHeader(out);
        HttpSession currentSession = request.getSession(false);
        if (checkAuthentication(out, currentSession) == true) {
            // User is logged in so get Product object 
            String productName = request.getParameter("productName");
            Product product = productBean.getProduct(productName);
            String comment = request.getParameter("comment");
            String username = (String) currentSession.getAttribute("username");
            // Actually create comment through persistence API
            commentBean.createComment(product.getId(), username, comment);
            out.println("</br>Comment Entered</br><br>");
        } else {
            out.println("<a href=registerCustomer>Register</a>&nbsp;&nbsp;");
            out.println("<a href=loginCustomer>Login</a>&nbsp;&nbsp;");
        }
        out.println("<a href=\"index.jsp\">Home</a>");
        out.println("</body></html>");
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
        out.println("<title>Update Quantities</title>");
        out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n"
                + "        <link rel=\"stylesheet\" href=\"project2.css\" type=\"text/css\">");
        out.println("</head>");
        out.println("<body>");
        out.println("<h1>Generic Shopping Cart Web Site</h1>");
    }

    private boolean checkAuthentication(PrintWriter out, HttpSession currentSession) {
        //Check Authentication
        if (currentSession.getAttribute("ID") == null) {
            return false;
        } else {
            // If they have any session at all print links dosnt matter if session is hacked or not cause linked pages are protected.
            out.println("<a href=searchProduct>Search Products by Name</a>&nbsp;&nbsp;");
            out.println("<a href=searchByID>Search Products by ID</a>&nbsp;&nbsp;");
            out.println("<a href=listAllProducts>Show All Products</a>&nbsp;&nbsp;");
            // Use ID and Username to identify users.
            int intID = (Integer) currentSession.getAttribute("ID");
            String username = (String) currentSession.getAttribute("username");
            if (adminBean.checkAdminId(intID) == true && adminBean.checkAdminUsername(username) == true) {
                return true;
            } else if (customerBean.checkCustomerId(intID) == true && customerBean.checkCustomerUsername(username) == true) {
                return true;
            } else {
                return false;
            }
        }
    }
}
