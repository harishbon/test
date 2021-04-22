package com.example.test.repositories;

import com.example.test.models.Order;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface OrderRepository extends CrudRepository<Order, Integer> {

    @Query( value = "select * from orders where customer_id= ?1", nativeQuery = true)
    public List<Order> findCustomerOrders(Integer id);

}
