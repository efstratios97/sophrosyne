<template>
  <div>
    <RouterView />
  </div>
</template>
<script setup></script>

<style lang="scss">
@import 'primeflex/primeflex.scss';

html {
  background:
    linear-gradient(135deg, #e6e8ea, #cfd2d4),
    radial-gradient(circle at top left, rgba(255, 255, 255, 0.2), transparent 80%);
  background-blend-mode: lighten;
  height: 100vh;
  width: 100vw;
}

body {
  font-family: 'Inter', sans-serif;
  font-size: 1rem;
}

.p-dialog {
  height: 80vh;
  width: 70vw;
}

.p-card {
  transition: box-shadow 0.2s ease;
  box-shadow: 0 0px 20px rgba(0, 0, 0, 0.15) !important;
}

.p-card:hover {
  box-shadow: 0 8px 24px rgba(0, 0, 0, 0.4) !important;
}

.sophrosyne-form {
  @include styleclass('flex flex-column w-full');
}

.sophrosyne-form-wrapper {
  @include styleclass('flex');
}

.sophrosyne-field-wrapper {
  @include styleclass('mb-4');
}

.sophrosyne-inputtext {
  width: 100%;
}

.sophrosyne-text-break {
  word-wrap: break-word;
  overflow-wrap: break-word;
  word-break: break-word;
}

.sophrosyne-code {
  font-family: 'Courier New', monospace;
  background-color: #f4f4f4;
  padding: 2px 4px;
  border: 1px solid #ddd;
  border-radius: 4px;
}

.sophrosyne-terminal-box {
  background-color: #1e1e1e;
  padding: 20px;
  border-radius: 5px;
  box-shadow: 0 0 10px rgba(0, 0, 0, 0.5);
  white-space: pre-wrap;
  color: white;
}

.sophrosyne-layout {
  overflow: auto;
}

.sophrosyne-center-element {
  text-align: center;
}

.sophrosyne-card {
  overflow: auto;
}

.p-multiselect {
  min-width: 100%;
}

.p-toolbar {
  overflow: auto;
  min-height: 100%;
}

/* Terminal Box Styling */
.p-textarea.sophrosyne-terminal-input {
  background-color: black;
  color: #ddd;
  font-family: 'Courier New', Courier, monospace;
  padding: 20px;
  border-radius: 5px;
  width: 100%; /* Terminal width */
  height: 60vh; /* Terminal height */
  overflow-y: auto; /* Enable vertical scrolling */
  overflow-x: hidden; /* Hide horizontal scrolling */
  box-shadow: 0 0 10px rgba(255, 255, 255, 0.5);
  position: relative;
}

.p-tablist-active-bar {
  z-index: 0 !important;
  pointer-events: none;
}
</style>
