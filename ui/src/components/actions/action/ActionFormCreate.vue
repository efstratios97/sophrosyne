<template>
  <div>
    <ActionFormTemplate
      :action="props.action ? props.action : {}"
      :settings="settings"
      @submitAction="createNewAction"
    ></ActionFormTemplate>
    <Toast />
  </div>
</template>

<script setup>
import { watchEffect } from 'vue'
import { useActionComposable } from '@/composables/ActionComposable.js'
import ActionFormTemplate from '@/components/actions/utils/ActionFormTemplate.vue'

const { createNewAction, settings } = useActionComposable()
const emit = defineEmits(['getActions'])
const props = defineProps(['action'])
watchEffect(() => {
  settings.value.succeded ? emit('getActions') : null
})
</script>
