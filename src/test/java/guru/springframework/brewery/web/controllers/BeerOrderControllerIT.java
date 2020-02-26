package guru.springframework.brewery.web.controllers;

import guru.springframework.brewery.domain.Customer;
import guru.springframework.brewery.repositories.CustomerRepository;
import guru.springframework.brewery.web.model.BeerOrderPagedList;
import guru.springframework.brewery.web.model.BeerPagedList;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class BeerOrderControllerIT {
    @Autowired
    private TestRestTemplate restTemplate;

    @Autowired
    private CustomerRepository customerRepository;

    @Test
    void testListOrders() {

        List<Customer> customerList = customerRepository.findAll();


        BeerOrderPagedList pagedList = restTemplate.getForObject("/api/v1/customers/{customerId}/orders"
                , BeerOrderPagedList.class
                ,customerList.get(0).getId());

        pagedList.forEach(item -> System.out.println(item.getId()
                                    + " " + item.getOrderStatus()));

        assertThat(pagedList.getContent()).hasSize(1);

    }
}
