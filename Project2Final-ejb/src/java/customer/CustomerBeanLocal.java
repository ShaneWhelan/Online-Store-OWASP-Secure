/**
 * 0144266, Chris O'Brien 09005763, Shane Whelan Distributed Systems Project 2
*
 */
package customer;

import java.util.List;
import javax.ejb.Local;

@Local
public interface CustomerBeanLocal {
    // Interface for customer tasks

    public boolean checkPassword(String name, String password);

    public boolean checkCustomerId(int id);

    public boolean checkCustomerUsername(String name);

    public Customer getCustomer(int ID);

    public Customer getCustomer(String username);
    
    public List<Customer> getCustomers(String city);
    
    public List<Customer> getAllCustomers();

    public void createCustomer(String username, String name, String password, String city);
}
