package cz.muni.fi.pa165.travelagency.service;

import cz.muni.fi.pa165.travelagency.entity.User;
import cz.muni.fi.pa165.travelagency.entity.Excursion;
import cz.muni.fi.pa165.travelagency.entity.Reservation;
import cz.muni.fi.pa165.travelagency.entity.Trip;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Service;

/**
 * 
 * @author Jan Duda
 */
@Service
public interface ReservationService {
    
    /**
     * Method adds new a reservation into database
     * 
     * @param r reservation to add
     * @return created reservation
     */
    Reservation createReservation(Reservation r);
    
    /**
     * Method updates a reservation r in database
     * 
     * @param r reservation to update
     * @return updated reservation
     */
    Reservation updateReservation(Reservation r);
    
    /**
     * Method removes a reservation r from database
     * 
     * @param r 
     */
    void removeReservation(Reservation r);
    
    /**
     * Method adds input excursion e to reservation r
     * 
     * @param r excursion will be added to this reservation
     * @param e excursion to add
     */
    void addExcursionToReservation(Reservation r, Excursion e);
    
    /**
     * Method returns all reservations from database
     * 
     * @return all reservations in database
     */
    List<Reservation> findAll();
    
    
    /**
     * Finds reservation by specified id
     *
     * @param id id of reservation to find
     * @return Reservation with specified id. Null if reservation with specified
     * id doesn't exist.
     */
    Reservation findById(Long id);

    /**
     * Finds reservations of specified user
     *
     * @param u user
     * @return list of user's reservations
     */
    List<Reservation> findByUser(User u);
    
    /**
     * Method returns all reservations with input trip
     * 
     * @param t all reservations will be returned with this trip
     * @return all reservations with input trip
     */
    List<Reservation> findByTrip(Trip t);

    /**
     * Method returns complete price of specified reservation (sum of trip price
     * and all excursions).
     *
     * @param r reservation to get it's total price
     * @return total price of reservation
     */
    BigDecimal getTotalPrice(Reservation r);
}
