<template>
  <div>
    <a-modal
      v-model:open="open"
      title="지출내역생성"
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
          label="카드내역"
          name="message"
          :rules="[{ required: true, message: '카드내역을 입력하세요.' }]"
        >
          <a-textarea v-model:value="formState.message" :rows="5" placeholder="카드내역을 복사 붙여넣기하세요."/>
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script lang="ts" setup>
import api from '@/api/axios'
import { message } from 'ant-design-vue';
import { reactive, ref } from 'vue';

const formRef = ref()
const loading = ref(false);
const open = ref(false);
const callback = ref(null);

const formState = reactive({
  message: '',
})

const show = (cb) => {
  callback.value = cb ?? null;
  open.value = true;
};

const handleOk = async () => {
  try {
    await formRef.value.validate(); // form 검증

    const response = await api.post('/api/economy/spend', formState, {
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${sessionStorage.getItem('accessToken')}`
      }
    });
    
    open.value = false;
    formRef.value.resetFields()
    message.success('지출내역이 생성되었습니다.');
    callback.value?.(); // 행위 수행
  } catch (error) {
    console.log(error)
  }
};

const handleCancel = () => {
  open.value = false;
  formRef.value.resetFields() // 또는 resetFields() 사용
};

defineExpose({ show });
</script>