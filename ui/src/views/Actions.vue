<template>
  <Layout>
    <Tabs value="0" scrollable :selectOnFocus="true">
      <TabList>
        <Tab value="0">
          <div class="flex align-items-center gap-2" style="font-size: 18px; margin-bottom: 4px">
            <i class="pi pi-bolt" style="font-size: 18px"></i>
            <span class="font-bold">{{
              $t('actions.action.tab_menu_options.tab_menu_action')
            }}</span>
          </div>
        </Tab>
        <Tab value="1">
          <div class="flex align-items-center gap-2" style="font-size: 18px; margin-bottom: 4px">
            <i class="pi pi-bolt" style="font-size: 18px"></i>
            <span class="font-bold">{{
              $t('actions.dynamic_action.tab_menu_options.tab_menu_action')
            }}</span>
          </div>
        </Tab>
      </TabList>
      <TabPanels>
        <TabPanel value="0">
          <div class="grid m-0">
            <div class="col-12">
              <ActionMenu />
            </div>
          </div>
        </TabPanel>
        <TabPanel value="1">
          <div class="grid m-0">
            <div class="col-12">
              <DynamicActionMenu />
            </div>
          </div>
        </TabPanel>
      </TabPanels>
    </Tabs>
  </Layout>
</template>
<script setup>
import Layout from '@/components/frame/Layout.vue'
import ActionMenu from '@/components/actions/action/ActionMenu.vue'
import DynamicActionMenu from '@/components/actions/dynamic_action/DynamicActionMenu.vue'
</script>
