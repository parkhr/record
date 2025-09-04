package com.example.demo.menu;

import static com.example.demo.common.ErrorMessage.ADMIN_NOT_FOUND;

import com.example.demo.admin.entity.Admin;
import com.example.demo.admin.repository.AdminRepository;
import com.example.demo.common.CustomUserDetails;
import com.example.demo.common.exception.ApplicationException;
import com.example.demo.common.util.UserUtil;
import com.example.demo.menu.entity.Menu;
import com.example.demo.menu.entity.RoleMenu;
import com.example.demo.menu.repository.MenuRepository;
import com.example.demo.menu.repository.RoleMenuRepository;
import com.example.demo.menu.request.SearchMenuRequest;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/menu")
public class MenuController {

    private final MenuRepository menuRepository;
    private final AdminRepository adminRepository;
    private final RoleMenuRepository roleMenuRepository;

    @GetMapping
//    @PreAuthorize("hasRole('READ_MENU')")
    public ResponseEntity<Object> findMenus(SearchMenuRequest request) {
        CustomUserDetails userDetails = UserUtil.getCustomUserDetails().orElseThrow(() -> new BadCredentialsException("로그인이 필요합니다."));
        Admin admin = adminRepository.findById(userDetails.getId()).orElseThrow(() -> new ApplicationException(ADMIN_NOT_FOUND));

        if (admin.isDeleted()) {
            throw new ApplicationException("삭제된 관리자입니다.");
        }

        List<Long> menuIds = roleMenuRepository.findByRoleId(admin.getRoleId()).stream().filter(item -> !item.isDeleted())
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
}
