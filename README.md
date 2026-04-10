#Development device information:
Virtual device (not tested on a physical device)
Medium phone
Anroid 16 ("Baklava") amr64
API level 36 (24+)

#Other informations:
Used Jetpack Compose


##Layout structure:

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

##Portrait mode:

```
 ┌──────────────┐
 │  GameMatrix  │ ← 4/5 of space
 ├──────────────┤
 │Sequence Text │ ← 1/5 of space
 ├──────────────┤
 │ GameButtons  │ ← Bottom
 └──────────────┘
```
