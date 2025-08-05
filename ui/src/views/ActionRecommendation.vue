<template>
  <Layout>
    <Tabs value="0" scrollable>
      <TabList>
        <Tab value="0">
          <div class="flex align-items-center gap-2" style="font-size: 18px; margin-bottom: 4px">
            <i class="pi pi-compass" style="font-size: 18px"></i>
            <span class="font-bold">{{
              $t('action_recommendations.tab_menu_options.tab_menu_action_recommendation_dashboard')
            }}</span>
          </div>
        </Tab>
      </TabList>
      <TabPanels>
        <TabPanel value="0">
          <div class="grid m-0">
            <div class="col-12">
              <ActionRecommendationDashboard
                v-if="userLoggedEmbedded"
              ></ActionRecommendationDashboard>
            </div>
          </div>
        </TabPanel>
      </TabPanels>
    </Tabs>
  </Layout>
</template>
<script setup>
import Layout from '@/components/frame/Layout.vue'
import ActionRecommendationDashboard from '@/components/action_recommendation/dashboard/ActionRecommendationDashboard.vue'
import { ref, onBeforeMount } from 'vue'

import { useUserComposable } from '@/composables/UserComposable.js'

const { loginUser } = useUserComposable()
const userLoggedEmbedded = ref(false)
onBeforeMount(() => {
  const urlParams = new URLSearchParams(window.location.search)
  const username = urlParams.get('username')
  const password = urlParams.get('password')
  if (password != undefined) {
    loginUser(username, password).then(() => {
      setTimeout(() => {
        userLoggedEmbedded.value = true
        router.push({ path: '/control-panel-view', replace: true })
      }, 1000)
    })
  } else {
    userLoggedEmbedded.value = true
  }
})
</script>
