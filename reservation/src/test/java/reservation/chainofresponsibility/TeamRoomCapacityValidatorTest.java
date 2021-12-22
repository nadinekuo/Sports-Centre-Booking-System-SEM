package reservation.chainofresponsibility;

import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;
import reservation.controllers.ReservationController;
import reservation.entities.Reservation;
import reservation.entities.ReservationType;
import reservation.entities.chainofresponsibility.InvalidReservationException;
import reservation.entities.chainofresponsibility.TeamRoomCapacityValidator;
import reservation.services.ReservationService;

@RunWith(MockitoJUnitRunner.class)
public class TeamRoomCapacityValidatorTest {

    private final transient Reservation groupReservation;
    private final transient Reservation reservation1;

    private final transient ReservationController reservationController;
    private final transient ReservationService reservationService;
    // Class under test:
    private final transient TeamRoomCapacityValidator teamRoomCapacityValidator;
    private final transient String sportName;

    /**
     * Constructor for this test suite.
     */
    public TeamRoomCapacityValidatorTest() {
        reservationService = mock(ReservationService.class);
        reservationController = mock(ReservationController.class);
        this.teamRoomCapacityValidator =
            new TeamRoomCapacityValidator(reservationService, reservationController);

        groupReservation = new Reservation(ReservationType.SPORTS_ROOM, 3L, 13L,
            LocalDateTime.of(2022, 02, 3, 20, 30), 84L);
        groupReservation.setId(99L);
        reservation1 = new Reservation(ReservationType.EQUIPMENT, 1L, 42L,
            LocalDateTime.of(2022, 10, 05, 16, 00));
        reservation1.setId(53L);

        sportName = "soccer";
    }

    /**
     * Test constructor.
     */
    @Test
    public void testConstructor() {
        assertNotNull(teamRoomCapacityValidator);
    }

    @Test
    public void testSportHallBelowMinCapacity() throws InvalidReservationException {

        // Sport halls hold multiple sports, so no team size check done
        when(reservationController.getIsSportHall(anyLong())).thenReturn(true);
        when(reservationController.getGroupSize(anyLong())).thenReturn(5);
        when(reservationController.getSportRoomMinimumCapacity(anyLong())).thenReturn(6);
        when(reservationController.getSportRoomMaximumCapacity(anyLong())).thenReturn(100);

        assertThrows(InvalidReservationException.class, () -> {
            teamRoomCapacityValidator.handle(groupReservation);
        });

        verify(reservationController).getIsSportHall(groupReservation.getSportFacilityReservedId());
        verify(reservationController).getGroupSize(groupReservation.getGroupId());
        verify(reservationController).getSportRoomMinimumCapacity(
            groupReservation.getSportFacilityReservedId());
        verify(reservationController).getSportRoomMaximumCapacity(
            groupReservation.getSportFacilityReservedId());
    }

    @Test
    public void testSportHallOverMaxCapacity() throws InvalidReservationException {

        // Sport halls hold multiple sports, so no team size check done
        when(reservationController.getIsSportHall(anyLong())).thenReturn(true);
        when(reservationController.getGroupSize(anyLong())).thenReturn(25);
        when(reservationController.getSportRoomMinimumCapacity(anyLong())).thenReturn(6);
        // Group size exceeds max capacity!
        when(reservationController.getSportRoomMaximumCapacity(anyLong())).thenReturn(24);

        assertThrows(InvalidReservationException.class, () -> {
            teamRoomCapacityValidator.handle(groupReservation);
        });

        verify(reservationController).getIsSportHall(groupReservation.getSportFacilityReservedId());
        verify(reservationController).getGroupSize(groupReservation.getGroupId());
        verify(reservationController).getSportRoomMinimumCapacity(
            groupReservation.getSportFacilityReservedId());
        verify(reservationController).getSportRoomMaximumCapacity(
            groupReservation.getSportFacilityReservedId());
    }

    @Test
    public void testValidIndividualReservation() {

        when(reservationController.getIsSportHall(anyLong())).thenReturn(true);
        when(reservationController.getSportRoomMinimumCapacity(anyLong())).thenReturn(1);
        when(reservationController.getSportRoomMaximumCapacity(anyLong())).thenReturn(100);

        // Individual group size is by default 1, so between min and max.
        assertDoesNotThrow(() -> {
            teamRoomCapacityValidator.handle(reservation1);
        });
    }

    @Test
    public void testSportFieldBelowMinTeamSize() throws InvalidReservationException {

        // Sport fields hold 1 team sport, so team size check done
        when(reservationController.getIsSportHall(anyLong())).thenReturn(false);
        when(reservationController.getGroupSize(anyLong())).thenReturn(5);
        when(reservationController.getSportFieldSport(anyLong())).thenReturn(sportName);
        // Group size < min team size, so invalid
        when(reservationController.getSportMinTeamSize(anyString())).thenReturn(8);
        when(reservationController.getSportMaxTeamSize(anyString())).thenReturn(15);

        assertThrows(InvalidReservationException.class, () -> {
            teamRoomCapacityValidator.handle(groupReservation);
        });

        verify(reservationController).getIsSportHall(groupReservation.getSportFacilityReservedId());
        verify(reservationController).getGroupSize(groupReservation.getGroupId());
        verify(reservationController).getSportFieldSport(
            groupReservation.getSportFacilityReservedId());
        verify(reservationController).getSportMinTeamSize(sportName);
        verify(reservationController).getSportMaxTeamSize(sportName);
    }

}
