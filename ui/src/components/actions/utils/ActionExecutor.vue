<template>
  <div>
    <Dialog
      v-model:visible="showActionExecutor"
      modal
      :autoZIndex="false"
      maximizable
      style="width: 80%; height: 100%"
      @update:visible="emits('close')"
    >
      <template #header>
        <div class="grid">
          <div class="col-12">
            <div class="sophrosyne-text-break text-xl font-bold">{{ props.action.name }}</div>
          </div>
          <div class="col-12">
            <div class="sophrosyne-text-break sophrosyne-code">
              {{ selectedRunningAction.command }}
              {{
                props.showDefaultDynamicParameters && selectedRunningAction.runningId == null
                  ? ' ' + selectedRunningAction.dynamicParameters
                  : ''
              }}
            </div>
          </div>
        </div>
      </template>
      <div class="grid" v-if="props.isAction">
        <div class="col-2" style="min-height: 400px; max-height: 425px">
          <div class="grid">
            <div class="col-12" style="min-height: 350px; max-height: 350px">
              <ActionControl
                :id="id"
                :action="props.action"
                @updateRateLimit="updateRateLimit($event)"
              />
            </div>
          </div>
        </div>
        <div class="col-10 h-full">
          <ActionPromptLiveStream :id="id" height="58vh" :rateLimitInfo="rateLimitInfo" :key="id" />
        </div>
      </div>
      <div class="grid" v-if="props.isDynamicAction">
        <div class="col-2" style="min-height: 400px; max-height: 425px">
          <div class="grid">
            <div class="col-12" style="min-height: 350px; max-height: 350px">
              <DynamicActionControl
                :action="selectedRunningAction"
                @isDynamicActionExecutorToggled="setSelectedRunningActionAfterExecution($event)"
                @updateRateLimit="updateRateLimit($event)"
              />
            </div>
          </div>
        </div>
        <div class="col-10">
          <ActionPromptLiveStream
            v-if="selectedRunningAction.runningId"
            :id="selectedRunningAction.runningId"
            height="58vh"
            :rateLimitInfo="rateLimitInfo"
            :deactivateStream="dynamicActionExecutorToggled && !showRunningActionSelection"
            :key="selectedRunningAction.runningId"
          />
          <ActionPromptLiveStream v-else height="58vh"></ActionPromptLiveStream>
        </div>
      </div>
    </Dialog>
    <Dialog
      v-model:visible="showRunningActionSelection"
      modal
      maximizable
      :header="$t('actions.dynamic_action.action_running_selection.title')"
      v-if="showRunningActionSelection"
      @update:visible="toggleShowRunningActionSelection"
    >
      <DynamicActionRunningSelector
        @getSelectedRunningAction="setSelectedRunningAction($event)"
        :runningActions="runningDynamicActionsById"
      ></DynamicActionRunningSelector>
    </Dialog>
  </div>
</template>

<script setup>
import { ref, onMounted } from 'vue'
import ActionPromptLiveStream from '@/components/actions/action/ActionPromptLiveStream.vue'
import ActionControl from '@/components/actions/action/ActionControl.vue'
import DynamicActionControl from '@/components/actions/dynamic_action/DynamicActionControl.vue'
import DynamicActionRunningSelector from '@/components/actions/dynamic_action/DynamicActionRunningSelector.vue'
import { useDynamicActionComposable } from '@/composables/DynamicActionComposable.js'
import { Dialog } from 'primevue'

const { getRunningDynamicActionsById, runningDynamicActionsById } = useDynamicActionComposable()

const props = defineProps([
  'id',
  'action',
  'isAction',
  'isDynamicAction',
  'showActionExecutor',
  'showDefaultDynamicParameters'
])

onMounted(() => {
  selectedRunningAction.value = props.action
  getRunningDynamicActionsById(props.id).then(() => {
    if (runningDynamicActionsById.value.length > 1) {
      toggleShowRunningActionSelection()
    } else if (runningDynamicActionsById.value.length == 1) {
      selectedRunningAction.value = runningDynamicActionsById.value.at(0)
    } else {
      selectedRunningAction.value = props.action
    }
  })
})

const emits = defineEmits(['close'])

const rateLimitInfo = ref({})
const updateRateLimit = (rateLimitInfoEvent) => {
  rateLimitInfo.value = rateLimitInfoEvent
}

const showActionExecutor = ref({ ...props.showActionExecutor })

const showRunningActionSelection = ref(false)
const toggleShowRunningActionSelection = () => {
  showRunningActionSelection.value = !showRunningActionSelection.value
}
const selectedRunningAction = ref({})
const setSelectedRunningAction = (selectedAction) => {
  selectedRunningAction.value = selectedAction
  toggleShowRunningActionSelection()
}

const dynamicActionExecutorToggled = ref(false)
const setSelectedRunningActionAfterExecution = (toggled) => {
  dynamicActionExecutorToggled.value = toggled
  if (!toggled) {
    getRunningDynamicActionsById(selectedRunningAction.value.id).then(() => {
      if (runningDynamicActionsById.value.length > 0) {
        selectedRunningAction.value = runningDynamicActionsById.value.at(-1)
      }
    })
  }
}
</script>
