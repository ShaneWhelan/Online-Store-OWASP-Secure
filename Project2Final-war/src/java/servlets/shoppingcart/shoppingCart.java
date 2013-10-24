/**
 * 0144266, Chris O'Brien 09005763, Shane Whelan Distributed Systems Project 2
*
 */
package servlets.shoppingcart;

import admin.*;
import customer.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Resource;
import javax.ejb.EJB;
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
import product.Product;
import product.ProductBeanLocal;

public class shoppingCart extends HttpServlet {

    @Resource(mappedName = "jms/shoping")
    private Queue shoping;
    @Resource(mappedName = "jms/shopingFactory")
    private ConnectionFactory shopingFactory;
    @EJB
    ProductBeanLocal productBean;
    @EJB
    AdminBeanLocal adminBean;
    @EJB
    CustomerBeanLocal customerBean;

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
        // Check if user is logged in
        if (checkAuthentication(out, currentSession) == true) {
            out.println("<form action=shoppingCart method=POST>");
            out.println("<table>");
            out.println("<tr><td>Item</td><td>Quantity</td><td>Remove</td></tr>");
            // Items that have previously been added to cart stored in hash map in session
            HashMap<Integer, Integer> cartItems = (HashMap) currentSession.getAttribute("cartItems");
            if (cartItems == null) {
                // Nothing in cart but have hash map ready to enter stuff.
                cartItems = new HashMap<Integer, Integer>();
            } else {
                // Print whats in cart from session
                // Iterate through HashMap cart items
                Iterator it = cartItems.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry pairs = (Map.Entry) it.next();
                    int itemId = (Integer) pairs.getKey();
                    int itemQty = (Integer) pairs.getValue();
                    Product currentProduct = productBean.getProduct(itemId);
                    // Print out tags with name, qty, checkbox to remove
                    out.println("<tr><td>" + currentProduct.getName() + "</td><td><input type=text name=" + currentProduct.getId() + " value=" + currentProduct.getQty() + "></td><td><input type=\"checkbox\" name=\"c" + currentProduct.getId() + "\" value=\"true\" /></td></tr>");
                }
            }

            // This is done to add the item to the cart the user just clicked on.
            String id = request.getParameter("productId");
            if (id != null) {
                // Get object of product to add
                Product addProduct = productBean.getProduct(Integer.parseInt(id));
                out.println("<tr><td>" + addProduct.getName() + "</td><td><input type=text name=" + addProduct.getId() + " value=1 ></td><td><input type=\"checkbox\" name=\"c" + addProduct.getId() + "\" value=\"true\" /></td></tr>");
                cartItems.put(addProduct.getId(), 1);
            }
            // End of cart do just once
            out.println("<tr><td><input type=submit name=submit value=\"Remove Selected\"></td></tr>");
            // Set session with new cart added in
            currentSession.setAttribute("cartItems", cartItems);
            currentSession.setMaxInactiveInterval(360);
            out.println("<tr><td colspan=2></td></tr>");
            out.println("</table>");
            out.println("<input type=submit name=submit value=Checkout>");
            out.println("</form>");
        }
        out.println("</br></br><a href=\"index.jsp\">Home</a>");
        out.println("</body>");
        out.println("</html>");
    }

    /**
     * ***************************
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        PrintWriter out = response.getWriter();
        // Print Generic header then check authenitcation and print HTML
        printHeader(out);
        // Is it an remove items or checkout?
        String submitType = request.getParameter("submit");
        Date date = new java.util.Date();
        HttpSession session = request.getSession();
        HashMap<Integer, Integer> cartItems = (HashMap) session.getAttribute("cartItems");
        if (submitType.equals("Checkout") == true) {
            // Get cart items from session whether they exist or not.

            if (cartItems != null) {
                // Iterate over cart
                Iterator it = cartItems.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry pairs = (Map.Entry) it.next();
                    int itemId = (Integer) pairs.getKey();
                    int itemQty;
                    try {
                        // Use item ID to get value of text box for QTY
                        itemQty = Integer.parseInt(request.getParameter(Integer.toString(itemId)));
                        // Use item id to get product
                        Product currentProduct = productBean.getProduct(itemId);
                        int currentQty = currentProduct.getQty();
                        // Compare qty in database with amount user suggested
                        if (currentQty > 0) {
                            if (itemQty > 0) {
                                if ((currentQty - itemQty) >= 0) {
                                    // You got this far... Its ok to change qty of database
                                    productBean.changeProductQty(itemId, currentQty - itemQty);
                                    out.println("</br>Product: " + currentProduct.getName() + " ordered.</br></br>");
                                    // Log a purchase in log file
                                    sendJMSMessageToShoping("[" + new Timestamp(date.getTime()) + "]" + " Product: \"" + currentProduct.getName() + "\" ordered by customer \"" + session.getAttribute("username") + "\"");
                                } else {
                                    out.println("Not enough of product " + currentProduct.getName() + " in stock.");
                                    sendJMSMessageToShoping("[" + new Timestamp(date.getTime()) + "]" + " Product: \"" + currentProduct.getName() + "\" attempted to order but not enough in stock to fulfill\"" + session.getAttribute("username") + "\"");
                                }
                            } else {
                                out.println("Choose a quantity greater than 0.");
                            }
                        } else {
                            out.println("Product " + currentProduct.getName() + " not in stock.");
                        }
                        it.remove(); // avoids a ConcurrentModificationException and clears out cart.
                    } catch (Exception e) {
                        out.println("Not a valid Qty </br></br>");
                    }
                }
            }
        } else if (submitType.equals("Remove Selected") == true) {
            // Remove only products that had checkbox selected
            out.println("</br></br><a href=shoppingCart>Cart</a>&nbsp;&nbsp;");
            out.println("<a href=logout>Logout</a>&nbsp;&nbsp;");
            if (cartItems != null) {
                out.println("<form action=shoppingCart method=POST>");
                out.println("<table>");
                out.println("<tr><td>Item</td><td>Quantity</td><td>Remove</td></tr>");
                // Iterate over cart stored in checkout.
                Iterator it = cartItems.entrySet().iterator();
                while (it.hasNext()) {
                    Map.Entry pairs = (Map.Entry) it.next();
                    int itemId = (Integer) pairs.getKey();
                    int itemQty;
                    Product currentProduct = productBean.getProduct(itemId);
                    try {
                        // Get item qty from user input to remeber when they increased qty in cart
                        itemQty = Integer.parseInt(request.getParameter(Integer.toString(itemId)));
                        String itemCheckboxName = "c" + itemId;
                        String isBoxChecked = request.getParameter(itemCheckboxName);
                        if (isBoxChecked == null) {
                            out.println("<tr><td>" + currentProduct.getName() + "</td><td><input type=text name=" + currentProduct.getId() + " value=" + currentProduct.getQty() + "></td><td><input type=\"checkbox\" name=\"c" + currentProduct.getId() + "\" value=\"true\" /></td></tr>");
                        } else {
                            // Remove from cart items hash map so doesnt get written out to session.
                            it.remove();
                            cartItems.remove(itemId);
                            // Record in log file
                            sendJMSMessageToShoping("[" + new Timestamp(date.getTime()) + "]" + " Product: \"" + currentProduct.getName() + "\" removed from cart by Customer: \"" + session.getAttribute("username") + "\"");
                        }
                    } catch (Exception e) {
                        out.println("Not a valid Qty </br></br>");
                    }
                }
            }
            //Cart processing finished dump out rest of page
            out.println("<tr><td><input type=submit name=submit value=\"Remove Selected\"></td></tr>");
            // Set session
            session.setAttribute("cartItems", cartItems);
            session.setMaxInactiveInterval(360);
            out.println("<tr><td colspan=2></td></tr>");
            out.println("</table>");
            out.println("<input type=submit name=submit value=Checkout>");
            out.println("</form>");
        }
        out.println("</br></br><a href=\"index.jsp\">Home</a>");
        out.println("</body>");
        out.println("</html>");

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

    private boolean checkAuthentication(PrintWriter out, HttpSession currentSession) {
        //Check Authentication
        if (currentSession.getAttribute("ID") == null) {
            out.println("</br></br><a href=registerCustomer>Register</a>&nbsp;&nbsp;");
            out.println("<a href=loginCustomer>Login</a>&nbsp;&nbsp;");
            return false;
        } else {
            // If they have any session at all print links dosnt matter if session is hacked or not cause linked pages are protected.
            out.println("<a href=searchProduct>Search Products by Name</a>&nbsp;&nbsp;");
            out.println("<a href=searchByID>Search Products by ID</a>&nbsp;&nbsp;");
            out.println("<a href=listAllProducts>Show All Products</a>&nbsp;&nbsp;");
            // Use ID and Username to identify users.
            int intID = (Integer) currentSession.getAttribute("ID");
            String username = (String) currentSession.getAttribute("username");
            if (customerBean.checkCustomerId(intID) == true && customerBean.checkCustomerUsername(username) == true) {
                Customer currentCustomer = customerBean.getCustomer(intID);
                out.println("</br></br>Welcome " + currentCustomer.getName() + "\n\n");
                out.println("</br></br><a href=shoppingCart>Cart</a>&nbsp;&nbsp;");
                out.println("<a href=logout>Logout</a>&nbsp;&nbsp;");
                return true;
            } else if (adminBean.checkAdminId(intID) == true && adminBean.checkAdminUsername(username) == true) {
                Admin currentAdmin = adminBean.getAdmin(intID);
                out.println("</br></br>Welcome " + currentAdmin.getName());
                out.println("</br><a href=createProduct>Create Product</a>&nbsp;&nbsp;");
                out.println("<a href=deleteProduct>Remove Product</a>&nbsp;&nbsp;");
                out.println("<a href=updateQty>Update Product Qty</a>&nbsp;&nbsp;");
                out.println("</br><a href=logout>Logout</a>&nbsp;&nbsp;" + "</br></br>");
                return true;
            }
            return false;
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
