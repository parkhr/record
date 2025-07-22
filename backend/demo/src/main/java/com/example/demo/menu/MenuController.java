package com.example.demo.menu;

import com.example.demo.common.CustomUserDetails;
import com.example.demo.common.exception.ApplicationException;
import com.example.demo.menu.entity.Menu;
import com.example.demo.menu.entity.RoleMenu;
import com.example.demo.menu.repository.MenuRepository;
import com.example.demo.menu.repository.RoleMenuRepository;
import com.example.demo.menu.request.SearchMenuRequest;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/menu")
public class MenuController {

    private final MenuRepository menuRepository;
    private final RoleMenuRepository roleMenuRepository;

    @GetMapping
//    @PreAuthorize("hasRole('READ_MENU')")
    public ResponseEntity<Object> findMenus(SearchMenuRequest request) {
        Optional<CustomUserDetails> customUserDetails = getCustomUserDetails();

        if (customUserDetails.isPresent()) {
            List<Long> menuIds = roleMenuRepository.findByRoleId(customUserDetails.get().getRoleId()).stream().filter(item -> !item.isDeleted())
                .map(RoleMenu::getMenuId).toList();

            if (menuIds.isEmpty()) {
                throw new ApplicationException("해당하는 메뉴가 없습니다.");
            }

            List<Menu> filteredMenus = menuRepository.findByIdIn(menuIds).stream()
                .filter(item -> request.getMenuLevel() == null || Objects.equals(item.getMenuLevel(), request.getMenuLevel()))
                .filter(item -> request.getParentId() == null || Objects.equals(item.getParentId(), request.getParentId()))
                .collect(Collectors.toList());

            if (filteredMenus.isEmpty()) {
                throw new ApplicationException("해당하는 메뉴가 없습니다.");
            }

            return ResponseEntity.ok().body(filteredMenus);
        }

        throw new BadCredentialsException("토큰이 유효하지 않습니다.");
    }

    private Optional<CustomUserDetails> getCustomUserDetails() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

        if (principal instanceof CustomUserDetails userDetails) {
            return Optional.of(userDetails);
        }

        return Optional.empty();
    }
}
