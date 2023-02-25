package ru.vsu.csf.asashina.marketserver.util;

import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Component;
import ru.vsu.csf.asashina.marketserver.exception.PageException;

@Component
@NoArgsConstructor
public class PageUtil {

    public void checkPageOutOfRange(Page<?> pages, Integer page) {
        if (page - 1 >= pages.getTotalPages() && page != 1) {
            throw new PageException("Page number is out of range");
        }
    }

    public PageRequest createPageRequest(Integer pageNumber, Integer size, Boolean isAsc, String sort) {
        return PageRequest.of(pageNumber - 1, size,
                isAsc ? Sort.by(sort).ascending() : Sort.by(sort).descending());
    }
}
