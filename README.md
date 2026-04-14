# Development device information:
- Virtual device (not tested on a physical device)
- Medium phone
- Android 16 ("Baklava") arm64
- API level 36 (24+)

# Used libraries:
- Jetpack Compose

## Features implemented

### Required features
- 3-row x 2-column matrix of rectangles.
- A text box shows the sequence of rectangles pressed so far.
- **Clear** and **Finish Game** buttons.
- A second screen with the list of finished games.
- Support for both **portrait** and **landscape** orientation.

### Additional features
- Ability to scroll the text box and the played games list, with a progress bar indicating scroll progress.
- Screen state persistence using **ViewModel**.
- Use of `onNavigateToScore()` (callback) to change screen and pass data between ViewModels, with navigation controlled by `NavigationStack`.


## Layout structure:

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

## Portrait mode:

```
 ┌──────────────┐
 │  GameMatrix  │ ← 4/5 of space
 ├──────────────┤
 │Sequence Text │ ← 1/5 of space
 ├──────────────┤
 │ GameButtons  │ ← Bottom
 └──────────────┘
```
