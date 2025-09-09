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
          label="지출금액"
          :rules="[{ required: true, message: '지출금액을 입력하세요.' }]"
        >
          <a-input v-model:value="formState.amount" placeholder="지출금액을 입력하세요."/>
        </a-form-item>
        <a-form-item
          label="장소"
          :rules="[{ required: true, message: '장소를 입력하세요.' }]"
        >
          <a-textarea v-model:value="formState.place" :rows="5" placeholder="장소를 입력하세요."/>
        </a-form-item>
        <a-form-item
          label="지출날짜"
          :rules="[{ required: true, message: '지출날짜를 입력하세요.' }]"
        >
          <a-date-picker
            v-model:value="formState.date"
            format="YYYY-MM-DD"
            style="width: 80%"
            placeholder="지출날짜 선택"
          />
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
  amount: '',
  place: '',
  date: ''
})

const show = (cb) => {
  callback.value = cb ?? null;
  open.value = true;
};

const handleOk = async () => {
  try {
    await formRef.value.validate(); // form 검증

    const response = await api.post('/api/economy/spend/write', formState, {
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${sessionStorage.getItem('accessToken')}`
      }
    });
    if(response.status !== 200) throw new Error();
    
    open.value = false;
    formRef.value.resetFields()
    message.success('지출내역이 생성되었습니다.');
    callback.value?.(); // 행위 수행
  } catch (error) {
    message.error('지출내역 생성 실패하였습니다.');
  }
};

const handleCancel = () => {
  open.value = false;
  formRef.value.resetFields() // 또는 resetFields() 사용
};

defineExpose({ show });
</script>