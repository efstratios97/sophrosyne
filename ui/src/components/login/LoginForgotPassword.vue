<template>
  <Dialog v-model:visible="visible" modal :style="{ width: '50rem' }">
    <Card>
      <template #header>
        <img
          src="@/assets/images/FORGOT_PASSWD.png"
          alt="Image"
          class="mb-0"
          style="max-height: 300px; width: 100%"
        />
      </template>
      <template #title>
        <h1>{{ $t('login.forgot_passwd.title') }}</h1>
      </template>
      <template #content>
        <h3>
          {{ $t('login.forgot_passwd.text') }}
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
