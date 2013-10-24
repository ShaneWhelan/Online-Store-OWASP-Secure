/**
 * 0144266, Chris O'Brien 09005763, Shane Whelan Distributed Systems Project 2
*
 */
package customer;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class CustomerBean implements CustomerBeanLocal {
    @PersistenceContext(unitName = "Project2Final-ejbPU")
    private EntityManager em;

    
    @Override
    public List<Customer> getCustomers(String city) {
        return em.createNamedQuery("Customer.findAllByAddress").setParameter("address", city).getResultList();
    }
    
    
    @Override
    public boolean checkPassword(String name, String password) {
        Customer c;
        try {
            // Get customer object if a null object is attempted to be cast exception is thrown and false is returned
            c = (Customer) em.createNamedQuery("Customer.findByUsername").setParameter("username", name).getSingleResult();
            // now check retrieved objects password
            if (c.getPassword().equals(password)) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }

        return false;
    }

    @Override
    public boolean checkCustomerId(int id) {
        Customer c;
        try {
            // Get customer object if a null object is attempted to be cast exception is thrown and false is returned
            c = (Customer) em.createNamedQuery("Customer.findByCid").setParameter("cid", id).getSingleResult();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean checkCustomerUsername(String username) {
        Customer c;
        try {
            // Get customer object if a null object is attempted to be cast exception is thrown and false is returned
            c = (Customer) em.createNamedQuery("Customer.findByUsername").setParameter("username", username).getSingleResult();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    /**
     * Method to search customer by ID in database
     *
     * @param ID
     * @return
     */
    @Override
    public Customer getCustomer(int cid) {
        try {
            // Get customer object if a null object is attempted to be cast exception is thrown and null is returned
            return (Customer) em.createNamedQuery("Customer.findByCid").setParameter("cid", cid).getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Search all customers by name
     *
     * @param name
     * @return
     */
    @Override
    public Customer getCustomer(String username) {
        try {
            // Get customer object if a null object is attempted to be cast exception is thrown and false is returned
            return (Customer) em.createNamedQuery("Customer.findByUsername").setParameter("username", username).getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }
    
    

    /**
     * Method to create a customer and persist changes to db.
     *
     * @param name
     * @param password
     * @param city
     * @return
     */
    @Override
    public void createCustomer(String username, String name, String password, String city) {
        // Create customer object and persist onto database
        Customer c = new Customer();
        persist(c);
        c.setUsername(username);
        c.setAddress(city);
        c.setPassword(password);
        c.setName(name);
    }

    /**
     * Method to persist an object to the db
     *
     * @param object
     */
    public void persist(Object object) {
        em.persist(object);
    }

    @Override
    public List<Customer> getAllCustomers() {
                try {
            // Get customer object if a null object is attempted to be cast exception is thrown and false is returned
            return em.createNamedQuery("Customer.findAll").getResultList();
        } catch (Exception e) {
            return null;
        }
    }
}
