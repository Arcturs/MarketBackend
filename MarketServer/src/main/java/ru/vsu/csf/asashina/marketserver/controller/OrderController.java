package ru.vsu.csf.asashina.marketserver.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import ru.vsu.csf.asashina.marketserver.model.ResponseBuilder;
import ru.vsu.csf.asashina.marketserver.model.dto.*;
import ru.vsu.csf.asashina.marketserver.model.request.AddProductToOrderRequest;
import ru.vsu.csf.asashina.marketserver.model.request.PaymentRequest;
import ru.vsu.csf.asashina.marketserver.service.OrderService;
import ru.vsu.csf.asashina.marketserver.service.PurchaseService;
import ru.vsu.csf.asashina.marketserver.service.UserService;

import static org.springframework.http.HttpStatus.OK;
import static ru.vsu.csf.asashina.marketserver.model.constant.Tag.ORDER;

@RestController
@AllArgsConstructor
@RequestMapping("/orders")
public class OrderController {

    private final OrderService orderService;
    private final UserService userService;
    private final PurchaseService purchaseService;

    @GetMapping("")
    @Operation(summary = "Fetches all user's orders in pages", tags = ORDER, responses = {
            @ApiResponse(responseCode = "200", description = "Returns orders in pages", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = OrdersInPagesDTO.class))
            }),
            @ApiResponse(responseCode = "403", description = "Anonymous user", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionDTO.class))
            }),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionDTO.class))
            })
    })
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

    @GetMapping("/{orderNumber}")
    @Operation(summary = "Returns user's order by its number", tags = ORDER, responses = {
            @ApiResponse(responseCode = "200", description = "Returns orders in pages", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = OrderWithUserDTO.class))
            }),
            @ApiResponse(responseCode = "403", description = "User has no access to this order", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionDTO.class))
            }),
            @ApiResponse(responseCode = "404", description = "Order does not exist", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionDTO.class))
            })
    })
    public ResponseEntity<?> getUsersOrderById(@PathVariable("orderNumber") String orderNumber, Authentication authentication) {
        UserDTO user = userService.getUserByEmail((String) authentication.getPrincipal());
        return ResponseBuilder.build(OK, orderService.getUsersOrderByOrderNumber(user, orderNumber));
    }

    @PostMapping("/manage")
    @Operation(summary = "Updates amount of existing product (if zero, removes it) or adds new product", tags = ORDER, responses = {
            @ApiResponse(responseCode = "200", description = "Returns order's number", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = OrderWithUserDTO.class))
            }),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionDTO.class))
            }),
            @ApiResponse(responseCode = "403", description = "Anonymous user", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionDTO.class))
            }),
            @ApiResponse(responseCode = "404", description = "Product does not exist", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionDTO.class))
            }),
            @ApiResponse(responseCode = "405", description = "Cannot add 0 new products", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionDTO.class))
            })
    })
    public ResponseEntity<?> manageOrder(@RequestBody @Valid AddProductToOrderRequest request,
                                         Authentication authentication) {
        UserDTO user = userService.getUserByEmail((String) authentication.getPrincipal());
        return ResponseBuilder.build(OK, purchaseService.manageProductInOrder(user, request));
    }

    @PostMapping("/pay")
    @Operation(summary = "Sets order paid", tags = ORDER, responses = {
            @ApiResponse(responseCode = "200", description = "Returns order's number", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = OrderWithUserDTO.class))
            }),
            @ApiResponse(responseCode = "400", description = "Invalid input data", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionDTO.class))
            }),
            @ApiResponse(responseCode = "403", description = "Anonymous user", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionDTO.class))
            }),
            @ApiResponse(responseCode = "405", description = "Something went wrong while payment process", content = {
                    @Content(mediaType = "application/json", schema = @Schema(implementation = ExceptionDTO.class))
            })
    })
    public ResponseEntity<?> payForOrder(@RequestBody @Valid PaymentRequest request,
                                         Authentication authentication) {
        UserDTO user = userService.getUserByEmail((String) authentication.getPrincipal());
        return ResponseBuilder.build(OK, purchaseService.payForOrder(user, request));
    }
}
