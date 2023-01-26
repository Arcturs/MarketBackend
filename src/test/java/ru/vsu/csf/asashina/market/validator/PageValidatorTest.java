package ru.vsu.csf.asashina.market.validator;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import ru.vsu.csf.asashina.market.exception.PageException;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class PageValidatorTest {

    @InjectMocks
    private PageValidator pageValidator;

    @Test
    void checkPageAndSizeOutOfRangeSuccess() {
        //given
        Page<Integer> pages = createValidPages();
        int page = 1;

        //when, then
        assertDoesNotThrow(() -> pageValidator.checkPageOutOfRange(pages, page));
    }

    @Test
    void checkPageAndSizeOutOfRangeForEmptyPagesWithPageEqualsOne() {
        //given
        Page<Integer> pages = createEmptyPages();
        int page = 1;

        //when, then
        assertDoesNotThrow(() -> pageValidator.checkPageOutOfRange(pages, page));
    }

    @Test
    void checkPageAndSizeOutOfRangeForEmptyPagesThrowsExceptionWhenPageNotEqualOne() {
        //given
        Page<Integer> pages = createEmptyPages();
        int page = 2;

        //when, then
        assertThatThrownBy(() -> pageValidator.checkPageOutOfRange(pages, page))
                .isInstanceOf(PageException.class);
    }

    @Test
    void checkPageAndSizeOutOfRangeThrowsExceptionWhenPageOutOfRange() {
        //given
        Page<Integer> pages = createValidPages();
        int page = 50;

        //when, then
        assertThatThrownBy(() -> pageValidator.checkPageOutOfRange(pages, page))
                .isInstanceOf(PageException.class);
    }

    private Page<Integer> createValidPages() {
        return new PageImpl<>(List.of(1, 2, 3, 4, 5));
    }

    private Page<Integer> createEmptyPages() {
        return Page.empty();
    }
}