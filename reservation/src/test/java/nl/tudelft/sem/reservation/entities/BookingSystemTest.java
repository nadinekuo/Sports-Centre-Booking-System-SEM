package nl.tudelft.sem.reservation.entities;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import nl.tudelft.sem.reservation.entities.strategy.BasicPremiumUserStrategy;
import nl.tudelft.sem.reservation.entities.strategy.BookingSystem;
import nl.tudelft.sem.reservation.entities.strategy.ChronologicalStrategy;
import nl.tudelft.sem.reservation.entities.strategy.EquipmentNameStrategy;
import nl.tudelft.sem.reservation.entities.strategy.UserIdStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class BookingSystemTest {

    transient List<Reservation> userIdStrategy = new ArrayList<>();
    transient BookingSystem bookingSystem = new BookingSystem(new ChronologicalStrategy());
    transient Reservation[] reservations;

    @BeforeEach
    void setup() {
        int size = 6;
        reservations = new Reservation[size];

        String[] titles = {"Tango", "Krav Maga", "Ziou Zitsou", "Krav Maga", "Box", "Yoga"};

        for (int i = 0; i < size; i++) {
            Reservation reservation =
                new Reservation(ReservationType.EQUIPMENT, titles[i], (long) i, (long) i,
                    LocalDateTime.of(2020, i + 1, 1, 1, 1), false);
            reservation.setReservationId((long) i + 1);
            userIdStrategy.add(reservation);
            reservations[i] = reservation;
        }
    }

    @Test
    void constructorTest() {
        assertNotNull(bookingSystem);
    }

    @Test
    void addReservationTest() {
        bookingSystem.addReservation(reservations[0]);
        assertEquals(reservations[0], bookingSystem.getNextReservation());
    }

    @Test
    void getNextReservationChronologicallyTest() {
        BookingSystem chronologicalStrategy = new BookingSystem(new ChronologicalStrategy());
        reservations[1].setStartingTime(reservations[0].getStartingTime());
        reservations[2].setStartingTime(LocalDateTime.of(2020, 1, 1, 0, 1));
        for (Reservation r : reservations) {
            chronologicalStrategy.addReservation(r);
        }

        assertEquals(reservations[5], chronologicalStrategy.getNextReservation());
    }

    @Test
    void getNextReservationChronologicallyEmptyTest() {
        BookingSystem chronologicalStrategy = new BookingSystem(new ChronologicalStrategy());

        assertNull(chronologicalStrategy.getNextReservation());
    }

    @Test
    void getNextReservationBasicPremiumTest() {
        BookingSystem userPremiumStrategy = new BookingSystem(new BasicPremiumUserStrategy());

        for (int i = 0; i < 5; i++) {
            userPremiumStrategy.addReservation(reservations[i]);

            //only first and second user is premium
            if (i == 1 || i == 2) {
                reservations[i].setMadeByPremiumUser(true);
            }
        }

        assertEquals(reservations[1], userPremiumStrategy.getNextReservation());
    }

    @Test
    void getNextReservationBasicPremiumEmptyTest() {
        BookingSystem userPremiumStrategy = new BookingSystem(new BasicPremiumUserStrategy());

        assertNull(userPremiumStrategy.getNextReservation());
    }

    @Test
    void getNextReservationEquipmentNameTest() {
        BookingSystem equipmentNameStrategy = new BookingSystem(new EquipmentNameStrategy());

        for (int i = 0; i < 6; i++) {
            equipmentNameStrategy.addReservation(reservations[i]);
        }

        assertEquals(reservations[4], equipmentNameStrategy.getNextReservation());
    }

    @Test
    void getNextReservationEquipmentNameEmptyTest() {
        BookingSystem equipmentNameStrategy = new BookingSystem(new EquipmentNameStrategy());

        assertNull(equipmentNameStrategy.getNextReservation());
    }

    @Test
    void getNextReservationEquipmentNameNullTest() {
        BookingSystem equipmentNameStrategy = new BookingSystem(new EquipmentNameStrategy());
        equipmentNameStrategy.addReservation(null);
        assertNull(equipmentNameStrategy.getNextReservation());
    }

    @Test
    void getNextReservationEquipmentNameWithDifferentObjectsTest() {
        BookingSystem equipmentNameStrategy = new BookingSystem(new EquipmentNameStrategy());

        for (int i = 0; i < 6; i++) {
            equipmentNameStrategy.addReservation(reservations[i]);
        }
        reservations[0].setTypeOfReservation(ReservationType.LESSON);
        reservations[2].setTypeOfReservation(ReservationType.LESSON);
        reservations[3].setTypeOfReservation(ReservationType.LESSON);
        reservations[5].setTypeOfReservation(ReservationType.LESSON);

        assertEquals(reservations[4], equipmentNameStrategy.getNextReservation());
    }

    @Test
    void getNextReservationEquipmentNameWithDifferentObjects2Test() {
        BookingSystem equipmentNameStrategy = new BookingSystem(new EquipmentNameStrategy());

        for (int i = 0; i < 4; i++) {
            equipmentNameStrategy.addReservation(reservations[i]);
        }
        reservations[0].setTypeOfReservation(ReservationType.LESSON);
        reservations[1].setTypeOfReservation(ReservationType.EQUIPMENT);
        reservations[2].setTypeOfReservation(ReservationType.EQUIPMENT);
        reservations[3].setTypeOfReservation(ReservationType.LESSON);

        assertEquals(reservations[1], equipmentNameStrategy.getNextReservation());
    }

    @Test
    void getNextReservationEquipmentNameWithDifferentObjects3Test() {
        BookingSystem equipmentNameStrategy = new BookingSystem(new EquipmentNameStrategy());

        for (int i = 0; i < 2; i++) {
            equipmentNameStrategy.addReservation(reservations[i]);
        }

        reservations[1].setTypeOfReservation(ReservationType.LESSON);

        assertEquals(reservations[0], equipmentNameStrategy.getNextReservation());
    }

    @Test
    void getNextReservationEquipmentNameWithDifferentObjects4Test() {
        BookingSystem equipmentNameStrategy = new BookingSystem(new EquipmentNameStrategy());

        for (int i = 0; i < 6; i++) {
            equipmentNameStrategy.addReservation(reservations[i]);
        }

        reservations[0].setTypeOfReservation(ReservationType.LESSON);
        reservations[1].setTypeOfReservation(ReservationType.LESSON);
        reservations[3].setTypeOfReservation(ReservationType.LESSON);
        reservations[4].setTypeOfReservation(ReservationType.LESSON);
        assertEquals(reservations[5], equipmentNameStrategy.getNextReservation());
    }

    @Test
    void getNextReservationEquipmentNameWithDifferentObjects5Test() {
        BookingSystem equipmentNameStrategy = new BookingSystem(new EquipmentNameStrategy());

        for (int i = 0; i < 4; i++) {
            equipmentNameStrategy.addReservation(reservations[i]);
        }

        reservations[0].setTypeOfReservation(ReservationType.LESSON);
        reservations[2].setTypeOfReservation(ReservationType.LESSON);

        reservations[1].setStartingTime(LocalDateTime.of(2020, 10 + 1, 1, 1, 1));
        assertEquals(reservations[3], equipmentNameStrategy.getNextReservation());
    }

    @Test
    void getNextReservationUserIdTest() {
        BookingSystem userIdStrategy = new BookingSystem(new UserIdStrategy());
        for (int i = 0; i < 4; i++) {
            userIdStrategy.addReservation(reservations[i]);
        }

        assertEquals(reservations[0], userIdStrategy.getNextReservation());
    }

    @Test
    void getNextReservationUserId2Test() {
        BookingSystem userIdStrategy = new BookingSystem(new UserIdStrategy());
        for (int i = 0; i < 4; i++) {
            userIdStrategy.addReservation(reservations[i]);
        }
        reservations[0].setCustomerId(2L);

        assertEquals(reservations[1], userIdStrategy.getNextReservation());
    }

    @Test
    void getNextReservationUserIdEmptyTest() {
        BookingSystem userIdStrategy = new BookingSystem(new UserIdStrategy());
        assertNull(userIdStrategy.getNextReservation());
    }

    @Test
    void toStringTest() {
        for (int i = 0; i < 3; i++) {
            bookingSystem.addReservation(reservations[i]);

        }
        assertEquals(
            "BookingSystem{bookings=[" + reservations[0].toString() + ", " + reservations[1]
                .toString() + ", " + reservations[2].toString() + "]}", bookingSystem.toString());
    }
}
