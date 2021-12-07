package reservationPackage.services;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import reservationPackage.entities.Reservation;
import reservationPackage.repositories.ReservationRepository;

@Service
public class ReservationService {

    private final transient ReservationRepository reservationRepository;

    @Autowired
    public ReservationService(ReservationRepository reservationRepository) {
        this.reservationRepository = reservationRepository;
    }

    public Reservation getReservation(long reservationId) {
        return reservationRepository.findById(reservationId);
    }

    public void deleteReservation(Long reservationId) {
        reservationRepository.deleteById(reservationId);
    }

    // All Reservations start at full hours, so only start time has to be checked.
    public boolean isAvailable(Long sportRoomId, LocalDateTime time) {
        return reservationRepository.findBySportRoomIdAndTime(sportRoomId, time).isEmpty();
    }

    public Reservation makeSportFacilityReservation(Reservation reservation) {
        return reservationRepository.save(reservation);
    }

    public int getUserReservationCountOnDay(String date, long customerId) {

        // All reservations on the same day (not time necessarily)
        List<Reservation> reservationsOnDay =
            reservationRepository.findReservationByStartingTimeContainsAndCustomerId(date, customerId);
        int count = 0;

        // Combined reservations of equipment(s) and sport room which will count as 1 reservation
        // No more than 1 combined reservation for this same time is possible, since a user cannot
        // reserve different sport rooms for the same time
        boolean combinedReservationFound = false;
        for (Reservation reservation : reservationsOnDay) {
            if (!reservation.isCombined()) {
                count++;
            } else {
                combinedReservationFound = true;
            }
        }
        if (combinedReservationFound) count++;
        return count;
    }

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}


