package reservation.entities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.web.client.RestTemplate;
import reservation.controllers.ReservationController;
import reservation.services.ReservationService;

public class BookingSystemTest {

    /**
     * The Reservation service.
     */
    @Mock
    transient ReservationService reservationService;
    /**
     * The Rest template.
     */
    @Mock
    transient RestTemplate restTemplate;

    ReservationSortingStrategy sortingStrategy = new ChronologicalStrategy();
    List<Reservation> userIdStrategy = new ArrayList<>();
    BookingSystem bookingSystem = new BookingSystem(new ChronologicalStrategy());

    Reservation reservation1 = new Reservation(ReservationType.EQUIPMENT, 10L, 1L,
        LocalDateTime.of(2020, 1, 1, 1, 1), false);
    Reservation reservation2 = new Reservation(ReservationType.EQUIPMENT, 3L, 2L,
        LocalDateTime.of(2020, 1, 1, 1, 1), false);
    Reservation reservation3 = new Reservation(ReservationType.EQUIPMENT, 5L, 3L,
        LocalDateTime.of(2020, 4, 1, 1, 1), false);
    Reservation reservation4 = new Reservation(ReservationType.EQUIPMENT, 20L, 4L,
        LocalDateTime.of(2020, 3, 1, 1, 1), false);
    Reservation reservation5 = new Reservation(ReservationType.EQUIPMENT, 4L, 5L,
        LocalDateTime.of(2020, 5, 1, 1, 1), false);
    Reservation reservation6 = new Reservation(ReservationType.EQUIPMENT, 5L, 6L,
        LocalDateTime.of(2020, 6, 1, 1, 1), false);

    @BeforeEach
    void setup() {
        restTemplate = Mockito.mock(RestTemplate.class);
        //Mockito.when(ReservationService.restTemplate()).thenReturn(restTemplate);
        reservation1.setReservationId(1L);
        reservation2.setReservationId(2L);
        reservation3.setReservationId(3L);
        reservation4.setReservationId(4L);
        reservation5.setReservationId(5L);
        reservation6.setReservationId(6L);
        userIdStrategy.add(reservation1);
        userIdStrategy.add(reservation2);
        userIdStrategy.add(reservation3);
        userIdStrategy.add(reservation4);
        userIdStrategy.add(reservation5);
        userIdStrategy.add(reservation6);
    }

    @Test
    void constructorTest() {
        assertNotNull(bookingSystem);
    }

    @Test
    void addReservation() {
        bookingSystem.addReservation(reservation1);
        assertEquals(reservation1, bookingSystem.getNextReservation());
    }

    @Test
    void getNextReservationChronologically() {
        BookingSystem chronologicalStrategy = new BookingSystem(new ChronologicalStrategy());
        chronologicalStrategy.addReservation(reservation1);
        chronologicalStrategy.addReservation(reservation2);
        chronologicalStrategy.addReservation(reservation3);
        chronologicalStrategy.addReservation(reservation4);
        chronologicalStrategy.addReservation(reservation5);
        chronologicalStrategy.addReservation(reservation6);

        assertEquals(reservation6, chronologicalStrategy.getNextReservation());
    }

    @Test
    void getNextReservationBasicPremium() {
        BookingSystem userPremiumStrategy =
            new BookingSystem(new BasicPremiumUserStrategy(restTemplate));

        Mockito.when(restTemplate.getForObject(
            ReservationController.userUrl + "/user/" + reservation1.getCustomerId()
                + "/isPremium", Boolean.class)).thenReturn(false);
        Mockito.when(restTemplate.getForObject(
            ReservationController.userUrl + "/user/" + reservation2.getCustomerId()
                + "/isPremium", Boolean.class)).thenReturn(false);
        Mockito.when(restTemplate.getForObject(
            ReservationController.userUrl + "/user/" + reservation3.getCustomerId()
                + "/isPremium", Boolean.class)).thenReturn(false);
        Mockito.when(restTemplate.getForObject(
            ReservationController.userUrl + "/user/" + reservation4.getCustomerId()
                + "/isPremium", Boolean.class)).thenReturn(true);
        Mockito.when(restTemplate.getForObject(
            ReservationController.userUrl + "/user/" + reservation5.getCustomerId()
                + "/isPremium", Boolean.class)).thenReturn(true);
        Mockito.when(restTemplate.getForObject(
            ReservationController.userUrl + "/user/" + reservation6.getCustomerId()
                + "/isPremium", Boolean.class)).thenReturn(true);

        userPremiumStrategy.addReservation(reservation1);
        userPremiumStrategy.addReservation(reservation2);
        userPremiumStrategy.addReservation(reservation3);
        userPremiumStrategy.addReservation(reservation4);
        userPremiumStrategy.addReservation(reservation5);
        userPremiumStrategy.addReservation(reservation6);

        Reservation first = userPremiumStrategy.getNextReservation();
        assertTrue(reservation4.equals(first));

    }

    @Test
    void getNextReservationEquipmentName() {
        BookingSystem equipmentNameStrategy =
            new BookingSystem(new EquipmentNameStrategy(ReservationService.restTemplate()));


        equipmentNameStrategy.addReservation(reservation1);
        equipmentNameStrategy.addReservation(reservation2);
        equipmentNameStrategy.addReservation(reservation3);
        equipmentNameStrategy.addReservation(reservation4);
        equipmentNameStrategy.addReservation(reservation5);
        equipmentNameStrategy.addReservation(reservation6);
        assertEquals(userIdStrategy.get(2), equipmentNameStrategy.getNextReservation());
    }

    @Test
    void getNextReservationUserId() {
        BookingSystem userIdStrategy = new BookingSystem(new UserIdStrategy());
        userIdStrategy.addReservation(reservation1);
        userIdStrategy.addReservation(reservation2);
        userIdStrategy.addReservation(reservation3);
        userIdStrategy.addReservation(reservation4);
        assertEquals(reservation2, userIdStrategy.getNextReservation());
    }

    @Test
    void getNextReservationUserIdNull() {
        BookingSystem userIdStrategy = new BookingSystem(new UserIdStrategy());
        assertNull(userIdStrategy.getNextReservation());
    }

    @Test
    void toStringTest() {
        bookingSystem.addReservation(reservation1);
        bookingSystem.addReservation(reservation2);
        bookingSystem.addReservation(reservation3);
        assertEquals("BookingSystem{bookings=[" + reservation1.toString() + ", "
            + reservation2.toString() + ", " + reservation3.toString() + "]}",
            bookingSystem.toString());
    }
}
