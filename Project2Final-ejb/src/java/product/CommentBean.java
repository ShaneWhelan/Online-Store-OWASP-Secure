/**
 * 0144266, Chris O'Brien 09005763, Shane Whelan Distributed Systems Project 2
*
 */
package product;

import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

@Stateless
public class CommentBean implements CommentBeanLocal {

    @PersistenceContext(unitName = "Project2Final-ejbPU")
    private EntityManager em;

    @Override
    public void createComment(int pid, String username, String comment) {
        // Create comment object and persist onto database
        Comment commentToEnter = new Comment();
        persist(commentToEnter);
        commentToEnter.setPid(pid);
        commentToEnter.setUsername(username);
        commentToEnter.setComment(comment);

    }

    @Override
    public void persist(Object o) {
        em.persist(o);
    }

    @Override
    public List<Comment> getAll() {
        return em.createNamedQuery("Comment.findAll").getResultList();
    }

    @Override
    public List<Comment> getAllProductComments(int id) {
        return em.createNamedQuery("Comment.findByPid").setParameter("pid", id).getResultList();
    }
}
