<template>
  
  <!-- SEARCH -->
  <a-row :gutter="[5, 5]" style="margin-bottom: 20px">
    <a-col :span="4">
      <a-input v-model:value="title" style="width: 100%" placeholder="기록물명을 검색하세요." />
    </a-col>
    <a-col :span="2">
      <a-select
        v-model:value="status"
        style="width: 100%"
        :options="statuses.map(pro => ({ value: pro }))"
      ></a-select>
    </a-col>
    <a-col :span="3">
      <a-range-picker v-model:value="insertDateRange" style="width: 300px" />
    </a-col>

    <a-col :span="24"></a-col>

    <a-col :span="1">
      <a-button :icon="h(SearchOutlined)" @click="search" style="width: 100%">Search</a-button>
    </a-col>
    <a-col :span="1">
      <a-button @click="reset" style="width: 100%">Reset</a-button>
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
      <template v-else-if="column.key === 'action'">
        <span>
          <!-- <a-divider type="vertical" /> -->
          <a @click="onDelete(record)">Delete</a>
          <!-- <a-divider type="vertical" /> -->
        </span>
      </template>
    </template>
  </a-table>

  <CommonModal ref="deleteModalRef"/>
</template>
<script lang="ts" setup>
import api from '@/api/axios'
import { onMounted, ref, h } from 'vue'
import type { Dayjs } from 'dayjs';
import { SearchOutlined } from '@ant-design/icons-vue';
import CommonModal from './CommonModal.vue';
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

const deleteModalRef = ref();

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
  {
    title: 'Action',
    key: 'action',
  },
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

const reset = () => {
  title.value = '';
  status.value = statuses[0];
  insertDateRange.value = null;

  searchParams.value = {
    title: '',
    status: '',
    startDate: null,
    endDate: null,
    page: 0,
    pageSize: pagination.value.pageSize
  };

  pagination.value.current = 1;

  fetchRecords(searchParams.value);
}

const onCreate = () => {
  // Logic to handle record registration
  console.log("Register new record");
}

const onExport = () => {
  // Logic to handle export functionality
  console.log("Export records to Excel");
};

const onDelete = (record) => {
  deleteModalRef.value?.show('기록물삭제', record.title + '을 삭제하시겠습니까?', 
    async () => {

      try {
        const response = await api.delete('/api/record/' + record.id, {
          headers: {
            'Content-Type': 'application/json',
            'Authorization': `Bearer ${sessionStorage.getItem('accessToken')}`
          },
        });

        alert("기록물 삭제되었습니다.")
      } catch (error) {
        alert("기록물 삭제 실패")
      }

      fetchRecords(searchParams.value);
    });
}

onMounted(() => {
  const params: any = {
    page: 0,
    pageSize: pagination.value.pageSize,
  };

  fetchRecords(params);
});
</script>
