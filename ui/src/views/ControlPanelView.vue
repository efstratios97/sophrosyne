<template>
  <Layout>
    <ControlPanel v-if="userLoggedEmbedded"></ControlPanel>
  </Layout>
</template>
<script setup>
import { ref, onBeforeMount } from 'vue'

import Layout from '@/components/frame/Layout.vue'
import ControlPanel from '@/components/controlpanel/view/ControlPanel.vue'
import { useUserComposable } from '@/composables/UserComposable.js'

const { loginUser } = useUserComposable()
const userLoggedEmbedded = ref(false)
onBeforeMount(() => {
  const urlParams = new URLSearchParams(window.location.search)
  const username = urlParams.get('username')
  const password = urlParams.get('password')
  if (password != undefined) {
    loginUser(username, password).then(() => {
      userLoggedEmbedded.value = true
      
    })
  } else {
    userLoggedEmbedded.value = true
  }
})
</script>
