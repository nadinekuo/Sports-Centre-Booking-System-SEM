package reservation.services;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import reservation.controllers.ReservationController;
import reservation.entities.Reservation;
import reservation.entities.ReservationType;
import reservation.entities.chainofresponsibility.InvalidReservationException;
import reservation.entities.chainofresponsibility.ReservationValidator;
import reservation.entities.chainofresponsibility.SportFacilityAvailabilityValidator;
import reservation.entities.chainofresponsibility.TeamRoomCapacityValidator;
import reservation.entities.chainofresponsibility.UserReservationBalanceValidator;
import reservation.repositories.ReservationRepository;

/**
 * The type Reservation service.
 */
@Service
public class ReservationService {

    private final transient ReservationRepository reservationRepository;

    /**
     * Instantiates a new Reservation service.
     *
     * @param reservationRepository the reservation repository
     */
    @Autowired
    public ReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    /**
     * Gets reservation.
     *
     * @param reservationId the reservation id
     * @return the reservation
     */
    public Reservation getReservation(Long reservationId) {
        return reservationRepository.findById(reservationId).orElseThrow(
            () -> new IllegalStateException(
                "Reservation with id " + reservationId + " does not exist!"));
    }

    /**
     * Delete reservation.
     *
     * @param reservationId the reservation id
     */
    public void deleteReservation(Long reservationId) {
        boolean exists = reservationRepository.existsById(reservationId);
        if (!exists) {
            throw new IllegalStateException(
                "Reservation with id " + reservationId + " does not " + "exist!");
        }
        reservationRepository.deleteById(reservationId);
    }

    /**
     * Sports facility is available boolean.
     *
     * @param sportFacilityId the sport facility id
     * @param time            the time
     * @return the boolean
     */
    // All Reservations start at full hours, so only start time has to be checked.
    public boolean sportsFacilityIsAvailable(Long sportFacilityId, LocalDateTime time) {
        return reservationRepository.findBySportFacilityReservedIdAndTime(sportFacilityId, time)
            .isEmpty();
    }

    /**
     * Make sport facility reservation reservation.
     *
     * @param reservation the reservation
     * @return the reservation
     */
    public Reservation makeSportFacilityReservation(Reservation reservation) {
        return reservationRepository.save(reservation);
    }

    /**
     * Gets user reservation count on day.
     *
     * @param start      the start
     * @param end        the end
     * @param customerId the customer id
     * @return the user reservation count on day
     */
    public int getUserReservationCountOnDay(LocalDateTime start, LocalDateTime end,
                                            long customerId) {

        List<Reservation> reservationsOnDay =
            reservationRepository.findReservationByStartingTimeBetweenAndCustomerId(start, end,
                customerId);
        int count = 0;

        // Customers have a limit on the number of sport rooms to be reserved
        // Basic: 1 per day, premium: 3 per day
        for (Reservation reservation : reservationsOnDay) {
            if (reservation.getTypeOfReservation() == ReservationType.SPORTS_FACILITY) {
                count++;
            }
        }
        return count;
    }

    /**
     * Check reservation boolean.
     *
     * @param reservation           the reservation
     * @param reservationController the reservation controller
     * @return the boolean
     */
    public boolean checkReservation(Reservation reservation,
                                    ReservationController reservationController) {

        // Start chain of responsibility
        ReservationValidator userBalanceHandler =
            new UserReservationBalanceValidator(this, reservationController);
        ReservationValidator sportFacilityHandler =
            new SportFacilityAvailabilityValidator(this, reservationController);
        userBalanceHandler.setNext(sportFacilityHandler);

        // Only for sports room reservations, we check the room capacity/team size
        if (reservation.getTypeOfReservation() == ReservationType.SPORTS_FACILITY) {
            ReservationValidator capacityHandler =
                new TeamRoomCapacityValidator(this, reservationController);
            sportFacilityHandler.setNext(capacityHandler);
        }

        try {
            return userBalanceHandler.handle(reservation);
        } catch (InvalidReservationException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Find by group id and time long.
     *
     * @param groupId the group id
     * @param time    the time
     * @return the long
     */
    public Long findByGroupIdAndTime(Long groupId, LocalDateTime time) {
        return reservationRepository.findByGroupIdAndTime(groupId, time).orElse(null);
    }

    /**
     * Gets last person that used equipment.
     *
     * @param equipmentId the equipment id
     * @return the last person that used equipment
     */
    public Long getLastPersonThatUsedEquipment(Long equipmentId) {
        List<Reservation> reservations =
            reservationRepository.findReservationsBySportFacilityReservedId(equipmentId);

        reservations.sort(Comparator.comparing(Reservation::getStartingTime).reversed());
        return reservations.get(0).getCustomerId();
    }

    /**
     * Rest template rest template.
     *
     * @return the rest template
     */
    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}


