<template>
  <div>
    <a-modal
      v-model:open="open"
      title="단어 학습"
      @ok="handleOk"
      @cancel="handleCancel"
    >
      <template #footer>
        <a-button key="back" @click="handleCancel">그만하기</a-button>
        <!-- <a-button key="submit" type="primary" :loading="loading" @click="handleOk">
          등록
        </a-button> -->
      </template>

      <div style="padding: 20px; text-align: center">
        <!-- 카드 영역 -->
        <div v-if="currentIndex < words.length" style="margin-top: 20px">
          <a-card style="width: 350px; margin: 0 auto; position: relative;">
            <!-- 단어 -->
            <p style="font-size: 22px; font-weight: bold; margin-bottom: 10px;">
              {{ words[currentIndex].word }}
            </p>
            <!-- 예문 -->
            <p style="font-size: 16px; color: #555; margin-bottom: 15px;">
              {{ words[currentIndex].example }}
            </p>
            <!-- 뜻 (클릭해서 토글) -->
            <p
              style="font-size: 18px; font-weight: bold; color: #1677ff; cursor: pointer;"
              @click="showMeaning = !showMeaning"
            >
              {{ showMeaning ? words[currentIndex].meaning : '뜻 보기 👀' }}
            </p>
            <!-- 외웠다고 표시 -->
            <!-- <span
              v-if="learned.includes(words[currentIndex].word)"
              style="position: absolute; top: 10px; right: 15px; color: green; font-weight: bold;"
            >
              ✅ 외웠음
            </span> -->
          </a-card>

          <!-- 버튼 영역 -->
          <div style="display: flex; justify-content: center; margin-top: 20px; gap: 40px;">
            <a-button type="primary" danger @click="handleCheck(true)">O</a-button>
            <a-button type="primary" @click="handleCheck(false)">X</a-button>
          </div>
          <!-- <div style="display: flex; justify-content: center; margin-top: 20px; gap: 40px;">
            <a-button type="dashed" @click="markAsLearned">외웠다 ✅</a-button>
          </div> -->
        </div>

        <!-- 끝났을 때 -->
        <div v-else>
          <p style="margin-top: 30px; font-size: 18px; font-weight: bold;">
            🎉 모든 단어를 확인했습니다!
          </p>
        </div>

        <div style="display: flex; justify-content: center; gap: 40px">
          <div>
            ✅ 외운 단어
          </div>
          <div>
            ❌ 못 외운 단어
          </div>
        </div>
        <div style="display: flex; justify-content: center; gap: 100px;">
          <a-statistic :value="learned.length" />
          <a-statistic :value="notLearned.length" />
        </div>
      </div>
    </a-modal>
  </div>
</template>

<script lang="ts" setup>
import { game, updateWord } from "@/api/wordApi";
import { message } from "ant-design-vue";
import { onMounted, ref } from "vue";

const open = ref(false);
const callback = ref<null | Function>(null);

const words = ref([]);

const currentIndex = ref(0);
const learned = ref<string[]>([]);
const notLearned = ref<string[]>([]);
const showMeaning = ref(false);
const loading = ref(false);

const show = async (cb?: Function) => {
  callback.value = cb ?? null;
  open.value = true;

  try{
    const response = await game();
    words.value = response.data;
  } catch (error) {
    message.error('단어를 불러오는데 실패했습니다.');
  }
};

const handleOk = async () => {
  if (callback.value) {
    callback.value({ learned: learned.value, notLearned: notLearned.value });
  }
  open.value = false;
};

const handleCancel = () => {
  open.value = false;
  currentIndex.value = 0;
  learned.value = [];
  notLearned.value = [];
  showMeaning.value = false;
};

const handleCheck = async (isKnown: boolean) => {
  const word = words.value[currentIndex.value];

  if (isKnown) {
    try {
      const requestData = {
        wordId: word.wordId,
        mean: word.meaning,
        completed: word.completed + 1,
        view : word.view + 1,
        sentence: word.example
      }

      await updateWord(requestData);

      learned.value.push(word);
    } catch (error) {
      message.error('단어 업데이트에 실패했습니다.');
      return;
    }

  } else {
    try {
      const requestData = {
        wordId: word.wordId,
        mean: word.meaning,
        completed: word.completed >= 5 ? 0 : word.completed,
        view : word.view + 1,
        sentence: word.example
      }

      await updateWord(requestData);

      notLearned.value.push(word);
    } catch (error) {
      message.error('단어 업데이트에 실패했습니다.');
      return;
    }
  }

  currentIndex.value++;
  showMeaning.value = false; // 다음 단어는 뜻 숨김
};

// const markAsLearned = () => {
//   const word = words[currentIndex.value].word;
//   if (!learned.value.includes(word)) {
//     learned.value.push(word);
//   }
// };

defineExpose({ show });
</script>
