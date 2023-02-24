package ru.vsu.csf.asashina.marketserver.service;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.vsu.csf.asashina.marketserver.model.dto.OrderDTO;
import ru.vsu.csf.asashina.marketserver.model.dto.OrderNumberDTO;
import ru.vsu.csf.asashina.marketserver.model.dto.ProductDTO;
import ru.vsu.csf.asashina.marketserver.model.dto.UserDTO;
import ru.vsu.csf.asashina.marketserver.model.request.AddProductToOrderRequest;

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
}
