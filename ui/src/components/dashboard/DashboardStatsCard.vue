<template>
  <div class="surface-card shadow-2 p-3 border-round" style="overflow: auto">
    <div class="flex justify-content-between mb-3">
      <div>
        <span class="block text-500 font-medium mb-3">{{ props.title }}</span>
        <slot name="value"></slot>
      </div>
      <div
        :class="
          'flex align-items-center justify-content-center border-round ' + props.icon_bg_color
        "
        style="width: 2.5rem; height: 2.5rem"
      >
        <slot name="icon"></slot>
      </div>
    </div>
    <slot name="subtitle"></slot>
  </div>
</template>
<script setup>
const props = defineProps(['title', 'icon_bg_color'])
</script>
