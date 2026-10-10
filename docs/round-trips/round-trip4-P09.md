## Round trip #4 — API reachability, tests → model

**See:** `models/round-trip4-use-cases.jpg`

**Trigger:** generating the end-to-end tests (P09), with the requirement that nothing may reach
the application except over HTTP.

**Compared:** `models/use-cases.jpg` against what a client can actually do through the REST API.

Unlike the earlier round trips, this one was not a comparison anybody set out to make. A person testing by hand guesses
that the first identifier is 1, and reads a pack's state indirectly through the storage-location
listing; a test told to use only HTTP has neither option, and the gap surfaced on the first
attempt.

### Findings

| # | Model says | System says | Verdict |
|---|---|---|---|
| 1 | nine use cases, eight of them commands and one a list | nine endpoints, one per use case | agrees |
| 2 | the use cases *place pack in storage* and *issue pack to patient* are available to their actors | a client that has just recorded a donation cannot perform either, because the response names the packs by code and the endpoints take identifiers | **disagrees** |
| 3 | nothing about observing a pack | no endpoint reads a single blood pack; its state can only be seen for packs that happen to be in a storage location | **model is silent, and the silence is wrong** |
| 4 | the actors and their use cases | every endpoint corresponds to exactly one use case, none is unreachable and none is undocumented | agrees |

### Actions taken

1. One use case, **View blood pack**, added to `models/round-trip4-use-cases.png` and linked to the Storage
   Operator. The diagram was updated before anything was generated from it, as at every earlier
   step.
2. Prompt P10 added `InventoryService.viewBloodPack`, the endpoint
   `GET /inventory/blood-packs/{packId}`, and a `packIds` field on `DonationResponse`.
3. The P09 prompt was re-run unchanged against the updated code.

Finding #2 was closed by the `packIds` field rather than by a model change. The model was not
wrong; it simply cannot express the point, so it was left as it is and the reason recorded here.

**See:** `models/round-trip4-use-cases.jpg`
