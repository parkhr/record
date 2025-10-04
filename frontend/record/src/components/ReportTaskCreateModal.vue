<template>
  <div>
    <a-modal
      v-model:open="open"
      title="데일리리포트 등록"
      @ok="handleOk"
      @cancel="handleCancel"
    >
      <template #footer>
        <a-button key="back" @click="handleCancel">Return</a-button>
        <a-button key="submit" type="primary" :loading="loading" @click="handleOk">등록</a-button>
      </template>

      
      <a-form
        ref="formRef"
        class="login-form"
        :model="formState"
        name="basic"
        :label-col="{ span: 8 }"
        :wrapper-col="{ span: 16 }"
        autocomplete="off"
      >
        <a-form-item
          label="시작시간"
          name="startTime"
          :rules="[{ required: true, message: '시작시간을 입력하세요.' }]"
        >
          <a-time-picker v-model:value="formState.startTime" format="HH:mm"/>
        </a-form-item>

        <a-form-item
          label="종료시간"
          name="endTime"
          :rules="[{ required: true, message: '종료시간을 입력하세요.' }]"
        >
          <a-time-picker v-model:value="formState.endTime" format="HH:mm"/>
        </a-form-item>

        <a-form-item
          label="작업명"
          name="title"
          :rules="[{ required: true, message: '작업명을 입력하세요.' }]"
        >
          <a-input v-model:value="formState.title" :rows="5" placeholder="작업명 입력하세요."/>
        </a-form-item>

        <a-form-item
          label="작업내용"
          name="content"
          :rules="[{ required: true, message: '작업내용을 입력하세요.' }]"
        >
          <a-textarea v-model:value="formState.content" :rows="5" placeholder="작업내용을 입력하세요."/>
        </a-form-item>

        <a-form-item
          label="타입"
          name="type"
          :rules="[{ required: true, message: '타입을 입력하세요.' }]"
        >
          <a-select
            v-model:value="formState.type"
            style="width: 200px"
            placeholder="선택하세요"
          >
            <a-select-option value="영어단어">영어단어</a-select-option>
            <a-select-option value="개발">개발</a-select-option>
            <a-select-option value="학습">학습</a-select-option>
            <a-select-option value="독서">독서</a-select-option>
          </a-select>
        </a-form-item>

        <a-form-item
          label="컬러"
          name="color"
          :rules="[{ required: true, message: '컬러를 입력하세요.' }]"
        >
          <a-select
            v-model:value="formState.color"
            style="width: 200px"
            placeholder="선택하세요"
          >
            <a-select-option value="#FDE2E4">연한 로즈 핑크</a-select-option>
            <a-select-option value="#E2F0CB">라이트 올리브 민트</a-select-option>
            <a-select-option value="#CDE7F0">소프트 하늘색</a-select-option>
            <a-select-option value="#FFF1E6">웜 베이지</a-select-option>
            <a-select-option value="#F6DFEB">연보라 핑크</a-select-option>
            <a-select-option value="#DDE7C7">세이지 그린</a-select-option>
            <a-select-option value="#EAE4E9">라일락 그레이</a-select-option>
          </a-select>
        </a-form-item>

        <a-form-item
          label="몰입"
          name="hard"
          :rules="[{ required: true, message: '몰입을 입력하세요.' }]"
        >
          <a-select
            v-model:value="formState.hard"
            style="width: 200px"
            placeholder="선택하세요"
          >
            <a-select-option value="상">상</a-select-option>
            <a-select-option value="중">중</a-select-option>
            <a-select-option value="하">하</a-select-option>
          </a-select>
        </a-form-item>

        <a-form-item
          label="컨디션"
          name="condition"
          :rules="[{ required: true, message: '컨디션을 입력하세요.' }]"
        >
          <a-select
            v-model:value="formState.condition"
            style="width: 200px"
            placeholder="선택하세요"
          >
            <a-select-option value="좋음">좋음</a-select-option>
            <a-select-option value="중간">중간</a-select-option>
            <a-select-option value="나쁨">나쁨</a-select-option>
          </a-select>
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script lang="ts" setup>
import { message } from 'ant-design-vue';
import { reactive, ref, watch } from 'vue';
import dayjs from 'dayjs';
import { createReport } from '@/api/reportApi';

const formRef = ref()
const loading = ref(false);
const open = ref(false);
const callback = ref(null);

const time = ref('');
const reportId = ref(0);

const formState = reactive({
  startTime: null,
  endTime: null,
  title: '',
  content: '',
  type: '개발',
  color: '#FDE2E4',
  hard: '중',
  condition: '중간'
})

const show = (hourParam, reportIdParam, cb) => {
  time.value = hourParam;
  reportId.value = reportIdParam;
  console.log(reportId.value)
  callback.value = cb ?? null;
  open.value = true;
};

const handleOk = async () => {
  try {
    await formRef.value.validate(); // form 검증

    const response = await createReport({
      reportId : reportId.value, 
      startTime: formState.startTime.format('YYYY-MM-DDTHH:mm:ss'),
      endTime: formState.endTime.format('YYYY-MM-DDTHH:mm:ss'),
      title: formState.title,
      content: formState.content,
      type: formState.type,
      color: formState.color,
      hard: formState.hard,
      condition: formState.condition
    });

    if(response.status !== 200) throw new Error();

    open.value = false;
    formRef.value.resetFields()
    message.success('데일리리포트가 등록되었습니다.');
    callback.value?.(); // 행위 수행
  } catch (error) {
    console.log(error)
    message.error('데일리리포트가 등록 실패하였습니다.');
  }
};

const handleCancel = () => {
  open.value = false;
  formRef.value.resetFields() // 또는 resetFields() 사용
};

watch(time, (newVal) => {
  if (newVal) {
    formState.startTime = dayjs(`${newVal}:00:00`, "HH:mm:ss");
    formState.endTime = dayjs(`${newVal}:59:00`, "HH:mm:ss");
  } else {
    formState.startTime = null;
    formState.endTime = null;
  }
});

defineExpose({ show });
</script>