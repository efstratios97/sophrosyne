<template>
  <div class="grid">
    <div class="col-12">
      <Button
        :label="$t('actions.action.action_control.btn.execute_action.label')"
        icon="pi pi-play"
        severity="success"
        rounded
        aria-label="Execute"
        class="w-full"
        @click="emit('executeAction')"
      />
    </div>
    <div class="col-12">
      <Button
        :label="$t('actions.action.action_control.btn.stop_action.label')"
        icon="pi pi-stop-circle"
        severity="danger"
        rounded
        aria-label="Stop"
        class="w-full"
        @click="emit('stopAction')"
      />
    </div>

    <div class="col-12">
      <Fieldset
        :legend="$t('actions.action.action_control.last_action_card.header')"
        :toggleable="true"
        :collapsed="true"
      >
        <Card style="min-height: 100%; overflow: auto">
          <template #content>
            <div class="grid">
              <div class="col-5">
                <label>{{
                  $t('actions.action.action_control.last_action_card.execution_exit_code')
                }}</label>
              </div>
              <div class="col-7">
                <Badge
                  id="password1"
                  :value="props.lastActionStatus.exitCodeDisplayText"
                  size="xlarge"
                  :severity="props.lastActionStatus.exitCodeDisplay"
                  v-tooltip="{
                    value: props.lastActionStatus.exitCodeDisplayTooltip,
                    showDelay: 100,
                    hideDelay: 300
                  }"
                ></Badge>
              </div>
              <div class="col-5">
                <label>{{
                  $t('actions.action.action_control.last_action_card.execution_start_time')
                }}</label>
              </div>
              <div class="col-6">
                <h6 class="m-1">
                  {{
                    props.lastActionStatus.status.executionStartPoint != null
                      ? props.lastActionStatus.status.executionStartPoint.substring(
                          0,
                          props.lastActionStatus.status.executionStartPoint.lastIndexOf('.')
                        )
                      : ''
                  }}
                </h6>
              </div>
            </div>
          </template>
        </Card>
      </Fieldset>
    </div>
    <div class="col-12" style="min-height: 50px; max-height: 50px">
      <Fieldset
        :legend="$t('control_panel_view.action_executor.rate_limit.header')"
        :toggleable="true"
        :collapsed="true"
      >
        <LiveStreamRateLimit @updateRateLimit="emit('updateRateLimit', $event)" />
      </Fieldset>
    </div>
  </div>
</template>
<script setup>
import LiveStreamRateLimit from '@/components/actions/utils/LiveStreamRateLimit.vue'

const props = defineProps(['lastActionStatus'])
const emit = defineEmits(['stopAction', 'executeAction', 'updateRateLimit'])
</script>
