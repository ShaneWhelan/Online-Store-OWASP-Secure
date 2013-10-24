/**
 * 0144266, Chris O'Brien 09005763, Shane Whelan Distributed Systems Project 2
*
 */
package servlets.product;

import admin.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.EJB;
import javax.ejb.EJBException;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import product.ProductBeanLocal;

public class createProduct extends HttpServlet {

    @EJB
    ProductBeanLocal productBean;
    @EJB
    AdminBeanLocal adminBean;
    @Resource(mappedName = "jms/shopingFactory")
    private ConnectionFactory shopingFactory;
    @Resource(mappedName = "jms/shoping")
    private Queue shoping;

    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        // Print Generic header then check authenitcation and print HTML
        printHeader(out);

        HttpSession currentSession = request.getSession(false);
        if (currentSession.getAttribute("ID") == null) {
            out.println("<a href=registerCustomer>Register</a>&nbsp;&nbsp;");
            out.println("<a href=loginCustomer>Login</a>&nbsp;&nbsp;");
        } else {
            // Use ID and Username to identify users.
            int intID = (Integer) currentSession.getAttribute("ID");
            String username = (String) currentSession.getAttribute("username");
            if (adminBean.checkAdminId(intID) == true && adminBean.checkAdminUsername(username) == true) {
                // No check for customer - we dont care if they are a customer Admin only functionality
                Admin currentAdmin = adminBean.getAdmin(intID);
                out.println("Welcome " + currentAdmin.getName());
                out.println("<a href=logout>Logout</a>&nbsp;&nbsp;" + "</br></br>");
                out.println("<form action=createProduct method=POST>");
                out.println("Product Name: <input type=\"text\" name=\"name\"></br>");
                out.println("Product Description: <input type=\"text\" name=\"desc\"></br>");
                out.println("Qty: <input type=\"text\" name=\"qty\"></br>");
                out.println("<input type=\"submit\" text=\"Submit\"></br>");
            }
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
            // User is admin create product
            try {
                System.out.print(request.getParameter("name").length());
                if (request.getParameter("name").length() > 0) {
                    productBean.createProduct(request.getParameter("name"), request.getParameter("desc"), request.getParameter("qty"));
                    out.println("<h1>Product has been created.</h1>");
                } else {
                    out.println("<h1>Please enter a product name</h1>");
                    out.println("<a href=createProduct>Create Product</a>");
                }
                Date date = new Date();
                try {
                    // Use message driven bean to write to logfile using the format I learned for timestamps on co-op
                    sendJMSMessageToShoping("[" + new Timestamp(date.getTime()) + "]" + " Product: \"" + request.getParameter("name") + "\" created by Administrator: \"" + currentSession.getAttribute("username") + "\"");
                } catch (JMSException ex) {
                    Logger.getLogger(createProduct.class.getName()).log(Level.SEVERE, null, ex);
                }

            } catch (EJBException e) {
                // catches any EJB exception (such as letter for quantity or product name too long) and prints out error
                out.print("<html><head></head><body><h1>Invalid parameters</h1></body></html>");
            }

        }
        out.println("<a href=\"index.jsp\">Home</a>");
        out.println("</body></html>");
    }

    @Override
    public String getServletInfo() {
        return "Short description";
    }

    private void printHeader(PrintWriter out) {
        out.println("<html>");
        out.println("<head>");
        out.println("<title>Create Product</title>");
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

    private Message createJMSMessageForjmsShoping(Session session, Object messageData) throws JMSException {
        // Populate Message object with string for log file.
        TextMessage tm = session.createTextMessage();
        tm.setText(messageData.toString());
        return tm;
    }

    private void sendJMSMessageToShoping(Object messageData) throws JMSException {
        Connection connection = null;
        Session session = null;
        try {
            // I'm aware that I misspelled shopping...
            connection = shopingFactory.createConnection();
            session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            // Generate event to sent to message driven bean on back end
            MessageProducer messageProducer = session.createProducer(shoping);
            messageProducer.send(createJMSMessageForjmsShoping(session, messageData));
        } finally {
            if (session != null) {
                try {
                    session.close();
                } catch (JMSException e) {
                    Logger.getLogger(this.getClass().getName()).log(Level.WARNING, "Cannot close session", e);
                }
            }
            if (connection != null) {
                connection.close();
            }
        }
    }
}
