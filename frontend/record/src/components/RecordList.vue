<template>
  <a-space>
    <a-input v-model:value="title" placeholder="기록물명을 검색하세요." />
    <a-select
      v-model:value="status"
      style="width: 120px"
      :options="statuses.map(pro => ({ value: pro }))"
    ></a-select>
    <a-range-picker v-model:value="insertDateRange" />
    <a-button :icon="h(SearchOutlined)" @click="search">Search</a-button>
  </a-space>

  <a-table :columns="columns" :data-source="data" :pagination="pagination" @change="handleTableChange">
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
import { SearchOutlined } from '@ant-design/icons-vue';
type RangeValue = [Dayjs, Dayjs];

const title = ref('');
const searchParams = ref({
  title: '',
  status: '',
  startDate: null,
  endDate: null,
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
    title: '기록물명',
    dataIndex: 'title',
    key: 'title',
  },
  {
    title: '상태',
    dataIndex: 'status',
    key: 'status',
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

const fetchRecords = async (params) => {
  try {
    const response = await api.get('/api/record/search', {
      headers: {
        'Content-Type': 'application/json',
        'Authorization': `Bearer ${sessionStorage.getItem('accessToken')}`
      },
      params: params
    });

    const totalElements = response.data.totalElements;
    pagination.value.total = totalElements
    data.value = response.data.content.map((item, index) => {
      item.key = totalElements - (params.page * params.pageSize + index);
      return item;
    });

  } catch (error) {
    console.error("Error fetching records:", error);
  }
};

const handleTableChange = (paginationConfig) => {
  const { current, pageSize } = paginationConfig
  pagination.value.current = current
  pagination.value.pageSize = pageSize

  searchParams.value.page = current - 1;
  searchParams.value.pageSize = pageSize;

  fetchRecords(searchParams.value)
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

  fetchRecords(params);
};

onMounted(() => {
  const params: any = {
    page: 0,
    pageSize: pagination.value.pageSize,
  };

  fetchRecords(params);
});
</script>
