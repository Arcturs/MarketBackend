package ru.vsu.csf.asashina.marketserver.controller;

import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.vsu.csf.asashina.marketserver.model.ResponseBuilder;
import ru.vsu.csf.asashina.marketserver.model.dto.*;
import ru.vsu.csf.asashina.marketserver.service.OrderService;
import ru.vsu.csf.asashina.marketserver.service.UserService;

import static org.springframework.http.HttpStatus.OK;

@RestController
@AllArgsConstructor
@RequestMapping("/orders")
//TODO: Integration test + security + documentation
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;

    @GetMapping("")
    public ResponseEntity<?> getAllUsersOrdersInPages(@RequestParam(value = "pageNumber", required = false, defaultValue = "1") Integer pageNumber,
                                                      @RequestParam(value = "size", required = false, defaultValue = "5") Integer size,
                                                      @RequestParam(value = "isAsc", required = false, defaultValue = "true") Boolean isAsc,
                                                      Authentication authentication) {
        UserDTO user = userService.getUserByEmail((String) authentication.getPrincipal());
        Page<OrderDTO> orders = orderService.getAllOrdersForUser(user, pageNumber, size, isAsc);
        return ResponseBuilder.build(OK, buildOrdersInPagesResponse(orders, pageNumber, size));
    }

    private OrdersInPagesDTO buildOrdersInPagesResponse(Page<OrderDTO> orders, Integer pageNumber, Integer size) {
        return new OrdersInPagesDTO(orders.getContent(),
                new PagingInfoDTO(pageNumber, size, orders.getTotalPages()));
    }
}
