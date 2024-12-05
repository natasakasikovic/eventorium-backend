package com.iss.eventorium.solution.models;

import com.iss.eventorium.shared.models.Solution;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import org.hibernate.annotations.SQLDelete;

@Entity
@Table (name = "products")
@SQLDelete(sql = "UPDATE products SET is_deleted = true WHERE id = ?")
public class Product extends Solution {  }
