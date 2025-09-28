<template>
  <a-row :gutter="[5, 5]" style="margin-bottom: 20px">
    <a-col :span="4">
      <a-input
        v-model:value="name"
        style="width: 100%"
        placeholder="단어를 검색하세요."
      />
    </a-col>
    <a-col :span="5">
      <a-range-picker v-model:value="insertDateRange" style="width: 340px" />
    </a-col>

    <!-- 정렬 선택 -->
    <a-col :span="3">
      <a-select v-model:value="sort" style="width: 200px" placeholder="정렬 선택">
        <a-select-option value="view,desc">조회순</a-select-option>
        <a-select-option value="createdAt,desc">최신순</a-select-option>
        <a-select-option value="createdAt,asc">오래된순</a-select-option>
        <a-select-option value="name,asc">이름 ↑</a-select-option>
        <a-select-option value="name,desc">이름 ↓</a-select-option>
      </a-select>
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
          <div v-if="wordStatus.totalCount === wordStatus.learnedCount" style="padding: 5px; background: #e6f7ff; border-radius: 8px; color: #096dd9; font-size: 14px;">
            단어를 모두 외우셨네요! 🎉  
          </div>

          <div v-if="wordStatus.totalCount > wordStatus.learnedCount" style="padding: 5px; background: #e6f7ff; border-radius: 8px; color: #096dd9; font-size: 14px;">
            총 {{wordStatus.totalCount}}개 중 
            {{wordStatus.learnedCount}}개를 이미 외운 것 같네요! 🎉  
            아직 {{wordStatus.unLearnedCount}}개 외우는 중인 것 같아요.
          </div>
        </a-col>
        <a-col>
          <!-- 0번일 때 -->
          <div
            v-if="gameAttempts === 0"
            style="padding: 5px; background: #fff1f0; border-radius: 8px; color: #cf1322; font-size: 14px;"
          >
            아직 단어를 외우지 않으셨네요! 😢
          </div>

          <!-- 1~3번일 때 -->
          <div
            v-else-if="gameAttempts > 0 && gameAttempts < 4"
            style="padding: 5px; background: #fffbe6; border-radius: 8px; color: #d48806; font-size: 14px;"
          >
            좋은 시작이에요! 지금까지 {{ gameAttempts }}번 도전했네요 💪
          </div>

          <!-- 4번 이상일 때 -->
          <div
            v-else
            style="padding: 5px; background: #f6ffed; border-radius: 8px; color: #389e0d; font-size: 14px;"
          >
            멋져요! 벌써 {{ gameAttempts }}번 도전했어요! 🚀
          </div>
        </a-col>
        <a-col>
          <a-button @click="onGame">단어외우기</a-button>
        </a-col>
        <a-col>
          <a-button type="primary" @click="onCreate">등록</a-button>
        </a-col>
      </a-row>
    </template>
    <template #bodyCell="{ column, record }">
      <template v-if="column.key === 'createdAt'">
        {{ utcToKst(record.createdAt) }}
      </template>
    </template>
  </a-table>

  <WordCreateModal ref="wordCreateModal" />
  <Word ref="word" />
  <CommonModal ref="deleteModalRef"/>
  <CommonModal ref="deductedModalRef"/>
  <CommonModal ref="cancelDeductedModalRef"/>
</template>

<script lang="ts" setup>
import { onMounted, ref, h, computed } from 'vue'
import type { Dayjs } from 'dayjs';
import { SearchOutlined } from '@ant-design/icons-vue';
import { message } from 'ant-design-vue';
import CommonModal from '@/components/CommonModal.vue';
import WordCreateModal from '@/components/WordCreateModal.vue';
import Word from '@/components/Word.vue';
import { fetchAttempts, fetchWords, fetchWordStatus } from '@/api/wordApi.js';
import { useDate } from '@/utils/useDate';

type RangeValue = [Dayjs, Dayjs];

const searchParams = ref({
  status: '',
  startDate: null,
  endDate: null,
  page: 0,
  size: 20
});

const name = ref('');
const insertDateRange = ref<RangeValue>();
const sort = ref('정렬 선택');

const deleteModalRef = ref();
const deductedModalRef = ref();
const cancelDeductedModalRef = ref();
const wordCreateModal = ref();
const word = ref();
const gameAttempts = ref(0);
const wordStatus = ref({
  totalCount: 0,
  learnedCount: 0,
  unLearnedCount: 0
});

const { utcToKst } = useDate();

const columns = [
  {
    title: 'No',
    dataIndex: 'key',
    key: 'key',
  },
  {
    title: '단어',
    dataIndex: 'name',
    key: 'name',
  },
  {
    title: '의미',
    dataIndex: 'mean',
    key: 'mean',
  },
  {
    title: '조회수',
    dataIndex: 'view',
    key: 'view',
  },
  {
    title: '등록날짜',
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

const getWords = async (params) => {
  try {
    const response = await fetchWords(params);
    if(response.status !== 200) throw new Error();

    const totalElements = response.data.totalElements;

    pagination.value.total = totalElements

    data.value = response.data.content.map((item, index) => {
      item.key = totalElements - (params.page * params.size + index);
      return item;
    });
  } catch (error) {
    message.error('단어를 불러올 수 없습니다.');
  }
};

const getAttempts = async () => {
  try {
    const response = await fetchAttempts();
    if(response.status !== 200) throw new Error();

    gameAttempts.value = response.data;
  } catch (error) {
    message.error('도전 횟수를 불러올 수 없습니다.');
    return 0;
  }
}

const getWordStatus = async () => {
  try {
    const response = await fetchWordStatus();
    if(response.status !== 200) throw new Error();

    wordStatus.value = response.data;
  } catch (error) {
    message.error('단어 상태를 불러올 수 없습니다.');
  }
}

const handleTableChange = (paginationConfig) => {
  const { current, pageSize } = paginationConfig
  pagination.value.current = current
  pagination.value.pageSize = pageSize

  searchParams.value.page = current - 1;
  searchParams.value.size = pageSize;

  getWords(searchParams.value)
}

const search = () => {
  const params: any = {
    page: 0,
    size: pagination.value.pageSize,
  };

  // if (status.value && status.value !== '전체') {
  //   params.status = deductedValue.value;
  // }
  if(name.value) {
    params.name = name.value;
  }

  if (insertDateRange.value) {
    params.startDate = insertDateRange.value[0].format('YYYY-MM-DD');
    params.endDate = insertDateRange.value[1].format('YYYY-MM-DD');
  }

  if (sort.value) {
    const [field, direction] = sort.value.split(",");
    params.sortBy = field;
    params.order = direction;
  }

  searchParams.value = params;

  getWords(params);
};

const reset = () => {

  name.value = '';
  insertDateRange.value = null;

  searchParams.value = {
    status: '',
    startDate: null,
    endDate: null,
    page: 0,
    size: pagination.value.pageSize
  };

  pagination.value.current = 1;

  getWords(searchParams.value);
}

const onCreate = () => {
  wordCreateModal.value.show(() => {
    getWords(searchParams.value);
  });
}

const onGame = () => {
  word.value.show(() => {
    getAttempts();
    getWordStatus();
  });
}

onMounted(() => {
  const params: any = {
    page: 0,
    size: pagination.value.pageSize,
  };

  getWords(params);

  getAttempts();

  getWordStatus();
});
</script>