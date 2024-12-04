package com.iss.eventorium.solution.models;

import com.iss.eventorium.shared.models.Solution;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table (name = "products")
public class Product extends Solution {  }
