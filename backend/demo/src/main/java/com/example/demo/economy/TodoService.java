package com.example.demo.economy;

import static com.example.demo.common.ErrorMessage.ADMIN_NOT_FOUND;

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
import com.example.demo.economy.request.SortTaskRequest;
import com.example.demo.economy.request.UpdateEpicRequest;
import com.example.demo.economy.request.UpdateTaskRequest;
import com.example.demo.economy.response.EpicResponse;
import java.util.ArrayList;
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
        CustomUserDetails userDetails = UserUtil.getCustomUserDetails().orElseThrow(() -> new BadCredentialsException("로그인이 필요합니다."));
        Admin admin = adminRepository.findById(userDetails.getId()).orElseThrow(() -> new ApplicationException(ADMIN_NOT_FOUND));

        return epicRepository.findTodo(admin.getId());
    }

    @Transactional
    public Epic createEpic(CreateEpicRequest request) {
        CustomUserDetails userDetails = UserUtil.getCustomUserDetails().orElseThrow(() -> new BadCredentialsException("로그인이 필요합니다."));
        Admin admin = adminRepository.findById(userDetails.getId()).orElseThrow(() -> new ApplicationException(ADMIN_NOT_FOUND));
        return epicRepository.save(Epic.createEpic(request, admin.getId()));
    }

    @Transactional
    public void updateEpic(UpdateEpicRequest request) {
        CustomUserDetails userDetails = UserUtil.getCustomUserDetails().orElseThrow(() -> new BadCredentialsException("로그인이 필요합니다."));
        Admin admin = adminRepository.findById(userDetails.getId()).orElseThrow(() -> new ApplicationException(ADMIN_NOT_FOUND));
        Epic epic = epicRepository.findById(request.getEpicId()).orElseThrow(() -> new ApplicationException("에픽을 찾을 수 없습니다."));

        if (admin.getId() != epic.getAdminId()) {
            throw new ApplicationException("계정이 없습니다.");
        }

        epic.update(request);
        epicRepository.save(epic);
    }

    @Transactional
    public void deleteEpic(long epicId) {
        CustomUserDetails userDetails = UserUtil.getCustomUserDetails().orElseThrow(() -> new BadCredentialsException("로그인이 필요합니다."));
        Admin admin = adminRepository.findById(userDetails.getId()).orElseThrow(() -> new ApplicationException(ADMIN_NOT_FOUND));
        Epic epic = epicRepository.findById(epicId).orElseThrow(() -> new ApplicationException("에픽을 찾을 수 없습니다."));

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
        CustomUserDetails userDetails = UserUtil.getCustomUserDetails().orElseThrow(() -> new BadCredentialsException("로그인이 필요합니다."));
        Admin admin = adminRepository.findById(userDetails.getId()).orElseThrow(() -> new ApplicationException(ADMIN_NOT_FOUND));

        return taskRepository.save(Task.createTask(request, admin.getId()));
    }

    @Transactional
    public void updateTask(UpdateTaskRequest request) {
        CustomUserDetails userDetails = UserUtil.getCustomUserDetails().orElseThrow(() -> new BadCredentialsException("로그인이 필요합니다."));
        Admin admin = adminRepository.findById(userDetails.getId()).orElseThrow(() -> new ApplicationException(ADMIN_NOT_FOUND));
        Task task = taskRepository.findById(request.getTaskId()).orElseThrow(() -> new ApplicationException("작업을 찾을 수 없습니다."));

        if (admin.getId() != task.getAdminId()) {
            throw new ApplicationException("계정이 없습니다.");
        }

        task.update(request);
        taskRepository.save(task);
    }

    @Transactional
    public void deleteTask(long taskId) {
        CustomUserDetails userDetails = UserUtil.getCustomUserDetails().orElseThrow(() -> new BadCredentialsException("로그인이 필요합니다."));
        Admin admin = adminRepository.findById(userDetails.getId()).orElseThrow(() -> new ApplicationException(ADMIN_NOT_FOUND));
        Task task = taskRepository.findById(taskId).orElseThrow(() -> new ApplicationException("작업을 찾을 수 없습니다."));

        if (admin.getId() != task.getAdminId()) {
            throw new ApplicationException("계정이 없습니다.");
        }

        task.delete();
        taskRepository.save(task);
    }

    @Transactional
    public void sortTask(SortTaskRequest request) {

        CustomUserDetails userDetails = UserUtil.getCustomUserDetails().orElseThrow(() -> new BadCredentialsException("로그인이 필요합니다."));
        Admin admin = adminRepository.findById(userDetails.getId()).orElseThrow(() -> new ApplicationException(ADMIN_NOT_FOUND));
        Epic epic = epicRepository.findById(request.getEpicId()).orElseThrow(() -> new ApplicationException("에픽을 찾을 수 없습니다."));

        if (admin.getId() != epic.getAdminId()) {
            throw new ApplicationException("계정이 없습니다.");
        }

        List<Task> tasks = taskRepository.findByEpicId(request.getEpicId());

        if (tasks.size() != request.getTaskIds().size()) {
            throw new ApplicationException("task 개수가 맞지 않습니다.");
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
}
