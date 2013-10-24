/**
 * 0144266, Chris O'Brien 09005763, Shane Whelan Distributed Systems Project 2
*
 */
package servlets.product;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import javax.ejb.EJB;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import product.Product;
import product.ProductBeanLocal;

public class listAllProducts extends HttpServlet {

    @EJB
    ProductBeanLocal productBean;

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
        // Get List of all products and print dynamic links to productDisplayPage
        List<Product> pList = productBean.getAll();
        if (!pList.isEmpty()) {
            for (int i = 0; i < pList.size(); i++) {
                out.println("<p>");
                out.println("<a href=\"productDisplayPage?productName=" + pList.get(i).getName() + "\" >" + pList.get(i).getName() + "</a>" + " - Desc: " + pList.get(i).getDescription() + " - Stock On Hand: " + pList.get(i).getQty());
                out.println("</p></br>");
            }
        }
        out.println("<a href=\"index.jsp\">Home</a>");
        out.println("</body></html>");

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
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
        out.println("<title>List All Products</title>");
        out.println("<meta http-equiv=\"Content-Type\" content=\"text/html; charset=UTF-8\">\n"
                + "        <link rel=\"stylesheet\" href=\"project2.css\" type=\"text/css\">");
        out.println("</head>");
        out.println("<body>");
        out.println("<h1>Generic Shopping Cart Web Site</h1>");
        out.println("<a href=searchProduct>Search Products by Name</a>&nbsp;&nbsp;");
        out.println("<a href=searchByID>Search Products by ID</a>&nbsp;&nbsp;");
        out.println("<a href=listAllProducts>Show All Products</a>&nbsp;&nbsp;");
    }
}
