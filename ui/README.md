# Sophrosyne

Sophrosyne complements the Data-Center Monitoring and contributes to the development of an automated and self-governed Data-Center

## Table of Contents

- [Sophrosyne](#sophrosyne)
  - [Table of Contents](#table-of-contents)
  - [Introduction](#introduction)
  - [Architecture and Design](#architecture-and-design)
    - [:pushpin: The GUI implements no logic](#pushpin-the-gui-implements-no-logic)
    - [:pushpin: Component based architecture](#pushpin-component-based-architecture)
    - [:pushpin: Common functionality is outsourced in composables](#pushpin-common-functionality-is-outsourced-in-composables)
    - [:pushpin: The application state is managed via a state machine](#pushpin-the-application-state-is-managed-via-a-state-machine)
    - [:pushpin: Common css styles are shared](#pushpin-common-css-styles-are-shared)
    - [:pushpin: All text is configurable through I18n](#pushpin-all-text-is-configurable-through-i18n)

## Introduction

The Sophrosyne ui project is sophrosyne's frontend service implementation providing the GUI to create and manually trigger actions, view execution logs and stats and much more

## Architecture and Design

The below points highlight the main architecture and design principles to be followed

### :pushpin: The GUI implements no logic

All the logic to be at the backend side (We try)

### :pushpin: Component based architecture

Every view consists of reusable components, thus, small interface pieces that can be arranged and utilized by a view

### :pushpin: Common functionality is outsourced in composables

[More info](https://vuejs.org/guide/reusability/composables)

### :pushpin: The application state is managed via a state machine

[More info](https://pinia.vuejs.org/core-concepts/state.html)

### :pushpin: Common css styles are shared

Global CSS styles are defined in one place and through scss-preprocessor inheritance enabled

### :pushpin: All text is configurable through I18n

Text is configurable via I18n [More Info](https://vue-i18n.intlify.dev/)
