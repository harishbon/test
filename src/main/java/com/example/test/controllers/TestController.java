package com.example.test.controllers;

import com.example.test.dto.CustomerDTO;
import com.example.test.dto.OrderDTO;
import com.example.test.models.Customer;
import com.example.test.models.Order;
import com.example.test.repositories.CustomerRepository;
import com.example.test.repositories.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
public class TestController {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private OrderRepository orderRepository;

    /**
     * This endpoint will create customer
     * @param customer customer details
     * @return customer id in response
     */
    @PostMapping(path = "customer")
    public ResponseEntity<Integer> createCustomer(@RequestBody CustomerDTO customer){
        return Optional
                .ofNullable( customerRepository.save(new Customer(customer.getFirstName(),customer.getLastName())) )
                .map( c -> ResponseEntity.ok(c.getId()))
                .orElse( ResponseEntity.badRequest().build() );
    }

    /**
     * This endpoint will fetch customer information
     * @param id customer identification number
     * @return customer details
     */
    @GetMapping(path = "customer/{id}")
    public ResponseEntity<Customer> getCustomerInfo(@PathVariable Integer id){
        return customerRepository.findById(id)
                .map( c -> ResponseEntity.ok(c))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * This endpoint will update customer information
     * @param id customer identification number
     * @param customer customer details to update
     * @return successful response will be sent if the customer is found and updated
     */
    @PutMapping(path = "customer/{id}")
    public ResponseEntity updateCustomerInfo(@PathVariable Integer id, @RequestBody CustomerDTO customer){
        return customerRepository.findById(id)
                .map(c -> Optional.ofNullable( customerRepository.save(new Customer(id,customer.getFirstName(),customer.getLastName())) )
                        .map( c1 -> ResponseEntity.ok().build())
                        .orElse( ResponseEntity.badRequest().build() ))
                .orElse(ResponseEntity.notFound().build());

    }

    /**
     * This endpoint will create an order for a customer
     * @param id customer identification
     * @param order order details
     * @return order id will be returned on success, if customer exists
     */
    @PostMapping(path = "customer/{id}/order")
    public ResponseEntity<Integer> createOrder(@PathVariable Integer id, @RequestBody OrderDTO order){
        return customerRepository.findById(id)
                .map( customer -> {
                    return Optional.ofNullable(orderRepository.save(new Order(customer, order.getAddress(),order.getTotalPrice())))
                            .map(o -> ResponseEntity.ok(o.getId()))
                            .orElse(ResponseEntity.badRequest().build());
                })
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * This endpoint will fetch all orders a customer has placed
     * @param id customer identification number
     * @return a list of order ids that were placed by a customer
     */
    @GetMapping(path = "customer/{id}/orders")
    public ResponseEntity<List<Integer>> getCustomerOrders(@PathVariable Integer id){
        return customerRepository.findById(id)
                .map( customer -> Optional.ofNullable(orderRepository.findCustomerOrders(customer.getId()))
                            .map( orders -> {
                                List<Integer> s_orders=orders.stream().map( order -> order.getId()).collect(Collectors.toList());
                                return ResponseEntity.ok(s_orders);
                            })
                            .orElse(ResponseEntity.ok().build()))
                .orElse(ResponseEntity.notFound().build());
    }

    /**
     * This endpoint will fetch all orders details and customer associated on an order
     * @param id order identification number
     * @return if order exists, order details along with customer details will be returned
     */
    @GetMapping(path = "order/{id}")
    public ResponseEntity<Order> getOrderInfo(@PathVariable Integer id){
        return orderRepository.findById(id)
                .map( order -> ResponseEntity.ok(order))
                .orElse(ResponseEntity.notFound().build());
    }

}
