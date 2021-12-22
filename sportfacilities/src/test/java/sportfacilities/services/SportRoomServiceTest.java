package sportfacilities.services;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;
import sportfacilities.entities.Sport;
import sportfacilities.entities.SportRoom;
import sportfacilities.repositories.SportRoomRepository;

/**
 * The type Sport room service test.
 */
@ExtendWith(MockitoExtension.class)
public class SportRoomServiceTest {


    @Mock
    private transient SportRoomRepository sportRoomRepository;

    private transient SportRoomService sportRoomService;

    private transient Sport soccer;
    private transient Sport hockey;
    private transient Sport volleyball;
    private transient Sport yoga;
    private transient Sport zumba;
    private transient Sport kickboxing;

    private transient SportRoom hallX1;
    private transient SportRoom hallX2;
    private transient SportRoom hallX3;

    private transient SportRoom hockeyField;

    /**
     * Sets .
     */
    @BeforeEach
    void setup() {
        sportRoomService = new SportRoomService(sportRoomRepository);
    }

    /**
     * Instantiates a new Sport room service test.
     */
    public SportRoomServiceTest() {
        soccer = new Sport("soccer", 6, 11);
        hockey = new Sport("hockey", 7, 14);
        volleyball = new Sport("volleyball", 4, 12);
        yoga = new Sport("yoga");
        zumba = new Sport("zumba");
        kickboxing = new Sport("kickbox");

        hallX1 = new SportRoom(34L, "X1", List.of(soccer, hockey), 10, 50);
        hallX2 = new SportRoom(84L, "X2", List.of(hockey, volleyball, zumba), 15, 60);
        hallX3 = new SportRoom(38L, "X3", List.of(yoga, zumba, kickboxing), 12, 55);
        hockeyField = new SportRoom(42L, "hockeyfieldA", List.of(hockey), 10, 200);
    }

    /**
     * Test constructor.
     */
    @Test
    public void testConstructor() {
        assertNotNull(sportRoomService);
    }

    /**
     * Gets sports room.
     */
    @Test
    public void getSportsRoom() {

        when(sportRoomRepository.findBySportRoomId(34L)).thenReturn(Optional.of(hallX1));

        SportRoom result = sportRoomService.getSportRoom(34L);

        assertThat(result).isNotNull();
        assertThat(result.getSportRoomId()).isEqualTo(34L);
        assertThat(result.getIsSportsHall()).isTrue();
        assertThat(result.getSportRoomName()).isEqualTo("X1");
        verify(sportRoomRepository, times(1)).findBySportRoomId(34L);
    }

    /**
     * Gets non existing sports room.
     */
    @Test
    public void getNonExistingSportsRoom() {

        when(sportRoomRepository.findBySportRoomId(34L)).thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class, () -> {
            sportRoomService.getSportRoom(34L);
        });
    }

    /**
     * Sports room exists.
     */
    @Test
    public void sportsRoomExists() {

        when(sportRoomRepository.findBySportRoomId(84L)).thenReturn(Optional.of(hallX2));

        assertThat(sportRoomService.sportRoomExists(84L)).isTrue();
    }

    /**
     * Sports room does not exist.
     */
    @Test
    public void sportsRoomDoesNotExist() {
        when(sportRoomRepository.findBySportRoomId(84L)).thenReturn(Optional.empty());

        assertThat(sportRoomService.sportRoomExists(84L)).isFalse();
    }

    /**
     * Rest template test.
     */
    @Test
    public void restTemplateTest() {
        RestTemplate restTemplate = sportRoomService.restTemplate();
        assertNotNull(restTemplate);
    }

}
