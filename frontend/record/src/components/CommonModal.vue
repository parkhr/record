<template>
  <div>
    <a-modal
      v-model:open="open"
      :title="modalTitle"
      @ok="handleOk"
      @cancel="handleCancel"
    >
      <template #footer>
        <a-button key="back" @click="handleCancel">Return</a-button>
        <a-button key="submit" type="primary" :loading="loading" @click="handleOk">Submit</a-button>
      </template>
      <a-row style="margin-bottom: 20px">
        <a-col :span="24">
          {{ modalContent }}
        </a-col>
      </a-row>
    </a-modal>
  </div>
</template>

<script lang="ts" setup>
import { ref } from 'vue';

const loading = ref(false);
const open = ref(false);

const modalTitle = ref('');     // 외부에서 전달받을 제목
const modalContent = ref('');   // 외부에서 전달받을 placeholder(내용)
const callback = ref(null); // 콜백 저장

const show = (titleParam, contentParam, cb) => {
  modalTitle.value = titleParam;
  modalContent.value = contentParam;
  callback.value = cb ?? null;
  open.value = true;
};

const handleOk = () => {
  loading.value = true;
  setTimeout(() => {
    loading.value = false;
    open.value = false;
    callback.value?.();
  }, 2000);
};

const handleCancel = () => {
  open.value = false;
};

defineExpose({ show });
</script>