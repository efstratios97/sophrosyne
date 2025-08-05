<template>
  <Splitter style="height: 100%" layout="vertical">
    <SplitterPanel class="p-5 w-full" style="overflow: auto">
      <Card class="sophrosyne-card">
        <template #header>
          <div class="flex justify-content-center text-xl font-bold">
            {{ $t('control_panel_menu.control_panel.menu.header') }}
          </div>
        </template>
        <template #title>
          <div class="flex justify-content-center text-base font-medium">
            {{ $t('control_panel_menu.control_panel.menu.title') }}
          </div>
        </template>
        <template #content> <DisplayTemplate :isControlPanel="true"></DisplayTemplate> </template
      ></Card>
    </SplitterPanel>
    <SplitterPanel class="p-5 w-full" style="overflow: auto">
      <Card>
        <template #header>
          <div class="flex justify-content-center text-xl font-bold">
            {{ $t('control_panel_menu.control_panel_dashboard.menu.header') }}
          </div>
        </template>
        <template #title>
          <div class="flex justify-content-center text-base font-medium">
            {{ $t('control_panel_menu.control_panel_dashboard.menu.title') }}
          </div>
        </template>
        <template #content>
          <DisplayTemplate :isControlPanelDashboard="true"></DisplayTemplate>
        </template>
      </Card>
    </SplitterPanel>
    <SplitterPanel class="p-5 w-full" style="overflow: auto">
      <Card>
        <template #header>
          <div class="flex justify-content-center text-xl font-bold">
            {{ $t('control_panel_menu.control_panel_dashboard_group.menu.header') }}
          </div>
        </template>
        <template #title>
          <div class="flex justify-content-center text-base font-medium">
            {{ $t('control_panel_menu.control_panel_dashboard_group.menu.title') }}
          </div>
        </template>
        <template #content>
          <DisplayTemplate :isControlPanelDashboardGroup="true"></DisplayTemplate>
        </template>
      </Card>
    </SplitterPanel>
  </Splitter>
</template>

<script setup>
import DisplayTemplate from '@/components/controlpanel/menu/utils/DisplayTemplate.vue'
</script>
