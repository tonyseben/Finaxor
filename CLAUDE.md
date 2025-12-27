# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this
repository.

## Build & Run Commands

```bash
# Android
./gradlew :composeApp:assembleDebug     # Build debug APK
./gradlew :composeApp:assembleRelease   # Build release APK

# Desktop (JVM)
./gradlew :composeApp:run               # Run desktop app

# iOS - Use Xcode with iosApp/iosApp.xcodeproj

# Testing
./gradlew test                          # Run all tests
./gradlew :composeApp:testDebug         # Android unit tests
```

## Architecture Overview

Finaxor is a **Kotlin Multiplatform (KMP)** app for managing investment portfolios and fixed
deposits, targeting Android, iOS, and Desktop using Compose Multiplatform.

### Clean Architecture Layers

```
composeApp/src/commonMain/kotlin/com/tonyseben/finaxor/
├── domain/           # Business logic (pure Kotlin)
│   ├── model/        # Domain models (User, Portfolio, FixedDeposit, AuthUser, AuthState)
│   ├── repository/   # Repository interfaces
│   └── usecase/      # Use cases (single responsibility)
├── data/             # Data layer
│   ├── auth/         # Auth services (Strategy pattern for auth providers)
│   ├── entity/       # Firestore entity models
│   ├── mapper/       # Entity ↔ Domain mappers (extension functions)
│   ├── source/remote/# Firebase data sources (Auth + Firestore)
│   └── repository/   # Repository implementations
├── ui/               # UI layer
│   └── auth/         # Platform-specific auth launchers (expect/actual)
├── di/               # Manual DI via AppContainer singleton
└── core/             # Result wrapper, AppError, utilities
```

### Key Patterns

**Result Pattern**: All async operations return `Result<T>` with three states:

- `Result.Success<T>` - Contains data
- `Result.Error` - Contains `AppError`
- `Result.Loading` - Loading state

**Use Case Types**:

- `UseCase<P, R>`: Suspend function returning `Result<R>`
- `FlowUseCase<P, R>`: Returns `Flow<Result<R>>`

**Error Handling**: Sealed `AppError` class with: `NetworkError`, `AuthError`, `PermissionError`,
`ValidationError`, `NotFoundError`, `BusinessError`, `UnknownError`

**Auth Strategy Pattern**: Extensible auth provider system:

- `AuthService` interface in `data/auth/`
- `GoogleAuthService` implementation (add `EmailAuthService`, `AppleAuthService` later)
- `AuthServiceFactory` creates services from `AuthProvider` sealed class

## Tech Stack

- **Kotlin**: 2.3.0 with Compose Compiler Plugin
- **Compose Multiplatform**: 1.9.3
- **Firebase (GitLive KMP)**: 2.1.0 (firebase-auth, firebase-firestore)
- **Kotlinx**: Serialization 1.8.0, DateTime 0.6.1, Coroutines 1.10.2
- **Android**: minSdk 24, targetSdk 36, compileSdk 35, JVM 11
- **Google Sign-In**: Credential Manager API (Android), GIDSignIn (iOS)

## Firebase Structure

```
users/{userId}                              # User documents
users/{userId}/portfolioAccess/             # User's portfolio access
portfolios/{portfolioId}                    # Portfolio documents
portfolios/{portfolioId}/members/           # Portfolio members
portfolios/{portfolioId}/fixedDeposits/     # Fixed deposits
```

## Adding a New Feature

1. Create domain model in `domain/model/`
2. Create repository interface in `domain/repository/`
3. Create use case(s) in `domain/usecase/`
4. Create entity in `data/entity/`
5. Create mappers in `data/mapper/`
6. Create data source in `data/source/remote/`
7. Implement repository in `data/repository/`
8. Register dependencies in `di/AppContainer.kt`

## Current State & Roadmap

**Current focus**: Fixed Deposits + Authentication (domain & data layers complete)

**Development phases**:

1. ~~User authentication (Firebase Auth)~~ ✓ Domain & data layers done
2. UI for portfolios and fixed deposits
3. Additional asset classes: Stocks, PPF, Bonds, Mutual Funds, Cryptocurrency

**Layer status**:

- Domain layer: Complete for FD and Auth
- Data layer: Complete for FD and Auth (Firestore + Firebase Auth)
- Presentation layer: GoogleSignInLauncher (expect/actual) ready, needs UI screens
