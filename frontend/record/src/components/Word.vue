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
          <a-card style="margin: 0 auto; position: relative;">
            <!-- 단어 -->
            <div
              style="
                display: flex;
                justify-content: center;   /* ✅ 가운데 정렬 */
                align-items: center;
                gap: 10px;                  /* 단어-아이콘 간격 */
                font-size: 27px;
                font-weight: bold;
                margin-bottom: 10px;
              "
            >
              <span>{{ words[currentIndex].word }}</span>
              <span
                role="button"
                aria-label="speak"
                style="font-size: 20px; color: #1677ff; cursor: pointer;"
                @click="speak(words[currentIndex].word)"
              >
                🔊
              </span>
            </div>
            <!-- 예문 -->
            <p style="font-size: 16px; color: #555; margin-bottom: 15px;">
              {{ words[currentIndex].example }}
            </p>
            <!-- 뜻 (클릭해서 토글) -->
            <a-row>
              <a-col :span="24">
                <div
                  style="font-size: 18px; font-weight: bold; color: #1677ff; cursor: pointer;"
                  @click="showMeaning = !showMeaning"
                >
                  {{ showMeaning ? words[currentIndex].meaning : '뜻 보기 👀' }}
                </div>
              </a-col>
            </a-row>
          </a-card>

          <!-- 버튼 영역 -->
          <a-row style="margin-top: 20px;">
            <a-col :span="12">
              <a-button type="primary" danger @click="handleCheck(true)">O</a-button>
              <div style="margin-top: 20px;">
                ✅ 외운 단어
              </div>
              <a-statistic :value="learned.length" />
            </a-col>
            <a-col :span="12">
              <a-button type="primary" @click="handleCheck(false)">X</a-button>
              <div style="margin-top: 20px;">
                ❌ 못 외운 단어
              </div>
              <a-statistic :value="notLearned.length" />
            </a-col>
          </a-row>
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

const speak = (text: string) => {
  if ("speechSynthesis" in window) {
    const utterance = new SpeechSynthesisUtterance(text);

    // 튜닝 옵션 적용
    utterance.lang = "en-US"; // 영어 읽기
    utterance.rate = 1;       // 속도 (0.5 ~ 2)
    utterance.pitch = Math.random() * 2; // 음높이 (0 ~ 2)

    speechSynthesis.cancel(); // 이전 발음 중지
    speechSynthesis.speak(utterance);
  } else {
    alert("이 브라우저는 음성 합성을 지원하지 않습니다.");
  }
};

const show = async (cb?: Function) => {
  callback.value = cb ?? null;
  open.value = true;

  try{
    const response = await game();
    words.value = response.data;
    callback.value?.();
    
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

      const response = await updateWord(requestData);
      if(response.status !== 200) throw new Error();

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

      const response = await updateWord(requestData);
      if(response.status !== 200) throw new Error();

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
