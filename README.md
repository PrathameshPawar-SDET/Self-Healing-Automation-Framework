# 🚀 Self-Healing Automation Framework (Selenium + Java + TestNG)

## 📌 Project Overview

This project is a **robust end-to-end test automation framework** built using:

* **Java**
* **Selenium WebDriver**
* **TestNG**
* **Maven**
* **Self-Healing Locator Strategy**

It automates a **bus booking flow** (search → select → book → validate) and demonstrates advanced automation techniques used in real-world applications.

---

## Objective

The goal of this project was to:

* Build a **scalable and maintainable automation framework**
* Implement **self-healing locators**
* Handle **modern UI challenges (React-based applications)**
* Simulate **real user interactions**
* Create a **production-ready test architecture**

---

## Key Learnings & Understanding

### 1. DOM vs Application State (Critical Insight)

One of the biggest learnings was:

> **Updating DOM does NOT mean updating application state (React).**

* Direct JavaScript value injection failed 
* React required **actual user interaction or proper event triggering** 

---

### 2. Handling Hidden Elements

* Date input had `opacity: 0`
* Selenium `visibilityOf` failed 
* Switched to:

  * **Presence-based waits**
  * **JavaScript execution**

---

### 3. React Controlled Components

* Inputs were controlled via `onChange`
* `sendKeys()` was blocked using `preventDefault()`
* Solution:

  * Trigger `input`, `change`, and `blur` events
  * OR interact via UI (calendar picker)

---

### 4. Dynamic Locator Strategy (Self-Healing)

Implemented a **custom self-healing driver**:

* Multiple fallback locators
* JSON-based locator repository
* Automatically tries alternate locators on failure

---

### 5. Handling Flaky UI (Real-world Issue)

* React async rendering caused intermittent failures
* Implemented:

  * Smart waits
  * Retry mechanism (TestNG RetryAnalyzer)
  * JavaScript fallback clicks

---

## Framework Architecture

```
TestingMint_Bus_Automation
│
├── src
│   ├── main
│   │   ├── java
│   │   │   ├── pages
│   │   │   │   ├── HomePage.java
│   │   │   │   ├── SearchResultPage.java
│   │   │   │   ├── SeatSelectionPage.java
│   │   │   │   ├── BookingPage.java
│   │   │   │   ├── SuccessPage.java
│   │   │   │
│   │   │   ├── utils
│   │   │   │   ├── ConfigReader.java
│   │   │   │   ├── JsonReader.java
│   │   │   │   ├── RetryAnalyzer.java
│   │   │   │   ├── SelfHealingDriver.java
│   │   │   │   ├── WaitUtils.java
│   │   │
│   │   ├── resources
│   │       ├── config.properties
│   │       ├── LocatorRepository.json
│   │       ├── testdata.json
│
│   ├── test
│   │   ├── java
│   │   │   ├── base
│   │   │   │   ├── BaseTest.java
│   │   │   │   ├── DriverManager.java
│   │   │   │
│   │   │   ├── tests
│   │   │       ├── EndToEndBookingTest.java
│   │
│   │   ├── resources
│
├── pom.xml
├── testng.xml
```

---

## ⚙️ Tech Stack

| Tool               | Purpose                       |
| ------------------ | ----------------------------- |
| Selenium WebDriver | Browser automation            |
| TestNG             | Test execution & assertions   |
| Maven              | Build & dependency management |
| JSON               | Data & locator management     |
| WebDriverManager   | Driver setup                  |

---

## Features Implemented

### Self-Healing Locator Engine

* JSON-based locator repository
* Fallback mechanism

### Page Object Model (POM)

* Clean separation of concerns
* Reusable components

### Data-Driven Testing

* Test data from JSON

### Retry Mechanism

* Automatic retry on failure

### Smart Wait Handling

* Explicit waits
* React synchronization

### End-to-End Flow Covered

1. Search bus
2. Validate results
3. Select bus
4. Select seat
5. Enter passenger details
6. Complete booking
7. Validate success (PNR)

---

## Challenges Faced & Solutions

| Challenge                 | Solution                      |
| ------------------------- | ----------------------------- |
| Date not getting selected | Used calendar UI interaction  |
| React state not updating  | Triggered proper events       |
| Hidden elements           | Used JS instead of visibility |
| Flaky tests               | Retry + waits                 |
| Locator failure           | Self-healing strategy         |

---

## How to Run

```bash
mvn clean test
```

---

## Sample Output

```
Bus Count: 50
Selected Price: ₹ 1200
PNR: MINT-XXXXX
Booking flow completed successfully!
```

---

## Future Enhancements

* Extent Reports integration
* Screenshot on failure
* Parallel execution
* AI-based healing (Healenium)
* Cross-browser testing

---

## Why This Project Stands Out

* Handles **modern React applications**
* Demonstrates **real debugging skills**
* Implements **self-healing automation**
* Covers **end-to-end real-world scenario**
* Built with **scalable architecture**

---

## Author

**Prathamesh Pawar**

---

## Final Note

This project reflects not just automation skills, but the ability to:

* Understand application behavior deeply
* Debug complex UI issues
* Build production-grade frameworks
