package ru.mtuci.antivirus.entities;


import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "products")
@JsonIgnoreProperties({"licenses"})
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "is_blocked")
    private boolean isBlocked;

    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<License> licenses;

    public Product(String name, boolean isBlocked, List<License> licenses) {
        this.name = name;
        this.isBlocked = isBlocked;
        this.licenses = licenses;
    }

    public Product() {

    }

    public String getBody(){
        return String.format(
                "Product:\n"
                        + "id: %d\n"
                        + "name: %s\n"
                        + "isBlocked: %b\n"
                        + "licenses: %s\n",
                id, name, isBlocked, licenses
        );
    }

}
