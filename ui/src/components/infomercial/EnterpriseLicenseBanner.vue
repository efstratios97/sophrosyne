<template>
  <div class="card flex justify-content-center">
    <Dialog
      v-model:visible="isVisibleInternal"
      modal
      :header="$t('infomercials.enterprise_license_banner.header')"
      :style="{ width: '50vw' }"
      :breakpoints="{ '960px': '75vw', '641px': '90vw' }"
      @close="closeBanner()"
    >
      <p class="line-height-3 mb-4">
        {{ $t('infomercials.enterprise_license_banner.subtitle.text_1') }}
        <Tag
          severity="info"
          :value="$t('infomercials.enterprise_license_banner.subtitle.text_tag_version')"
        />{{ $t('infomercials.enterprise_license_banner.subtitle.text_2')
        }}<strong>{{
          $t('infomercials.enterprise_license_banner.subtitle.text_3_strong_tier')
        }}</strong>
        {{ $t('infomercials.enterprise_license_banner.subtitle.text_4') }}
      </p>

      <div class="flex flex-column gap-3 mb-4">
        <div class="flex align-items-center gap-2 border-bottom-1 surface-border pb-2">
          <i class="pi pi-check-circle text-primary text-xl"></i>
          <span
            ><strong>
              {{ $t('infomercials.enterprise_license_banner.feature_1.main_text') }}
            </strong>
            {{ $t('infomercials.enterprise_license_banner.feature_1.secondary_text') }}
          </span>
        </div>
        <div class="flex align-items-center gap-2 border-bottom-1 surface-border pb-2">
          <i class="pi pi-shield text-primary text-xl"></i>
          <span
            ><strong>
              {{ $t('infomercials.enterprise_license_banner.feature_2.main_text') }}
            </strong>
            {{ $t('infomercials.enterprise_license_banner.feature_2.secondary_text') }}
          </span>
        </div>
        <div class="flex align-items-center gap-2 border-bottom-1 surface-border pb-2">
          <i class="pi pi-palette text-primary text-xl"></i>
          <span
            ><strong>
              {{ $t('infomercials.enterprise_license_banner.feature_3.main_text') }}
            </strong>
            {{ $t('infomercials.enterprise_license_banner.feature_3.secondary_text') }}
          </span>
        </div>
        <div class="flex align-items-center gap-2 pb-2">
          <i class="pi pi-sliders-h text-primary text-xl"></i>
          <span
            ><strong>
              {{ $t('infomercials.enterprise_license_banner.feature_4.main_text') }}
            </strong>
            {{ $t('infomercials.enterprise_license_banner.feature_4.secondary_text') }}
          </span>
        </div>
      </div>

      <!-- PrimeVue Action Banner Footer -->
      <Message severity="warn" :closable="false" class="mt-4">
        <div class="flex align-items-center flex-wrap gap-2">
          <span>
            <strong>
              {{ $t('infomercials.enterprise_license_banner.call_to_action.call_to_action_text') }}
            </strong></span
          >
          <a
            href="mailto:sophrosyne@ep-monitoring.com"
            class="font-bold no-underline text-amber-700 hover:underline"
          >
            {{ $t('infomercials.enterprise_license_banner.call_to_action.email') }}
          </a>
          <span class="text-sm block w-full mt-1 text-600">
            {{ $t('infomercials.enterprise_license_banner.call_to_action.contact_text') }}
          </span>
        </div>
      </Message>
    </Dialog>
  </div>
</template>

<script setup>
import { ref, watch } from 'vue'
import Dialog from 'primevue/dialog'
import Tag from 'primevue/tag'
import Message from 'primevue/message'

const props = defineProps({
  isVisible: {
    type: Boolean,
    default: true
  }
})

const isVisibleInternal = ref(props.isVisible)

watch(
  () => props.isVisible,
  (newValue) => {
    isVisibleInternal.value = newValue
  }
)

const closeBanner = () => {
  isVisibleInternal.value = false
}
</script>

<style scoped>
/* Optional style adjustments if your global theme doesn't handle them */
.text-primary {
  color: var(--primary-color, #3b82f6);
}
</style>
