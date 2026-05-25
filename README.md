# Simon Game

# Development device information:
- Virtual device (not tested on a physical device)
- Medium phone
- Android 16 ("Baklava") arm64
- API level 36 (minSdk 24+)

# Used libraries:

## UI and navigation
- Jetpack Compose (UI, Material3, Foundation)
- Navigation Compose
- Lifecycle ViewModel Compose

## Persistence (Database)
- Room Runtime
- Room KTX
- Room Compiler (via KSP)

## Android core/runtime
- Core KTX
- Lifecycle Runtime KTX
- Activity Compose

## App flow

1. App starts on **ScoreScreen**.
2. User taps **Play** -> navigates to **GameScreen**.
3. User starts game; sequence is generated and presented.
4. User reproduces sequence by pressing colored tiles.
5. On finish/back, game result is saved (when valid) and app navigates back to Score.
6. User taps a score row -> opens **DetailScreen** for that score id.

## Architecture and data flow

### Layers

- **UI layer**
  - `MainActivity`: app entry point (`setContent`).
  - `NavigationStack`: controls navigation (`Game`, `Score`, `Details`).
  - Composables render state from ViewModels.

- **State layer**
  - `GameViewModel`: owns gameplay state and triggers score save.
  - `ScoreViewModel`: exposes played games as `StateFlow<List<Score>>`.

- **Data layer**
  - `ScoreRepository`: maps `Score` <-> `ScoreEntity`, centralizes data access.
  - `ScoreDao`: Room queries/inserts (`observePlayedGames`, `observePlayedGameById`, `insertScore`).
  - `SimonDatabase`: Room database singleton.
  - `ScoreConverters`: converts `List<Char>` <-> `String` for Room fields.
  - `SimonApplication`: provides a shared app-level `ScoreRepository`.


## Views

- **GameScreen**

## Landscape structure:

```
  ┌──────────────────────────────┐
  │             │ SequenceDisplay│ 
  │ GameMatrix  │  (takes most   │
  │   (50%      │    space)      │
  │   width,    ├────────────────┤
  │   full      │  GameButtons   │
  │   height)   │ (minimal space)│
  └──────────────────────────────┘
```

## Portrait structure:

```
 ┌──────────────┐
 │  GameMatrix  │ ← 4/5 of space
 ├──────────────┤
 │Sequence Text │ ← 1/5 of space
 ├──────────────┤
 │ GameButtons  │ ← Bottom
 └──────────────┘
```

- **ScoreScreen**

## Portrait/Landscape structure:

```
 ┌──────────────────────┐
 │     Played Games     │ ← Title
 ├──────────────────────┤
 │      ScoreList       │ ← scrollable list (takes most space)
 │   (played games)     │
 ├──────────────────────┤
 │      Play button     │ ← Bottom
 └──────────────────────┘
```

- **DetailScreen**

## Portrait/Landscape structure:

```
 ┌──────────────────────────────────┐
 │         Game Details             │ ← Title
 ├──────────────────────────────────┤
 │ Max correct sequence: ...        │
 │ Error position: ...              │
 │ Game sequence:   User sequence:  │
 │   ....              .....        │
 │   ....               ....        │
 └──────────────────────────────────┘
```
