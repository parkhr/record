package com.example.demo.service;

import static com.example.demo.common.ErrorMessage.MENU_NOT_FOUND;

import com.example.demo.common.exception.ApplicationException;
import com.example.demo.entity.Menu;
import com.example.demo.repository.MenuRepository;
import com.example.demo.request.CreateMenuRequest;
import com.example.demo.request.UpdateMenuRequest;
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

        menu.update(request);

        return menuRepository.save(menu);
    }

    @Transactional
    public void deleteMenu(long menuId) {
        Menu menu = menuRepository.findById(menuId).orElseThrow(() -> new ApplicationException(MENU_NOT_FOUND));

        menu.delete();
        menuRepository.save(menu);
    }
}
