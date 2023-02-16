package ru.vsu.csf.asashina.marketserver.service;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.vsu.csf.asashina.marketserver.mapper.OrderMapper;
import ru.vsu.csf.asashina.marketserver.model.dto.OrderDTO;
import ru.vsu.csf.asashina.marketserver.model.dto.OrderProductDTO;
import ru.vsu.csf.asashina.marketserver.model.dto.UserDTO;
import ru.vsu.csf.asashina.marketserver.model.entity.Order;
import ru.vsu.csf.asashina.marketserver.repository.OrderRepository;
import ru.vsu.csf.asashina.marketserver.util.PageUtils;

import java.math.BigDecimal;
import java.util.Set;

@Service
@AllArgsConstructor
public class OrderService {

    private final static String PAGE_SORT_BY_CREATED_TIMESTAMP = "created";

    private final OrderRepository orderRepository;

    private final OrderMapper orderMapper;

    private final PageUtils pageUtils;

    public Page<OrderDTO> getAllOrdersForUser(UserDTO user, Integer pageNumber, Integer size, Boolean isAsc) {
        PageRequest pageRequest = pageUtils.createPageRequest(pageNumber, size, isAsc, PAGE_SORT_BY_CREATED_TIMESTAMP);
        Page<Order> pages = orderRepository.getAllInPagesByUserId(user.getUserId(), pageRequest);

        pageUtils.checkPageOutOfRange(pages, pageNumber);

        Page<OrderDTO> dtoPages = pages.map(orderMapper::toDTOFromEntity);
        dtoPages.forEach(el -> el.setFinalPrice(calculateFinalPrice(el.getProducts())));
        return dtoPages;
    }

    private BigDecimal calculateFinalPrice(Set<OrderProductDTO> products) {
        return products.stream()
                .map(OrderProductDTO::getOverallPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
