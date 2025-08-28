<script setup>
</script>

<template>
  <a-row style="margin-bottom: 20px">
    <a-col :span="24">
      <a-input v-model:value="title" style="width: 300px" placeholder="권한그룹명을 검색하세요." />
      <a-select
        v-model:value="status"
        style="width: 120px"
        :options="statuses.map(pro => ({ value: pro }))"
      ></a-select>
      <a-range-picker v-model:value="insertDateRange" style="width: 300px" />
    </a-col>

    <a-col :span="24">
      <a-button :icon="h(SearchOutlined)" @click="search">Search</a-button>
      <a-button @click="reset">Reset</a-button>
    </a-col>
  </a-row>

  <!-- LIST -->
  <a-table :columns="columns" :data-source="data" :pagination="pagination" @change="handleTableChange">
    <template #title>
      <a-row :gutter="5" justify="end">
        <a-col>
          <a-button @click="onExport">엑셀 다운로드</a-button>
        </a-col>
        <a-col>
          <a-button type="primary" @click="onCreate">등록</a-button>
        </a-col>
      </a-row>
    </template>
    <template #bodyCell="{ column, record }">
      <template v-if="column.key === 'title'">
        <a>
          {{ record.title }}
        </a>
      </template>
    </template>
  </a-table>
</template>

<script lang="ts" setup>
import api from '@/api/axios'
import { onMounted, ref, h } from 'vue'
import type { Dayjs } from 'dayjs';
import { message } from 'ant-design-vue';
import { SearchOutlined } from '@ant-design/icons-vue';
type RangeValue = [Dayjs, Dayjs];

const title = ref('');
const searchParams = ref({
  page: 0,
  pageSize: 20
});
const statuses = ['전체', 'TEMP', 'REGISTER', 'DELETE'];
const status = ref(statuses[0]);
const insertDateRange = ref<RangeValue>();

const columns = [
  {
    title: 'No',
    dataIndex: 'key',
    key: 'key',
  },
  {
    title: '권한그룹명',
    dataIndex: 'name',
    key: 'name',
  },
  {
    title: '권한그룹내용',
    dataIndex: 'content',
    key: 'content',
  },
  {
    title: '등록일',
    key: 'createdAt',
    dataIndex: 'createdAt',
  },
//   {
//     title: 'Action',
//     key: 'action',
//   },
];

const data = ref([]);
const pagination = ref({
  current: 1,
  pageSize: 20,
  position: ['bottomCenter'],
  total: 0,
})

const fetchRoles = async (params) => {
  try {
    const response = await api.get('/api/role/search', {
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${sessionStorage.getItem('accessToken')}`
      },
      params: params
    });
    if(response.status !== 200) throw new Error();

    const totalElements = response.data.totalElements;
    pagination.value.total = totalElements
    data.value = response.data.content.map((item, index) => {
      item.key = totalElements - (params.page * params.pageSize + index);
      return item;
    });

  } catch (error) {
    message.error('권한그룹을 불러올 수 없습니다.');
  }
};

const handleTableChange = (paginationConfig) => {
  const { current, pageSize } = paginationConfig
  pagination.value.current = current
  pagination.value.pageSize = pageSize

  searchParams.value.page = current - 1;
  searchParams.value.pageSize = pageSize;

  fetchRoles(searchParams.value)
}

const search = () => {
  const params: any = {
    page: 0,
    pageSize: pagination.value.pageSize,
  };

  if (title.value) {
    params.title = title.value;
  }
  if (status.value && status.value !== '전체') {
    params.status = status.value;
  }
  if (insertDateRange.value) {
    params.startDate = insertDateRange.value[0].format('YYYY-MM-DD');
    params.endDate = insertDateRange.value[1].format('YYYY-MM-DD');
  }

  searchParams.value = params;

  fetchRoles(params);
};

const reset = () => {
  searchParams.value = {
    page: 0,
    pageSize: pagination.value.pageSize
  };

  pagination.value.current = 1;

  fetchRoles(searchParams.value);
}

const onCreate = () => {
  // Logic to handle record registration
  console.log("Register new record");
}

const onExport = () => {
  // Logic to handle export functionality
  console.log("Export records to Excel");
};

onMounted(() => {
  const params: any = {
    page: 0,
    pageSize: pagination.value.pageSize,
  };

  fetchRoles(params);
});
</script>