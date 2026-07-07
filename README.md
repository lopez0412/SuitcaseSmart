# SuitcaseSmart 🧳

**Smart packing, zero forgotten items.**

A travel packing app with a suitcase-centric model — you organize 
around your bags, not your trips. Available on Google Play Store.

[![Get it on Google Play](https://img.shields.io/badge/Google_Play-Download-3DDC84?style=flat&logo=google-play&logoColor=white)]([https://play.google.com/store/apps/details?id=com.lopeztechnologies.suitcasesmart](https://play.google.com/store/apps/details?id=com.loptech.suitcasesmart))

## The Problem

Most packing apps are trip-centric. You end up recreating the same 
lists every time, forgetting what's in which bag, and losing track 
of what you've actually used mid-trip.

## The Solution

SuitcaseSmart makes the **suitcase** the central unit. Create your 
bags once, build reusable item lists by category, and track what 
you've packed and used in real time — bag by bag.

## Tech Stack

- **Language:** Kotlin
- **UI:** Jetpack Compose
- **Architecture:** MVVM + Clean Architecture
- **Auth:** Firebase Authentication
- **Database:** Firestore (cloud sync)
- **DI:** Koin
- **Async:** Coroutines + Flow
- **Build:** Gradle Version Catalogs

## Architecture
presentation/   → Composables + ViewModels (StateFlow UiState)

domain/         → Use Cases + Repository interfaces + Entities

data/           → Firestore repos + Firebase Auth + Mappers

## Features

- Create and manage multiple suitcases
- Add items by category (clothing, electronics, toiletries...)
- Check items as packed / used mid-trip
- AI-powered packing suggestions (in development)
- Cloud sync across devices via Firestore
- Freemium model — core free, premium in development

## Roadmap

- [ ] iOS version (Swift + SwiftUI)
- [ ] AI packing suggestions via Claude API
- [ ] Collaborative packing (share bags with travel companions)
- [ ] Freemium paywall + subscription

## Product

This app was designed end-to-end using an AI-assisted workflow 
(Claude/Anthropic) — PRD, architecture, UX wireframes, and 
go-to-market strategy.

Full PRD available in [`PRD-SuitcaseSmart.md`](./PRD-SuitcaseSmart.md)

## Author

**Alejandro Lopez** — Senior Android & iOS Developer  
Lopez Technologies · El Salvador  
[GitHub](https://github.com/lopez0412) · 
[Play Store]([https://play.google.com/store/apps/details?id=com.lopeztechnologies.suitcasesmart](https://play.google.com/store/apps/details?id=com.loptech.suitcasesmart))
