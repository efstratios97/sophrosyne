<template>
  <div>
    <Tabs v-if="loaded && controlPanelView.length > 0" :value="controlPanelView[0].name" scrollable>
      <TabList>
        <Tab v-for="dashboard in controlPanelView" :value="dashboard.name" :key="dashboard">
          <div class="flex align-items-center gap-2" style="font-size: 18px; margin-bottom: 4px">
            <i class="pi pi-sliders-h" style="font-size: 18px"></i>
            <span class="font-bold">{{ dashboard.name }}</span>
          </div>
        </Tab>
      </TabList>
      <TabPanels>
        <TabPanel v-for="dashboard in controlPanelView" :value="dashboard.name" :key="dashboard">
          <div class="grid m-0">
            <div class="col-12">
              <ControlPanelDashboardGroup
                :dashboard="dashboard"
                v-if="loaded && userLoggedEmbedded"
              ></ControlPanelDashboardGroup>
            </div>
          </div>
        </TabPanel>
      </TabPanels>
    </Tabs>
  </div>
</template>
<script setup>
import { ref, inject, onBeforeMount } from 'vue'

import { useUserComposable } from '@/composables/UserComposable.js'
import ControlPanelDashboardGroup from '@/components/controlpanel/view/ControlPanelDashboardGroup.vue'
import { useLoggedUser } from '@/stores/loggedUser'

const { loginUser } = useUserComposable()
const userLoggedEmbedded = ref(false)

const loggedUserStore = useLoggedUser()

const axiosCore = inject('axios-core')

const controlPanelView = ref([{}])
onBeforeMount(() => {
  const urlParams = new URLSearchParams(window.location.search)
  const username = urlParams.get('username')
  const password = urlParams.get('password')
  if (password != undefined) {
    loginUser(username, password).then(() => {
      getControlPanelView()

      userLoggedEmbedded.value = true
    })
  } else {
    getControlPanelView()
    userLoggedEmbedded.value = true
  }
})

const loaded = ref(false)
const getControlPanelView = () => {
  axiosCore
    .get('/int/client/controlpanelview/' + loggedUserStore.loggedUserUsername)
    .then((res) => {
      if (res.data.associatedControlPanelDashboardObjects != undefined) {
        controlPanelView.value = res.data.associatedControlPanelDashboardObjects
      }
      loaded.value = true
    })
}
</script>
