package sportfacilities.controllers;

import java.util.NoSuchElementException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import sportfacilities.services.SportRoomService;

/**
 * The type Sport room controller.
 */
//TODO make every test use assertJ
@RestController
@RequestMapping("sportRoom")
public class SportRoomController {

    private final transient SportRoomService sportRoomService;

    private final getSportRoomServices getSportRoomServices;

    private final setSportRoomServices setSportRoomServices;

    /**
     * Instantiates a new Sport room controller.
     *
     * @param sportRoomService the sport room service
     */
    @Autowired
    public SportRoomController(SportRoomService sportRoomService) {
        this.sportRoomService = sportRoomService;
        this.getSportRoomServices = new getSportRoomServices(sportRoomService);
        this.setSportRoomServices = new setSportRoomServices(sportRoomService);
    }

    /**
     * Gets sport room.
     *
     * @param sportRoomId the sport room id
     * @return the sport room
     */
    @GetMapping("/{sportRoomId}")
    @ResponseBody
    public ResponseEntity<?> getSportRoom(@PathVariable Long sportRoomId) {
        return getSportRoomServices.getSportRoom(sportRoomId);
    }

    /**
     * Response entity containing boolean, whether or not sport room exists in database.
     *
     * @param sportRoomId the sport room id
     * @return the response entity
     */
    @GetMapping("/{sportRoomId}/exists")
    @ResponseBody
    public ResponseEntity<?> sportRoomExists(@PathVariable Long sportRoomId) {
        Boolean exists = sportRoomService.sportRoomExists(sportRoomId);
        return new ResponseEntity<>(exists.toString(), HttpStatus.OK);
    }

    /**
     * Sport room is hall response entity.
     *
     * @param sportRoomId the sport room id
     * @return the response entity
     */
    @GetMapping("/{sportRoomId}/isHall")
    @ResponseBody
    public ResponseEntity<?> sportRoomIsHall(@PathVariable Long sportRoomId) {
        try {
            Boolean isHall = sportRoomService.getSportRoom(sportRoomId).getIsSportsHall();
            return new ResponseEntity<>(isHall.toString(), HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Gets sport room name.
     *
     * @param sportRoomId the sport room id
     * @return the sport room name
     */
    @GetMapping("/{sportRoomId}/getName")
    @ResponseBody
    public ResponseEntity<String> getSportRoomName(@PathVariable Long sportRoomId) {
        return getSportRoomServices.getSportRoomName(sportRoomId);
    }

    /**
     * Sets sport room name.
     *
     * @param sportRoomId   the sport room id
     * @param sportRoomName the sport room name
     * @return the sport room name
     */
    @PostMapping("/{sportRoomId}/{sportRoomName}/setSportRoomName/admin")
    @ResponseBody
    public ResponseEntity<?> setSportRoomName(@PathVariable Long sportRoomId,
                                              @PathVariable String sportRoomName) {
        return setSportRoomServices.setSportRoomName(sportRoomId, sportRoomName);
    }

    /**
     * Gets sport room maximum capacity.
     *
     * @param sportRoomId the sport room id
     * @return the sport room maximum capacity
     */
    @GetMapping("/{sportRoomId}/getMaximumCapacity")
    @ResponseBody
    public ResponseEntity<?> getSportRoomMaximumCapacity(@PathVariable Long sportRoomId) {
        return getSportRoomServices.getSportRoomMaximumCapacity(sportRoomId);
    }

    /**
     * Gets sport room minimum capacity.
     *
     * @param sportRoomId the sport room id
     * @return the sport room minimum capacity
     */
    @GetMapping("/{sportRoomId}/getMinimumCapacity")
    @ResponseBody
    public ResponseEntity<?> getSportRoomMinimumCapacity(@PathVariable Long sportRoomId) {
        return getSportRoomServices.getSportRoomMinimumCapacity(sportRoomId);
    }

    /**
     * Sets sport room minimum capacity.
     *
     * @param sportRoomId the sport room id
     * @param minCapacity the min capacity
     * @return the sport room minimum capacity
     */
    @PostMapping("/{sportRoomId}/{minCapacity}/setMinimumCapacity/admin")
    @ResponseBody
    public ResponseEntity<?> setSportRoomMinimumCapacity(@PathVariable Long sportRoomId,
                                                         @PathVariable int minCapacity) {
        return setSportRoomServices.setSportRoomMinimumCapacity(sportRoomId, minCapacity);
    }

    /**
     * Sets sport room maximum capacity.
     *
     * @param sportRoomId the sport room id
     * @param maxCapacity the max capacity
     * @return the sport room maximum capacity
     */
    @PostMapping("/{sportRoomId}/{maxCapacity}/setMaximumCapacity/admin")
    @ResponseBody
    public ResponseEntity<?> setSportRoomMaximumCapacity(@PathVariable Long sportRoomId,
                                                         @PathVariable int maxCapacity) {
        return setSportRoomServices.setSportRoomMaximumCapacity(sportRoomId, maxCapacity);
    }

    /**
     * Gets sport field sport.
     *
     * @param sportFieldId the sport field id
     * @return the sport field sport
     */
    @GetMapping("/{sportFieldId}/getSport")
    @ResponseBody
    public ResponseEntity<String> getSportFieldSport(@PathVariable Long sportFieldId) {
        return getSportRoomServices.getSportFieldSport(sportFieldId);
    }

    /**
     * Add sport room response entity.
     *
     * @param name        the name
     * @param sport       the sport
     * @param minCapacity the min capacity
     * @param maxCapacity the max capacity
     * @param isSportHall the is sport hall
     * @return the response entity
     */
    @PutMapping("/{name}/{sport}/{minCapacity}/{maxCapacity}/{isSportHall}/addSportRoom/admin")
    @ResponseBody
    public ResponseEntity<?> addSportRoom(@PathVariable String name, @PathVariable String sport,
                                          @PathVariable int minCapacity,
                                          @PathVariable int maxCapacity,
                                          @PathVariable boolean isSportHall) {

        sportRoomService.addSportRoom(name, sport, minCapacity, maxCapacity, isSportHall);
        return new ResponseEntity<>(HttpStatus.OK);

    }

    /**
     * Delete sport room response entity.
     *
     * @param sportRoomId the sport room id
     * @return the response entity
     */
    @DeleteMapping("/{sportRoomId}/deleteSportRoom/admin")
    public ResponseEntity<String> deleteSportRoom(@PathVariable Long sportRoomId) {
        try {
            sportRoomService.deleteSportRoom(sportRoomId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Add sport to sports hall response entity.
     *
     * @param sportRoomId the sport room id
     * @param sportName   the sport name
     * @return the response entity
     */
    @PostMapping("/{sportRoomId}/{sportName}/addSportToSportHall/admin")
    @ResponseBody
    public ResponseEntity<?> addSportToSportsHall(@PathVariable Long sportRoomId,
                                                  @PathVariable String sportName) {
        try {
            sportRoomService.addSportToSportsHall(sportRoomId, sportName);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (NoSuchElementException | IllegalArgumentException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

    }

}

