package nl.tudelft.sem.sportfacilities.controllers;

import java.util.NoSuchElementException;
import nl.tudelft.sem.sportfacilities.entities.Equipment;
import nl.tudelft.sem.sportfacilities.entities.Sport;
import nl.tudelft.sem.sportfacilities.services.EquipmentService;
import nl.tudelft.sem.sportfacilities.services.SportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("equipment")
public class EquipmentController {

    private final transient EquipmentService equipmentService;
    private final transient SportService sportService;

    /**
     * Autowired constructor for the class.
     *
     * @param equipmentService equipmentService
     * @param sportService     sportService
     */
    @Autowired
    public EquipmentController(EquipmentService equipmentService, SportService sportService) {
        this.equipmentService = equipmentService;
        this.sportService = sportService;
    }

    /**
     * Gets equipment.
     *
     * @param equipmentId the equipment id
     * @return the equipment
     */
    @GetMapping("/{equipmentId}")
    @ResponseBody
    public ResponseEntity<?> getEquipment(@PathVariable Long equipmentId) {
        try {
            Equipment equipment = equipmentService.getEquipment(equipmentId);
            return new ResponseEntity<>(equipment, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Gets equipment name.
     *
     * @param equipmentId the equipment id
     * @return the equipment name
     */
    @GetMapping("/{equipmentId}/getEquipmentName")
    @ResponseBody
    public ResponseEntity<String> getEquipmentName(@PathVariable Long equipmentId) {
        try {
            String equipmentName = equipmentService.getEquipmentName(equipmentId);
            return new ResponseEntity<>(equipmentName, HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Gets one instance of equipment that is available.
     *
     * @param equipmentName the equipment name, example: "hockeyStick"
     * @return the first available equipment Id, will be -1 if non-existent or not available
     */
    @GetMapping("/{equipmentName}/getAvailableEquipment")
    @ResponseBody
    public ResponseEntity<String> getAvailableEquipment(@PathVariable String equipmentName) {
        try {
            Long equipmentId = equipmentService.getAvailableEquipmentIdsByName(equipmentName);
            return new ResponseEntity<>(equipmentId.toString(), HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Add new equipment.
     *
     * @param equipmentName    the equipment name
     * @param relatedSportName the related sport
     */
    @PutMapping("/{equipmentName}/{relatedSportName}/addNewEquipment/admin")
    @ResponseBody
    public ResponseEntity<String> addNewEquipment(@PathVariable String equipmentName,
                                             @PathVariable String relatedSportName) {
        try {
            Sport sport = sportService.getSportById(relatedSportName);
            equipmentService.addEquipment(new Equipment(equipmentName, sport, false));
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Delete an instance of equipment.
     *
     * @param equipmentId the equipment id
     * @return the response
     */
    @DeleteMapping("/{equipmentId}/deleteEquipment/admin")
    public ResponseEntity<String> deleteEquipment(@PathVariable long equipmentId) {
        try {
            equipmentService.deleteEquipment(equipmentId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

    }

    /**
     * Sets inUse for equipment to false.
     *
     * @param equipmentId the equipment id
     */
    @PutMapping("/{equipmentId}/broughtBack/admin")
    @ResponseBody
    public ResponseEntity<String> equipmentBroughtBack(@PathVariable Long equipmentId) {
        try {
            equipmentService.setEquipmentToNotInUse(equipmentId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * Sets inUse for equipment to true.
     *
     * @param equipmentId the equipment id
     */
    @PutMapping("/{equipmentId}/reserved")
    @ResponseBody
    public ResponseEntity<String> equipmentReserved(@PathVariable Long equipmentId) {
        try {
            equipmentService.setEquipmentToInUse(equipmentId);
            return new ResponseEntity<>(HttpStatus.OK);
        } catch (NoSuchElementException e) {
            return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
        }

    }

}
