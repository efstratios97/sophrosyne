<template>
  <div
    v-for="controlPanelDashboardGroupObject in dashboard.associatedControlPanelDashboardGroupObjects"
    :key="controlPanelDashboardGroupObject"
  >
    <Fieldset :legend="controlPanelDashboardGroupObject.name" :toggleable="true" class="w-12">
      <DataView
        :value="
          getAllActions(
            controlPanelDashboardGroupObject.associatedActionObjects,
            controlPanelDashboardGroupObject.associatedDynamicActionObjects
          )
        "
        :layout="layout"
      >
        <template #header> </template>

        <template #list="slotProps">
          <div class="grid flex flex-col justify-content-center flex-wrap">
            <div v-for="(item, index) in slotProps.items" :key="index">
              <div
                class="flex flex-col align-items-center justify-content-center sm:flex-row sm:items-center p-6 gap-4"
              >
                <ActionUnit
                  :id="item.id"
                  :isAction="item.dynamicParameters == undefined ? true : false"
                  :isDynamicAction="item.dynamicParameters != undefined ? true : false"
                  :action="item"
                  style="overflow: auto"
                ></ActionUnit>
              </div>
            </div>
          </div>
        </template>
      </DataView>
    </Fieldset>
  </div>
</template>
<script setup>
import ActionUnit from '@/components/controlpanel/view/ActionUnit.vue'
import { ref } from 'vue'
const layout = ref('list')
defineProps(['dashboard'])

const getAllActions = (actions, dynamicActions) => {
  let allActions = []
  if (actions != null) {
     allActions = actions.concat(dynamicActions)
  } else if (dynamicActions != null) {
     allActions = dynamicActions.concat(actions)
  }
  allActions = allActions.filter((item) => item != null)
  return allActions
}
</script>
