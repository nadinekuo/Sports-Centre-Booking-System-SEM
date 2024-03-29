package nl.tudelft.sem.sportfacilities.controllers;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.NoSuchElementException;
import nl.tudelft.sem.sportfacilities.entities.Sport;
import nl.tudelft.sem.sportfacilities.services.SportService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
public class SportControllerTest {

    @Mock
    transient SportService sportService;
    transient Sport soloSport;
    transient Sport teamSport;
    transient String sportName = "Box";
    @Autowired
    private transient MockMvc mockMvc;

    /**
     * Sets up the tests.
     */
    @BeforeEach
    public void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(new SportController(sportService)).build();

        teamSport = new Sport("hockey", 5, 10);
        soloSport = new Sport("bowling");
    }

    @Test
    public void getSportMaxTeamSizeTest() throws Exception {

        given(sportService.getSportById(anyString())).willReturn(teamSport);

        mockMvc.perform(get("/sport/{sportName}/getMaxTeamSize", "bowling"))
            .andExpect(status().isOk()).andDo(MockMvcResultHandlers.print()).andReturn();

        verify(sportService).getSportById("bowling");
    }

    @Test
    public void invalidSportNameGetMaxTeamSizeTest() throws Exception {
        doThrow(NoSuchElementException.class).when(sportService).getSportById(anyString());

        mockMvc.perform(get("/sport/{sportName}/getMaxTeamSize", "bowling"))
            .andExpect(status().isBadRequest());
    }

    @Test
    public void getSportMinTeamSizeTest() throws Exception {

        given(sportService.getSportById(anyString())).willReturn(teamSport);

        mockMvc.perform(get("/sport/{sportName}/getMinTeamSize", "bowling"))
            .andExpect(status().isOk()).andDo(MockMvcResultHandlers.print()).andReturn();

        verify(sportService).getSportById("bowling");
    }

    @Test
    public void invalidSportNameGetMinTeamSizeTest() throws Exception {
        doThrow(NoSuchElementException.class).when(sportService).getSportById(anyString());

        mockMvc.perform(get("/sport/{sportName}/getMinTeamSize", "bowling"))
            .andExpect(status().isBadRequest());
    }

    /**
     * Add non team sport test.
     *
     * @throws Exception the exception
     */
    @Test
    public void addNonTeamSportTest() throws Exception {
        mockMvc.perform(put("/sport/{sportName}/addNonTeamSport/admin", sportName))
            .andExpect(status().isOk()).andDo(MockMvcResultHandlers.print()).andReturn();

        verify(sportService).addSport(new Sport(sportName));
    }

    /**
     * Add team sport test.
     *
     * @throws Exception the exception
     */
    @Test
    public void addTeamSportTest() throws Exception {
        //when(sportService.addSport(soloSport));

        mockMvc.perform(
            put("/sport/{sportName}/{minCapacity}/{maxCapacity}/addTeamSport/admin", sportName, 5,
                10)).andExpect(status().isOk()).andDo(MockMvcResultHandlers.print()).andReturn();

        verify(sportService).addSport(new Sport(sportName, 5, 10));
    }

    /**
     * Delete sport test.
     *
     * @throws Exception the exception
     */
    @Test
    public void deleteSportTest() throws Exception {
        mockMvc.perform(delete("/sport/{sportName}/deleteSport/admin", sportName))
            .andExpect(status().isOk()).andDo(MockMvcResultHandlers.print());
        verify(sportService).deleteSport(sportName);

    }

    /**
     * Delete sport with invalid name test.
     *
     * @throws Exception the exception
     */
    @Test
    public void deleteSportWithInvalidNameTest() throws Exception {
        doThrow(NoSuchElementException.class).when(sportService).deleteSport("Box");

        mockMvc.perform(delete("/sport/{sportName}/deleteSport/admin", "Box"))
            .andExpect(status().isBadRequest());
    }

}
