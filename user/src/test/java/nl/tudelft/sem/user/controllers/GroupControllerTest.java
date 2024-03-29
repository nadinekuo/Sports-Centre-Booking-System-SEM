package nl.tudelft.sem.user.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.get;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.post;
import static org.springframework.restdocs.mockmvc.RestDocumentationRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import java.util.NoSuchElementException;
import nl.tudelft.sem.user.entities.Customer;
import nl.tudelft.sem.user.entities.Group;
import nl.tudelft.sem.user.services.GroupService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.client.RestTemplate;

@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
public class GroupControllerTest {

    private final transient long groupId = 15L;

    @Mock
    transient GroupService groupService;

    @Mock
    transient RestTemplate restTemplate;
    @Autowired
    private transient MockMvc mockMvc;

    /**
     * Sets up the tests.
     */
    @BeforeEach
    public void setup() {

        when(groupService.restTemplate()).thenReturn(restTemplate);
        this.mockMvc = MockMvcBuilders.standaloneSetup(new GroupController(groupService)).build();
    }

    @Test
    public void getGroupSizeTest() throws Exception {
        MvcResult result = mockMvc.perform(get("/group/{groupId}/getGroupSize", groupId))
            .andExpect(status().isOk()).andDo(MockMvcResultHandlers.print()).andReturn();
        verify(groupService).getGroupSizeById(groupId);
        assertNotNull(result.getResponse().getContentAsString());
    }

    @Test
    public void getGroupSizeInvalidTest() throws Exception {
        when(groupService.getGroupSizeById(groupId)).thenThrow(NoSuchElementException.class);
        mockMvc.perform(get("/group/{groupId}/getGroupSize", groupId))
            .andExpect(status().isBadRequest()).andDo(MockMvcResultHandlers.print());
        verify(groupService).getGroupSizeById(groupId);
    }

    @Test
    void getGroupByIdTest() throws Exception {
        MvcResult result = mockMvc.perform(get("/group/{id}/", groupId)).andExpect(status().isOk())
            .andDo(MockMvcResultHandlers.print()).andReturn();
        verify(groupService).getGroupById(groupId);
        assertNotNull(result.getResponse());
    }

    @Test
    void getGroupByIdThrowsExceptionTest() throws Exception {
        when(groupService.getGroupById(groupId)).thenThrow(NoSuchElementException.class);
        mockMvc.perform(get("/group/{id}/", groupId)).andExpect(status().isBadRequest())
            .andDo(MockMvcResultHandlers.print());
        verify(groupService).getGroupById(groupId);
    }

    @Test
    void getGroupByGroupNameTest() throws Exception {
        MvcResult result = mockMvc.perform(get("/group/groupName/{groupName}/", "basketball"))
            .andExpect(status().isOk()).andDo(MockMvcResultHandlers.print()).andReturn();
        verify(groupService).getGroupByGroupName("basketball");

        assertNotNull(result.getResponse());
    }

    @Test
    void getGroupByGroupNameThrowsExceptionTest() throws Exception {
        when(groupService.getGroupByGroupName("basketball")).thenThrow(
            NoSuchElementException.class);
        mockMvc.perform(get("/group/groupName/{groupName}/", "basketball"))
            .andExpect(status().isBadRequest()).andDo(MockMvcResultHandlers.print());
        verify(groupService).getGroupByGroupName("basketball");
    }

    @Test
    void getUsersInGroupTest() throws Exception {
        MvcResult result =
            mockMvc.perform(get("/group/getCustomers/{id}/", groupId)).andExpect(status().isOk())
                .andDo(MockMvcResultHandlers.print()).andReturn();
        verify(groupService).getUsersInaGroup(groupId);

        assertNotNull(result.getResponse());
    }

    @Test
    void getUsersInGroupThrowsExceptionTest() throws Exception {
        when(groupService.getUsersInaGroup(groupId)).thenThrow(NoSuchElementException.class);
        mockMvc.perform(get("/group/getCustomers/{id}/", groupId))
            .andExpect(status().isBadRequest()).andDo(MockMvcResultHandlers.print());
        verify(groupService).getUsersInaGroup(groupId);
    }

    @Test
    void createGroupValidTest() throws Exception {
        when(groupService.createGroup("basketball")).thenReturn(true);
        MvcResult result = mockMvc.perform(post("/group/create/{groupName}/", "basketball"))
            .andExpect(status().isOk()).andDo(MockMvcResultHandlers.print()).andReturn();

        assertThat(result.getResponse().getContentAsString()).isEqualTo(
            "Group created successfully");
    }

    @Test
    void createGroupInvalidTest() throws Exception {
        when(groupService.createGroup("basketball")).thenThrow(IllegalArgumentException.class);
        mockMvc.perform(post("/group/create/{groupName}/", "basketball"))
            .andExpect(status().isBadRequest()).andDo(MockMvcResultHandlers.print());
    }

    @Test
    void addCustomerToGroupTest() throws Exception {
        MvcResult result =
            mockMvc.perform(put("/group/addCustomer/{groupId}/{customerId}", groupId, 1L))
                .andExpect(status().isOk()).andDo(MockMvcResultHandlers.print()).andReturn();
        verify(groupService).addCustomerToGroup(1L, groupId);
        assertThat(result.getResponse().getContentAsString()).isEqualTo(
            "Customer added successfully to the group!");
    }

    @Test
    void addCustomerToGroupThrowsExceptionTest() throws Exception {
        when(groupService.addCustomerToGroup(1L, groupId)).thenThrow(NoSuchElementException.class);
        mockMvc.perform(put("/group/addCustomer/{groupId}/{customerId}", groupId, 1L))
            .andExpect(status().isBadRequest()).andDo(MockMvcResultHandlers.print());
        verify(groupService).addCustomerToGroup(1L, groupId);
    }

    @Test
    void makeValidGroupReservationTest() throws Exception {

        final String url =
            "http://eureka-reservation/reservation" + "/" + 0L + "/" + groupId + "/" + 2 + "/"
                + "2099-01-06T21:00:00" + "/" + false + "/" + "makeSportRoomBooking";

        List<Customer> customers = List.of(new Customer("arslan123", "password1", false),
            new Customer("emil123", "password2", false),
            new Customer("emma123", "password3", false));

        Group group = new Group("basketball", customers);
        group.setGroupId(groupId);
        when(groupService.getUsersInaGroup(groupId)).thenReturn(customers);
        MvcResult result = mockMvc.perform(
                post("/group/reservation/{groupId}/{sportRoomId}/{date}" + "/makeSportRoomBooking",
                    groupId, 2L, "2099-01-06T21:00:00")).andExpect(status().isOk())
            .andDo(MockMvcResultHandlers.print()).andReturn();

        verify(restTemplate, times(customers.size())).exchange(eq(url), eq(HttpMethod.POST),
            any(HttpEntity.class), eq(String.class));

        assertThat(result.getResponse().getContentAsString()).isEqualTo(
            "Group Reservation Successful");
    }
}
