# Sophrosyne

Sophrosyne complements the Data-Center Monitoring and contributes to the development of an automated and self-governed Data-Center

[![Build Status](https://github.com/efstratios97/sophrosyne_core/workflows/sophrosyne-core-ci/badge.svg)](https://github.com/efstratios97/sophrosyne_core/actions)

## Table of Contents

- [Sophrosyne](#sophrosyne)
  - [Table of Contents](#table-of-contents)
  - [Introduction](#introduction)
  - [Architecture and Design](#architecture-and-design)
    - [:pushpin: MVC Pattern](#pushpin-mvc-pattern)
    - [:pushpin: 4 - Layer Architecture](#pushpin-4---layer-architecture)
    - [:pushpin: Microservice aligned structuring](#pushpin-microservice-aligned-structuring)
    - [:pushpin: 3 API "Branches"](#pushpin-3-api-branches)
    - [:pushpin: Continous, real-time Data flows use web-socket](#pushpin-continous-real-time-data-flows-use-web-socket)
    - [:pushpin: Repository-Pattern and ORM](#pushpin-repository-pattern-and-orm)
    - [:pushpin: Use of API-Generators \& Avoidance of Boiler-Plate Code](#pushpin-use-of-api-generators--avoidance-of-boiler-plate-code)
    - [:pushpin: Avoid Code-Duplication and/or outsource functionality to shared libraries](#pushpin-avoid-code-duplication-andor-outsource-functionality-to-shared-libraries)
    - [:pushpin: Infrastructure \& Technology Independence](#pushpin-infrastructure--technology-independence)

## Introduction

The Sophrosyne core project is sophrosyne's backend service implementation providing the APIs to trigger Actions from the GUI as well as externally from 3rd Parties like the Prometheus-Alertmanager. It handles the internal and external action triggering, the authentification and user-management and much more

## Architecture and Design

The below points highlight the main architecture and design principles to be followed

### :pushpin: MVC Pattern

[More Info](https://www.geeksforgeeks.org/mvc-design-pattern/)

### :pushpin: 4 - Layer Architecture

1. The Data layer representing the data Model
2. The Repository layer manages the I/O operation to the database
3. The Service layer implements the business logic
4. The Controller layer manages requests and orchestrates the services

### :pushpin: Microservice aligned structuring

Although Sophrosyne is not built and composed like classic Microservices each functionality is logically structured and organized as a service, where within each service the above-described 4-Layer architecture is followed

### :pushpin: 3 API "Branches"

1. Internal for Calls from the GUI (/int/)
2. External from Calls from 3rd parties like Monitoring/Alerting Software (/api/v1/)
3. Pull for pulling state and statistics data (/pull/)

The internal and external perform additional "expensive" authentification to verify each request, while the pull is more lightweight for serving large data amounts and repeated requests

### :pushpin: Continous, real-time Data flows use web-socket

Data-Streams are served via web-socket to ensure real-time data delivery and inexpensive continious connection establishment

### :pushpin: Repository-Pattern and ORM

[More Info on Repository Pattern](https://www.geeksforgeeks.org/repository-design-pattern/)

[More Info on ORM](https://www.baeldung.com/cs/object-relational-mapping)

### :pushpin: Use of API-Generators & Avoidance of Boiler-Plate Code

Using packages, tools and technologies to automatically document and generate boiler-plate code should be used. Examples: API-generation, defining classes etc.

### :pushpin: Avoid Code-Duplication and/or outsource functionality to shared libraries

Common functionality shall be outsourced in methods, util-classes or shared libraries

### :pushpin: Infrastructure & Technology Independence

By using the Repository Pattern, ORM and the 4-Layer Architecture the goal is to maintain the ability to perform relative low-cost technology switch (like othe database provider or communication protocol) and stay flexible in the further development
