package guru.springframework.brewery.web.controllers;

import guru.springframework.brewery.services.BeerOrderService;
import guru.springframework.brewery.web.model.BeerOrderDto;
import guru.springframework.brewery.web.model.BeerOrderLineDto;
import guru.springframework.brewery.web.model.BeerOrderPagedList;
import guru.springframework.brewery.web.model.OrderStatusEnum;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.jaxb.SpringDataJaxb;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.core.Is.is;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.reset;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(BeerOrderController.class)
class BeerOrderControllerTest {

    @MockBean
    BeerOrderService beerOrderService;

    @Autowired
    MockMvc mockMvc;

    BeerOrderDto validOrder;

    @BeforeEach
    void setUp() {
        validOrder = BeerOrderDto.builder().id(UUID.randomUUID())
                .version(1)
                .orderStatus(OrderStatusEnum.NEW)
                .beerOrderLines(new ArrayList<BeerOrderLineDto>())
                .createdDate(OffsetDateTime.now())
                .lastModifiedDate(OffsetDateTime.now())
                .customerId(UUID.randomUUID())
                .build();
    }

    @AfterEach
    void tearDown() {
        reset(beerOrderService);
    }

    @Test
    void testGetOrder() throws Exception {
        given(beerOrderService.getOrderById(any(),any())).willReturn(validOrder);

        mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/customers/{customerId}/orders/{orderId}",validOrder.getCustomerId()
                ,validOrder.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id",is(validOrder.getId().toString())));
    }



    @DisplayName("List -")
    @Nested
    public class TestListOperations {

        @Captor
        ArgumentCaptor<UUID> uuidArgumentCaptor;

        @Captor
        ArgumentCaptor<Pageable> pageableArgumentCaptor;

        BeerOrderPagedList beerOrderPagedList;

        @BeforeEach
        void setUp() {

            List<BeerOrderDto> orders = new ArrayList<>();
            orders.add(validOrder);
            orders.add(BeerOrderDto.builder().id(UUID.randomUUID())
                        .orderStatus(OrderStatusEnum.NEW)
                        .customerId(validOrder.getCustomerId())
                        .build());


            beerOrderPagedList = new BeerOrderPagedList(orders, PageRequest.of(1,1),2L);

            given(beerOrderService.listOrders(uuidArgumentCaptor.capture(),pageableArgumentCaptor.capture()))
                    .willReturn(beerOrderPagedList);
        }

        @Test
        void listOrders() throws Exception {
            mockMvc.perform(MockMvcRequestBuilders.get("/api/v1/customers/{customerId}/orders",validOrder.getCustomerId()))
                    .andExpect(status().isOk())
                    .andExpect(content().contentType(MediaType.APPLICATION_JSON_UTF8))
                    .andExpect(jsonPath("$.content",hasSize(2)));
        }
    }


}