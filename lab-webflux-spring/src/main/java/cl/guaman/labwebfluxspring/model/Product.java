package cl.guaman.labwebfluxspring.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
@ToString
@AllArgsConstructor
public class Product {

    @Id
    private String id;
    private String name;
    private double price;
}
