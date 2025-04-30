package com.example.productservice.mapper;


import com.example.productservice.dto.ProductDto;
import com.example.productservice.model.Product;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;


@Mapper(componentModel = "spring")
public interface ProductMapper {

    @Mapping(target = "id", ignore = true)
    Product toEntity(ProductDto dto);

    ProductDto toDto(Product entity);

}
