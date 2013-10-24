/**
 * 0144266, Chris O'Brien 09005763, Shane Whelan Distributed Systems Project 2
*
 */
package product;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

@Stateless
public class ProductBean implements ProductBeanLocal {

    @PersistenceContext(unitName = "Project2Final-ejbPU")
    private EntityManager em;

    @Override
    public List<Product> getProductName(String name) {
        return em.createNamedQuery("Product.findByName").
                setParameter("name", "%" + name + "%").getResultList();
    }

    @Override
    public Product getProduct(int ID) {
        try {
            // Get Product object if a null object is attempted to be cast exception is thrown and null is returned
            return (Product) em.createNamedQuery("Product.findById").setParameter("id", ID).getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public Product getProduct(String name) {
        try {
            // Get Product object if a null object is attempted to be cast exception is thrown and null is returned
            return (Product) em.createNamedQuery("Product.findByName").setParameter("name", name).getSingleResult();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void createProduct(String name, String description, String qty) {
        // Create Product object and persist onto database
        Product p = new Product();
        persist(p);
        p.setName(name);
        p.setDescription(description);
        p.setQty(Integer.parseInt(qty));
    }

    @Override
    public int changeProductQty(int id, int qty) {
        // Update qty in database
        Query q = em.createNamedQuery("Product.updateQty");
        q.setParameter("id", id);
        q.setParameter("qty", qty);
        q.executeUpdate();
        return qty;
    }

    @Override
    public void deleteProduct(int id) {
        // Delete product from database
        em.createNamedQuery("Product.deleteProduct").setParameter("id", id).executeUpdate();
    }

    @Override
    public List<Product> getAll() {
        return em.createNamedQuery("Product.findAll").getResultList();
    }

    @Override
    public void persist(Object o) {
        em.persist(o);
    }
}
