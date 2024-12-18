package com.iss.eventorium.solution.models;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.hibernate.annotations.SQLDelete;

@Entity
@Table (name = "products")
@SQLDelete(sql = "UPDATE products SET is_deleted = true WHERE id = ?")
public class Product extends Solution {

    @Override
    public void restore(Memento memento) {
        setName(memento.getName());
        setPrice(memento.getPrice());
        setDescription(memento.getDescription());
        setDiscount(memento.getDiscount());
        setEventTypes(memento.getEventTypes());
        setValidFrom(memento.getValidFrom());
        setIsVisible(memento.isVisible());
        setIsAvailable(memento.isAvailable());
    }
}
