import { createRouter, createWebHistory } from 'vue-router'

import Login from '@/views/Login.vue'
import Home from '@/views/Home.vue'
import Settings from '@/views/Settings.vue'
import UserProfile from '@/views/UserProfile.vue'
import Actions from '@/views/Actions.vue'
import ActionArchiveDisplay from '@/components/actions_archive/ActionArchiveDisplay.vue'
import ActionsArchive from '@/views/ActionsArchive.vue'
import ActionConfirmation from '@/views/ActionConfirmation.vue'
import ConfirmationDisplay from '@/components/action_confirmation/ConfirmationDisplay.vue'
import ActionRecommendation from '@/views/ActionRecommendation.vue'
import ActionRecommendationMenu from '@/views/ActionRecommendationMenu.vue'
import ActionRecommendationDashboard from '@/components/action_recommendation/dashboard/ActionRecommendationDashboard.vue'
import ActionPromptLiveStream from '@/components/actions/action/ActionPromptLiveStream.vue'
import ControlPanelMenu from '@/views/ControlPanelMenu.vue'
import ControlPanelView from '@/views/ControlPanelView.vue'
import ControlPanel from '@/components/controlpanel/view/ControlPanel.vue'
const routes = [
  { path: '/', name: 'Login', component: Login },
  {
    path: '/home',
    name: 'Home',
    component: Home
  },
  {
    path: '/settings',
    name: 'Settings',
    component: Settings
  },
  {
    path: '/profile',
    name: 'UserProfile',
    component: UserProfile
  },
  {
    path: '/actions',
    name: 'Actions',
    component: Actions
  },
  {
    path: '/actions-archive',
    name: 'ActionsArchive',
    component: ActionsArchive
  },
  {
    path: '/action-confirmation',
    name: 'ActionConfirmation',
    component: ActionConfirmation
  },
  {
    path: '/action-recommendation',
    name: 'ActionRecommendation',
    component: ActionRecommendation
  },
  {
    path: '/action-recommendation-menu',
    name: 'ActionRecommendationMenu',
    component: ActionRecommendationMenu
  },
  {
    path: '/control-panel-menu',
    name: 'ControlPanelMenu',
    component: ControlPanelMenu
  },
  {
    path: '/control-panel-view',
    name: 'ControlPanelView',
    component: ControlPanelView
  },
  {
    path: '/embedded-sophrosyne',
    name: 'EmbeddedControlPanelView',
    component: ControlPanelView
  },
  {
    path: '/embedded-control-panel',
    name: 'EmbeddedControlPanel',
    component: ControlPanel
  },
  {
    path: '/embedded-action-confirmation',
    name: 'EmbeddedActionConfirmation',
    component: ConfirmationDisplay
  },
  {
    path: '/embedded-action-recommendation',
    name: 'EmbeddedActionRecommendation',
    component: ActionRecommendationDashboard
  },
  {
    path: '/embedded-action-archive',
    name: 'EmbeddedActionArchive',
    component: ActionArchiveDisplay
  },
  {
    path: '/embedded-stream',
    name: 'EmbeddedStream',
    component: ActionPromptLiveStream
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

export default router
