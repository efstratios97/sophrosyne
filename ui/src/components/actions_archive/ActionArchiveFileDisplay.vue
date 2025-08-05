<template>
  <div>
    <ActionArchiveFileToolbar :textData="props.textData"></ActionArchiveFileToolbar>
    <ScrollPanel
      style="width: 95%; height: 335px; max-height: 335px"
      :pt="{
        root: { class: 'sophrosyne-terminal-box' },
        barY: { class: 'bg-primary' }
      }"
    >
      <pre style="white-space: pre-line" v-html="ansi_up.ansi_to_html(props.textData)"></pre>
    </ScrollPanel>
  </div>
</template>
<script setup>
import ActionArchiveFileToolbar from '@/components/actions_archive/ActionArchiveFileToolbar.vue'
import { defineProps } from 'vue'
import { AnsiUp } from 'ansi_up'

const props = defineProps(['textData'])
const ansi_up = new AnsiUp()
</script>
