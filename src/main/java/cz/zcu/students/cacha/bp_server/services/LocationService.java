package cz.zcu.students.cacha.bp_server.services;

import cz.zcu.students.cacha.bp_server.domain.*;
import cz.zcu.students.cacha.bp_server.exceptions.CannotPerformActionException;
import cz.zcu.students.cacha.bp_server.exceptions.NotFoundException;
import cz.zcu.students.cacha.bp_server.exceptions.ValidationErrorException;
import cz.zcu.students.cacha.bp_server.repositories.*;
import cz.zcu.students.cacha.bp_server.view_models.BuildingVM;
import cz.zcu.students.cacha.bp_server.view_models.RoomVM;
import cz.zcu.students.cacha.bp_server.view_models.ShowcaseVM;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.Collator;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Class represent service which is responsible for location (building, room and show-case) operations
 */
@Service
public class LocationService {

    @Autowired
    private BuildingRepository buildingRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private ShowcaseRepository showcaseRepository;

    @Autowired
    private InstitutionRepository institutionRepository;

    @Autowired
    private ExhibitRepository exhibitRepository;

    @Autowired
    private Collator czechCollator;

    /**
     * Gets all buildings of user's institution
     * @param user owner of an institution
     * @return all buildings of user's institution
     */
    public List<BuildingVM> getAllBuildingsOfUsersInstitution(User user) {
        // return all buildings mapped to view model and sorted by name
        return user.getInstitution().getBuildings().stream().map(BuildingVM::new)
                .sorted(Comparator.comparing(BuildingVM::getName, czechCollator))
                .collect(Collectors.toList());
    }

    /**
     * Gets all buildings of institution defined by its id
     * @param institutionId institution id
     * @return all buildings of institution defined by its id
     */
    public List<BuildingVM> getAllBuildings(Long institutionId) {
        // check if institution with this id exists
        Optional<Institution> institutionOptional = institutionRepository.findById(institutionId);
        if(institutionOptional.isEmpty()) {
            throw new NotFoundException("Institution not found");
        }

        // get all buildings and return them sorted by name
        return institutionOptional.get().getBuildings().stream().map(BuildingVM::new).sorted(Comparator.comparing(BuildingVM::getName, czechCollator))
                .collect(Collectors.toList());
    }

    /**
     * Gets details about building defined by its id
     * @param buildingId building id
     * @param user owner of an institution
     * @return details about building defined by its id
     */
    public BuildingVM getBuilding(Long buildingId, User user) {
        Building building = verifyUserManagesBuilding(buildingId, user);
        return new BuildingVM(building);
    }

    /**
     * Saves new building to logged in user's institution
     * @param building new building
     * @param user institution manager
     */
    public void saveBuilding(Building building, User user) {
        Institution institution = user.getInstitution();

        // check name is unique
        Optional<Building> foundBuildingOptional = buildingRepository.findByNameAndInstitution(building.getName(), user.getInstitution());
        if(foundBuildingOptional.isPresent()) {
            // if building name is already taken throw exception
            HashMap<String, String> errorsMap = new HashMap<>();
            errorsMap.put("name", "Name is already taken");
            throw new ValidationErrorException(errorsMap);
        }

        // set institution and save building
        building.setInstitution(institution);
        buildingRepository.save(building);
    }

    /**
     * Updates building with new information based on its id
     * @param updatedBuilding updated building
     * @param buildingId building id
     * @param user institution manager
     */
    public void updateBuilding(Building updatedBuilding, Long buildingId, User user) {
        Building building = verifyUserManagesBuilding(buildingId, user);

        // check updated name is unique
        Optional<Building> foundBuildingOptional = buildingRepository.findByNameAndInstitution(updatedBuilding.getName(), user.getInstitution());
        if(foundBuildingOptional.isPresent() && !foundBuildingOptional.get().getId().equals(building.getId())) {
            // if building name is already taken throw exception
            HashMap<String, String> errorsMap = new HashMap<>();
            errorsMap.put("name", "Name is already taken");
            throw new ValidationErrorException(errorsMap);
        }

        // set new information and save building
        building.setName(updatedBuilding.getName());
        building.setDescription(updatedBuilding.getDescription());
        buildingRepository.save(building);
    }

    /**
     * Deletes building by its id
     * @param buildingId building id
     * @param user institution manager
     */
    @Transactional
    public void deleteBuilding(Long buildingId, User user) {
        Building building = verifyUserManagesBuilding(buildingId, user);

        // remove location from exhibits
        Set<Exhibit> exhibits = building.getExhibits();
        for (Exhibit e : exhibits) {
            e.setBuilding(null);
            e.setRoom(null);
            e.setShowcase(null);
        }
        exhibitRepository.saveAll(exhibits);

        // delete building
        buildingRepository.delete(building);
    }

    /**
     * Checks if given user manages given building and returns the building
     * @param buildingId building id
     * @param user user
     * @return building selected by its id
     */
    private Building verifyUserManagesBuilding(Long buildingId, User user) {
        // get building if exists
        Optional<Building> buildingOptional = buildingRepository.findById(buildingId);
        if(buildingOptional.isEmpty()) {
            throw new NotFoundException("Building not found");
        }
        Building building = buildingOptional.get();

        // check if user is manager of given building
        if(!building.getInstitution().getId().equals(user.getInstitution().getId())) {
            throw new CannotPerformActionException("Logged in user does not have permission to this building");
        }
        return building;
    }

    /**
     * Gets all rooms of building defined by id
     * @param buildingId building id
     * @return all rooms of building defined by id
     */
    public List<RoomVM> getAllRooms(Long buildingId) {
        // check if building with this id exists
        Optional<Building> buildingOptional = buildingRepository.findById(buildingId);
        if(buildingOptional.isEmpty()) {
            throw new NotFoundException("Building not found");
        }

        // get all rooms and return them sorted by name
        return buildingOptional.get().getRooms().stream().map(RoomVM::new).sorted(Comparator.comparing(RoomVM::getName, czechCollator))
                .collect(Collectors.toList());
    }

    /**
     * Gets room details by its id
     * @param roomId room id
     * @param user institution manager
     * @return room details
     */
    public RoomVM getRoom(Long roomId, User user) {
        Room room = verifyUserManagesRoom(roomId, user);
        return new RoomVM(room);
    }

    /**
     * Saves new room to building defined by id
     * @param room new room
     * @param buildingId building id
     * @param user institution manager
     */
    public void saveRoom(Room room, Long buildingId, User user)  {
        // check if building with this id exists
        Building targetBuilding = verifyUserManagesBuilding(buildingId, user);

        // check name is unique
        Optional<Room> foundRoomOptional = roomRepository.findByNameAndBuilding(room.getName(), targetBuilding);
        if(foundRoomOptional.isPresent()) {
            // if room name is already taken throw exception
            HashMap<String, String> errorsMap = new HashMap<>();
            errorsMap.put("name", "Name is already taken");
            throw new ValidationErrorException(errorsMap);
        }

        // set building and save room
        room.setBuilding(targetBuilding);
        roomRepository.save(room);
    }

    /**
     * Updates room defined by id with new information
     * @param updatedRoom updated room
     * @param roomId room id
     * @param user institution manager
     */
    public void updateRoom(Room updatedRoom, Long roomId, User user) {
        // check that user manages this room
        Room room = verifyUserManagesRoom(roomId, user);

        // check updated name is unique
        Optional<Room> foundRoomOptional = roomRepository.findByNameAndBuilding(updatedRoom.getName(), room.getBuilding());
        if(foundRoomOptional.isPresent() && !foundRoomOptional.get().getId().equals(room.getId())) {
            // if room name is already taken throw exception
            HashMap<String, String> errorsMap = new HashMap<>();
            errorsMap.put("name", "Name is already taken");
            throw new ValidationErrorException(errorsMap);
        }

        // set new information and save room
        room.setName(updatedRoom.getName());
        room.setDescription(updatedRoom.getDescription());
        roomRepository.save(room);
    }

    /**
     * Deletes room by its id
     * @param roomId room id
     * @param user institution manager
     */
    @Transactional
    public void deleteRoom(Long roomId, User user) {
        Room room = verifyUserManagesRoom(roomId, user);

        // remove location from exhibits
        Set<Exhibit> exhibits = room.getExhibits();
        for (Exhibit e : exhibits) {
            e.setRoom(null);
            e.setShowcase(null);
        }
        exhibitRepository.saveAll(exhibits);

        // delete room
        roomRepository.delete(room);
    }

    /**
     * Checks if given user manages given room and returns the room
     * @param roomId room id
     * @param user user
     * @return room selected by its id
     */
    private Room verifyUserManagesRoom(Long roomId, User user) {
        // get room if exists
        Optional<Room> roomOptional = roomRepository.findById(roomId);
        if(roomOptional.isEmpty()) {
            throw new NotFoundException("Room not found");
        }
        Room room = roomOptional.get();

        // check if user is manager of given room
        if(!room.getBuilding().getInstitution().getId().equals(user.getInstitution().getId())) {
            throw new CannotPerformActionException("Logged in user does not have permission to this room");
        }
        return room;
    }

    /**
     * Gets all showcases of room defined by id
     * @param roomId room id
     * @return all showcases of room defined by id
     */
    public List<ShowcaseVM> getAllShowcases(Long roomId) {
        // check if room with this id exists
        Optional<Room> roomOptional = roomRepository.findById(roomId);
        if(roomOptional.isEmpty()) {
            throw new NotFoundException("Room not found");
        }

        // get all showcases and return them sorted by name
        return roomOptional.get().getShowcases().stream().map(ShowcaseVM::new).sorted(Comparator.comparing(ShowcaseVM::getName, czechCollator))
                .collect(Collectors.toList());
    }

    /**
     * Gets showcase details by its id
     * @param showcaseId showcase id
     * @param user institution manager
     * @return showcase details
     */
    public ShowcaseVM getShowcase(Long showcaseId, User user) {
        Showcase showcase = verifyUserManagesShowcase(showcaseId, user);
        return new ShowcaseVM(showcase);
    }

    /**
     * Saves new showcase to room defined by id
     * @param showcase new showcase
     * @param roomId room id
     * @param user institution manager
     */
    public void saveShowcase(Showcase showcase, Long roomId, User user)  {
        // check if room with this id exists
        Room targetRoom = verifyUserManagesRoom(roomId, user);

        // check name is unique
        Optional<Showcase> foundShowcaseOptional = showcaseRepository.findByNameAndRoom(showcase.getName(), targetRoom);
        if(foundShowcaseOptional.isPresent()) {
            // if showcase name is already taken throw exception
            HashMap<String, String> errorsMap = new HashMap<>();
            errorsMap.put("name", "Name is already taken");
            throw new ValidationErrorException(errorsMap);
        }

        // set room and save showcase
        showcase.setRoom(targetRoom);
        showcaseRepository.save(showcase);
    }

    /**
     * Updates showcase defined by id with new information
     * @param updatedShowcase updated showcase
     * @param showcaseId showcase id
     * @param user institution manager
     */
    public void updateShowcase(Showcase updatedShowcase, Long showcaseId, User user) {
        // check that user manages this showcase
        Showcase showcase = verifyUserManagesShowcase(showcaseId, user);

        // check updated name is unique
        Optional<Showcase> foundShowcaseOptional = showcaseRepository.findByNameAndRoom(updatedShowcase.getName(), showcase.getRoom());
        if(foundShowcaseOptional.isPresent() && !foundShowcaseOptional.get().getId().equals(showcase.getId())) {
            // if showcase name is already taken throw exception
            HashMap<String, String> errorsMap = new HashMap<>();
            errorsMap.put("name", "Name is already taken");
            throw new ValidationErrorException(errorsMap);
        }

        // set new information and save showcase
        showcase.setName(updatedShowcase.getName());
        showcase.setDescription(updatedShowcase.getDescription());
        showcaseRepository.save(showcase);
    }

    /**
     * Deletes showcase by its id
     * @param showcaseId showcase id
     * @param user institution manager
     */
    @Transactional
    public void deleteShowcase(Long showcaseId, User user) {
        Showcase showcase = verifyUserManagesShowcase(showcaseId, user);

        // remove location from exhibits
        Set<Exhibit> exhibits = showcase.getExhibits();
        for (Exhibit e : exhibits) {
            e.setShowcase(null);
        }
        exhibitRepository.saveAll(exhibits);

        // delete showcase
        showcaseRepository.delete(showcase);
    }

    /**
     * Checks if given user manages given showcase and returns the showcase
     * @param showcaseId showcase id
     * @param user user
     * @return showcase selected by its id
     */
    private Showcase verifyUserManagesShowcase(Long showcaseId, User user) {
        // get showcase if exists
        Optional<Showcase> showcaseOptional = showcaseRepository.findById(showcaseId);
        if(showcaseOptional.isEmpty()) {
            throw new NotFoundException("Showcase not found");
        }
        Showcase showcase = showcaseOptional.get();

        // check if user is manager of given showcase
        if(!showcase.getRoom().getBuilding().getInstitution().getId().equals(user.getInstitution().getId())) {
            throw new CannotPerformActionException("Logged in user does not have permission to this showcase");
        }
        return showcase;
    }
}
