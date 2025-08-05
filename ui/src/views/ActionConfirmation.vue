<template>
  <Layout>
    <Tabs value="0" scrollable>
      <TabList>
        <Tab value="0">
          <div class="flex align-items-center gap-2" style="font-size: 18px; margin-bottom: 4px">
            <i class="pi pi-check-square" style="font-size: 18px"></i>
            <span class="font-bold">{{
              $t('action_confirmation.tab_menu_options.tab_menu_confirmation_requests')
            }}</span>
          </div>
        </Tab>
      </TabList>
      <TabPanels>
        <TabPanel value="0">
          <div class="grid m-0">
            <div class="col-12">
              <ConfirmationDisplay></ConfirmationDisplay>
            </div>
          </div>
        </TabPanel>
      </TabPanels>
    </Tabs>
  </Layout>
</template>
<script setup>
import Layout from '@/components/frame/Layout.vue'
import ConfirmationDisplay from '@/components/action_confirmation/ConfirmationDisplay.vue'
</script>
