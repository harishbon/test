package com.example.test.models;

import lombok.*;

import javax.persistence.*;

@Entity
@Table(name = "orders")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "customer_id")
    private Customer customer;

    @Column(name = "address", nullable = false)
    private String address;

    @Column(name = "totalprice", nullable = false)
    private Double totalPrice;

    public Order(Customer customer, String address,Double totalPrice){
        this.address=address;
        this.totalPrice=totalPrice;
        this.customer=customer;
    }
}
