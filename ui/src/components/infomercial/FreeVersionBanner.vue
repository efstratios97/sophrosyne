<template>
  <div class="flex justify-content-center px-4">
    <Message severity="info" :closable="false" class="w-full max-w-30rem m-0 text-center">
      {{ $t('infomercials.free_version_banner.text_1') }}
      <strong> {{ $t('infomercials.version.version') }} </strong>.<span
        @click="toggleIsVisible()"
        class="cursor-pointer text-blue-600 underline font-semibold ml-1"
      >
        {{ $t('infomercials.free_version_banner.call_to_action_text') }}
      </span>
    </Message>

    <div v-if="isVisible" class="flex justify-content-center px-4">
      <EnterpriseLicenseBanner :isVisible="isVisible" @update:isVisible="isVisible = $event" />
    </div>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import Message from 'primevue/message'
import EnterpriseLicenseBanner from '@/components/infomercial/EnterpriseLicenseBanner.vue'

const isVisible = ref(false)

const toggleIsVisible = () => {
  isVisible.value = true
}
</script>

<style scoped></style>
