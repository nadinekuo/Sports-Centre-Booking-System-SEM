package nl.tudelft.sem.reservation.chainofresponsibility;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import nl.tudelft.sem.reservation.controllers.ReservationController;
import nl.tudelft.sem.reservation.controllers.SportFacilityCommunicator;
import nl.tudelft.sem.reservation.entities.Reservation;
import nl.tudelft.sem.reservation.entities.ReservationType;
import nl.tudelft.sem.reservation.entities.chainofresponsibility.InvalidReservationException;
import nl.tudelft.sem.reservation.entities.chainofresponsibility.SportFacilityAvailabilityValidator;
import nl.tudelft.sem.reservation.services.ReservationService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SportFacilityAvailabilityValidatorTest {

    private final transient Reservation reservationInvalidTime;

    private final transient Reservation equipmentReservation;
    private final transient Reservation equipmentReservationInvalid;

    private final transient Reservation sportRoomReservation;
    private final transient Reservation groupReservation;

    private final transient ReservationController reservationController;
    private final transient ReservationService reservationService;

    private final transient SportFacilityCommunicator sportFacilityCommunicator;
    // Class under test:
    private final transient SportFacilityAvailabilityValidator sportFacilityAvailabilityValidator;
    private final transient boolean madeByPremiumUser = true;
    private transient Long groupId;

    /**
     * Constructor for this test suite.
     */
    public SportFacilityAvailabilityValidatorTest() {
        reservationService = mock(ReservationService.class);
        reservationController = mock(ReservationController.class);
        sportFacilityCommunicator = mock(SportFacilityCommunicator.class);
        when(reservationController.getSportFacilityCommunicator()).thenReturn(
            sportFacilityCommunicator);
        this.sportFacilityAvailabilityValidator =
            new SportFacilityAvailabilityValidator(reservationService, reservationController);

        reservationInvalidTime = new Reservation(ReservationType.EQUIPMENT, "hockey", 1L, 42L,
            LocalDateTime.of(2022, 10, 05, 15, 59), madeByPremiumUser);
        reservationInvalidTime.setId(53L);

        equipmentReservation = new Reservation(ReservationType.EQUIPMENT, "hockey", 1L, 42L,
            LocalDateTime.of(2022, 10, 05, 15, 59), madeByPremiumUser);
        equipmentReservation.setId(54L);
        equipmentReservationInvalid = new Reservation(ReservationType.EQUIPMENT, "hockey", 1L, -1L,
            LocalDateTime.of(2022, 10, 05, 15, 59), madeByPremiumUser);
        equipmentReservationInvalid.setId(55L);

        sportRoomReservation = new Reservation(ReservationType.SPORTS_ROOM, "hockey", 2L, 25L,
            LocalDateTime.of(2022, 10, 05, 17, 45), madeByPremiumUser);
        sportRoomReservation.setId(84L);

        groupReservation = new Reservation(ReservationType.SPORTS_ROOM, "hockey", 3L, 13L,
            LocalDateTime.of(2022, 02, 3, 20, 30), 84L, madeByPremiumUser);
        groupReservation.setId(99L);
    }

    /**
     * Test constructor.
     */
    @Test
    public void constructorTest() {
        assertNotNull(sportFacilityAvailabilityValidator);
    }

    @Test
    public void invalidTimeTest() throws InvalidReservationException {

        assertThrows(InvalidReservationException.class, () -> {
            sportFacilityAvailabilityValidator.handle(reservationInvalidTime);
        });
    }

    @Test
    public void invalidEquipmentIdTest() throws InvalidReservationException {

        assertThrows(InvalidReservationException.class, () -> {
            sportFacilityAvailabilityValidator.handle(equipmentReservationInvalid);
        });
    }

    @Test
    public void unavailableSportRoomTest() throws InvalidReservationException {

        when(reservationService.sportsFacilityIsAvailable(anyLong(), any())).thenReturn(false);

        assertThrows(InvalidReservationException.class, () -> {
            sportFacilityAvailabilityValidator.handle(sportRoomReservation);
        });
        verify(reservationService).sportsFacilityIsAvailable(
            sportRoomReservation.getSportFacilityReservedId(),
            sportRoomReservation.getStartingTime());
    }

    @Test
    public void nonExistentSportRoomTest() throws InvalidReservationException {

        // Room is "available" (no reservation for that room yet), but does not exist!
        when(reservationService.sportsFacilityIsAvailable(anyLong(), any())).thenReturn(true);
        when(sportFacilityCommunicator.getSportsRoomExists(anyLong())).thenReturn(false);

        assertThrows(InvalidReservationException.class, () -> {
            sportFacilityAvailabilityValidator.handle(sportRoomReservation);
        });
        verify(reservationService).sportsFacilityIsAvailable(
            sportRoomReservation.getSportFacilityReservedId(),
            sportRoomReservation.getStartingTime());
        verify(sportFacilityCommunicator).getSportsRoomExists(
            sportRoomReservation.getSportFacilityReservedId());
    }

    @Test
    public void validGroupReservationTest() throws InvalidReservationException {

        // Assuming reservation with id 5L is another reservation by another group member,
        // for the same room
        when(reservationService.findByGroupIdAndTime(anyLong(), any())).thenReturn(5L);
        when(sportFacilityCommunicator.getSportsRoomExists(anyLong())).thenReturn(true);

        assertDoesNotThrow(() -> {
            sportFacilityAvailabilityValidator.handle(groupReservation);
        });

        verify(reservationService).findByGroupIdAndTime(groupReservation.getGroupId(),
            groupReservation.getStartingTime());
        verify(sportFacilityCommunicator).getSportsRoomExists(
            groupReservation.getSportFacilityReservedId());
    }

}
