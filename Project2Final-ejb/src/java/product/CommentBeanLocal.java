/**
 * 0144266, Chris O'Brien 09005763, Shane Whelan Distributed Systems Project 2
*
 */
package product;

import java.util.List;
import javax.ejb.Local;

@Local
public interface CommentBeanLocal {
    // Interface for comment system

    public void createComment(int pid, String username, String comment);

    public void persist(Object o);

    public List<Comment> getAll();

    public List<Comment> getAllProductComments(int id);
}
