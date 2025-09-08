<template>
  <div>
    <a-modal
      v-model:open="open"
      title="활동내역생성"
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
          label="유형"
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
          label="분"
          name="minutes"
          :rules="[{ required: true, message: '분을 입력하세요.' }]"
        >
          <a-input v-model:value="formState.minutes" placeholder="분을 입력하세요."/>
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script lang="ts" setup>
import { createActive } from '@/api/active';
import { message } from 'ant-design-vue';
import { reactive, ref } from 'vue';

const formRef = ref()
const loading = ref(false);
const open = ref(false);
const callback = ref(null);

const formState = reactive({
  minutes: 0,
  type: '개발'
})

const show = (cb) => {
  callback.value = cb ?? null;
  open.value = true;
};

const handleOk = async () => {
  try {
    await formRef.value.validate(); // form 검증

    if (formState.minutes <= 0) {
      message.error('활동시간은 0분 보다 커야 합니다.');
      return;
    } else {

      const requestBody = {
          minutes: formState.minutes,
          type: formState.type
      }

      const response = await createActive(requestBody);
      if(response.status !== 200) throw new Error();

      open.value = false;
      formRef.value.resetFields()
      message.success('활동내역이 생성되었습니다.');
      callback.value?.(); // 행위 수행
    }
  } catch (error) {
    message.error('활동내역이 생성 실패하였습니다.');
  }
};

const handleCancel = () => {
  open.value = false;
  formRef.value.resetFields() // 또는 resetFields() 사용
};

defineExpose({ show });
</script>