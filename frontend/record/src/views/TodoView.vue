<template>
  <div style="background-color: #f0f2f5; padding: 16px; height: 100vh;">
    <a-row :gutter="16">
      <a-col></a-col>
      <a-col :span="3" style="margin-bottom: 16px;">
        <a-button type="primary" block size="large" @click="addEpic()" style="margin-bottom: 12px;">
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
                <div @click="startEpicTitleEdit(epic)">
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
                <a @click="deleteEpic(epic)" style="color: red; cursor: pointer;">삭제</a>
              </template>

              <a-button
                type="primary"
                block
                size="large"
                @click="addTodo(epic)"
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
                          <div v-if="todo.isCompleted" style="display: flex; flex-direction: column; gap: 4px;">
                            <a @click="cancelCompleteTodo(epic, todo)">미완료</a>
                            <!-- <a @click="addAlert(todo)">알림</a> -->
                          </div>
                          <div v-else style="display: flex; flex-direction: column; gap: 4px;">
                            <a @click="completeTodo(epic, todo)">완료</a>
                            <!-- <a @click="addAlert(todo)">알림</a> -->
                          </div>
                          <div style="display: flex; flex-direction: row; gap: 8px;">
                            <a @click="deleteTodo(epic, todo)" style="color: red; cursor: pointer;">삭제</a>
                          </div>
                        </div>
                      </template>

                      <a-list-item-meta>
                        <template #title>
                          <div @click="startTitleEdit(todo)">
                            <a-input
                              v-if="todo.isTitleEditing"
                              ref="titleInput"
                              v-model:value="todo.title"
                              @blur="endTitleEdit(epic, todo)"
                              @pressEnter="todo.isTitleEditing = false"
                            />
                            <span style="font-size: 16px" v-else>
                              <div v-if="todo.isCompleted">
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
                          <div @click="startDescriptionEdit(todo)">
                            <a-textarea
                              v-if="todo.isDescriptionEditing"
                              ref="descriptionInput"
                              v-model:value="todo.description"
                              auto-size
                              @blur="endDescriptionEdit(epic, todo)"
                            />
                            <span v-else>
                              <div v-if="todo.isCompleted">
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
                          
                          <div
                            v-if="isToday(todo.dueDate)"
                            style="color: red; font-weight: bold; cursor: pointer;"
                            @click="openDatePicker(todo.id)"
                          >
                            오늘
                          </div>
                          <div style="margin-top: 4px;" v-else>
                            <a-date-picker
                              v-model:value="todo.dueDate"
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
import { nextTick, ref } from 'vue';
import draggable from 'vuedraggable';
import dayjs from 'dayjs';
import { message } from 'ant-design-vue';
import api from '@/api/axios';

const epicTitleInput = ref(null)
const titleInput = ref(null)
const descriptionInput = ref(null)
const datePickerRefs = ref({});

const epics = ref([
  { id: 1, title: '진행중', isTitleEditing: false, todo: [
    { id: 1, title: 'API 설계2', description: 'API 설계 및 문서화', isTitleEditing: false, isCompleted: false, dueDate: null, sortOrder: 0 },
    { id: 2, title: 'UI 디자인2', description: '사용자 인터페이스 디자인', isTitleEditing: false, isCompleted: false, dueDate: null, sortOrder: 0 },
    { id: 3, title: '백엔드 개발2', description: '서버 및 데이터베이스 개발', isTitleEditing: false, isCompleted: false, dueDate: null, sortOrder: 0 },
    { id: 4, title: '테스트 및 배포2', description: '애플리케이션 테스트 및 배포', isTitleEditing: false, isCompleted: false, dueDate: null, sortOrder: 0 }
  ]},
  { id: 2, title: '완료', isTitleEditing: false, todo: [
    { id: 1, title: 'API 설계2', description: 'API 설계 및 문서화', isTitleEditing: false, isCompleted: false, dueDate: null, sortOrder: 0 },
    { id: 2, title: 'UI 디자인2', description: '사용자 인터페이스 디자인', isTitleEditing: false, isCompleted: false, dueDate: null, sortOrder: 0 },
    { id: 3, title: '백엔드 개발2', description: '서버 및 데이터베이스 개발', isTitleEditing: false, isCompleted: false, dueDate: null, sortOrder: 0 },
    { id: 4, title: '테스트 및 배포2', description: '애플리케이션 테스트 및 배포', isTitleEditing: false, isCompleted: false, dueDate: null, sortOrder: 0 }
  ]},
  { id: 3, title: '보류', isTitleEditing: false, todo: [
    { id: 1, title: 'API 설계2', description: 'API 설계 및 문서화', isTitleEditing: false, isCompleted: false, dueDate: null, sortOrder: 0 },
    { id: 2, title: 'UI 디자인2', description: '사용자 인터페이스 디자인', isTitleEditing: false, isCompleted: false, dueDate: null, sortOrder: 0 },
    { id: 3, title: '백엔드 개발2', description: '서버 및 데이터베이스 개발', isTitleEditing: false, isCompleted: false, dueDate: null, sortOrder: 0 },
    { id: 4, title: '테스트 및 배포2', description: '애플리케이션 테스트 및 배포', isTitleEditing: false, isCompleted: false, dueDate: null, sortOrder: 0 }
  ]},
  { id: 4, title: '취소됨', isTitleEditing: false, todo: [
    { id: 1, title: 'API 설계2', description: 'API 설계 및 문서화', isTitleEditing: false, isCompleted: false, dueDate: null, sortOrder: 0 },
    { id: 2, title: 'UI 디자인2', description: '사용자 인터페이스 디자인', isTitleEditing: false, isCompleted: false, dueDate: null, sortOrder: 0 },
    { id: 3, title: '백엔드 개발2', description: '서버 및 데이터베이스 개발', isTitleEditing: false, isCompleted: false, dueDate: null, sortOrder: 0 },
    { id: 4, title: '테스트 및 배포2', description: '애플리케이션 테스트 및 배포', isTitleEditing: false, isCompleted: false, dueDate: null, sortOrder: 0 }
  ]}
])

const addEpic = async () => {
  try {
    const requestBody = {
      title: `새로운 카드`,
    }

    let response = await api.post('/api/economy/epic', requestBody, {
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${sessionStorage.getItem('accessToken')}`
      }
    })

    const epic = response.data;
    epics.value.push({ id: epic.id, title: epic.title, isTitleEditing: false, todo: [] });
    
  } catch (error) {
    message.error('카드 추가에 실패했습니다.');
  }
}

const addTodo = async (epic) => {
  try {
    const requestBody = {
      epicId: epic.id,
      title: `새로운 할 일`,
      content: '',
      startAt: null,
      sortOrder: 0,
    }

    let response = await api.post('/api/economy/task', requestBody, {
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${sessionStorage.getItem('accessToken')}`
      }
    })

    const todo = response.data;
    epic.todo.push({ id: todo.id, title: todo.title, description: todo.content, isTitleEditing: false, isCompleted: false, sortOrder: 0 });
    
  } catch (error) {
    message.error('할 일 추가에 실패했습니다.');
  }
};

const startEpicTitleEdit = (epic) => {
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
    
    const response = await api.put('/api/economy/epic', requestBody, {
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${sessionStorage.getItem('accessToken')}`
      }
    });

    epic.isTitleEditing = false
  } catch (error) {
    message.error('할 일 수정에 실패했습니다.');
    return;
  }
}

const startTitleEdit = (todo) => {
  // 제목 편집 모드로 진입
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
    
    const response = await api.put('/api/economy/task', requestBody, {
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${sessionStorage.getItem('accessToken')}`
      }
    });

    todo.isTitleEditing = false

  } catch (error) {
    message.error('할 일 수정에 실패했습니다.');
    return;
  }
}

const startDescriptionEdit = (todo) => {
  // 설명 편집 모드로 진입
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
    
    const response = await api.put('/api/economy/task', requestBody, {
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${sessionStorage.getItem('accessToken')}`
      }
    });

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
    
    const response = await api.put('/api/economy/task', requestBody, {
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${sessionStorage.getItem('accessToken')}`
      }
    });

  } catch (error) {
    message.error('할 일 수정에 실패했습니다.');
    return;
  }
}

const completeTodo = async (epic, todo) => {
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
    
    const response = await api.put('/api/economy/task', requestBody, {
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${sessionStorage.getItem('accessToken')}`
      }
    });

    todo.isCompleted = true;
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

    const response = await api.put('/api/economy/task', requestBody, {
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${sessionStorage.getItem('accessToken')}`
      }
    });
    
    todo.isCompleted = false;
  } catch (error) {
    message.error('할 일 완료 취소에 실패했습니다.');
    return;
  }
}

const deleteTodo = async (epic, todo) => {
  try {
    const response = await api.delete('/api/economy/task/' + todo.id, {
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${sessionStorage.getItem('accessToken')}`
      },
    });

    epic.todo = epic.todo.filter(t => t.id !== todo.id);
    
  } catch (error) {
    message.error('할 일 삭제에 실패했습니다.');
  }
}

const deleteEpic = async (epic) => {
  try {
    const response = await api.delete('/api/economy/epic/' + epic.id, {
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${sessionStorage.getItem('accessToken')}`
      },
    });

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
      
      const response = await api.put('/api/economy/task', requestBody, {
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${sessionStorage.getItem('accessToken')}`
        }
      });
    } catch (error) {
      message.error('할 일 수정에 실패했습니다.');
      return;
    }

    // 2. epic 내의 task 순서 재정렬
    try {
      let requestBody = {
        epicId: epicId,
        taskIds: targetEpic.todo.map(todo => todo.id),
      }
      
      const response = await api.post('/api/economy/task/sort', requestBody, {
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${sessionStorage.getItem('accessToken')}`
        }
      });
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
      
      const response = await api.post('/api/economy/task/sort', requestBody, {
        headers: {
          'Content-Type': 'application/json',
          'Authorization': `Bearer ${sessionStorage.getItem('accessToken')}`
        }
      });
    } catch (error) {
      message.error('할 일 정렬에 실패했습니다.');
      return;
    }
  }
}

const isToday = (date) => {
  if (!date) return false
  return dayjs(date).isSame(dayjs(), "day")
}

const openDatePicker = (id) => {
  const picker = datePickerRefs.value[id];
  if (picker) {
    picker.focus(); // 포커스 주면 바로 달력 펼쳐짐
  }
}

</script>

<style scoped>

</style>