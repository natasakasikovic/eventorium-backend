package com.iss.eventorium.solution.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.hibernate.annotations.Filter;
import org.hibernate.annotations.SQLDelete;

@Getter
@Setter
@SuperBuilder
@AllArgsConstructor
@Entity
@Table (name = "products")
@SQLDelete(sql = "UPDATE products SET is_deleted = true WHERE id = ?")
@Filter(name = "activeFilter", condition = "is_deleted = :isDeleted")
public class Product extends Solution {

    @Override
    public void restore(Memento memento) {
        setName(memento.getName());
        setPrice(memento.getPrice());
        setDiscount(memento.getDiscount());
    }
}
