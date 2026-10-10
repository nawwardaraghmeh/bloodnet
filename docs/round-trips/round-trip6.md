## Round trip #6 — package structure, code → model

**Trigger:** the end of the build. The package diagram was designed from the finished code: it
was drawn last, from the imports the code actually contains, not from the architecture the
project claims. It was then compared with that architecture.

**See:** `models/round-trip6-package-diagram` 


### Findings

| # | Claimed | Observed in the model drawn from the code | Verdict |
|---|---|---|---|
| 1 | the domain layer depends on nothing above it | no file in `it.unifi.swam.domain` imports any other project package — the package does not appear once on the left side of the scan | agrees |
| 2 | dependencies point downward only | every import runs rest → services → dao → domain. No package imports one above it, and there is no cycle | agrees |
| 3 | the DAO layer knows the domain and nothing else | all six DAOs import `domain` only | agrees |
| 4 | the service layer knows the DAOs and the domain | all three services import `dao` and `domain`, and nothing from `rest` | agrees |
| 5 | error handling is part of the REST layer | `rest.error` imports nothing from the project at all | **isolated, more than claimed** |

### Actions

- Findings #1 to #5 recorded without change.