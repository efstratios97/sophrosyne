<template>
  <Layout>
    <Tabs value="0" scrollable>
      <TabList>
        <Tab value="0">
          <div class="flex align-items-center gap-2" style="font-size: 18px; margin-bottom: 4px">
            <i class="pi pi-key" style="font-size: 18px"></i>
            <span class="font-bold">{{ $t('settings.tab_menu_options.tab_menu_apikeys') }}</span>
          </div>
        </Tab>
        <Tab value="1">
          <div class="flex align-items-center gap-2" style="font-size: 18px; margin-bottom: 4px">
            <i class="pi pi-user" style="font-size: 18px"></i>

            <span class="font-bold">
              <span class="font-bold">{{
                $t('settings.tab_menu_options.tab_menu_users')
              }}</span></span
            >
          </div>
        </Tab>
        <Tab value="2">
          <div class="flex align-items-center gap-2" style="font-size: 18px; margin-bottom: 4px">
            <i class="pi pi-cog" style="font-size: 18px"></i>
            <span class="font-bold">
              <span class="font-bold">{{
                $t('settings.tab_menu_options.tab_menu_config')
              }}</span></span
            >
          </div>
        </Tab>
        <Tab value="3"
          ><div class="flex align-items-center gap-2" style="font-size: 18px; margin-bottom: 4px">
            <i class="pi pi-wrench" style="font-size: 18px"></i>
            <span class="font-bold">
              <span class="font-bold">{{
                $t('settings.tab_menu_options.tab_menu_general')
              }}</span></span
            >
          </div>
        </Tab>
      </TabList>
      <TabPanels>
        <TabPanel value="0">
          <div class="grid m-0">
            <div class="col-12">
              <ApikeysDisplay />
            </div>
            <div class="col-6">
              <ApikeysCreation />
            </div>
            <div class="col-6">
              <ApikeysNuke />
            </div>
          </div>
        </TabPanel>
        <TabPanel value="1">
          <UsersDisplay></UsersDisplay>
        </TabPanel>
        <TabPanel value="2">
          <div class="grid m-0">
            <div class="col-12">
              <Configuration></Configuration>
            </div>
            <div class="col-6">
              <ImportConfiguration></ImportConfiguration>
            </div>
            <div class="col-6">
              <ImportConfigurationForce></ImportConfigurationForce>
            </div>
          </div>
        </TabPanel>
        <TabPanel value="3">
          <div class="grid m-0">
            <div class="col-8 col-offset-2">
              <General></General>
            </div>
          </div>
        </TabPanel>
      </TabPanels>
    </Tabs>
  </Layout>
</template>
<script setup>
import Layout from '@/components/frame/Layout.vue'
import ApikeysDisplay from '@/components/settings/apikeys/ApikeysDisplay.vue'
import ApikeysCreation from '@/components/settings/apikeys/ApikeysCreation.vue'
import ApikeysNuke from '@/components/settings/apikeys/ApikeysNuke.vue'
import UsersDisplay from '@/components/settings/users/UsersDisplay.vue'
import Configuration from '@/components/settings/configuration/Configuration.vue'
import ImportConfiguration from '@/components/settings/configuration/ImportConfiguration.vue'
import ImportConfigurationForce from '@/components/settings/configuration/ImportConfigurationForce.vue'
import General from '@/components/settings/administration/General.vue'
</script>
