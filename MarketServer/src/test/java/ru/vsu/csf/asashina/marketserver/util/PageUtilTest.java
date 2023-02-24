package ru.vsu.csf.asashina.marketserver.util;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import ru.vsu.csf.asashina.marketserver.exception.PageException;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PageUtilTest {

    @InjectMocks
    private PageUtil pageUtil;

    private Page<Integer> createValidPages() {
        return new PageImpl<>(List.of(1, 2, 3, 4, 5));
    }

    private Page<Integer> createEmptyPages() {
        return Page.empty();
    }

    @Test
    void checkPageAndSizeOutOfRangeSuccess() {
        //given
        Page<Integer> pages = createValidPages();
        int page = 1;

        //when, then
        assertDoesNotThrow(() -> pageUtil.checkPageOutOfRange(pages, page));
    }

    @Test
    void checkPageAndSizeOutOfRangeForEmptyPagesWithPageEqualsOne() {
        //given
        Page<Integer> pages = createEmptyPages();
        int page = 1;

        //when, then
        assertDoesNotThrow(() -> pageUtil.checkPageOutOfRange(pages, page));
    }

    @Test
    void checkPageAndSizeOutOfRangeForEmptyPagesThrowsExceptionWhenPageNotEqualOne() {
        //given
        Page<Integer> pages = createEmptyPages();
        int page = 2;

        //when, then
        assertThatThrownBy(() -> pageUtil.checkPageOutOfRange(pages, page))
                .isInstanceOf(PageException.class);
    }

    @Test
    void checkPageAndSizeOutOfRangeThrowsExceptionWhenPageOutOfRange() {
        //given
        Page<Integer> pages = createValidPages();
        int page = 50;

        //when, then
        assertThatThrownBy(() -> pageUtil.checkPageOutOfRange(pages, page))
                .isInstanceOf(PageException.class);
    }

    @Test
    void createPageRequestSuccess() {
        //given
        int pageNumber = 2;
        int size = 5;
        boolean isAsc = false;
        String sort = "sort";

        //when
        PageRequest result = pageUtil.createPageRequest(pageNumber, size, isAsc, sort);

        //then
        assertEquals(PageRequest.of(1, 5, Sort.by("sort").descending()), result);
    }
}