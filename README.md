# Simon Game

# Development device information:
- Virtual device (not tested on a physical device)
- Medium phone
- Android 16 ("Baklava") arm64
- targetSdk 36, minSdk 24

# Used libraries:

## UI and navigation
- Jetpack Compose (UI, Material3, Foundation)
- Navigation Compose
- Lifecycle ViewModel Compose

## Persistence (Database)
- Room Runtime (RoomDatabase, DAO base classes, query execution, migrations support, etc... used at runtime)
- Room KTX
- Room Compiler (via KSP, at build time it reads your @Database, @Dao, @Entity and generates implementation classes)
- KSP (the engine/plugin that runs symbol processors, Room Compiler is one of those processors)

## Android core/runtime
- Core KTX
- Lifecycle Runtime KTX
- Activity Compose

## Audio (tones)
- presentation timings and tones are inspired by: https://www.waitingforfriday.com/?p=586
- tones are generated at runtime as 16-bit mono PCM and played with `AudioTrack`
- tone frequencies are chosen to be easier to reproduce on typical phone speakers (they are different from the original)
- note set is based on the C major pentatonic scale (Do–Re–Mi–Sol–La / C–D–E–G–A)
- each tile tone is a sine wave `sin(2πft)` with a short fade in/out to reduce crackling
- losing tone is played on wrong user input for 1.5s
- occasional crackle/buzz at the start is most likely an emulator audio issue

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

### **GameScreen**

- Landscape structure:

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

- Portrait structure:

```
 ┌──────────────┐
 │  GameMatrix  │
 ├──────────────┤
 │Sequence Text │
 ├──────────────┤
 │ GameButtons  │
 └──────────────┘
```

### **ScoreScreen**

- Portrait/Landscape structure:

```
 ┌──────────────────────┐
 │     Played Games     │
 ├──────────────────────┤
 │      ScoreList       │
 │   (played games)     │
 ├──────────────────────┤
 │      Play button     │
 └──────────────────────┘
```

### **DetailScreen**

- Portrait structure:

```
 ┌──────────────────────────────────┐
 │         Game Details             │
 ├──────────────────────────────────┤
 │ Max correct sequence: ...        │
 │ Error position: ...              │
 │ Game sequence:                   │
 │   ....                           │
 │ User sequence:                   │
 │   ....                           │
 └──────────────────────────────────┘
```

- Landscape structure:

```
 ┌──────────────────────────────────┐
 │         Game Details             │
 ├──────────────────────────────────┤
 │ Max correct sequence: ...        │
 │ Error position: ...              │
 │ Game sequence:   User sequence:  │
 │   ....              .....        │
 │   ....               ....        │
 └──────────────────────────────────┘
```
