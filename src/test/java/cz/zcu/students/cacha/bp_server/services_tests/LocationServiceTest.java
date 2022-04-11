package cz.zcu.students.cacha.bp_server.services_tests;

import cz.zcu.students.cacha.bp_server.common.TestUtils;
import cz.zcu.students.cacha.bp_server.domain.*;
import cz.zcu.students.cacha.bp_server.repositories.*;
import cz.zcu.students.cacha.bp_server.services.InstitutionService;
import cz.zcu.students.cacha.bp_server.services.LocationService;
import cz.zcu.students.cacha.bp_server.services.UserService;
import cz.zcu.students.cacha.bp_server.view_models.BuildingVM;
import cz.zcu.students.cacha.bp_server.view_models.RoomVM;
import cz.zcu.students.cacha.bp_server.view_models.ShowcaseVM;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Set of tests to check the quality of LocatinService class
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class LocationServiceTest {

    @Autowired
    private TestUtils testUtils;

    @Autowired
    private InstitutionRepository institutionRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BuildingRepository buildingRepository;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private ShowcaseRepository showcaseRepository;

    @Autowired
    private LocationService locationService;

    @Autowired
    private InstitutionService institutionService;

    @Autowired
    private UserService userService;

    /**
     * called after each test
     * provides cleanup of db and images
     */
    @AfterEach
    public void cleanup() {
        // delete data from db
        showcaseRepository.deleteAll();
        roomRepository.deleteAll();
        buildingRepository.deleteAll();
        userRepository.deleteAll();
        institutionRepository.deleteAll();

        // delete all images from img folders
        testUtils.clearImages();
    }

    /**
     * prepares user with institution in db
     * @return user
     */
    public User prepareUserWithInstitution() {
        // prepare institution
        User user = testUtils.createValidUser();
        Institution institution = testUtils.createValidInstitution();

        // save institution and user
        userService.save(user);
        institutionService.saveInstitution(institution, user);

        return user;
    }

    /**
     * tests method for getting all buildings of user institution
     */
    @Test
    public void testGetAllBuildingsOfUsersInstitution() {
        User user = prepareUserWithInstitution();
        // prepare building
        Building building = testUtils.createValidBuilding();
        // save building
        locationService.saveBuilding(building, user);

        // call tested method
        List<BuildingVM> buildings = locationService.getAllBuildingsOfUsersInstitution(userRepository.findById(user.getId()).get());

        // check buildings count
        assertEquals(1, buildings.size());
    }

    /**
     * tests method for getting all buildings depending on institution id
     */
    @Test
    public void testGetAllBuildings() {
        User user = prepareUserWithInstitution();
        Long institutionId = user.getInstitution().getId();
        // prepare building
        Building building = testUtils.createValidBuilding();
        // save building
        locationService.saveBuilding(building, user);

        // call tested method
        List<BuildingVM> buildings = locationService.getAllBuildings(institutionId);

        // check buildings count
        assertEquals(1, buildings.size());
    }

    /**
     * tests method for getting building
     */
    @Test
    public void testGetBuilding() {
        User user = prepareUserWithInstitution();
        // prepare building
        Building building = testUtils.createValidBuilding();
        // save building
        locationService.saveBuilding(building, user);

        // test method for getting building
        assertNotNull(locationService.getBuilding(building.getId(), userRepository.findById(user.getId()).get()));
    }

    /**
     * tests method for saving buildings
     */
    @Test
    public void testSaveBuilding() {
        User user = prepareUserWithInstitution();
        // prepare building
        Building building = testUtils.createValidBuilding();

        // call tested method
        locationService.saveBuilding(building, user);
        // check right buildings count in db
        assertEquals(1, buildingRepository.count());
    }

    /**
     * tests updating building info
     */
    @Test
    public void testUpdateBuilding() {
        User user = prepareUserWithInstitution();
        // prepare building
        Building building = testUtils.createValidBuilding();
        building.setDescription("testDescription");
        // save building
        locationService.saveBuilding(building, user);

        // prepare updated building
        Building updated = testUtils.createValidBuilding();
        building.setName("updatedName");
        building.setDescription("updatedDescription");

        // call tested method
        locationService.updateBuilding(updated, building.getId(), userRepository.findById(user.getId()).get());

        // check updated info is persisted
        Building persisted = buildingRepository.findById(building.getId()).get();
        assertEquals(updated.getName(), persisted.getName());
        assertEquals(updated.getDescription(), persisted.getDescription());
    }

    /**
     * tests method for deleting building
     */
    @Test
    public void testDeleteBuilding() {
        User user = prepareUserWithInstitution();
        // prepare building
        Building building = testUtils.createValidBuilding();
        building.setDescription("testDescription");
        // save building
        locationService.saveBuilding(building, user);

        // call tested method
        locationService.deleteBuilding(building.getId(), userRepository.findById(user.getId()).get());
        // check there is no building left in db
        assertEquals(0, buildingRepository.count());
    }

    /**
     * tests method for getting all rooms
     */
    @Test
    public void testGetAllRooms() {
        User user = prepareUserWithInstitution();
        long buildingId = prepareBuilding(user);
        // save room
        Room room = testUtils.createValidRoom();
        locationService.saveRoom(room, buildingId, user);

        // call tested method
        List<RoomVM> rooms = locationService.getAllRooms(buildingId);

        // check rooms count
        assertEquals(1, rooms.size());
    }

    /**
     * saves building in db
     * @param user user
     * @return saved building id
     */
    private long prepareBuilding(User user) {
        // prepare building
        Building building = testUtils.createValidBuilding();
        // save building
        locationService.saveBuilding(building, user);

        return building.getId();
    }

    /**
     * tests method for getting room
     */
    @Test
    public void testGetRoom() {
        User user = prepareUserWithInstitution();
        long buildingId = prepareBuilding(user);
        // prepare room
        Room room = testUtils.createValidRoom();
        // save room
        locationService.saveRoom(room, buildingId, user);

        // test method for getting room
        assertNotNull(locationService.getRoom(room.getId(), userRepository.findById(user.getId()).get()));
    }

    /**
     * tests method for saving rooms
     */
    @Test
    public void testSaveRoom() {
        User user = prepareUserWithInstitution();
        long buildingId = prepareBuilding(user);
        // prepare room
        Room room = testUtils.createValidRoom();

        // call tested method
        locationService.saveRoom(room, buildingId, user);
        // check right rooms count in db
        assertEquals(1, roomRepository.count());
    }

    /**
     * tests updating room info
     */
    @Test
    public void testUpdateRoom() {
        User user = prepareUserWithInstitution();
        long buildingId = prepareBuilding(user);
        // prepare room
        Room room = testUtils.createValidRoom();
        room.setDescription("testDescription");
        // save room
        locationService.saveRoom(room, buildingId, user);

        // prepare updated room
        Room updated = testUtils.createValidRoom();
        updated.setName("updatedName");
        updated.setDescription("updatedDescription");

        // call tested method
        locationService.updateRoom(updated, room.getId(), userRepository.findById(user.getId()).get());

        // check updated info is persisted
        Room persisted = roomRepository.findById(room.getId()).get();
        assertEquals(updated.getName(), persisted.getName());
        assertEquals(updated.getDescription(), persisted.getDescription());
    }

    /**
     * tests method for deleting room
     */
    @Test
    public void testDeleteRoom() {
        User user = prepareUserWithInstitution();
        long buildingId = prepareBuilding(user);
        // prepare room
        Room room = testUtils.createValidRoom();
        // save room
        locationService.saveRoom(room, buildingId, user);

        // call tested method
        locationService.deleteRoom(room.getId(), userRepository.findById(user.getId()).get());
        // check there is no room left in db
        assertEquals(0, roomRepository.count());
    }

    /**
     * tests method for getting all showcases
     */
    @Test
    public void testGetAllShowcases() {
        User user = prepareUserWithInstitution();
        long roomId = prepareRoom(user);
        // save showcase
        Showcase showcase = testUtils.createValidShowcase();
        locationService.saveShowcase(showcase, roomId, user);

        // call tested method
        List<ShowcaseVM> showcases = locationService.getAllShowcases(roomId);

        // check showcases count
        assertEquals(1, showcases.size());
    }

    /**
     * Prepares room in db
     * @param user user
     * @return room id
     */
    private long prepareRoom(User user) {
        long buildingID = prepareBuilding(user);
        // prepare room
        Room room = testUtils.createValidRoom();
        // save room
        locationService.saveRoom(room, buildingID, user);

        return room.getId();
    }

    /**
     * tests method for getting showcase
     */
    @Test
    public void testGetShowcase() {
        User user = prepareUserWithInstitution();
        long roomId = prepareRoom(user);
        // prepare showcase
        Showcase showcase = testUtils.createValidShowcase();
        // save showcase
        locationService.saveShowcase(showcase, roomId, user);

        // test method for getting showcase
        assertNotNull(locationService.getShowcase(showcase.getId(), userRepository.findById(user.getId()).get()));
    }

    /**
     * tests method for saving showcases
     */
    @Test
    public void testSaveShowcase() {
        User user = prepareUserWithInstitution();
        long roomId = prepareRoom(user);
        // prepare showcase
        Showcase showcase = testUtils.createValidShowcase();

        // call tested method
        locationService.saveShowcase(showcase, roomId, user);
        // check right showcases count in db
        assertEquals(1, showcaseRepository.count());
    }

    /**
     * tests updating showcase info
     */
    @Test
    public void testUpdateShowcase() {
        User user = prepareUserWithInstitution();
        long roomId = prepareRoom(user);
        // prepare showcase
        Showcase showcase = testUtils.createValidShowcase();
        showcase.setDescription("testDescription");
        // save showcase
        locationService.saveShowcase(showcase, roomId, user);

        // prepare updated showcase
        Showcase updated = testUtils.createValidShowcase();
        updated.setName("updatedName");
        updated.setDescription("updatedDescription");

        // call tested method
        locationService.updateShowcase(updated, showcase.getId(), userRepository.findById(user.getId()).get());

        // check updated info is persisted
        Showcase persisted = showcaseRepository.findById(showcase.getId()).get();
        assertEquals(updated.getName(), persisted.getName());
        assertEquals(updated.getDescription(), persisted.getDescription());
    }

    /**
     * tests method for deleting showcase
     */
    @Test
    public void testDeleteShowcase() {
        User user = prepareUserWithInstitution();
        long roomId = prepareRoom(user);
        // prepare showcase
        Showcase showcase = testUtils.createValidShowcase();
        // save showcase
        locationService.saveShowcase(showcase, roomId, user);

        // call tested method
        locationService.deleteShowcase(showcase.getId(), userRepository.findById(user.getId()).get());
        // check there is no showcase left in db
        assertEquals(0, showcaseRepository.count());
    }
}
