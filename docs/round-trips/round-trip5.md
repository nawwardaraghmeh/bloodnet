## Round trip #5 — interaction, code → model

Reading one path end to end is the only way to see whether the layers behave as layers when
the system is actually running. A package structure can look correct while the objects inside
it talk to whoever they like. For this round trip the sequence diagram was not drawn from the
design: it was designed from the generated code, by tracing `TransfusionService.issuePackToPatient`
call by call, and then compared with what the architecture says should happen.

**See:** `models/round-trip5-seq-diagram` 

### Findings

| # | Expected | Observed in the model drawn from the code | Verdict |
|---|---|---|---|
| 1 | the controller talks only to the service | messages 1–2 and 12–14: the controller calls the service once and maps the result | agrees |
| 2 | the service talks only to DAOs and domain objects | messages 3–11: two DAO lookups, three domain calls, one construction, two saves. Never another service, never a controller | agrees |
| 3 | the rules live on the domain classes and the service invokes them | messages 5, 6 and 7 are `isCompatibleWith`, `canBeIssued` and `issue`, all on `:BloodPack`. The service decides nothing itself | agrees |
| 4 | objects change when something asks them to | message 9: the `Issue` constructor calls `addIssue` back into `:BloodPack`. Nothing in `TransfusionService` says so | **not in any earlier model** |
| 5 | a write is saved once | message 11, `update(pack)`, is redundant: the pack was loaded inside the transaction and is written at commit regardless | leftover |
| 6 | compatibility is one call | message 5 hides three navigations — pack → donation → donor → blood type | the diagram simplifies |

### Actions

1. Findings #4, #5 and #6: accepted and recorded, no change to the code.
2. Findings #1, #2 and #3: no action. They are the confirmations that make the rest meaningful.
