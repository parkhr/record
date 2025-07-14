package com.example.demo.menu;

import static com.example.demo.common.ErrorMessage.MENU_NOT_FOUND;

import com.example.demo.common.exception.ApplicationException;
import com.example.demo.menu.entity.Menu;
import com.example.demo.menu.repository.MenuRepository;
import com.example.demo.menu.request.CreateMenuRequest;
import com.example.demo.menu.request.UpdateMenuRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MenuService {

    private final MenuRepository menuRepository;

    public Menu createMenu(CreateMenuRequest request) {

        return menuRepository.save(Menu.createMenu(request));
    }

    @Transactional
    public Menu updateMenu(UpdateMenuRequest request) {

        Menu menu = menuRepository.findById(request.getId()).orElseThrow(() -> new ApplicationException(MENU_NOT_FOUND));

        if (menu.isDeleted()) {
            throw new ApplicationException(MENU_NOT_FOUND);
        }

        menu.update(request);

        return menuRepository.save(menu);
    }

    @Transactional
    public void deleteMenu(long menuId) {
        Menu menu = menuRepository.findById(menuId).orElseThrow(() -> new ApplicationException(MENU_NOT_FOUND));

        if (menu.isDeleted()) {
            throw new ApplicationException(MENU_NOT_FOUND);
        }

        menu.delete();
        menuRepository.save(menu);
    }
}
