package cz.muni.fi.pa165.travelagency.dao;

import cz.muni.fi.pa165.travelagency.entity.Trip;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 *
 * @author Ondrej Mular
 */
@Repository
@Transactional
public class TripDaoImpl implements TripDao {

    @PersistenceContext
    EntityManager em;
    
    @Override
    public void create(Trip t) {
        em.persist(t);
    }

    @Override
    public Trip update(Trip t) {
        return em.merge(t);
    }

    @Override
    public void remove(Trip t) throws IllegalArgumentException {
        em.remove(findById(t.getId()));
    }

    @Override
    public List<Trip> findAll() {
        return em.createQuery("SELECT t FROM Trip t", Trip.class)
                .getResultList();
    }

    @Override
    public Trip findById(Long id) {
        return em.find(Trip.class, id);
    }

    @Override
    public List<Trip> findByNameSubstring(String name) {
        return em.createQuery(
                "SELECT t FROM Trip t WHERE t.name LIKE :name ", Trip.class
        ).setParameter("name", "%" + name + "%").getResultList();
    }

    @Override
    public List<Trip> findByDestination(String destination) {
        return em.createQuery(
                "SELECT t FROM Trip t WHERE t.destination LIKE :dest ",
                Trip.class
        ).setParameter("dest", "%" + destination + "%").getResultList();
    }

    @Override
    public Trip findByName(String name) {
        try {
            return em.createQuery(
                "SELECT t FROM Trip t WHERE t.name = :name ", Trip.class
            ).setParameter("name", name).getSingleResult();
        } catch(NoResultException ex) {
            return null;
        }
    }
}
