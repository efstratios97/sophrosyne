<template>
  <div>
    <Card style="width: 12rem">
      <template #header>
        <img
          alt="CONTROL_PANEL_ACTION_UNIT"
          src="@/assets/images/CONTROL_PANEL_ACTION_UNIT.jpg"
          style="max-height: 10vh; min-width: 100%"
        />
      </template>
      <template #title
        ><div class="sophrosyne-text-break">{{ props.action.name }}</div></template
      >
      <template #subtitle
        ><div class="sophrosyne-text-break">{{ props.action.description }}</div></template
      >
      <template #content>
        <div class="flex-auto flex justify-content-center align-items-center font-medium">
          <Button
            icon="pi pi-bolt"
            iconClass="text-4xl"
            rounded
            style="width: 4rem; height: 4rem"
            @click="toggleShowActionExecutor"
          />
        </div>
      </template>
    </Card>
    <ActionExecutor
      class="h-full"
      v-if="showActionExecutor"
      :id="props.id"
      :action="props.action"
      :isAction="props.isAction"
      :isDynamicAction="props.isDynamicAction"
      :showDefaultDynamicParameters="props.isDynamicAction"
      @close="toggleShowActionExecutor"
    ></ActionExecutor>
  </div>
</template>

<script setup>
import { ref } from 'vue'
import ActionExecutor from '@/components/actions/utils/ActionExecutor.vue'

const props = defineProps(['id', 'action', 'isAction', 'isDynamicAction'])

const showActionExecutor = ref(false)
const toggleShowActionExecutor = () => {
  showActionExecutor.value = !showActionExecutor.value
}
</script>
<style scoped>
.p-card {
  transition: all 0.3s ease;
  border: 1px solid rgba(0, 0, 0, 0.1);
  border-radius: 12px;
  overflow: hidden;
}

.p-card:hover {
  border: 1px solid rgba(0, 0, 0, 0.15);
  transform: translateY(-2px);
  border-radius: 12px;
}
</style>
