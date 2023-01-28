package ru.vsu.csf.asashina.marketserver.model.request;

import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ProductsListToAttachToCategoryRequest {

    @NotEmpty
    private List<Long> productsId;
}
