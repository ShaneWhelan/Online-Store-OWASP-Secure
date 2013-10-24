/**
 * 0144266, Chris O'Brien 09005763, Shane Whelan Distributed Systems Project 2
*
 */
package servlets.product;

import admin.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import product.Product;
import product.ProductBeanLocal;

public class updateQty extends HttpServlet {

    @EJB
    ProductBeanLocal productBean;
    @EJB
    AdminBeanLocal adminBean;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        // Print Generic header then check authenitcation and print HTML
        printHeader(out);
        HttpSession currentSession = request.getSession(false);
        if (checkAuthentication(out, currentSession) == true) {
            // User is logged in as admin
            // Get list of all products iterate through so user can decide what items to update qty.
            List<Product> pList = productBean.getAll();
            out.println("</br></br>");
            if (!pList.isEmpty()) {
                for (int i = 0; i < pList.size(); i++) {
                    out.println("<b>");
                    out.println(pList.get(i).getName());
                    out.println("</br>Update Qty: <form action=updateQty method=POST><input type=\"text\" name=\"qty\" value=\"" + pList.get(i).getQty() + "\" ><input type=\"hidden\" value =\"" + pList.get(i).getId() + "\" name=\"pID\"> <input type=\"submit\" name=\"submit\"></form>");
                    out.println("</b></br></br>");
                }
            }
        } else {
            out.println("<a href=registerCustomer>Register</a>&nbsp;&nbsp;");
            out.println("<a href=loginCustomer>Login</a>&nbsp;&nbsp;");
        }
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
        if (checkAuthentication(out, currentSession) == true) {
            // User is logged in as Administrator delete product
            try {
                int itemQty = Integer.parseInt(request.getParameter("qty"));
                if (itemQty > 0) {
                    // Qty changed -  print new qtys
                    productBean.changeProductQty(Integer.parseInt(request.getParameter("pID")), itemQty);
                    List<Product> pList = productBean.getAll();
                    out.println("</br></br>");
                    if (!pList.isEmpty()) {
                        for (int i = 0; i < pList.size(); i++) {
                            out.println("<b>");
                            out.println(pList.get(i).getName());
                            out.println("</br>Update Qty: <form action=updateQty method=POST><input type=\"text\" name=\"qty\" value=\"" + pList.get(i).getQty() + "\" ><input type=\"hidden\" value =\"" + pList.get(i).getId() + "\" name=\"pID\"> <input type=\"submit\" name=\"submit\"></form>");
                            out.println("</b></br></br>");
                            out.println("</br>Updated</br><br>");
                        }
                    }
                }else{
                    out.println("</br>Must be greater than 0</br><br>");
                }

            } catch (Exception e) {
                // catches any exception (such as letter for quantity or product name too long) and prints out error
                out.println("</br></br>Not a valid Qty </br></br>");
            }
        } else {
            out.println("<a href=registerCustomer>Register</a>&nbsp;&nbsp;");
            out.println("<a href=loginCustomer>Login</a>&nbsp;&nbsp;");
        }
        out.println("<a href=\"index.jsp\">Home</a>");
        out.println("</body></html>");
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
            } else {
                return false;
            }
        }
    }
}
