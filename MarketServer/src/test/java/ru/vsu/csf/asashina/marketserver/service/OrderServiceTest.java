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
import org.springframework.security.access.AccessDeniedException;
import ru.vsu.csf.asashina.marketserver.exception.AddZeroAmountProductToOrderException;
import ru.vsu.csf.asashina.marketserver.exception.AllOrdersAreAlreadyPaidException;
import ru.vsu.csf.asashina.marketserver.exception.ObjectNotExistException;
import ru.vsu.csf.asashina.marketserver.exception.PageException;
import ru.vsu.csf.asashina.marketserver.mapper.OrderMapper;
import ru.vsu.csf.asashina.marketserver.model.dto.*;
import ru.vsu.csf.asashina.marketserver.model.entity.*;
import ru.vsu.csf.asashina.marketserver.repository.OrderRepository;
import ru.vsu.csf.asashina.marketserver.util.PageUtil;
import ru.vsu.csf.asashina.marketserver.util.UuidUtil;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static ru.vsu.csf.asashina.marketserver.model.constant.RoleName.ADMIN;
import static ru.vsu.csf.asashina.marketserver.model.constant.RoleName.USER;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @InjectMocks
    private OrderService orderService;

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserService userService;

    @Spy
    private OrderMapper orderMapper = Mappers.getMapper(OrderMapper.class);

    @Spy
    private PageUtil pageUtil;

    @Spy
    private UuidUtil uuidUtil;

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

    private UserWithoutPasswordDTO createValidUserWithoutPasswordDTO() {
        return UserWithoutPasswordDTO.builder()
                .userId(1L)
                .name("name")
                .email("email")
                .surname("surname")
                .build();
    }

    private User createValidUser() {
        return User.builder()
                .userId(1L)
                .name("name")
                .passwordHash("hash")
                .email("email")
                .surname("surname")
                .roles(Set.of(new Role(2L, USER)))
                .build();
    }

    private UserDTO createValidAdminDTO() {
        return UserDTO.builder()
                .userId(2L)
                .name("admin")
                .passwordHash("hash")
                .email("admin")
                .surname("surname")
                .roles(Set.of(new RoleDTO(1L, ADMIN)))
                .build();
    }

    private UserWithoutPasswordDTO createValidAdminWithoutPasswordDTO() {
        return UserWithoutPasswordDTO.builder()
                .userId(2L)
                .name("admin")
                .email("admin")
                .surname("surname")
                .build();
    }

    private User createValidAdmin() {
        return User.builder()
                .userId(2L)
                .name("admin")
                .passwordHash("hash")
                .email("admin")
                .surname("surname")
                .roles(Set.of(new Role(1L, ADMIN)))
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

    private Order createUsersValidOrder() {
        return Order.builder()
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
                .build();
    }

    private OrderWithUserDTO createUsersValidOrderDTO() {
        return OrderWithUserDTO.builder()
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
                .user(createValidUserWithoutPasswordDTO())
                .finalPrice(new BigDecimal("109.80"))
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

    private Order createAdminsValidOrder() {
        return Order.builder()
                .orderNumber("num1")
                .isPaid(false)
                .created(Instant.parse("2022-02-16T18:35:24.00Z"))
                .user(createValidAdmin())
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
                .build();
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

    @Test
    void getUsersOrderByOrderNumberSuccess() {
        //given
        UserDTO user = createValidUserDTO();
        String orderNumber = "num1";

        Order orderFromRepository = createUsersValidOrder();
        OrderWithUserDTO expectedOrder = createUsersValidOrderDTO();

        when(orderRepository.findById(orderNumber)).thenReturn(Optional.of(orderFromRepository));

        //when
        OrderWithUserDTO result = orderService.getUsersOrderByOrderNumber(user, orderNumber);

        //then
        assertEquals(expectedOrder, result);
    }

    @Test
    void getUsersOrderByOrderNumberSuccessAdminsCase() {
        //given
        UserDTO user = createValidAdminDTO();
        String orderNumber = "num1";

        Order orderFromRepository = createUsersValidOrder();
        OrderWithUserDTO expectedOrder = createUsersValidOrderDTO();

        when(orderRepository.findById(orderNumber)).thenReturn(Optional.of(orderFromRepository));
        when(userService.isUserAdmin(user)).thenReturn(true);

        //when
        OrderWithUserDTO result = orderService.getUsersOrderByOrderNumber(user, orderNumber);

        //then
        assertEquals(expectedOrder, result);
    }

    @Test
    void getUsersOrderByOrderNumberThrowsExceptionForNonExistingOrder() {
        //given
        UserDTO user = createValidUserDTO();
        String orderNumber = "num10";

        when(orderRepository.findById(orderNumber)).thenReturn(Optional.empty());

        //when, then
        assertThatThrownBy(() -> orderService.getUsersOrderByOrderNumber(user, orderNumber))
                .isInstanceOf(ObjectNotExistException.class);
    }

    @Test
    void getUsersOrderByOrderNumberThrowsExceptionWhenUserHasNoAccess() {
        //given
        UserDTO user = createValidUserDTO();
        String orderNumber = "num10";

        when(orderRepository.findById(orderNumber)).thenReturn(Optional.of(createAdminsValidOrder()));

        //when, then
        assertThatThrownBy(() -> orderService.getUsersOrderByOrderNumber(user, orderNumber))
                .isInstanceOf(AccessDeniedException.class);
    }

    @Test
    void getUsersLastOrderOrCreateNewOneSuccessWhenNotPaidOrderExists() {
        //given
        UserDTO user = createValidUserDTO();

        Order orderFromRepo = createUsersValidOrder();
        OrderDTO expectedOrder = createValidOrderDTO();

        when(orderRepository.findLastNotPaidOrderByUserId(user.getUserId())).thenReturn(Optional.of(orderFromRepo));

        //when
        OrderDTO result = orderService.getUsersLastOrderOrCreateNewOne(user);

        //then
        assertEquals(expectedOrder, result);
    }

    @Test
    void getUsersLastOrderOrCreateNewOneSuccessWhenNotPaidOrderNotExists() {
        //given
        UserDTO user = createValidUserDTO();

        Order orderFromRepo = createUsersValidOrder();
        OrderDTO expectedOrder = createValidOrderDTO();

        when(orderRepository.findLastNotPaidOrderByUserId(user.getUserId())).thenReturn(Optional.empty());
        when(orderRepository.save(any(Order.class))).thenReturn(orderFromRepo);

        //when
        OrderDTO result = orderService.getUsersLastOrderOrCreateNewOne(user);

        //then
        assertEquals(expectedOrder, result);
    }

    @Test
    void getUsersLastOrderSuccess() {
        //given
        UserDTO user = createValidUserDTO();

        Order orderFromRepository = createUsersValidOrder();
        OrderDTO expectedOrder = createValidOrderDTO();

        when(orderRepository.findLastNotPaidOrderByUserId(user.getUserId())).thenReturn(Optional.of(orderFromRepository));

        //when
        OrderDTO result = orderService.getUsersLastOrder(user);

        //then
        assertEquals(expectedOrder, result);
    }

    @Test
    void getUsersLastOrderThrowsExceptionWhenAllOrdersArePaid() {
        //given
        UserDTO user = createValidUserDTO();

        when(orderRepository.findLastNotPaidOrderByUserId(user.getUserId())).thenReturn(Optional.empty());

        //when, then
        assertThatThrownBy(() -> orderService.getUsersLastOrder(user))
                .isInstanceOf(AllOrdersAreAlreadyPaidException.class);
    }

    @Test
    void addOrUpdateProductAmountInOrderSuccessWhenProductInOrder() {
        //given
        OrderDTO order = createValidOrderDTO();
        ProductDTO product = ProductDTO.builder()
                .productId(1L)
                .name("pr1")
                .price(new BigDecimal("10.98"))
                .categories(Collections.emptySet())
                .build();
        int amount = 10;

        //when
        assertDoesNotThrow(() -> orderService.addOrUpdateProductAmountInOrder(order, product, amount));

        //then
        verify(orderRepository, never()).deleteProductFromOrder(order.getOrderNumber(), product.getProductId());
        verify(orderRepository, only()).updateProductAmountInOrder(order.getOrderNumber(), product.getProductId(), amount);
        verify(orderRepository, never()).addProductToOrder(any(String.class), eq(order.getOrderNumber()),
                eq(product.getProductId()), eq(amount));
    }

    @Test
    void addOrUpdateProductAmountInOrderSuccessWhenProductInOrderAndAmountEqualsZero() {
        //given
        OrderDTO order = createValidOrderDTO();
        ProductDTO product = ProductDTO.builder()
                .productId(1L)
                .name("pr1")
                .price(new BigDecimal("10.98"))
                .categories(Collections.emptySet())
                .build();
        int amount = 0;

        //when
        assertDoesNotThrow(() -> orderService.addOrUpdateProductAmountInOrder(order, product, amount));

        //then
        verify(orderRepository, only()).deleteProductFromOrder(order.getOrderNumber(), product.getProductId());
        verify(orderRepository, never()).updateProductAmountInOrder(order.getOrderNumber(), product.getProductId(), amount);
        verify(orderRepository, never()).addProductToOrder(any(String.class), eq(order.getOrderNumber()),
                eq(product.getProductId()), eq(amount));
    }

    @Test
    void addOrUpdateProductAmountInOrderSuccessWhenNewProduct() {
        //given
        OrderDTO order = createValidOrderDTO();
        ProductDTO product = ProductDTO.builder()
                .productId(10L)
                .name("pr10")
                .price(new BigDecimal("8.67"))
                .categories(Collections.emptySet())
                .build();
        int amount = 8;

        //when
        assertDoesNotThrow(() -> orderService.addOrUpdateProductAmountInOrder(order, product, amount));

        //then
        verify(orderRepository, never()).deleteProductFromOrder(order.getOrderNumber(), product.getProductId());
        verify(orderRepository, never()).updateProductAmountInOrder(order.getOrderNumber(), product.getProductId(), amount);
        verify(orderRepository, only()).addProductToOrder(any(String.class), eq(order.getOrderNumber()),
                eq(product.getProductId()), eq(amount));
    }

    @Test
    void addOrUpdateProductAmountInOrderThrowsExceptionWhenAddingNewProductAndAmountEqualsZero() {
        //given
        OrderDTO order = createValidOrderDTO();
        ProductDTO product = ProductDTO.builder()
                .productId(10L)
                .name("pr10")
                .price(new BigDecimal("8.67"))
                .categories(Collections.emptySet())
                .build();
        int amount = 0;

        //when, then
        assertThatThrownBy(() -> orderService.addOrUpdateProductAmountInOrder(order, product, amount))
                .isInstanceOf(AddZeroAmountProductToOrderException.class);
    }

    @Test
    void setOrderPaidSuccess() {
        //given
        String orderNumber = "num1";

        doNothing().when(orderRepository).setOrderPaid(orderNumber);

        //when, then
        assertDoesNotThrow(() -> orderService.setOrderPaid(orderNumber));
    }
}