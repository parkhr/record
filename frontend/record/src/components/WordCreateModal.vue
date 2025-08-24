<template>
  <div>
    <a-modal
      v-model:open="open"
      title="단어등록"
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
          label="단어"
          name="name"
          :rules="[{ required: true, message: '단어를 입력하세요.' }]"
        >
          <a-input v-model:value="formState.name" placeholder="단어를 입력하세요."/>
        </a-form-item>

        <a-form-item
          label="의미"
          name="mean"
          :rules="[{ required: true, message: '의미를 입력하세요.' }]"
        >
          <a-input v-model:value="formState.mean" placeholder="의미를 입력하세요."/>
        </a-form-item>

        <a-form-item
          label="예문"
          name="sentence"
          :rules="[{ required: true, message: '예문을 입력하세요.' }]"
        >
          <a-textarea v-model:value="formState.sentence" :rows="5" placeholder="예문을 입력하세요."/>
        </a-form-item>
      </a-form>
    </a-modal>
  </div>
</template>

<script lang="ts" setup>
import api from '@/api/axios'
import { message } from 'ant-design-vue';
import { reactive, ref } from 'vue';
import { createWord } from '@/api/wordApi.js';

const formRef = ref()
const loading = ref(false);
const open = ref(false);
const callback = ref(null);

const formState = reactive({
  name: '',
  mean: '',
  sentence: '',
})

const show = (cb) => {
  callback.value = cb ?? null;
  open.value = true;
};

const handleOk = async () => {
  try {
    await formRef.value.validate(); // form 검증

    const response = await createWord({
      name: formState.name,
      mean: formState.mean,
      sentence: formState.sentence
    });

    open.value = false;
    formRef.value.resetFields()
    message.success('단어가 등록되었습니다.');
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