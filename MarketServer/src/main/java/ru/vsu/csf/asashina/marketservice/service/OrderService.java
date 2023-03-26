package ru.vsu.csf.asashina.marketservice.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import ru.vsu.csf.asashina.marketservice.exception.AddZeroAmountProductToOrderException;
import ru.vsu.csf.asashina.marketservice.exception.AllOrdersAreAlreadyPaidException;
import ru.vsu.csf.asashina.marketservice.exception.ObjectNotExistException;
import ru.vsu.csf.asashina.marketservice.mapper.OrderMapper;
import ru.vsu.csf.asashina.marketservice.model.dto.*;
import ru.vsu.csf.asashina.marketservice.model.entity.Order;
import ru.vsu.csf.asashina.marketservice.repository.OrderRepository;
import ru.vsu.csf.asashina.marketservice.util.PageUtil;
import ru.vsu.csf.asashina.marketservice.util.UuidUtil;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Set;

@Service
@AllArgsConstructor
public class OrderService {

    private final static String PAGE_SORT_BY_CREATED_TIMESTAMP = "created";

    private final OrderRepository orderRepository;

    private final UserService userService;

    private final OrderMapper orderMapper;

    private final PageUtil pageUtil;
    private final UuidUtil uuidUtil;

    public Page<OrderDTO> getAllOrdersForUser(UserDTO user, Integer pageNumber, Integer size, Boolean isAsc) {
        PageRequest pageRequest = pageUtil.createPageRequest(pageNumber, size, isAsc, PAGE_SORT_BY_CREATED_TIMESTAMP);
        Page<Order> pages = orderRepository.getAllInPagesByUserId(user.getUserId(), pageRequest);

        pageUtil.checkPageOutOfRange(pages, pageNumber);

        return pages.map(this::mapOrderWithCalculatingFinalPrice);
    }

    private OrderDTO mapOrderWithCalculatingFinalPrice(Order order) {
        OrderDTO dto = orderMapper.toDTOFromEntity(order);
        dto.setFinalPrice(calculateFinalPrice(dto.getProducts()));
        return dto;
    }

    private BigDecimal calculateFinalPrice(Set<OrderProductDTO> products) {
        if (products == null) {
            return BigDecimal.ZERO;
        }
        return products.stream()
                .map(OrderProductDTO::getOverallPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    public OrderWithUserDTO getUsersOrderByOrderNumber(UserDTO user, String orderNumber) {
        Order order = findOrderByOrderNumber(orderNumber);
        checkIfUserHasAccessToOrder(order, user);

        OrderWithUserDTO orderDTO = orderMapper.toWithUserDTOFromEntity(order);
        orderDTO.setFinalPrice(calculateFinalPrice(orderDTO.getProducts()));
        return orderDTO;
    }

    private Order findOrderByOrderNumber(String orderNumber) {
        return orderRepository.findById(orderNumber).orElseThrow(
                () -> new ObjectNotExistException("Order with following id number does not exist")
        );
    }

    private void checkIfUserHasAccessToOrder(Order order, UserDTO user) {
        if (!userService.isUserAdmin(user) && !order.getUser().getEmail().equals(user.getEmail())) {
            throw new AccessDeniedException("Access denied");
        }
    }

    @Transactional
    public OrderDTO getUsersLastOrderOrCreateNewOne(UserDTO user) {
        Order order = orderRepository.findLastNotPaidOrderByUserId(user.getUserId())
                .orElseGet(() -> createOrder(user));
        return mapOrderWithCalculatingFinalPrice(order);
    }

    public OrderDTO getUsersLastOrder(UserDTO user) {
        Order order = orderRepository.findLastNotPaidOrderByUserId(user.getUserId()).orElseThrow(
                () -> new AllOrdersAreAlreadyPaidException("All orders are already paid")
        );
        return mapOrderWithCalculatingFinalPrice(order);
    }

    private Order createOrder(UserDTO user) {
       Order order = orderMapper.createEntity(uuidUtil.generateRandomUUIDInString(), Instant.now(), user);
       return orderRepository.save(order);
    }

    @Transactional
    public void addOrUpdateProductAmountInOrder(OrderDTO order, ProductDTO product, Integer amount) {
        if (containsProduct(order, product)) {
            if (amount == 0) {
                orderRepository.deleteProductFromOrder(order.getOrderNumber(), product.getProductId());
            } else {
                orderRepository.updateProductAmountInOrder(order.getOrderNumber(), product.getProductId(), amount);
            }
        } else {
            if (amount == 0) {
                throw new AddZeroAmountProductToOrderException("You cannot add 0 products");
            }
            orderRepository.addProductToOrder(uuidUtil.generateRandomUUIDInString(),
                    order.getOrderNumber(), product.getProductId(), amount);
        }
    }

    private boolean containsProduct(OrderDTO order, ProductDTO product) {
        if (order.getProducts() == null) {
            return false;
        }
        for (OrderProductDTO orderProduct : order.getProducts()) {
            if (orderProduct.getProduct().equals(product)) {
                return true;
            }
        }
        return false;
    }

    @Transactional(propagation = Propagation.MANDATORY)
    public void setOrderPaid(String orderNumber) {
        orderRepository.setOrderPaid(orderNumber);
    }
}
