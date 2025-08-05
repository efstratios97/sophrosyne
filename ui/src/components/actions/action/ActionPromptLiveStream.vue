<template>
  <ScrollPanel
    ref="scrollPanel"
    :style="customStyle"
    :pt="{
      root: { class: 'sophrosyne-terminal-box' },
      barY: { class: 'bg-primary text-primary-contrast' }
    }"
  >
    <div ref="logContainer"></div>
  </ScrollPanel>
</template>

<script setup>
import {
  ref,
  onUnmounted,
  onBeforeMount,
  defineProps,
  getCurrentInstance,
  watch,
  nextTick
} from 'vue'
import { Client } from '@stomp/stompjs'
import { AnsiUp } from 'ansi_up'
import autoScroll from '@/directives/autoscroll.js'

const props = defineProps(['id', 'height', 'rateLimitInfo', 'deactivateStream'])

const client = ref({})
const customStyle = ref('')
const ansi_up = new AnsiUp()
const buffer = ref([])
const logContainer = ref(null)

const instance = getCurrentInstance()
instance.appContext.app.directive('auto-scroll', autoScroll)

onBeforeMount(() => {
  //Reset
  try {
    client.value.deactivate(true)
  } catch (error) {
    /* empty */
  }
  try {
    const content = document.querySelector('.p-scrollpanel-content')
    content.innerHTML = ''
  } catch (error) {
    /* empty */
  }
  buffer.value = []

  const urlParams = new URLSearchParams(window.location.search)
  const overriddenCustomStyle = urlParams.get('custom_style')
  if (overriddenCustomStyle == undefined) {
    customStyle.value =
      props.height === undefined
        ? 'width: 95%; height: 100%;'
        : `width: 95%; height: ${props.height};`
  } else {
    customStyle.value = overriddenCustomStyle
  }
  // Connect to WebSocket
  startStream()
})

const startStream = () => {
  // Override Options via URL
  const urlParams = new URLSearchParams(window.location.search)
  const overriddenHostname = urlParams.get('sophrosyne_backend_host')
  const overriddenId = urlParams.get('id')

  const streamId = ref(overriddenId || props.id)
  // Fallback to window.location.hostname if no override is provided
  const hostname = overriddenHostname || window.location.hostname
  client.value = new Client({
    brokerURL: `ws://${hostname}:17609/sophrosynebroadcast`
  })
  client.value.activate()
  client.value.onConnect = () => {
    // Subscribe to your destination
    client.value.subscribe(`/topic/sophrosyne/${streamId.value}`, (message) => {
      // Handle incoming message
      buffer.value.push(ansi_up.ansi_to_html(message.body + '\n'))
      updateLogs()
    })
  }
}

watch(
  () => props.deactivateStream,
  async (newValue) => {
    if (newValue) {
      try {
        client.value.deactivate(true)
        let content = document.querySelector('.p-scrollpanel-content')
        content.innerHTML = ''
        /* empty */
      } catch (error) {
        /* empty */
      }
    } else {
      startStream()
    }
  }
)

watch(buffer, async () => {
  await nextTick()
  const content = document.querySelector('.p-scrollpanel-content')
  if (content) {
    content.scrollTop = content.scrollHeight
  }
})

onUnmounted(() => {
  try {
    const content = document.querySelector('.p-scrollpanel-content')
    content.innerHTML = ''
  } catch (error) {
    /* empty */
  }
  buffer.value = []
  client.value.deactivate(true)
})

const updateLogs = () => {
  const content = document.querySelector('.p-scrollpanel-content')
  if (content) {
    if (props.rateLimitInfo != undefined && props.rateLimitInfo.isActive) {
      const currentLineCount = content.children.length
      if (currentLineCount > props.rateLimitInfo.rateLimit) {
        content.innerHTML = ''
      }
    }
    buffer.value.forEach((log) => {
      const div = document.createElement('div')
      div.innerHTML = log
      content.appendChild(div)
    })
    buffer.value = []
  }
}
</script>
