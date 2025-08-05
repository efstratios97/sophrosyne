export default {
  mounted(el) {
    const content = el.querySelector('.p-scrollpanel-content')
    if (content) {
      content.scrollTop = content.scrollHeight
    }
  },
  updated(el) {
    const content = el.querySelector('.p-scrollpanel-content')
    if (content) {
      content.scrollTop = content.scrollHeight
    }
  }
}
