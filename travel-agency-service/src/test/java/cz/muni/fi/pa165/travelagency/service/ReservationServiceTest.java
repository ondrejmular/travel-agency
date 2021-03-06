package cz.muni.fi.pa165.travelagency.service;

import cz.muni.fi.pa165.travelagency.dao.ReservationDao;
import cz.muni.fi.pa165.travelagency.entity.User;
import cz.muni.fi.pa165.travelagency.entity.Excursion;
import cz.muni.fi.pa165.travelagency.entity.Reservation;
import cz.muni.fi.pa165.travelagency.entity.Trip;
import cz.muni.fi.pa165.travelagency.exceptions.TravelAgencyServiceException;
import cz.muni.fi.pa165.travelagency.service.config.ServiceConfiguration;
import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.hibernate.service.spi.ServiceException;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.testng.AbstractTransactionalTestNGSpringContextTests;
import static org.testng.Assert.*;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 *
 * @author Jan Duda
 */
@ContextConfiguration(classes = ServiceConfiguration.class)
public class ReservationServiceTest extends AbstractTransactionalTestNGSpringContextTests {
    
    @Mock
    private ReservationDao reservationDao;

    @Mock
    private TripService tripService;
    
    @Autowired
    @InjectMocks
    private ReservationService reservationService;
    
    @BeforeClass
    public void setUp() throws ServiceException {
        MockitoAnnotations.initMocks(this);
    }
    
    private Reservation testReservation;
    
    @BeforeMethod
    public void prepareTestReservation(){
        
        User user = createUser(0);
        Trip trip = createTrip(0);
        Excursion excursion = createExcursion(0);
        
        testReservation = new Reservation();
        testReservation.setTrip(trip);
        testReservation.setUser(user);
        testReservation.addExcursion(excursion);
    }
    
    @Test
    public void testCreateReservation(){
        when(
                tripService.getNumberOfAvailableTripsLeft(testReservation.getTrip())
        ).thenReturn(1l);
        reservationService.createReservation(testReservation);
        verify(reservationDao).create(testReservation);
    }

    @Test(expectedExceptions = TravelAgencyServiceException.class)
    public void testCreateReservationFull(){
        when(
                tripService.getNumberOfAvailableTripsLeft(testReservation.getTrip())
        ).thenReturn(0l);
        reservationService.createReservation(testReservation);
    }
    
    @Test
    public void testUpdateReservation(){
        reservationService.updateReservation(testReservation);
        verify(reservationDao).update(testReservation);
    }
    
    @Test
    public void testRemoveReservation(){
        reservationService.removeReservation(testReservation);
        verify(reservationDao).remove(testReservation);
    }
    
    @Test
    public void testFindAll(){
        when(reservationDao.findAll()).thenReturn(new ArrayList<>());
        assertEquals(reservationService.findAll().size(), 0);
        when(reservationDao.findAll()).thenReturn(Collections.singletonList(testReservation));
        assertEquals(reservationService.findAll().size(), 1);
        
        Reservation r = new Reservation();
        r.setUser(createUser(1));
        r.setTrip(createTrip(1));
        r.addExcursion(createExcursion(1));
        
        when(reservationDao.findAll()).thenReturn(Arrays.asList(testReservation, r));
        assertEquals(reservationService.findAll().size(), 2);
    }
    
    @Test
    public void testFindById(){
        testReservation.setId(1l);
        when(reservationDao.findById(testReservation.getId())).thenReturn(testReservation);
        assertDeepEquals(reservationService.findById(testReservation.getId()), testReservation);
    }
    
    @Test
    public void testFindByIdWhichDoesntExist(){
        when(reservationDao.findById(Long.MIN_VALUE)).thenReturn(null);
        assertNull(reservationService.findById(Long.MIN_VALUE));
    }
    
    @Test
    public void testFindByUser(){ 
        User u = testReservation.getUser();
        when(reservationDao.findByUser(u)).thenReturn(Collections.singletonList(testReservation));
        List<Reservation> l = reservationService.findByUser(u);
        assertEquals(l.size(), 1);
        assertDeepEquals(l.get(0), testReservation);
    }
    
    @Test
    public void testFindByUserWhoDoesntExist(){
        User u = createUser(2);
        when(reservationDao.findByUser(u)).thenReturn(new ArrayList<>());
        assertEquals(reservationService.findByUser(u).size(), 0);
    }
    
    @Test
    public void testFindByTrip(){
        Trip t = testReservation.getTrip();
        when(reservationDao.findByTrip(t)).thenReturn(Collections.singletonList(testReservation));
        List<Reservation> l = reservationService.findByTrip(t);
        assertEquals(l.size(), 1);
        assertDeepEquals(l.get(0), testReservation);
    }
    
    @Test
    public void testFindByTripWhichDoesntExist(){
        Trip t = createTrip(2);
        when(reservationDao.findByTrip(t)).thenReturn(new ArrayList<>());
        assertEquals(reservationService.findByTrip(t).size(), 0);
    }

    @Test
    public void testGetTotalPriceNoExcursions() {
        Reservation r = new Reservation(1l);
        Trip t = createTrip(0);
        t.setPrice(new BigDecimal("5000.00"));
        r.setTrip(t);
        when(reservationDao.findById(1l)).thenReturn(r);
        assertEquals(reservationService.getTotalPrice(r), new BigDecimal("5000.00"));
    }

    public void testGetTotalPriceMultipleExcursions() {
        Reservation r = new Reservation(1l);
        Trip t = createTrip(0);
        t.setPrice(new BigDecimal("5000.00"));
        r.setTrip(t);
        Excursion e = createExcursion(0);
        e.setPrice(new BigDecimal("1500.00"));
        r.addExcursion(e);
        e = createExcursion(1);
        e.setPrice(new BigDecimal("999.99"));
        r.addExcursion(e);
        e = createExcursion(2);
        e.setPrice(new BigDecimal("0.00"));
        r.addExcursion(e);
        when(reservationDao.findById(1l)).thenReturn(r);
        assertEquals(reservationService.getTotalPrice(r), new BigDecimal("7499.99"));
    }
    
    private void assertDeepEquals(Reservation r1, Reservation r2){
        assertEquals(r1, r2);
        assertEquals(r1.getId(), r2.getId());
        assertTrue(r1.getUser().equals(r2.getUser()));
        assertTrue(r1.getTrip().equals(r2.getTrip()));
        assertTrue(r1.getExcursions().equals(r2.getExcursions()));
    }
    
    private Trip createTrip(int numberTrip){
        Trip trip = new Trip();
        trip.setName("Trip " + numberTrip);
        trip.setDateFrom(Date.valueOf(LocalDate.of(2015, 1, 2 + numberTrip)));
        trip.setDateTo(Date.valueOf(LocalDate.of(2015, 1, 2 + numberTrip).plusDays(1)));
        trip.setDestination("Dest " + numberTrip);
        trip.setPrice(new BigDecimal("200" + numberTrip + ".0"));
        trip.setAvailableTrips(new Long(100));
        
        return trip;
    }
    
    private User createUser(int numberUser){
        User user = new User();
        user.setEmail("test" + numberUser + "@mail.com");
        user.setName("User Name " + numberUser);
        user.setPhoneNumber("123456789" + numberUser);
        return user;
    }
    
    private Excursion createExcursion(int numberExcursion){
        Excursion excursion = new Excursion();
        excursion.setName("Excursion " + numberExcursion);
        excursion.setDescription("Description " + numberExcursion);
        excursion.setDateFrom(Date.valueOf(LocalDate.of(2015, 1, 1 + numberExcursion)));
        excursion.setDateTo(Date.valueOf(LocalDate.of(2015, 1, 1 + numberExcursion).plusDays(1)));
        excursion.setDestination("Germany " + numberExcursion);
        excursion.setPrice(new BigDecimal("100" + numberExcursion + ".0"));
        return excursion;
    }
}
