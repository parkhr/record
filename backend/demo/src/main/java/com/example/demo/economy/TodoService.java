package com.example.demo.economy;

import static com.example.demo.common.ErrorMessage.ADMIN_NOT_FOUND;
import static com.example.demo.common.ErrorMessage.DELETED_ADMIN;
import static com.example.demo.common.ErrorMessage.LOGIN_REQUIRED;

import com.example.demo.admin.entity.Admin;
import com.example.demo.admin.repository.AdminRepository;
import com.example.demo.common.CustomUserDetails;
import com.example.demo.common.exception.ApplicationException;
import com.example.demo.common.util.UserUtil;
import com.example.demo.economy.entity.Epic;
import com.example.demo.economy.entity.Task;
import com.example.demo.economy.repository.EpicRepository;
import com.example.demo.economy.repository.TaskRepository;
import com.example.demo.economy.request.CreateEpicRequest;
import com.example.demo.economy.request.CreateTaskRequest;
import com.example.demo.economy.request.SortEpicRequest;
import com.example.demo.economy.request.SortTaskRequest;
import com.example.demo.economy.request.TaskResponse;
import com.example.demo.economy.request.UpdateEpicRequest;
import com.example.demo.economy.request.UpdateTaskRequest;
import com.example.demo.economy.response.EpicResponse;
import com.example.demo.role.entity.RolePermission;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TodoService {

    private final EpicRepository epicRepository;
    private final TaskRepository taskRepository;
    private final AdminRepository adminRepository;

    public List<EpicResponse> findEpic() {
        CustomUserDetails userDetails = UserUtil.getCustomUserDetails().orElseThrow(() -> new BadCredentialsException(LOGIN_REQUIRED));
        Admin admin = adminRepository.findById(userDetails.getId()).orElseThrow(() -> new ApplicationException(ADMIN_NOT_FOUND));

        if (admin.isDeleted()) {
            throw new ApplicationException(DELETED_ADMIN);
        }

        return epicRepository.findByAdminId(admin.getId()).stream()
            .filter(epic -> !epic.isDeleted())
            .map(epic -> EpicResponse.builder()
                .id(epic.getId())
                .title(epic.getTitle())
                .sortOrder(epic.getSortOrder())
                .todo(
                    taskRepository.findByEpicId(epic.getId()).stream()
                        .filter(task -> !task.isDeleted())
                        .map(task -> TaskResponse.builder()
                            .id(task.getId())
                            .title(task.getTitle())
                            .description(task.getContent())
                            .completed(task.isCompleted())
                            .dueDate(task.getStartAt())
                            .sortOrder(task.getSortOrder())
                            .build()
                        )
                        .sorted(Comparator.comparing(TaskResponse::getSortOrder, Comparator.nullsLast(Integer::compareTo)))
                        .toList()
                )
                .build()
            )
            .sorted(Comparator.comparing(EpicResponse::getSortOrder, Comparator.nullsLast(Integer::compareTo)))
            .toList();
    }

    @Transactional
    public Epic createEpic(CreateEpicRequest request) {
        CustomUserDetails userDetails = UserUtil.getCustomUserDetails().orElseThrow(() -> new BadCredentialsException(LOGIN_REQUIRED));
        Admin admin = adminRepository.findById(userDetails.getId()).orElseThrow(() -> new ApplicationException(ADMIN_NOT_FOUND));

        if (admin.isDeleted()) {
            throw new ApplicationException(DELETED_ADMIN);
        }

        return epicRepository.save(Epic.createEpic(request, admin.getId()));
    }

    @Transactional
    public void updateEpic(UpdateEpicRequest request) {
        CustomUserDetails userDetails = UserUtil.getCustomUserDetails().orElseThrow(() -> new BadCredentialsException(LOGIN_REQUIRED));
        Admin admin = adminRepository.findById(userDetails.getId()).orElseThrow(() -> new ApplicationException(ADMIN_NOT_FOUND));

        if (admin.isDeleted()) {
            throw new ApplicationException(DELETED_ADMIN);
        }

        Epic epic = epicRepository.findById(request.getEpicId()).orElseThrow(() -> new ApplicationException("카드를 찾을 수 없습니다."));

        if (epic.isDeleted()) {
            throw new ApplicationException("삭제된 카드 입니다.");
        }

        if (admin.getId() != epic.getAdminId()) {
            throw new ApplicationException("계정이 없습니다.");
        }

        epic.update(request);
        epicRepository.save(epic);
    }

    @Transactional
    public void deleteEpic(long epicId) {
        CustomUserDetails userDetails = UserUtil.getCustomUserDetails().orElseThrow(() -> new BadCredentialsException(LOGIN_REQUIRED));
        Admin admin = adminRepository.findById(userDetails.getId()).orElseThrow(() -> new ApplicationException(ADMIN_NOT_FOUND));

        if (admin.isDeleted()) {
            throw new ApplicationException(DELETED_ADMIN);
        }

        Epic epic = epicRepository.findById(epicId).orElseThrow(() -> new ApplicationException("카드를 찾을 수 없습니다."));

        if (epic.isDeleted()) {
            throw new ApplicationException("삭제된 카드 입니다.");
        }

        if (admin.getId() != epic.getAdminId()) {
            throw new ApplicationException("계정이 없습니다.");
        }

        epic.delete();
        epicRepository.save(epic);

        List<Task> tasks = taskRepository.findByEpicId(epic.getId());

        for (Task task : tasks) {
            task.delete();
        }

        taskRepository.saveAll(tasks);
    }

    @Transactional
    public Task createTask(CreateTaskRequest request) {
        CustomUserDetails userDetails = UserUtil.getCustomUserDetails().orElseThrow(() -> new BadCredentialsException(LOGIN_REQUIRED));
        Admin admin = adminRepository.findById(userDetails.getId()).orElseThrow(() -> new ApplicationException(ADMIN_NOT_FOUND));

        if (admin.isDeleted()) {
            throw new ApplicationException(DELETED_ADMIN);
        }

        return taskRepository.save(Task.createTask(request, admin.getId()));
    }

    @Transactional
    public void updateTask(UpdateTaskRequest request) {
        CustomUserDetails userDetails = UserUtil.getCustomUserDetails().orElseThrow(() -> new BadCredentialsException(LOGIN_REQUIRED));
        Admin admin = adminRepository.findById(userDetails.getId()).orElseThrow(() -> new ApplicationException(ADMIN_NOT_FOUND));

        if (admin.isDeleted()) {
            throw new ApplicationException(DELETED_ADMIN);
        }

        Task task = taskRepository.findById(request.getTaskId()).orElseThrow(() -> new ApplicationException("할 일을 찾을 수 없습니다."));

        if (task.isDeleted()) {
            throw new ApplicationException("삭제된 할 일 입니다.");
        }

        if (admin.getId() != task.getAdminId()) {
            throw new ApplicationException("계정이 없습니다.");
        }

        task.update(request);
        taskRepository.save(task);
    }

    @Transactional
    public void deleteTask(long taskId) {
        CustomUserDetails userDetails = UserUtil.getCustomUserDetails().orElseThrow(() -> new BadCredentialsException(LOGIN_REQUIRED));
        Admin admin = adminRepository.findById(userDetails.getId()).orElseThrow(() -> new ApplicationException(ADMIN_NOT_FOUND));

        if (admin.isDeleted()) {
            throw new ApplicationException(DELETED_ADMIN);
        }

        Task task = taskRepository.findById(taskId).orElseThrow(() -> new ApplicationException("할 일을 찾을 수 없습니다."));

        if (task.isDeleted()) {
            throw new ApplicationException("삭제된 할일 입니다.");
        }

        if (admin.getId() != task.getAdminId()) {
            throw new ApplicationException("계정이 없습니다.");
        }

        task.delete();
        taskRepository.save(task);
    }

    @Transactional
    public void sortTask(SortTaskRequest request) {

        CustomUserDetails userDetails = UserUtil.getCustomUserDetails().orElseThrow(() -> new BadCredentialsException(LOGIN_REQUIRED));
        Admin admin = adminRepository.findById(userDetails.getId()).orElseThrow(() -> new ApplicationException(ADMIN_NOT_FOUND));

        if (admin.isDeleted()) {
            throw new ApplicationException(DELETED_ADMIN);
        }

        Epic epic = epicRepository.findById(request.getEpicId()).orElseThrow(() -> new ApplicationException("에픽을 찾을 수 없습니다."));

        if (admin.getId() != epic.getAdminId()) {
            throw new ApplicationException("계정이 없습니다.");
        }

        List<Task> tasks = taskRepository.findByEpicId(request.getEpicId());
        tasks = tasks.stream().filter(task -> !task.isDeleted()).toList();

        if (tasks.size() != request.getTaskIds().size()) {
            throw new ApplicationException("할일 개수가 맞지 않습니다.");
        }

        // id → Task 매핑
        Map<Long, Task> taskMap = tasks.stream()
            .collect(Collectors.toMap(Task::getId, Function.identity()));

        List<Task> updatedTasks = new ArrayList<>();

        for (int i = 0; i < request.getTaskIds().size(); i++) {
            Long taskId = request.getTaskIds().get(i);
            Task task = taskMap.get(taskId);

            if (task == null) {
                throw new ApplicationException("잘못된 taskId: " + taskId);
            }

            task.setSortOrder(i);
            updatedTasks.add(task);
        }

        taskRepository.saveAll(updatedTasks); // 배치 저장
    }

    @Transactional
    public void sortEpic(SortEpicRequest request) {
        CustomUserDetails userDetails = UserUtil.getCustomUserDetails().orElseThrow(() -> new BadCredentialsException(LOGIN_REQUIRED));
        Admin admin = adminRepository.findById(userDetails.getId()).orElseThrow(() -> new ApplicationException(ADMIN_NOT_FOUND));

        if (admin.isDeleted()) {
            throw new ApplicationException(DELETED_ADMIN);
        }

        List<Epic> epics = epicRepository.findByAdminId(admin.getId());
        epics = epics.stream().filter(epic -> !epic.isDeleted()).toList();

        if (epics.size() != request.getEpicIds().size()) {
            throw new ApplicationException("카드 개수가 맞지 않습니다.");
        }

        // id → Task 매핑
        Map<Long, Epic> taskMap = epics.stream()
            .collect(Collectors.toMap(Epic::getId, Function.identity()));

        List<Epic> updatedEpics = new ArrayList<>();

        for (int i = 0; i < request.getEpicIds().size(); i++) {
            Long epicId = request.getEpicIds().get(i);
            Epic epic = taskMap.get(epicId);

            if (epic == null) {
                throw new ApplicationException("잘못된 epicId: " + epicId);
            }

            epic.setSortOrder(i);
            updatedEpics.add(epic);
        }

        epicRepository.saveAll(updatedEpics); // 배치 저장
    }
}
