package com.example.demo.entity;

import jakarta.persistence.*;

@Entity
@Table(name = "packages")
public class Package {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String description;
    private Double weightKg;
    private Double lengthCm;
    private Double widthCm;
    private Double heightCm;
    private String category; 

    public Package() {}

    public Package(Long id, String description, Double weightKg,
                   Double lengthCm, Double widthCm, Double heightCm, String category) {
        this.id = id;
        this.description = description;
        this.weightKg = weightKg;
        this.lengthCm = lengthCm;
        this.widthCm = widthCm;
        this.heightCm = heightCm;
        this.category = category;
    }

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Double getWeightKg() {
		return weightKg;
	}

	public void setWeightKg(Double weightKg) {
		this.weightKg = weightKg;
	}

	public Double getLengthCm() {
		return lengthCm;
	}

	public void setLengthCm(Double lengthCm) {
		this.lengthCm = lengthCm;
	}

	public Double getWidthCm() {
		return widthCm;
	}

	public void setWidthCm(Double widthCm) {
		this.widthCm = widthCm;
	}

	public Double getHeightCm() {
		return heightCm;
	}

	public void setHeightCm(Double heightCm) {
		this.heightCm = heightCm;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

    
}