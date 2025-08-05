<template>
  <Dialog v-model:visible="visible" modal :style="{ width: '50rem' }">
    <Card>
      <template #header>
        <img
          src="@/assets/images/logos/SOPHROSYNE_LOGO_BG_EMPTY.png"
          alt="Image"
          class="mb-0"
          style="max-height: 200px; max-width: 25%"
        />
      </template>
      <template #title>
        <h1>{{ $t('login.create_account.title') }}</h1>
      </template>
      <template #content>
        <h3>
          {{ $t('login.create_account.text') }}
        </h3>
      </template>
    </Card>
  </Dialog>
</template>
<script setup>
import { ref, defineProps } from 'vue'
const props = defineProps(['visible'])
const visible = ref(props.visible)
</script>
