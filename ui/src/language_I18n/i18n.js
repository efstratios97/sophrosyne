// I18n
import { createI18n } from 'vue-i18n'

import en from './locales/en.json'

const messages = { en: en }

// 2. Create i18n instance with options
const i18n = createI18n({
  legacy: false,
  locale: 'en', // set locale
  fallbackLocale: 'en', // set fallback locale
  messages, // set locale messages
  globalInjection: true // Everywhere available $t
})

export default i18n
