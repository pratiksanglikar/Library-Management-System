package edu.cmpe275.team13.persistence;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import edu.cmpe275.team13.beans.Librarian;
import edu.cmpe275.team13.exceptions.LibrarianNotFoundException;

@Repository
public class LibrarianDAOImpl {
    
    @Transactional
    public void createLibrarian(Librarian librarian) {
        EntityManager em = EMF.get().createEntityManager();
        em.getTransaction().begin();
        em.persist(librarian);
        em.getTransaction().commit();
    }

    @Transactional
    public boolean isLibrarianPresent(Librarian librarian) {
        EntityManager em = EMF.get().createEntityManager();
        @SuppressWarnings("unchecked")
		List<Librarian> librarians = em.createQuery("SELECT e FROM Librarian e").getResultList();
        for (Librarian o : librarians) {
            if (o.getLibrarian_email().equalsIgnoreCase(librarian.getLibrarian_email())
                    || o.getLibrarian_id() == librarian.getLibrarian_id())
                return true;
        }
        return false;
    }

    @Transactional
    public Librarian getLibrarian(int librarian_id) {
        EntityManager em = EMF.get().createEntityManager();
        Librarian librarian = em.find(Librarian.class, librarian_id);
        if (librarian == null) {
            throw new LibrarianNotFoundException();
        }
        return librarian;
    }

    @Transactional
    public void updateLibrarian(Librarian librarian) {
        EntityManager em = EMF.get().createEntityManager();
        em.getTransaction().begin();
        em.merge(librarian);
        em.getTransaction().commit();
    }

	@Transactional
	public Librarian validateLibrarian(String username, String password) {
		EntityManager em = EMF.get().createEntityManager();

		Query query = em.createQuery(
				"SELECT e FROM Librarian e where e.librarian_email = :email AND e.librarian_password = :password");
		query.setParameter("email", username);
		query.setParameter("password", password);

		try {
			Librarian librarian = (Librarian) query.getSingleResult();
			if (null != librarian) {
				if (username.equalsIgnoreCase(librarian.getLibrarian_email())
						&& password.equals(librarian.getLibrarian_password())) {
					return librarian;
				}
			} else {
				return null;
			}

		} catch (Exception e) {
			// TODO: handle exception
			System.out.println(e);
		}
		return null;

	}
}
