package cz.zcu.students.cacha.bp_server.controllers;

import cz.zcu.students.cacha.bp_server.domain.Building;
import cz.zcu.students.cacha.bp_server.domain.Room;
import cz.zcu.students.cacha.bp_server.domain.Showcase;
import cz.zcu.students.cacha.bp_server.domain.User;
import cz.zcu.students.cacha.bp_server.responses.GenericResponse;
import cz.zcu.students.cacha.bp_server.services.LocationService;
import cz.zcu.students.cacha.bp_server.shared.CurrentUser;
import cz.zcu.students.cacha.bp_server.view_models.BuildingVM;
import cz.zcu.students.cacha.bp_server.view_models.RoomVM;
import cz.zcu.students.cacha.bp_server.view_models.ShowcaseVM;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

/**
 * Class represents rest controller which is responsible for all operations connected to managing building, room and show-case
 */
@RestController
@RequestMapping("/location")
public class LocationController {

    @Autowired
    private LocationService locationService;

    /**
     * Gets all buildings of logged in user's institution
     * @param user logged in user
     * @return all buildings of logged in user's institution
     */
    @GetMapping("/buildings")
    public List<BuildingVM> getAllBuildingsOfUsersInstitution(@CurrentUser User user) {
        List<BuildingVM> buildings = locationService.getAllBuildingsOfUsersInstitution(user);
        return buildings;
    }

    /**
     * Gets all buildings of institution based on its id
     * @param institutionId institution id
     * @return all buildings of institution based on its id
     */
    @GetMapping("/buildings/all/{institutionId}")
    public List<BuildingVM> getAllBuildings(@PathVariable Long institutionId) {
        List<BuildingVM> buildings = locationService.getAllBuildings(institutionId);
        return buildings;
    }

    /**
     * Gets building based on its id
     * @param buildingId building id
     * @param user logged in user
     * @return building based on its id
     */
    @GetMapping("/buildings/{buildingId}")
    public BuildingVM getBuilding(@PathVariable Long buildingId, @CurrentUser User user) {
        BuildingVM building = locationService.getBuilding(buildingId, user);
        return building;
    }

    /**
     * Saves new building to logged in user's institution
     * @param building building
     * @param user logged in user
     * @return message containing whether operation was processed
     */
    @PostMapping("/buildings")
    @ResponseStatus(HttpStatus.CREATED)
    public GenericResponse saveBuilding(@Valid @RequestBody Building building, @CurrentUser User user) {
        locationService.saveBuilding(building, user);
        return new GenericResponse("Building saved");
    }

    /**
     * Updates building information based on its id
     * @param building updated building
     * @param buildingId building id
     * @param user logged in user
     * @return message containing whether operation was processed
     */
    @PutMapping("/buildings/{buildingId}")
    public GenericResponse updateBuilding(@Valid @RequestBody Building building, @PathVariable Long buildingId, @CurrentUser User user) {
        locationService.updateBuilding(building, buildingId, user);
        return new GenericResponse("Building updated");
    }

    /**
     * Deletes building based on its id
     * @param buildingId building id
     * @param user logged in user
     * @return message containing whether operation was processed
     */
    @DeleteMapping("/buildings/{buildingId}")
    public GenericResponse deleteBuilding(@PathVariable Long buildingId, @CurrentUser User user) {
        locationService.deleteBuilding(buildingId, user);
        return new GenericResponse("Building deleted");
    }

    /**
     * Gets all rooms of building based on its id
     * @param buildingId building id
     * @return all rooms of building based on its id
     */
    @GetMapping("/rooms/all/{buildingId}")
    public List<RoomVM> getAllRooms(@PathVariable Long buildingId) {
        List<RoomVM> rooms = locationService.getAllRooms(buildingId);
        return rooms;
    }

    /**
     * Gets room based on its id
     * @param roomId room id
     * @param user logged in user
     * @return room based on its id
     */
    @GetMapping("/rooms/{roomId}")
    public RoomVM getRoom(@PathVariable Long roomId, @CurrentUser User user) {
        RoomVM room = locationService.getRoom(roomId, user);
        return room;
    }

    /**
     * Saves new room to given building defined by id
     * @param room room
     * @param buildingId building id
     * @param user logged in user
     * @return message containing whether operation was processed
     */
    @PostMapping("/rooms/{buildingId}")
    @ResponseStatus(HttpStatus.CREATED)
    public GenericResponse saveRoom(@Valid @RequestBody Room room, @PathVariable Long buildingId, @CurrentUser User user) {
        locationService.saveRoom(room, buildingId, user);
        return new GenericResponse("Room saved");
    }

    /**
     * Updates room information based on its id
     * @param room updated room
     * @param roomId room id
     * @param user logged in user
     * @return message containing whether operation was processed
     */
    @PutMapping("/rooms/{roomId}")
    public GenericResponse updateRoom(@Valid @RequestBody Room room, @PathVariable Long roomId, @CurrentUser User user) {
        locationService.updateRoom(room, roomId, user);
        return new GenericResponse("Room updated");
    }

    /**
     * Deletes room based on its id
     * @param roomId room id
     * @param user logged in user
     * @return message containing whether operation was processed
     */
    @DeleteMapping("/rooms/{roomId}")
    public GenericResponse deleteRoom(@PathVariable Long roomId, @CurrentUser User user) {
        locationService.deleteRoom(roomId, user);
        return new GenericResponse("Room deleted");
    }

    /**
     * Gets all showcases of room based on its id
     * @param roomId room id
     * @return all showcases of room based on its id
     */
    @GetMapping("/showcases/all/{roomId}")
    public List<ShowcaseVM> getAllShowcases(@PathVariable Long roomId) {
        List<ShowcaseVM> showcases = locationService.getAllShowcases(roomId);
        return showcases;
    }

    /**
     * Gets showcase based on its id
     * @param showcaseId showcase id
     * @param user logged in user
     * @return showcase based on its id
     */
    @GetMapping("/showcases/{showcaseId}")
    public ShowcaseVM getShowcase(@PathVariable Long showcaseId, @CurrentUser User user) {
        ShowcaseVM showcase = locationService.getShowcase(showcaseId, user);
        return showcase;
    }

    /**
     * Saves new showcase to given room defined by id
     * @param showcase showcase
     * @param roomId room id
     * @param user logged in user
     * @return message containing whether operation was processed
     */
    @PostMapping("/showcases/{roomId}")
    @ResponseStatus(HttpStatus.CREATED)
    public GenericResponse saveShowcase(@Valid @RequestBody Showcase showcase, @PathVariable Long roomId, @CurrentUser User user) {
        locationService.saveShowcase(showcase, roomId, user);
        return new GenericResponse("Showcase saved");
    }

    /**
     * Updates showcase information based on its id
     * @param showcase updated showcase
     * @param showcaseId showcase id
     * @param user logged in user
     * @return message containing whether operation was processed
     */
    @PutMapping("/showcases/{showcaseId}")
    public GenericResponse updateShowcase(@Valid @RequestBody Showcase showcase, @PathVariable Long showcaseId, @CurrentUser User user) {
        locationService.updateShowcase(showcase, showcaseId, user);
        return new GenericResponse("Showcase updated");
    }

    /**
     * Deletes showcase based on its id
     * @param showcaseId showcase id
     * @param user logged in user
     * @return message containing whether operation was processed
     */
    @DeleteMapping("/showcases/{showcaseId}")
    public GenericResponse deleteShowcase(@PathVariable Long showcaseId, @CurrentUser User user) {
        locationService.deleteShowcase(showcaseId, user);
        return new GenericResponse("Showcase deleted");
    }
}
