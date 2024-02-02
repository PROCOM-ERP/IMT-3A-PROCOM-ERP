package com.example.inventoryservice.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.Hibernate;

import java.io.Serializable;
import java.util.Objects;

@Getter
@Setter
@Embeddable
public class CategoryProductId implements Serializable {
    private static final long serialVersionUID = -9129727364688640180L;
    @NotNull
    @Column(name = "id_category", nullable = false)
    private Integer idCategory;

    @NotNull
    @Column(name = "id_product", nullable = false)
    private Integer idProduct;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        CategoryProductId entity = (CategoryProductId) o;
        return Objects.equals(this.idProduct, entity.idProduct) &&
                Objects.equals(this.idCategory, entity.idCategory);
    }

    @Override
    public int hashCode() {
        return Objects.hash(idProduct, idCategory);
    }

}