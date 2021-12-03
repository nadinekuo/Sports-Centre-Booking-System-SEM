package reservationPackage.controllers;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import java.time.LocalDateTime;
import javax.ws.rs.core.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import reservationPackage.entities.Reservation;
import reservationPackage.entities.ReservationType;
import reservationPackage.services.ReservationService;

@RestController
@RequestMapping("reservation")
public class ReservationController {

    protected static final Gson gson = new Gson();
    private static final String sportFacilityUrl = "http://eureka-sport-facilities";
    private static final String userUrl = "http://eureka-user";

    private final transient ReservationService reservationService;

    @Autowired
    private final RestTemplate restTemplate;

    /**
     * Autowired constructor for the class.
     *
     * @param reservationService sportRoomService
     */
    @Autowired
    public ReservationController(ReservationService reservationService) {
        this.reservationService = reservationService;
        this.restTemplate = reservationService.restTemplate();
    }

    //To see how eureka works
    @GetMapping("/hey")
    @ResponseBody
    public Long getReservation() {
        return reservationService.getReservation(1L).getCustomerId();
    }

    @GetMapping("/{reservationId}")
    @ResponseBody
    public Reservation getReservation(@PathVariable Long reservationId) {
        return reservationService.getReservation(reservationId);
    }

    //    @GetMapping("/{reservationId}/getStartingTime")
    //    @ResponseBody
    //    public LocalDate getReservationSportRoom(@PathVariable Long reservationId) {
    //        return reservationService.getStartingTime(reservationId);
    //    }

    @DeleteMapping("/{reservationId}")
    @ResponseBody
    public void deleteReservation(@PathVariable Long reservationId) {
        reservationService.deleteReservation(reservationId);
    }

    @GetMapping("/{sportRoomId}/{date}/isAvailable")
    @ResponseBody
    public boolean isAvailable(@PathVariable Long sportRoomId, @PathVariable String date) {
        return reservationService.isAvailable(sportRoomId, LocalDateTime.parse(date));
    }


    // TODO: the 2 methods below have to be combined into 1 Reservation method, which allows
    //  combining Equipment and SportRooms

    @PostMapping("/{userId}/{sportRoomId}/{date}/makeSportRoomBooking")
    @ResponseBody
    //@ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Parameters input for the request "
    //    + "were wrong")
    public ResponseEntity<String> makeSportRoomReservation(@PathVariable Long userId,
                                                           @PathVariable Long sportRoomId,
                                                           @PathVariable String date) {

        // TODO: check if userId exists

        //can throw errors
        LocalDateTime dateTime = LocalDateTime.parse(date);

        if (dateTime.isBefore(LocalDateTime.now())) {
            return new ResponseEntity<>("Date and time has to be after now",
                HttpStatus.BAD_REQUEST);
        }

        if ((dateTime.getHour() < 16) || (dateTime.getHour() == 23)) {
            return new ResponseEntity<>("Time has to be between 16:00 and 23:00.",
                HttpStatus.NOT_FOUND);
        }

        // TODO: To be moved to another method (Chain of Responsibility) and should be applied to
        //  whole Reservation (equipment and sportRoom may be combined in 1 reservation)

        String yearMonthDay = date.substring(0, 9);
        int reservationBalanceOnDate = getReservationBalance(userId, yearMonthDay);

        // Basic users can have 1 reservation per day (Equipment and SportRoom are separated!)
        if (!getUserIsPremium(userId) && reservationBalanceOnDate == 1) {
            return new ResponseEntity<>("No more than 1 reservation per day can be made. "
                + "Please try another date, or get a premium subscription to be able to make up "
                + "to 3 reservations per day.",
                HttpStatus.BAD_REQUEST);
        }

        // Premium users can have up to 3 reservations per day
        if (getUserIsPremium(userId) && reservationBalanceOnDate == 3) {
            return new ResponseEntity<>("No more than 3 reservations per day can be made. "
                + "Please try another date.",
                HttpStatus.BAD_REQUEST);
        }

        if (!reservationService.isAvailable(sportRoomId, dateTime)) {
            return new ResponseEntity<>("Sport Room is already booked for this time slot.",
                HttpStatus.NOT_FOUND);
        }

        String methodSpecificUrl = "/" + sportRoomId.toString() + "/exists";

        // Call to SportRoomController in Sport Facilities microservice
        Boolean sportHallExists =
            restTemplate.getForObject(sportFacilityUrl + "/sportRoom/" + methodSpecificUrl,
                Boolean.class);

        if (sportHallExists == null || !sportHallExists) {
            return new ResponseEntity<>("The SportRoom requested doesn't exist",
                HttpStatus.NOT_FOUND);
        }

        Reservation reservation =
            new Reservation(ReservationType.SPORTS_FACILITY, userId, sportRoomId, dateTime);
        Reservation reservationMade = reservationService.makeSportFacilityReservation(reservation);
        return new ResponseEntity<>("Reservation Successful!", HttpStatus.OK);
    }



    @PostMapping("/{userId}/{equipmentName}/{date}/makeEquipmentBooking")
    @ResponseBody
    public ResponseEntity<String> makeEquipmentReservation(@PathVariable Long userId,
                                                           @PathVariable String date,
                                                           @PathVariable String equipmentName) {

        // TODO: some code duplication that should be removed when implementing chain of
        //  responsibility
        LocalDateTime dateTime = LocalDateTime.parse(date);
        if (dateTime.isBefore(LocalDateTime.now())) {
            return new ResponseEntity<>("Date and time has to be after now",
                HttpStatus.BAD_REQUEST);
        }
        if ((dateTime.getHour() < 16) || (dateTime.getHour() == 23)) {
            return new ResponseEntity<>("Time has to be between 16:00 and 23:00.",
                HttpStatus.BAD_REQUEST);
        }

        // TODO: check if equipment available (enough stock)

        // TODO: To be moved to another method (Chain of Responsibility) and should be applied to
        //  whole Reservation (equipment and sportRoom may be combined in 1 reservation)

        String yearMonthDay = date.substring(0, 9);
        int reservationBalanceOnDate = getReservationBalance(userId, yearMonthDay);

        // Basic users can have 1 reservation per day
        if (!getUserIsPremium(userId) && reservationBalanceOnDate == 1) {
            return new ResponseEntity<>("No more than 1 reservation per day can be made. "
                + "Please try another date, or get a premium subscription to be able to make up "
                + "to 3 reservations per day.",
                HttpStatus.BAD_REQUEST);
        }

        // Premium users can have up to 3 reservations per day
        if (getUserIsPremium(userId) && reservationBalanceOnDate == 3) {
            return new ResponseEntity<>("No more than 3 reservations per day can be made. "
                + "Please try another date.",
                HttpStatus.BAD_REQUEST);
        }

        String methodSpecificUrl = "/equipment/" + equipmentName + "/getAvailableEquipment";

        // Call to EquipmentController in Sport Facilities microservice
        ResponseEntity<String> response =
            restTemplate.getForObject(sportFacilityUrl + methodSpecificUrl, ResponseEntity.class);

        if (!response.getStatusCode().equals(Response.status(Response.Status.OK))) {
            return response;
        }

        Long equipmentId = gson.fromJson(response.getBody(), new TypeToken<Long>() {}.getType());

        Reservation reservation =
            new Reservation(ReservationType.EQUIPMENT, userId, equipmentId, dateTime);
        Reservation reservationMade = reservationService.makeSportFacilityReservation(reservation);
        return new ResponseEntity<>("Equipment reservation was successful!", HttpStatus.OK);
    }



    @GetMapping("/{userId}/isPremium")
    @ResponseBody
    public Boolean getUserIsPremium(@PathVariable Long userId){
        String methodSpecificUrl = "/user/" + userId + "/isPremium";

        Boolean isPremium =
            restTemplate.getForObject(userUrl + methodSpecificUrl, Boolean.class);

        return isPremium;
    }


    @GetMapping("/{userId}/{date}/getReservationCountOnDay")
    @ResponseBody
    public int getReservationBalance(@PathVariable Long userId, @PathVariable String date){
        return reservationService.getUserReservationCountOnDay(date, userId);
    }


}
