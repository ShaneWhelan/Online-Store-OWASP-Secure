/**
 * 0144266, Chris O'Brien 09005763, Shane Whelan Distributed Systems Project 2
*
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package product;

import java.util.List;
import javax.ejb.Local;

/**
 *
 * @author Shane
 */
@Local
public interface ProductBeanLocal {
    // Interface for product operations

    public List<Product> getProductName(String name);

    public Product getProduct(int ID);

    public Product getProduct(String name);

    public List<Product> getAll();

    public void createProduct(String name, String description, String qty);

    public int changeProductQty(int id, int qty);

    public void deleteProduct(int id);

    public void persist(Object o);
}
