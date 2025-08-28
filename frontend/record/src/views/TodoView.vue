<template>
  <div style="background-color: #f0f2f5; padding: 16px; height: 100vh;">
    <a-row :gutter="16">
      <a-col></a-col>
      <a-col :span="3" style="margin-bottom: 16px;">
        <a-button type="primary" block size="large" @click="onAddEpic()" style="margin-bottom: 12px;">
          카드 추가
        </a-button>
      </a-col>
    </a-row>
    <a-row :gutter="16" style="display: flex; flex-wrap: nowrap; overflow-x: auto; width: 100%;">
      <draggable
        v-model="epics"
        group="epics"
        animation="200"
        item-key="id"
        direction="horizontal"
        style="display: flex; flex-wrap: nowrap; overflow-x: auto;"
      >
        <template #item="{ element: epic }">
          <a-col :key="epic.id" style="flex: 0 0 500px; max-width: 500px;">
            <a-card
              style="height: 1000px; overflow-y: auto;"
            >
              <!-- 카드 제목 -->
              <template #title>
                <div @click="setEpicTitleEdit(epic)">
                  <a-input
                    v-if="epic.isTitleEditing"
                    ref="epicTitleInput"
                    v-model:value="epic.title"
                    @blur="endEpicTitleEdit(epic)"
                    @pressEnter="epic.isTitleEditing = false"
                  />
                  <span style="font-size: 20px" v-else>
                    <div v-if="epic.title === ''">
                      제목이 없습니다
                    </div>
                    <div v-else>
                      {{ epic.title }}
                    </div>
                  </span>
                </div>
              </template>

              <!-- 카드 헤더 오른쪽 삭제 버튼 -->
              <template #extra>
                <a @click="onDeleteEpic(epic)" style="color: red; cursor: pointer;">삭제</a>
              </template>

              <a-button
                type="primary"
                block
                size="large"
                @click="onAddTodo(epic)"
                style="margin-bottom: 12px;"
              >
                할일 추가
              </a-button>

              <a-list size="small" :data-source="epic.todo">
                <!-- todo 리스트 드래그 유지 -->
                <draggable
                  v-model="epic.todo"
                  group="tasks"
                  animation="200"
                  item-key="id"
                  style="min-height: 900px; width: 100%; overflow-y: auto;"
                  @change="onTaskChange(epic, $event)"
                >
                  <template #item="{ element: todo }">
                    <a-list-item>
                      <template #actions>
                        <div style="display: flex; flex-direction: row; gap: 8px;">
                          <div v-if="todo.completed" style="display: flex; flex-direction: column; gap: 4px;">
                            <a @click="cancelCompleteTodo(epic, todo)">미완료</a>
                            <!-- <a @click="addAlert(todo)">알림</a> -->
                          </div>
                          <div v-else style="display: flex; flex-direction: column; gap: 4px;">
                            <a @click="onToggleTodoComplete(epic, todo)">완료</a>
                            <!-- <a @click="addAlert(todo)">알림</a> -->
                          </div>
                          <div style="display: flex; flex-direction: row; gap: 8px;">
                            <a @click="onDeleteTodo(epic, todo)" style="color: red; cursor: pointer;">삭제</a>
                          </div>
                        </div>
                      </template>

                      <a-list-item-meta>
                        <template #title>
                          <div @click="setTitleEdit(todo)">
                            <a-input
                              v-if="todo.isTitleEditing"
                              ref="titleInput"
                              v-model:value="todo.title"
                              @blur="endTitleEdit(epic, todo)"
                              @pressEnter="todo.isTitleEditing = false"
                            />
                            <span style="font-size: 16px" v-else>
                              <div v-if="todo.completed">
                                <div v-if="todo.title === ''">
                                  <s>제목이 없습니다</s>
                                </div>
                                <div v-else>
                                  <s>{{ todo.title }}</s>
                                </div>
                              </div>
                              <div v-else>
                                <div v-if="todo.title === ''">
                                  제목이 없습니다
                                </div>
                                <div v-else>
                                  {{ todo.title }}
                                </div>
                              </div>
                            </span>
                          </div>
                        </template>

                        <template #description>
                          <div @click="setDescriptionEdit(todo)">
                            <a-textarea
                              v-if="todo.isDescriptionEditing"
                              ref="descriptionInput"
                              v-model:value="todo.description"
                              auto-size
                              @blur="endDescriptionEdit(epic, todo)"
                            />
                            <span v-else>
                              <div v-if="todo.completed">
                                <div v-if="todo.description === ''">
                                  <s>세부내용이 없습니다</s>
                                </div>
                                <div v-else>
                                  <s>{{ todo.description }}</s>
                                </div>
                              </div>
                              <div v-else>
                                <div v-if="todo.description === ''">
                                  세부내용이 없습니다
                                </div>
                                <div v-else>
                                  {{ todo.description }}
                                </div> 
                              </div>
                            </span>
                          </div>
                          
                          <div style="margin-top: 4px;">
                            <a-date-picker
                              v-model:value="todo.dueDate"
                              value-format="YYYY-MM-DDTHH:mm:ss"
                              format="YYYY-MM-DD"
                              style="width: 50%;"
                              placeholder="마감일"
                              @change="onDueDateChange(epic, todo)"
                            />
                          </div>
                        </template>
                      </a-list-item-meta>
                    </a-list-item>
                  </template>
                </draggable>
              </a-list>
            </a-card>
          </a-col>
        </template>
      </draggable>
    </a-row>
  </div>
</template>

<script setup lang="ts">
import { nextTick, onMounted, ref } from 'vue';
import draggable from 'vuedraggable';
import { message } from 'ant-design-vue';
import { createEpic, createTask, deleteEpic, deleteTask, fetchEpics, sortTask, updateEpic, updateTask } from '@/api/todoApi';

const epicTitleInput = ref(null)
const titleInput = ref(null)
const descriptionInput = ref(null)

const epics = ref([])

const onAddEpic = async () => {
  try {
    const requestBody = {
      title: `새로운 카드`,
    }

    const response = await createEpic(requestBody);
    if(response.status !== 200) throw new Error();

    const epic = response.data;
    epics.value.push({ id: epic.id, title: epic.title, isTitleEditing: false, todo: [] });
    
  } catch (error) {
    message.error('카드 추가에 실패했습니다.');
  }
}

const onAddTodo = async (epic) => {
  try {
    const requestBody = {
      epicId: epic.id,
      title: `새로운 할 일`,
      content: '',
      startAt: null,
      sortOrder: 0,
    }

    const response = await createTask(requestBody);
    if(response.status !== 200) throw new Error();

    const todo = response.data;
    epic.todo.push({ id: todo.id, title: todo.title, description: todo.content, isTitleEditing: false, completed: false, sortOrder: 0 });
    
  } catch (error) {
    message.error('할 일 추가에 실패했습니다.');
  }
};

const setEpicTitleEdit = (epic) => {
  epic.isTitleEditing = true;
  nextTick(() => {
    if (epicTitleInput.value?.focus) {
      epicTitleInput.value.focus()
    }
  })
}

const endEpicTitleEdit = async (epic) => {

  try {
    let requestBody = {
      epicId: epic.id,
      title: epic.title
    }
    
    const response = await updateEpic(requestBody);
    if(response.status !== 200) throw new Error();

    epic.isTitleEditing = false
  } catch (error) {
    message.error('할 일 수정에 실패했습니다.');
    return;
  }
}

const setTitleEdit = (todo) => {
  todo.isTitleEditing = true
  nextTick(() => {
    if (titleInput.value?.focus) {
      titleInput.value.focus()
    }
  })
}

const endTitleEdit = async (epic, todo) => {

  try {
    let requestBody = {
      taskId: todo.id,
      epicId: epic.id,
      title: todo.title,
      content: todo.description,
      startAt: todo.dueDate,
      sortOrder: todo.sortOrder,
      completed: todo.completed
    }
    
    const response = await updateTask(requestBody);
    if(response.status !== 200) throw new Error();

    todo.isTitleEditing = false

  } catch (error) {
    message.error('할 일 수정에 실패했습니다.');
    return;
  }
}

const setDescriptionEdit = (todo) => {
  todo.isDescriptionEditing = true
  nextTick(() => {
    if (descriptionInput.value?.focus) {
      descriptionInput.value.focus()
    }
  })
}

const endDescriptionEdit = async (epic, todo) => {

  try {
    let requestBody = {
      taskId: todo.id,
      epicId: epic.id,
      title: todo.title,
      content: todo.description,
      startAt: todo.dueDate,
      sortOrder: todo.sortOrder,
      completed: todo.completed
    }
    
    const response = await updateTask(requestBody);
    if(response.status !== 200) throw new Error();

    todo.isDescriptionEditing = false

  } catch (error) {
    message.error('할 일 수정에 실패했습니다.');
    return;
  }
}

const onDueDateChange = async (epic, todo) => {
  try {
    let requestBody = {
      taskId: todo.id,
      epicId: epic.id,
      title: todo.title,
      content: todo.description,
      startAt: todo.dueDate,
      sortOrder: todo.sortOrder,
      completed: todo.completed
    }
    
    const response = await updateTask(requestBody);
    if(response.status !== 200) throw new Error();

  } catch (error) {
    message.error('할 일 수정에 실패했습니다.');
    return;
  }
}

const onToggleTodoComplete = async (epic, todo) => {
  try {
    let requestBody = {
      taskId: todo.id,
      epicId: epic.id,
      title: todo.title,
      content: todo.description,
      startAt: todo.dueDate,
      sortOrder: todo.sortOrder,
      completed: true
    }
    
    const response = await updateTask(requestBody);
    if(response.status !== 200) throw new Error();

    todo.completed = true;
  } catch (error) {
    message.error('할 일 완료에 실패했습니다.');
    return;
  }
}

const cancelCompleteTodo = async (epic, todo) => {

  try {
    let requestBody = {
      taskId: todo.id,
      epicId: epic.id,
      title: todo.title,
      content: todo.description,
      startAt: todo.dueDate,
      sortOrder: todo.sortOrder,
      completed: false
    }

    const response = await updateTask(requestBody);
    if(response.status !== 200) throw new Error();
    
    todo.completed = false;
  } catch (error) {
    message.error('할 일 완료 취소에 실패했습니다.');
    return;
  }
}

const onDeleteTodo = async (epic, todo) => {
  try {
    const response = await deleteTask(todo.id);
    if(response.status !== 200) throw new Error();

    epic.todo = epic.todo.filter(t => t.id !== todo.id);
    
  } catch (error) {
    message.error('할 일 삭제에 실패했습니다.');
  }
}

const onDeleteEpic = async (epic) => {
  try {
    await deleteEpic(epic.id);

    epics.value = epics.value.filter(e => e.id !== epic.id);
    
  } catch (error) {
    message.error('카드 삭제에 실패했습니다.');
  }
}

const onTaskChange = async (targetEpic, evt) => {
  // 다른 epic 으로 이동
  if (evt.added) {

    // 1. epic으로 온 task 수정
    const todo = evt.added.element;
    const epicId = targetEpic.id;

    try {
      let requestBody = {
        taskId: todo.id,
        epicId: epicId,
        title: todo.title,
        content: todo.description,
        startAt: todo.dueDate,
        sortOrder: evt.added.newIndex,
        completed: todo.completed
      }
      
      const response = await updateTask(requestBody);
      if(response.status !== 200) throw new Error();
    } catch (error) {
      message.error('할 일 정렬에 실패했습니다.');
      return;
    }

    // 2. epic 내의 task 순서 재정렬
    try {
      let requestBody = {
        epicId: epicId,
        taskIds: targetEpic.todo.map(todo => todo.id),
      }
      
      const response = await sortTask(requestBody);
      if(response.status !== 200) throw new Error();
    } catch (error) {
      message.error('할 일 정렬에 실패했습니다.');
      return;
    }
  }

  // 같은 epic 내 순서 변경
  if (evt.moved) {
    const epicId = targetEpic.id;

    // 1. epic 내의 task 순서 재정렬
    try {
      let requestBody = {
        epicId: epicId,
        taskIds: targetEpic.todo.map(todo => todo.id),
      }
      
      const response = await sortTask(requestBody);
      if(response.status !== 200) throw new Error();

    } catch (error) {
      message.error('할 일 정렬에 실패했습니다.');
    }
  }
}

const fetchTodos = async () => {
  try {
    const response = await fetchEpics();
    if(response.status !== 200) throw new Error();

    epics.value = response.data

  } catch (error) {
    message.error('목록을 불러올 수 없습니다.');
  }
}

onMounted(() => {
  
  fetchTodos();
});

</script>

<style scoped>

</style>