<template>
  <div class="gird">
    <div class="col-12">
      <Dropdown
        v-model="selectedRunningAction"
        :options="props.runningActions"
        filter
        optionLabel="name"
        :placeholder="
          $t('actions.dynamic_action.action_running_selection.fields.running_action_selection')
        "
        class="w-full"
      >
        <template #option="slotProps">
          <div class="flex align-items-center">
            <div>{{ slotProps.option.name }} : {{ slotProps.option.command }}</div>
          </div>
        </template>
      </Dropdown>
      <div class="col-12">
        <Button
          class="w-full"
          :label="$t('actions.dynamic_action.action_running_selection.btn.submit.label')"
          @click="emit('getSelectedRunningAction', selectedRunningAction)"
        />
      </div>
    </div>
  </div>
</template>
<script setup>
import { ref } from 'vue'

const emit = defineEmits(['getSelectedRunningAction'])
const props = defineProps(['runningActions'])

const selectedRunningAction = ref('')
</script>
