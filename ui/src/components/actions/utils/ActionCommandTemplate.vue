<template>
  <div>
    <Dialog v-model:visible="visible" modal :style="{ width: '66rem' }">
      <template #header>
        <div class="h-2" v-if="!props.isDynamicActionParameters">
          {{ $t('actions.' + I18nSource + '.action_creation_form.command_dialog.header') }}
        </div>
        <div class="h-2" v-else>
          {{
            $t('actions.' + I18nSource + '.action_creation_form.command_parameter_dialog.header')
          }}
        </div>
      </template>
      <Textarea v-model="commandInput" class="sophrosyne-terminal-input"> </Textarea>
      <template #footer>
        <div class="inline-flex align-items-center justify-content-center gap-2">
          <Button
            :label="$t('actions.' + I18nSource + '.action_creation_form.command_dialog.btn.cancel')"
            text
            severity="secondary"
            @click="noCommand()"
            autofocus
          />
          <Button
            :label="$t('actions.' + I18nSource + '.action_creation_form.command_dialog.btn.save')"
            outlined
            severity="secondary"
            @click="sendCommand()"
            autofocus
          />
        </div>
      </template>
    </Dialog>
  </div>
</template>
<script setup>
import { ref, onMounted } from 'vue'

const visible = ref(true)
const commandInput = ref('')

const props = defineProps(['command', 'isDynamicAction', 'isDynamicActionParameters'])
const I18nSource = props.isDynamicAction ? ref('dynamic_action') : ref('action')

const toggleVisible = () => {
  visible.value = !visible.value
}

onMounted(() => {
  commandInput.value = props.command
})

const emit = defineEmits(['submitCommand', 'noCommand'])

const sendCommand = () => {
  emit('submitCommand', commandInput.value)
  toggleVisible()
}

const noCommand = () => {
  emit('noCommand')
  toggleVisible()
}
</script>
