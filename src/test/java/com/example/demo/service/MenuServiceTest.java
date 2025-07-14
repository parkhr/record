package com.example.demo.service;

import static com.example.demo.common.ErrorMessage.MENU_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;

import com.example.demo.common.exception.ApplicationException;
import com.example.demo.menu.entity.Menu;
import com.example.demo.menu.repository.MenuRepository;
import com.example.demo.menu.MenuService;
import com.example.demo.menu.request.CreateMenuRequest;
import com.example.demo.menu.request.UpdateMenuRequest;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class MenuServiceTest {

    @Autowired
    private MenuService menuService;

    @Autowired
    private MenuRepository menuRepository;

    @DisplayName("메뉴 생성 성공")
    @Test
    void createRecord() {
        //given
        CreateMenuRequest request = new CreateMenuRequest();
        request.setName("메뉴이름");
        request.setContent("메뉴내용");
        request.setLink("/test");
        request.setLink2("새창");

        //when
        Menu menu = menuService.createMenu(request);

        //then
        assertThat(menu.getName()).isEqualTo("메뉴이름");
        assertThat(menu.getContent()).isEqualTo("메뉴내용");
        assertThat(menu.getLink()).isEqualTo("/test");
        assertThat(menu.getLink2()).isEqualTo("새창");
        assertNotNull(menu.getCreatedAt());
    }

    @DisplayName("메뉴 수정 성공")
    @Test
    void updateRecord() {
        //given
        CreateMenuRequest request = new CreateMenuRequest();
        request.setName("메뉴이름");
        request.setContent("메뉴내용");
        request.setLink("/test");
        request.setLink2("새창");

        Menu menu = menuService.createMenu(request);

        UpdateMenuRequest updateRequest = new UpdateMenuRequest();
        updateRequest.setId(menu.getId());
        updateRequest.setName("수정된메뉴이름");
        updateRequest.setContent("수정된메뉴내용");
        updateRequest.setLink("/update");
        updateRequest.setLink2("현재창");

        //when
        Menu updatedMenu = menuService.updateMenu(updateRequest);

        //then
        assertThat(updatedMenu.getName()).isEqualTo("수정된메뉴이름");
        assertThat(updatedMenu.getContent()).isEqualTo("수정된메뉴내용");
        assertThat(updatedMenu.getLink()).isEqualTo("/update");
        assertThat(updatedMenu.getLink2()).isEqualTo("현재창");
        assertNotNull(updatedMenu.getUpdatedAt());
    }

    @DisplayName("메뉴 수정 실패 (메뉴이 존재하지 않는 경우)")
    @Test
    void updateRecord_fail1() {
        //given

        UpdateMenuRequest updateRequest = new UpdateMenuRequest();
        updateRequest.setId(0L);
        updateRequest.setName("수정된메뉴이름");
        updateRequest.setContent("수정된메뉴내용");
        updateRequest.setLink("/update");
        updateRequest.setLink2("현재창");

        //when
        //then
        assertThatThrownBy(() -> menuService.updateMenu(updateRequest)).isInstanceOf(ApplicationException.class).hasMessage(MENU_NOT_FOUND);
    }

    @DisplayName("메뉴 삭제 성공")
    @Test
    void deleteRecord() {
        //given
        CreateMenuRequest request = new CreateMenuRequest();
        request.setName("메뉴이름");
        request.setContent("메뉴내용");
        request.setLink("/test");
        request.setLink2("새창");

        Menu menu = menuService.createMenu(request);

        //when
        menuService.deleteMenu(menu.getId());

        // then
        Optional<Menu> deletedMenu = menuRepository.findById(menu.getId());
        assertTrue(deletedMenu.isPresent());  // 1단계: 실제로는 DB엔 남아있고
        assertNotNull(deletedMenu.get().getDeletedAt());  // 3단계: 삭제 시간도 찍혀있는지
    }

    @DisplayName("메뉴 삭제 실패 (메뉴이 존재하지 않는 경우)")
    @Test
    void deleteRecord_fail1() {

        //when
        // then
        assertThatThrownBy(() -> menuService.deleteMenu(0L)).isInstanceOf(ApplicationException.class).hasMessage(MENU_NOT_FOUND);
    }
}