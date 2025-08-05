<template>
  <Layout>
    <Tabs value="0" scrollable>
      <TabList>
        <Tab value="0">
          <div class="flex align-items-center gap-2" style="font-size: 18px; margin-bottom: 4px">
            <i class="pi pi-book" style="font-size: 18px"></i>
            <span class="font-bold">{{
              $t('actions_archive.tab_menu_options.tab_menu_actions_archive')
            }}</span>
          </div>
        </Tab>
      </TabList>
      <TabPanels>
        <TabPanel value="0">
          <div class="grid m-0">
            <div class="col-12 h-full">
              <ActionArchiveDisplay></ActionArchiveDisplay>
            </div>
          </div>
        </TabPanel>
      </TabPanels>
    </Tabs>
  </Layout>
</template>
<script setup>
import Layout from '@/components/frame/Layout.vue'
import ActionArchiveDisplay from '@/components/actions_archive/ActionArchiveDisplay.vue'
</script>
