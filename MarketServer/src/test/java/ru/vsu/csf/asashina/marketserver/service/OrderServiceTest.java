package ru.vsu.csf.asashina.marketserver.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.vsu.csf.asashina.marketserver.exception.PageException;
import ru.vsu.csf.asashina.marketserver.mapper.OrderMapper;
import ru.vsu.csf.asashina.marketserver.model.dto.*;
import ru.vsu.csf.asashina.marketserver.model.entity.*;
import ru.vsu.csf.asashina.marketserver.model.constant.RoleName;
import ru.vsu.csf.asashina.marketserver.repository.OrderRepository;
import ru.vsu.csf.asashina.marketserver.util.PageUtils;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static ru.vsu.csf.asashina.marketserver.model.constant.RoleName.USER;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;

    @Spy
    private OrderMapper orderMapper = Mappers.getMapper(OrderMapper.class);

    @Spy
    private PageUtils pageUtils;

    private UserDTO createValidUserDTO() {
        return UserDTO.builder()
                .userId(1L)
                .name("name")
                .passwordHash("hash")
                .email("email")
                .surname("surname")
                .roles(Set.of(new RoleDTO(1L, USER)))
                .build();
    }

    private User createValidUser() {
        return User.builder()
                .userId(1L)
                .name("name")
                .passwordHash("hash")
                .email("email")
                .surname("surname")
                .roles(Set.of(new Role(1L, USER)))
                .build();
    }

    private Page<Order> createValidOrderPages() {
        return new PageImpl<>(List.of(
           Order.builder()
                   .orderNumber("num1")
                   .isPaid(false)
                   .created(Instant.parse("2022-02-16T18:35:24.00Z"))
                   .user(createValidUser())
                   .products(Set.of(
                           new OrderProduct("1", 10,
                                   Product.builder()
                                           .productId(1L)
                                           .name("pr1")
                                           .amount(100)
                                           .price(new BigDecimal("10.98"))
                                           .description("prpr")
                                           .categories(Collections.emptySet())
                                           .build()
                           )
                   ))
                   .build(),
                Order.builder()
                        .orderNumber("num2")
                        .isPaid(false)
                        .created(Instant.parse("2022-01-16T18:35:24.00Z"))
                        .user(createValidUser())
                        .products(Set.of(
                                new OrderProduct("2", 3,
                                        Product.builder()
                                                .productId(1L)
                                                .name("pr1")
                                                .amount(100)
                                                .price(new BigDecimal("10.98"))
                                                .description("prpr")
                                                .categories(Collections.emptySet())
                                                .build()
                                )
                        ))
                        .build(),
                Order.builder()
                        .orderNumber("num3")
                        .isPaid(false)
                        .created(Instant.parse("2022-02-10T10:35:24.00Z"))
                        .user(createValidUser())
                        .products(Set.of(
                                new OrderProduct("3", 3,
                                        Product.builder()
                                                .productId(2L)
                                                .name("pr2")
                                                .amount(30)
                                                .price(new BigDecimal("11.98"))
                                                .description("prpr")
                                                .categories(Collections.emptySet())
                                                .build()
                                )
                        ))
                        .build()
        ));
    }

    private Page<OrderDTO> createValidOrderDTOPages() {
        return new PageImpl<>(List.of(
                OrderDTO.builder()
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
                        .build(),
                OrderDTO.builder()
                        .orderNumber("num2")
                        .isPaid(false)
                        .created(Instant.parse("2022-01-16T18:35:24.00Z"))
                        .products(Set.of(
                                new OrderProductDTO(3, new BigDecimal("32.94"),
                                        ProductDTO.builder()
                                                .productId(1L)
                                                .name("pr1")
                                                .price(new BigDecimal("10.98"))
                                                .categories(Collections.emptySet())
                                                .build()
                                )
                        ))
                        .finalPrice(new BigDecimal("32.94"))
                        .build(),
                OrderDTO.builder()
                        .orderNumber("num3")
                        .isPaid(false)
                        .created(Instant.parse("2022-02-10T10:35:24.00Z"))
                        .products(Set.of(
                                new OrderProductDTO(3, new BigDecimal("35.94"),
                                        ProductDTO.builder()
                                                .productId(2L)
                                                .name("pr2")
                                                .price(new BigDecimal("11.98"))
                                                .categories(Collections.emptySet())
                                                .build()
                                )
                        ))
                        .finalPrice(new BigDecimal("35.94"))
                        .build()
        ));
    }

    @Test
    void getAllOrdersForUserSuccess() {
        //given
        UserDTO user = createValidUserDTO();
        int pageNumber = 1;
        int size = 5;
        boolean isAsc = true;

        Page<Order> pagesFromRepository = createValidOrderPages();
        Page<OrderDTO> expectedPages = createValidOrderDTOPages();

        when(orderRepository.getAllInPagesByUserId(eq(user.getUserId()), any(Pageable.class)))
                .thenReturn(pagesFromRepository);

        //when
        Page<OrderDTO> result = orderService.getAllOrdersForUser(user, pageNumber, size, isAsc);

        //then
        assertEquals(expectedPages, result);
    }

    @Test
    void getAllOrdersForUserThrowsExceptionForPageNumberOutOfRange() {
        //given
        UserDTO user = createValidUserDTO();
        int pageNumber = 10;
        int size = 5;
        boolean isAsc = true;

        when(orderRepository.getAllInPagesByUserId(eq(user.getUserId()), any(Pageable.class)))
                .thenReturn(Page.empty());

        //when, then
        assertThatThrownBy(() -> orderService.getAllOrdersForUser(user, pageNumber, size, isAsc))
                .isInstanceOf(PageException.class);
    }
}