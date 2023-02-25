package ru.vsu.csf.asashina.marketserver.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.vsu.csf.asashina.marketserver.exception.LowBalanceException;
import ru.vsu.csf.asashina.marketserver.model.dto.*;
import ru.vsu.csf.asashina.marketserver.model.request.AddProductToOrderRequest;
import ru.vsu.csf.asashina.marketserver.model.request.PaymentRequest;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;

@Service
@AllArgsConstructor
public class PurchaseService {

    private final ProductService productService;
    private final OrderService orderService;

    @Transactional
    public OrderNumberDTO manageProductInOrder(UserDTO user, AddProductToOrderRequest request) {
        ProductDTO product = productService.getProductFromAddToOrderRequest(request);
        OrderDTO orderDTO = orderService.getUsersLastOrderOrCreateNewOne(user);
        orderService.addOrUpdateProductAmountInOrder(orderDTO, product, request.getAmount());
        return new OrderNumberDTO(orderDTO.getOrderNumber());
    }

    @Transactional
    public OrderNumberDTO payForOrder(UserDTO user, PaymentRequest request) {
        OrderDTO orderDTO = orderService.getUsersLastOrder(user);
        checkUsersBalance(orderDTO.getFinalPrice(), request.getBalance());
        productService.decreaseProductsAmount(orderDTO.getProducts());
        orderService.setOrderPaid(orderDTO.getOrderNumber());
        return new OrderNumberDTO(orderDTO.getOrderNumber());
    }

    private void checkUsersBalance(BigDecimal price, BigDecimal balance) {
        if (balance.compareTo(price) < 0) {
            throw new LowBalanceException("Low balance");
        }
    }
}
