package com.example.test;

import com.example.test.dto.CustomerDTO;
import com.example.test.dto.OrderDTO;
import com.example.test.models.Customer;
import com.example.test.models.Order;
import lombok.NoArgsConstructor;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.junit4.SpringRunner;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@NoArgsConstructor
public class TestApplicationTests {

	@Autowired
	private TestRestTemplate testRestTemplate;

	@LocalServerPort
	int randomServerPort;

	@Test
	public void createCustomer() throws Exception {
		CustomerDTO customer=new CustomerDTO("first","last");
		HttpEntity<CustomerDTO> request=new HttpEntity<>(customer);
		ResponseEntity<String> result = testRestTemplate.postForEntity("http://localhost:"+randomServerPort+"/customer",request, String.class);
		Assert.assertEquals(result.getStatusCodeValue(),HttpStatus.OK.value());
		Assert.assertTrue(result.getBody().matches("[0-9]*"));
	}

	@Test
	public void updateCustomerInformation() throws Exception {
		Integer customerId= createCustomer("first","last");
		Customer updateCustomer= new Customer("testFirstName", "testLastName");

		HttpHeaders headers=new HttpHeaders();
		headers.add(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON.getType());
		HttpEntity<Customer> customerUpdate=new HttpEntity<>(updateCustomer, headers);
		testRestTemplate.put("http://localhost:"+randomServerPort+"/customer/"+customerId,customerUpdate);

		ResponseEntity<Customer> getCustomerDetails=testRestTemplate.getForEntity("http://localhost:"+randomServerPort+"/customer/"+customerId,Customer.class);
		Assert.assertEquals(getCustomerDetails.getStatusCodeValue(), HttpStatus.OK.value());
		Assert.assertEquals(getCustomerDetails.getBody().getId(),customerId);
		Assert.assertEquals(getCustomerDetails.getBody().getFirstName(), updateCustomer.getFirstName());
		Assert.assertEquals(getCustomerDetails.getBody().getLastName(), updateCustomer.getLastName());
	}

	@Test
	public void createCustomerOrder() throws Exception {
		Integer customerId= createCustomer("first","last");

		OrderDTO order=new OrderDTO("test address",120.00);
		HttpEntity<OrderDTO> orderHttpEntity=new HttpEntity<>(order);
		ResponseEntity<Integer> result = testRestTemplate.postForEntity("http://localhost:"+randomServerPort+"/customer/"+customerId+"/order",orderHttpEntity, Integer.class);
		Assert.assertEquals(result.getStatusCodeValue(), HttpStatus.OK.value());
		Assert.assertTrue(result.getBody().toString().matches("[0-9]*"));
	}


	@Test
	public void getAllCustomerOrders() throws Exception {
		Integer customerId= createCustomer("first","last");
		List<Integer> orders= new ArrayList<>();
		orders.add(createOrder(customerId,"1100 street dr, city, state - 100001",110.00));
		orders.add(createOrder(customerId,"1101 street dr, city, state - 100001",110.00));
		orders.add(createOrder(customerId,"1102 street dr, city, state - 100001",110.00));
		orders.add(createOrder(customerId,"1103 street dr, city, state - 100001",110.00));
		orders.add(createOrder(customerId,"1104 street dr, city, state - 100001",110.00));
		orders.add(createOrder(customerId,"1105 street dr, city, state - 100001",110.00));

		ResponseEntity<List> getOrders=testRestTemplate.getForEntity("http://localhost:"+randomServerPort+"/customer/"+customerId+"/orders",List.class);
		Assert.assertEquals(getOrders.getStatusCodeValue(), HttpStatus.OK.value());
		Assert.assertEquals(getOrders.getBody(),orders);
	}

	@Test
	public void getOrderDetails() throws Exception {
		String firstname="first";
		String lastname="last";
		Integer customerId= createCustomer(firstname,lastname);
		String address="1100 street dr, city, state - 100001";
		Double total=110.00;
		Integer orderId=createOrder(customerId,address,total);

		ResponseEntity<Order> getOrders=testRestTemplate.getForEntity("http://localhost:"+randomServerPort+"/order/"+orderId,Order.class);
		Assert.assertEquals(getOrders.getStatusCodeValue(), HttpStatus.OK.value());
		Assert.assertEquals(getOrders.getBody().getId(),orderId);
		Assert.assertEquals(getOrders.getBody().getAddress(),address);
		Assert.assertEquals(getOrders.getBody().getTotalPrice(),total);
		Assert.assertEquals(getOrders.getBody().getCustomer().getId(),customerId);
		Assert.assertEquals(getOrders.getBody().getCustomer().getFirstName(),firstname);
		Assert.assertEquals(getOrders.getBody().getCustomer().getLastName(),lastname);
	}

	private Integer createCustomer(String firstname,String lastname){
		CustomerDTO customer=new CustomerDTO(firstname,lastname);
		HttpEntity<CustomerDTO> request=new HttpEntity<>(customer);
		return testRestTemplate.postForEntity("http://localhost:"+randomServerPort+"/customer",request, Integer.class).getBody();
	}

	private Integer createOrder(Integer customerId, String address, Double price){
		OrderDTO order=new OrderDTO(address,price);
		HttpEntity<OrderDTO> orderHttpEntity=new HttpEntity<>(order);
		return testRestTemplate.postForEntity("http://localhost:"+randomServerPort+"/customer/"+customerId+"/order",orderHttpEntity, Integer.class).getBody();
	}
}
