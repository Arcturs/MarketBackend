package ru.vsu.csf.asashina.market.validator;

import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Component;
import ru.vsu.csf.asashina.market.exception.PageException;

@Component
@NoArgsConstructor
public class PageValidator {

    public void checkPageOutOfRange(Page<?> pages, int page) {
        if (page - 1 >= pages.getTotalPages() && page != 1) {
            throw new PageException("Page number is out of range");
        }
    }
}
