## Round trip #3 — statechart, code → model

**Trigger:** generating the service layer (P05) with the statechart given as the authority on
which state changes are allowed.

**Compared:** `models/bloodpack-statechart.jpg` against the generated domain and service code.

### Findings

| # | Model says | Code says | Verdict |
|---|---|---|---|
| 1 | `ISSUED → ◉` when the outcome is transfused | `markTransfused()` sets the state to `DISCARDED` | **the model cannot be implemented as drawn** |
| 2 | a pack moves between states only along the drawn transitions | `BloodPack` also had a public `setState(BloodPackState)` | **disagrees** |
| 3 | `AVAILABLE → DISCARDED [now > expiresAt]` | no use case performs this transition | **model element with nothing behind it** |
| 4 | the remaining seven transitions | one guarded method each: `markAvailable`, `issue`, `returnToAvailable`, `markTransfused`, `discard` | agrees |

### Actions taken

1. The statechart was changed first: the transition `ISSUED → ◉ [transfused]` was replaced by
   `ISSUED → TRANSFUSED` and `TRANSFUSED → ◉`.
   Re-exported as `models/round-trip3-bloodpack-statechart.jpg`.
2. The affected classes were regenerated: `TRANSFUSED` added to `BloodPackState`,
   `markTransfused()` changed to set it, and `setState` removed with no replacement.
3. Finding #3 accepted without change and recorded: expiry is evaluated when a pack is issued,
   not on a schedule.

**See:** `models/round-trip3-bloodpack-statechart.jpg`