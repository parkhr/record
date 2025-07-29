<template>
  <div>
    <a-modal
      v-model:open="open"
      title="권한그룹수정"
      @ok="handleOk"
      @cancel="handleCancel"
    >
      <template #footer>
        <a-button key="back" @click="handleCancel">Return</a-button>
        <a-button key="submit" type="primary" :loading="loading" @click="handleOk">Submit</a-button>
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
          label="권한그룹명"
          name="name"
          :rules="[{ required: true, message: '권한그룹명을 입력하세요.' }]"
        >
          <a-input v-model:value="formState.name" />
        </a-form-item>

        <a-form-item
          label="권한그룹내용"
          name="content"
          :rules="[{ required: true, message: '권한그룹내용을 입력하세요.' }]"
        >
          <a-input v-model:value="formState.content" />
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script lang="ts" setup>
import api from '@/api/axios'
import { message } from 'ant-design-vue';
import { onMounted, reactive, ref } from 'vue';

const formRef = ref()
const loading = ref(false);
const open = ref(false);
const callback = ref(null);
// const roleId = ref(null);

const formState = reactive({
  name: '',
  content: '',
})

const show = (roleId, cb) => {
  callback.value = cb ?? null;
  open.value = true;
  // roleId.value = roleId;
  fetchRoleDetail(roleId);
};

const fetchRoleDetail = async (roleId) => {
  try {
    const response = await api.get('/api/role/' + roleId, {
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${sessionStorage.getItem('accessToken')}`
      },
    });

    console.log(response.data);

  } catch (error) {
    console.error("Error fetching records:", error);
  }
};

const handleOk = async () => {
  try {
    await formRef.value.validate(); // form 검증

    const response = await api.put('/api/role', formState, {
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${sessionStorage.getItem('accessToken')}`
      }
    });
    
    open.value = false;
    formRef.value.resetFields()
    message.success('권한그룹이 수정되었습니다.');
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