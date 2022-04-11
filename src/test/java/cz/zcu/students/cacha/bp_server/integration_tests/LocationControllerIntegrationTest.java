package cz.zcu.students.cacha.bp_server.integration_tests;

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
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Set of tests to check the quality of LocationController endpoints
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class LocationControllerIntegrationTest {
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

    @Autowired
    private TestRestTemplate testRestTemplate;

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
    public User prepareUserWithInstitution(User user) {
        // prepare institution
        Institution institution = testUtils.createValidInstitution();

        // save institution and user
        userService.save(user);
        institutionService.saveInstitution(institution, user);

        return user;
    }

    /**
     * tests endpoint for getting all buildings depending on institution id
     */
    @Test
    public void testGetAllBuildings() {
        User user = prepareUserWithInstitution(testUtils.createValidUser());
        Long institutionId = user.getInstitution().getId();
        // prepare building
        Building building = testUtils.createValidBuilding();
        // save building
        locationService.saveBuilding(building, user);

        // call tested endpoint
        ResponseEntity<List> response = testRestTemplate.getForEntity("/location/buildings/all/" + institutionId, List.class);
        // check buildings count
        assertEquals(1, response.getBody().size());
        // check status code
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    /**
     * tests endpoint for getting building
     */
    @Test
    public void testGetBuilding() {
        User user = testUtils.createValidUser();
        String password = user.getPassword();
        user = prepareUserWithInstitution(user);
        // prepare building
        Building building = testUtils.createValidBuilding();
        // save building
        locationService.saveBuilding(building, user);

        // authenticate
        testUtils.authenticate(user, password, testRestTemplate);

        // call tested endpoint
        ResponseEntity<BuildingVM> response = testRestTemplate.getForEntity("/location/buildings/" + building.getId(), BuildingVM.class);
        // check result
        assertNotNull(response.getBody());
        // check status code
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    /**
     * tests endpoint for saving buildings
     */
    @Test
    public void testSaveBuilding() {
        User user = testUtils.createValidUser();
        String password = user.getPassword();
        user = prepareUserWithInstitution(user);
        // prepare building
        Building building = testUtils.createValidBuilding();

        // authenticate
        testUtils.authenticate(user, password, testRestTemplate);

        // call tested endpoint
        ResponseEntity<Object> response = testRestTemplate.postForEntity("/location/buildings", building, Object.class);
        // check building saved
        assertEquals(1, buildingRepository.count());
        // check status code
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    /**
     * tests endpoint for getting all rooms depending on building id
     */
    @Test
    public void testGetAllRooms() {
        User user = prepareUserWithInstitution(testUtils.createValidUser());
        long buildingId = prepareBuilding(user);
        // save room
        Room room = testUtils.createValidRoom();
        locationService.saveRoom(room, buildingId, user);

        // call tested endpoint
        ResponseEntity<List> response = testRestTemplate.getForEntity("/location/rooms/all/" + buildingId, List.class);
        // check rooms count
        assertEquals(1, response.getBody().size());
        // check status code
        assertEquals(HttpStatus.OK, response.getStatusCode());
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
     * tests endpoint for getting room
     */
    @Test
    public void testGetRoom() {
        User user = testUtils.createValidUser();
        String password = user.getPassword();
        user = prepareUserWithInstitution(user);
        long buildingId = prepareBuilding(user);
        // prepare room
        Room room = testUtils.createValidRoom();
        // save room
        locationService.saveRoom(room, buildingId, user);

        // authenticate
        testUtils.authenticate(user, password, testRestTemplate);

        // call tested endpoint
        ResponseEntity<RoomVM> response = testRestTemplate.getForEntity("/location/rooms/" + room.getId(), RoomVM.class);
        // check result
        assertNotNull(response.getBody());
        // check status code
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    /**
     * tests endpoint for saving rooms
     */
    @Test
    public void testSaveRoom() {
        User user = testUtils.createValidUser();
        String password = user.getPassword();
        user = prepareUserWithInstitution(user);
        long buildingId = prepareBuilding(user);
        // prepare room
        Room room = testUtils.createValidRoom();

        // authenticate
        testUtils.authenticate(user, password, testRestTemplate);

        // call tested endpoint
        ResponseEntity<Object> response = testRestTemplate.postForEntity("/location/rooms/" + buildingId, room, Object.class);
        // check room saved
        assertEquals(1, roomRepository.count());
        // check status code
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }

    /**
     * tests endpoint for getting all showcases depending on room id
     */
    @Test
    public void testGetAllShowcases() {
        User user = prepareUserWithInstitution(testUtils.createValidUser());
        long roomId = prepareRoom(user);
        // save showcase
        Showcase showcase = testUtils.createValidShowcase();
        locationService.saveShowcase(showcase, roomId, user);

        // call tested endpoint
        ResponseEntity<List> response = testRestTemplate.getForEntity("/location/showcases/all/" + roomId, List.class);
        // check showcases count
        assertEquals(1, response.getBody().size());
        // check status code
        assertEquals(HttpStatus.OK, response.getStatusCode());
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
     * tests endpoint for getting showcase
     */
    @Test
    public void testGetShowcase() {
        User user = testUtils.createValidUser();
        String password = user.getPassword();
        user = prepareUserWithInstitution(user);
        long roomId = prepareRoom(user);
        // prepare showcase
        Showcase showcase = testUtils.createValidShowcase();
        // save showcase
        locationService.saveShowcase(showcase, roomId, user);

        // authenticate
        testUtils.authenticate(user, password, testRestTemplate);

        // call tested endpoint
        ResponseEntity<ShowcaseVM> response = testRestTemplate.getForEntity("/location/showcases/" + showcase.getId(), ShowcaseVM.class);
        // check result
        assertNotNull(response.getBody());
        // check status code
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    /**
     * tests endpoint for saving showcases
     */
    @Test
    public void testSaveShowcase() {
        User user = testUtils.createValidUser();
        String password = user.getPassword();
        user = prepareUserWithInstitution(user);
        long roomId = prepareRoom(user);
        // prepare showcase
        Showcase showcase = testUtils.createValidShowcase();

        // authenticate
        testUtils.authenticate(user, password, testRestTemplate);

        // call tested endpoint
        ResponseEntity<Object> response = testRestTemplate.postForEntity("/location/showcases/" + roomId, showcase, Object.class);
        // check showcase saved
        assertEquals(1, showcaseRepository.count());
        // check status code
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
    }
}
