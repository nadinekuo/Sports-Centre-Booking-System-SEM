package sportfacilities.entities;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SportTest {

    private final transient Sport soccer;
    private final transient Sport hockey;
    private final transient Sport volleyball;
    private final transient Sport tennis;
    private final transient Sport yoga;
    private final transient Sport zumba;
    private final transient Sport kickboxing;

    private final transient SportRoom hallX1;
    private final transient SportRoom hallX2;
    private final transient SportRoom hallX3;
    private final transient SportRoom hockeyField;

    public SportTest() {
        soccer = new Sport("soccer", true, 6, 11);
        hockey = new Sport("hockey", true, 7, 14);
        volleyball = new Sport("volleyball", true, 4, 12);
        tennis = new Sport("tennis", true, 4, 13);
        yoga = new Sport("yoga", false, 1, -1);
        zumba = new Sport("zumba", false, 1, -1);
        kickboxing = new Sport("kickbox", false, 1, -1);

        hallX1 = new SportRoom("X1", List.of(soccer, hockey), 10, 50);
        hallX2 = new SportRoom("X2", List.of(hockey, volleyball, tennis, zumba), 15, 60);
        hallX3 = new SportRoom("X3", List.of(yoga, zumba, kickboxing), 1, 55);
        hockeyField = new SportRoom("hockeyfieldA", List.of(hockey), 10, 200);

    }

    @Test
    void getSportName() {
        assertThat(soccer.getSportName()).isEqualTo("soccer");
        assertThat(zumba.getSportName()).isEqualTo("zumba");
    }

    @Test
    void setSportName() {
        soccer.setSportName("soccer1");
        assertThat(soccer.getSportName()).isEqualTo("soccer1");
    }

    @Test
    void isTeamSport() {
        assertThat(soccer.isTeamSport()).isTrue();
        assertThat(yoga.isTeamSport()).isFalse();
    }

    @Test
    void setTeamSport() {
        yoga.setTeamSport(true);
        assertThat(yoga.isTeamSport()).isTrue();
    }

    @Test
    void getMinTeamSize() {
        assertThat(soccer.getMinTeamSize()).isEqualTo(6);
    }

    @Test
    void setMinTeamSize() {
        hockey.setMinTeamSize(10);
        assertThat(hockey.getMinTeamSize()).isEqualTo(10);
    }

    @Test
    void getMaxTeamSize() {
        assertThat(tennis.getMaxTeamSize()).isEqualTo(13);
    }

    @Test
    void setMaxTeamSize() {
        volleyball.setMaxTeamSize(15);
        System.out.println(volleyball.toString());
        assertThat(volleyball.getMaxTeamSize()).isEqualTo(15);
    }

    @Test
    void toStringTest() {
        String expected = "volleyball";
        assertThat(volleyball.toString()).isEqualTo(expected);
    }

//    @Test
//    void getSportLocations() {
//    }
//
//    @Test
//    void setSportLocations() {
//    }
//
//    @Test
//    void getEquipmentList() {
//    }
//
//    @Test
//    void setEquipmentList() {
//    }
}