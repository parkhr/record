<template>  
  <div>
    <a-button @click="changeLang('ko')">한국어</a-button>
    <a-button @click="changeLang('en')">English</a-button>
    <a-button @click="changeLang('zh')">中文</a-button>
  </div>
  <a-card>
    <div v-if="!isSubmitted">
      <!-- Step 표시 -->
      <a-steps :current="step - 1" size="small" class="mb-6">
        <a-step @click="moveStep(index)" v-for="(s, index) in steps" :key="index" :title="$t(s.title)" />
      </a-steps>

      <a-row style="margin-top: 20px" :gutter="[5, 5]">
        <a-col>
          <a-button @click="prevStep" class="w-full sm:w-auto" :disabled="step === 1">
            {{ $t('button.prev') }}
          </a-button>
        </a-col>
        <a-col>
          <a-button
            type="primary"
            @click="step === steps.length ? saveData() : nextStep()"
            class="w-full sm:w-auto"
          >
            {{ step === steps.length ? $t('button.save') : $t('button.next') }}
          </a-button>
        </a-col>
      </a-row>
      
      <div v-if="step === 1">
        <a-form layout="vertical" style="margin-top: 20px">
          <a-form-item :label="$t('form.label.application')">
            <a-radio-group v-model:value="form.application">
              <a-row :gutter="[5, 5]">
                <a-col>
                  <a-tooltip placement="topLeft" :title="$t('form.tooltip.application.1')">
                    <a-radio value="1">{{ $t('form.value.registerForigner') }}</a-radio>
                  </a-tooltip>
                  <a-tooltip placement="topLeft" :title="$t('form.tooltip.application.2')">
                    <a-radio value="2">{{ $t('form.value.reissueCard') }}</a-radio>
                  </a-tooltip>
                  <a-tooltip placement="topLeft" :title="$t('form.tooltip.application.3')">
                    <a-radio value="3">{{ $t('form.value.extendStay') }}</a-radio>
                  </a-tooltip>
                  <a-tooltip placement="topLeft" :title="$t('form.tooltip.application.4')">
                    <a-radio value="4">{{ $t('form.value.changeStatus') }}</a-radio>
                  </a-tooltip>
                  <a-tooltip placement="topLeft" :title="$t('form.tooltip.application.5')">
                    <a-radio value="5">{{ $t('form.value.grantStatus') }}</a-radio>
                  </a-tooltip>
                </a-col>
              </a-row>
              <a-row :gutter="[5, 5]" style="margin-top: 10px">
                <a-col>
                  <a-tooltip placement="topLeft" :title="$t('form.tooltip.application.6')">                
                    <a-radio value="6">{{ $t('form.value.permitOutsideActivity') }}</a-radio>
                  </a-tooltip>
                  <a-tooltip placement="topLeft" :title="$t('form.tooltip.application.7')">
                    <a-radio value="7">{{ $t('form.value.changeOrAddWorkplace') }}</a-radio>
                  </a-tooltip>
                  <a-tooltip placement="topLeft" :title="$t('form.tooltip.application.8')">
                    <a-radio value="8">{{ $t('form.value.reentryPermit') }}</a-radio>
                  </a-tooltip>
                  <a-tooltip placement="topLeft" :title="$t('form.tooltip.application.9')">
                    <a-radio value="9">{{ $t('form.value.reportChangeResidence') }}</a-radio>
                  </a-tooltip>
                  <a-tooltip placement="topLeft" :title="$t('form.tooltip.application.10')">
                    <a-radio value="10">{{ $t('form.value.reportChangeDetails') }}</a-radio>
                  </a-tooltip>
                </a-col>
              </a-row>
            </a-radio-group>
          </a-form-item>
          <a-form-item v-if="form.application === '4' || form.application === '5' || form.application === '6'" :label="$t('form.label.desiredStatus')">
            <a-row>
              <a-col :span="12">
                <a-input v-model:value="form.desiredStatus" placeholder="희망 자격을 입력하세요" />
              </a-col>
            </a-row>
          </a-form-item>
          <a-form-item v-if="form.application === '8'" :label="$t('form.label.singleOrMultiple')">
            <a-radio-group v-model:value="form.singleOrMultiple">
              <a-row :gutter="[5, 5]">
                <a-col>
                  <a-radio value="1">{{ $t('form.value.single') }}</a-radio>
                  <a-radio value="2">{{ $t('form.value.multiple') }}</a-radio>
                </a-col>
              </a-row>
            </a-radio-group>
          </a-form-item>
        </a-form>
      </div>

      <div v-if="step === 2">
        <a-form layout="vertical" style="margin-top: 20px">
          <!-- 성명 -->
          <a-form-item :label="$t('form.label.name')">
            <a-row :gutter="[5, 5]">
              <a-col :span="3">
                <a-input v-model:value="form.firstName" :placeholder="$t('form.placeholder.firstName')" />
              </a-col>
              <a-col :span="3">
                <a-input v-model:value="form.lastName" :placeholder="$t('form.placeholder.lastName')" />
              </a-col>
            </a-row>
          </a-form-item>
          
          <a-row :gutter="[5, 5]">
            <a-col :span="6">
              <!-- 생년월일 -->
              <a-form-item :label="$t('form.label.birth')">
                <a-date-picker
                  v-model:value="form.birth"
                  format="YYYY-MM-DD"
                  style="width: 80%"
                  :placeholder="$t('form.placeholder.birth')"
                />
              </a-form-item>
            </a-col>
            <a-col :span="6">
              <!-- 성별 -->
              <a-form-item :label="$t('form.label.gender')">
                <a-radio-group v-model:value="form.gender">
                  <a-radio :value="1">{{ $t('form.value.male') }}</a-radio>
                  <a-radio value="2">{{ $t('form.value.female') }}</a-radio>
                </a-radio-group>
              </a-form-item>
            </a-col>
            <a-col :span="6">
              <!-- 국적 -->
              <a-form-item :label="$t('form.label.nationality')">
                <a-input v-model:value="form.nationality" :placeholder="$t('form.placeholder.nationality')" />
              </a-form-item>
            </a-col>
          </a-row>

          <!-- 외국인등록번호 -->
          <a-row :gutter="[16, 16]">
            <!-- 입력 영역 -->
            <a-col :span="6">
              <a-form-item :label="$t('form.label.alienRegNo')">
                <a-input
                  v-model:value="form.foreignerId"
                  :placeholder="$t('form.placeholder.alienRegNo')"
                  style="width: 100%"
                  @input="onAlienRegNoInput"
                  :maxlength="14"
                />
                <small class="text-gray-500">
                  {{ $t('form.ex') }}) 123456-1234567
                </small>
              </a-form-item>
            </a-col>

            <!-- 안내 카드 -->
            <!-- <a-col :span="5">
              <a-card
                hoverable
                style="border-radius: 12px; text-align: center;"
              >
                <p style="margin-bottom: 12px; font-weight: 500; font-size: 14px;">
                  외국인등록번호가 기억나지 않으신가요?
                </p>
                <p style="margin-bottom: 10px; color: #555; font-size: 13px;">
                  QR 코드를 스캔하여 확인해보세요.
                </p>
                <a-image
                  :preview="false"
                  width="120"
                  src="/src/assets/check.png"
                  style="margin: 0 auto;"
                />
              </a-card>
            </a-col> -->
          </a-row>

          <a-row :gutter="[5, 5]">
            <a-col :span="6">
              <!-- 여권번호 -->
              <a-form-item :label="$t('form.label.passportNo')">
                <a-input v-model:value="form.passportNo" :placeholder="$t('form.placeholder.passportNo')" />
                <small class="text-gray-500">
                  {{ $t('form.ex') }}) 123456-1234567
                </small>
              </a-form-item>
            </a-col>
            <a-col :span="6">
              <!-- 여권 발급일자 -->
              <a-form-item :label="$t('form.label.passportIssue')">
                <a-date-picker
                  v-model:value="form.passportIssued"
                  format="YYYY-MM-DD"
                  style="width: 80%"
                  :placeholder="$t('form.placeholder.passportIssue')"
                />
              </a-form-item>
            </a-col>
            <a-col :span="6">
              <!-- 여권 유효기간 -->
              <a-form-item :label="$t('form.label.passportExpiry')">
                <a-date-picker
                  v-model:value="form.passportValid"
                  format="YYYY-MM-DD"
                  style="width: 80%"
                  :placeholder="$t('form.placeholder.passportExpiry')"
                />
              </a-form-item>
            </a-col>
          </a-row>

          <a-row :gutter="[5, 5]">
            <!-- 대한민국 내 주소 -->
            <a-col :span="6">
              <a-form-item :label="$t('form.label.koreaAddress')">
                <a-input v-model:value="form.addressKR" :placeholder="$t('form.placeholder.koreaAddress')" />
              </a-form-item>
            </a-col>
            <!-- 전화번호 -->
            <a-col :span="6">
              <a-form-item :label="$t('form.label.tel')">
                <a-input v-model:value="form.phoneKR" :placeholder="$t('form.placeholder.tel')" />
              </a-form-item>
            </a-col>
            <!-- 휴대전화 -->
            <a-col :span="6">
              <a-form-item :label="$t('form.label.mobile')">
                <a-input v-model:value="form.mobileKR" :placeholder="$t('form.placeholder.mobile')" />
              </a-form-item>
            </a-col>
          </a-row>

          <a-row :gutter="[5, 5]">
            <!-- 본국 주소 -->
            <a-col :span="6">
              <a-form-item :label="$t('form.label.homeAddress')">
                <a-input v-model:value="form.addressHome" :placeholder="$t('form.placeholder.homeAddress')" />
              </a-form-item>
            </a-col>
            <!-- 본국 전화번호 -->
            <a-col :span="6">
              <a-form-item :label="$t('form.label.homeTel')">
                <a-input v-model:value="form.phoneHome" :placeholder="$t('form.placeholder.homeTel')" />
              </a-form-item>
            </a-col>
            <a-col :span="6">
              <a-form-item :label="$t('form.label.email')">
                <a-input v-model:value="form.email" :placeholder="$t('form.placeholder.email')" />
              </a-form-item>
            </a-col>
          </a-row>
        </a-form>
      </div>

      <div v-if="step === 3">
        <a-form layout="vertical" style="margin-top: 20px">
          <a-row :gutter="[5, 5]">
            <!-- 추가된 학교/교육 항목 -->
            <a-col :span="6">
              <a-form-item :label="$t('form.label.isEnrolled')">
                <a-radio-group v-model:value="form.enrolled">
                  <a-radio value="1">{{ $t('form.value.enroll') }}</a-radio>
                  <a-radio value="2">{{ $t('form.value.notEnroll') }}</a-radio>
                </a-radio-group>
              </a-form-item>
            </a-col>
            <!-- 본국 전화번호 -->
            <a-col :span="6">
              <a-form-item :label="$t('form.label.schoolName')">
                <a-input v-model:value="form.schoolName" :placeholder="$t('form.placeholder.schoolName')" />
              </a-form-item>
            </a-col>
            <a-col :span="6">
              <a-form-item :label="$t('form.label.schoolTel')">
                <a-input v-model:value="form.schoolPhone" :placeholder="$t('form.placeholder.schoolTel')" />
              </a-form-item>
            </a-col>
          </a-row>
          <a-row :gutter="[5, 5]">
            <a-col :span="6">
              <a-form-item :label="$t('form.label.schoolType')">
                <a-input v-model:value="form.schoolType" :placeholder="$t('form.placeholder.schoolType')" />
              </a-form-item>
            </a-col>
            <a-col :span="6">
              <a-form-item :label="$t('form.label.eduApproved')">
                <a-radio-group v-model:value="form.eduApprove">
                  <a-radio value="1">{{ $t('form.value.eduApprove') }}</a-radio>
                  <a-radio value="2">{{ $t('form.value.eduNotApprove') }}</a-radio>
                </a-radio-group>
              </a-form-item>
            </a-col>
          </a-row>
        </a-form> 
      </div>

      <div v-if="step === 4">
        <a-form layout="vertical" style="margin-top: 20px">
          <a-row :gutter="[5, 5]">
            <a-col :span="6">
              <a-form-item :label="$t('form.label.workplaceOriginal')">
                <a-input v-model:value="form.workplaceOriginal" :placeholder="$t('form.placeholder.workplaceOriginal')" />
              </a-form-item>
            </a-col>
            <a-col :span="6">
              <a-form-item :label="$t('form.label.bizNoOriginal')">
                <a-input v-model:value="form.businessNoOriginal" :placeholder="$t('form.placeholder.bizNoOriginal')" />
                <small class="text-gray-500">
                  {{ $t('form.ex') }}) 123456-1234567
                </small>
              </a-form-item>
            </a-col>
            <a-col :span="6">
              <a-form-item :label="$t('form.label.telOriginal')">
                <a-input v-model:value="form.phoneOriginal" :placeholder="$t('form.placeholder.telOriginal')" />
              </a-form-item>
            </a-col>
          </a-row>
          <a-row :gutter="[5, 5]">
            <a-col :span="6">
              <a-form-item :label="$t('form.label.workplaceFuture')">
                <a-input v-model:value="form.workplaceFuture" :placeholder="$t('form.placeholder.workplaceFuture')" />
              </a-form-item>
            </a-col>
            <a-col :span="6">
              <a-form-item :label="$t('form.label.bizNoFuture')">
                <a-input v-model:value="form.businessNoFuture" :placeholder="$t('form.placeholder.bizNoFuture')" />
                <small class="text-gray-500">
                  {{ $t('form.ex') }}) 123456-1234567
                </small>
              </a-form-item>
            </a-col>
            <a-col :span="6">
              <a-form-item :label="$t('form.label.telFuture')">
                <a-input v-model:value="form.phoneFuture" :placeholder="$t('form.placeholder.telFuture')" />
              </a-form-item>
            </a-col>
          </a-row>
          <a-row :gutter="[5, 5]">
            <a-col :span="6">
              <a-form-item :label="$t('form.label.annualIncome')">
                <a-input-number
                  v-model:value="form.annualIncome"
                  :placeholder="$t('form.placeholder.annualIncome')"
                  style="width: 100%"
                  :formatter="value => `${value}`.replace(/\B(?=(\d{3})+(?!\d))/g, ',')" 
                  :parser="value => value.replace(/,/g, '')"
                />
              </a-form-item>
            </a-col>
            <a-col :span="6">
              <a-form-item :label="$t('form.label.job')">
                <a-input v-model:value="form.job" :placeholder="$t('form.placeholder.job')" />
              </a-form-item>
            </a-col>
            <a-col :span="6">

            </a-col>
          </a-row>
        </a-form> 
      </div>

      <div v-if="step === 5">
        <a-form layout="vertical" style="margin-top: 20px">
          <a-row :gutter="[5, 5]">
            <a-col :span="6">
              <a-form-item :label="$t('form.label.reentryPeriod')">
                <a-date-picker
                  v-model:value="form.reIn"
                  format="YYYY-MM-DD"
                  style="width: 80%"
                  :placeholder="$t('form.placeholder.reentryPeriod')"
                />
              </a-form-item>
            </a-col>
          </a-row>
        </a-form>
      </div>
    </div>

    <div v-if="isSubmitted">
      <a-result v-if="loading"
        status="success"
        :title="$t('form.result.1')"
        :sub-title="$t('form.result.2')"
      >
        <template #extra>
          <a-spin />
        </template>
      </a-result>
      <iframe v-else
        :src="viewerUrl"
        width="100%"
        height="600px"
        style="border: none; margin-top: 20px;"
      ></iframe>
    </div>
    
  </a-card>
</template>

<script setup>
import { ref } from 'vue';
import { useI18n } from 'vue-i18n'

const pdfUrl = encodeURIComponent("/files/test.pdf");
const viewerUrl = `/pdfjs/web/viewer.html?file=${pdfUrl}`;

const { locale } = useI18n()

const application = ref(0);
const isSubmitted = ref(false);
const loading = ref(true);

// Step 정의
const steps = [
  { title: 'steps.application' },
  { title: 'steps.basic' },
  { title: 'steps.enrollment' },
  { title: 'steps.workplace' },
  { title: 'steps.reentry' }
]
const step = ref(1);

// form 초기값 생성
const form = ref({
  application: 0,
  desiredStatus: '',
  singleOrMultiple: 0,
  firstName: '',
  lastName: '',
  birth: '',
  gender: 0,
  nationality: '',
  foreignerId: '',
  passportNo: '',
  passportIssued: '',
  passportValid: '',
  addressKR: '',
  phoneKR: '',
  mobileKR: '',
  addressHome: '',
  phoneHome: '',
  email: '',
  enrolled: 0,
  schoolName: '',
  schoolPhone: '',
  schoolType: '',
  eduApprove: 0,
  workplaceOriginal: '',
  businessNoOriginal: '',
  phoneOriginal: '',
  workplaceFuture: '',
  businessNoFuture: '',
  phoneFuture: '',
  annualIncome: '',
  job: '',
  reIn: ''

});

const nextStep = () => { if (step.value < steps.length) step.value++; };
const prevStep = () => { if (step.value > 1) step.value--; };

const moveStep = (clickStep) => {

  step.value = clickStep + 1;
}

const saveData = () => {
  console.log('최종 데이터:', form.value);
  isSubmitted.value = true;

  // 몇초후에 loading false
  setTimeout(() => {
    loading.value = false;
  }, 3000);
};

function changeLang(lang) {
  locale.value = lang  // 'ko' 또는 'en'
}

const onAlienRegNoInput = (e) => {
  let value = e.target.value.replace(/[^0-9]/g, "") // 숫자만 허용
  console.log(value)
  if (value.length > 6) {
    value = value.slice(0, 6) + "-" + value.slice(6, 13)
  }
  form.value.foreignerId = value
}
</script>