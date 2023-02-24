package ru.vsu.csf.asashina.marketserver.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.vsu.csf.asashina.marketserver.model.dto.*;
import ru.vsu.csf.asashina.marketserver.model.request.AddProductToOrderRequest;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static ru.vsu.csf.asashina.marketserver.model.constant.RoleName.USER;

@ExtendWith(MockitoExtension.class)
class PurchaseServiceTest {

    @InjectMocks
    private PurchaseService purchaseService;

    @Mock
    private OrderService orderService;

    @Mock
    private ProductService productService;

    private UserDTO createValidUserDTO() {
        return UserDTO.builder()
                .userId(1L)
                .name("name")
                .passwordHash("hash")
                .email("email")
                .surname("surname")
                .roles(Set.of(new RoleDTO(2L, USER)))
                .build();
    }

    private ProductDTO createValidProductDTO() {
        return ProductDTO.builder()
                .productId(1L)
                .name("pr1")
                .price(new BigDecimal("10.98"))
                .categories(Collections.emptySet())
                .build();
    }

    private OrderDTO createValidOrderDTO() {
        return OrderDTO.builder()
                .orderNumber("num1")
                .isPaid(false)
                .created(Instant.parse("2022-02-16T18:35:24.00Z"))
                .products(Set.of(
                        new OrderProductDTO(10, new BigDecimal("109.80"),
                                ProductDTO.builder()
                                        .productId(1L)
                                        .name("pr1")
                                        .price(new BigDecimal("10.98"))
                                        .categories(Collections.emptySet())
                                        .build()
                        )
                ))
                .finalPrice(new BigDecimal("109.80"))
                .build();
    }

    @Test
    void manageProductInOrderSuccess() {
        //given
        UserDTO user = createValidUserDTO();
        AddProductToOrderRequest request = new AddProductToOrderRequest(1L, 10);

        ProductDTO productFromService = createValidProductDTO();
        OrderDTO orderFromService = createValidOrderDTO();
        OrderNumberDTO expectedOrderNumber = new OrderNumberDTO("num1");

        when(productService.getProductFromAddToOrderRequest(request)).thenReturn(productFromService);
        when(orderService.getUsersLastOrderOrCreateNewOne(user)).thenReturn(orderFromService);
        doNothing().when(orderService).addOrUpdateProductAmountInOrder(orderFromService, productFromService, request.getAmount());

        //when
        OrderNumberDTO result = purchaseService.manageProductInOrder(user, request);

        //then
        assertEquals(expectedOrderNumber, result);
    }
}