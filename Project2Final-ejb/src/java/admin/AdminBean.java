/**
 * 0144266, Chris O'Brien 09005763, Shane Whelan Distributed Systems Project 2
*
 */
package admin;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class AdminBean implements AdminBeanLocal {

    @PersistenceContext(unitName = "Project2Final-ejbPU")
    private EntityManager em;

    @Override
    public boolean checkPassword(String name, String password) {
        Admin a;
        try {
            // First get admin Object
            a = (Admin) em.createNamedQuery("Admin.findByName").setParameter("name", name).getSingleResult();
            // now check retrieved objects password
            if (a.getPassword().equals(password)) {
                return true;
            }
        } catch (Exception e) {
            return false;
        }
        return false;
    }

    /**
     *
     * @param id
     * @return
     */
    @Override
    public boolean checkAdminId(int id) {
        Admin a;
        try {
            // Attempt to cast an admin object if sql query returns null then catch exception its false.
            a = (Admin) em.createNamedQuery("Admin.findById").setParameter("id", id).getSingleResult();
        } catch (Exception e) {
            return false;
        }
        return true;
    }

    @Override
    public boolean checkAdminUsername(String username) {
        Admin a;
        try {
            // Attempt to cast an admin object if sql query returns null then catch exception its false.
            a = (Admin) em.createNamedQuery("Admin.findByName").setParameter("name", username).getSingleResult();
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
    public Admin getAdmin(int id) {
        try {
            // Attempt to cast an admin object if sql query returns null then catch exception return null.
            return (Admin) em.createNamedQuery("Admin.findById").setParameter("id", id).getSingleResult();
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
    public Admin getAdmin(String username) {
        try {
            // Attempt to cast an admin object if sql query returns null then catch exception return null.
            return (Admin) em.createNamedQuery("Admin.findByName").setParameter("name", username).getSingleResult();
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
    public void createAdmin(String name, String password) {
        // Create admin object and persist onto database
        Admin a = new Admin();
        persist(a);
        a.setName(name);
        a.setPassword(password);
    }

    /**
     * Method to persist an object to the db
     *
     * @param object
     */
    public void persist(Object object) {
        em.persist(object);
    }
}
