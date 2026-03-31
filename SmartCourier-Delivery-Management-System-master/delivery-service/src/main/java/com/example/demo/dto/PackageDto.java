package com.example.demo.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class PackageDto {

    @NotBlank
    private String description;

    @NotNull
    private Double weightKg;

    @NotNull
    private Double lengthCm;

    @NotNull
    private Double widthCm;

    @NotNull
    private Double heightCm;

    @NotBlank
    private String category;

    public PackageDto() {}

    public PackageDto(String description, Double weightKg, Double lengthCm,
                      Double widthCm, Double heightCm, String category) {
        this.description = description;
        this.weightKg = weightKg;
        this.lengthCm = lengthCm;
        this.widthCm = widthCm;
        this.heightCm = heightCm;
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public Double getWeightKg() {
        return weightKg;
    }

    public Double getLengthCm() {
        return lengthCm;
    }

    public Double getWidthCm() {
        return widthCm;
    }

    public Double getHeightCm() {
        return heightCm;
    }

    public String getCategory() {
        return category;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setWeightKg(Double weightKg) {
        this.weightKg = weightKg;
    }

    public void setLengthCm(Double lengthCm) {
        this.lengthCm = lengthCm;
    }

    public void setWidthCm(Double widthCm) {
        this.widthCm = widthCm;
    }

    public void setHeightCm(Double heightCm) {
        this.heightCm = heightCm;
    }

    public void setCategory(String category) {
        this.category = category;
    }
}