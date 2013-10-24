/**
 * 0144266, Chris O'Brien 09005763, Shane Whelan Distributed Systems Project 2
*
 */
package admin;

import javax.ejb.Local;

@Local
public interface AdminBeanLocal {
    // Interface for administrator tasks

    public boolean checkPassword(String name, String password);

    public boolean checkAdminId(int id);

    public boolean checkAdminUsername(String username);

    public Admin getAdmin(int ID);

    public Admin getAdmin(String username);

    public void createAdmin(String name, String password);
}
