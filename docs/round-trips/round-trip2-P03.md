## Round trip #2 — database schema, code → model

**Trigger:** the schema Hibernate derives from the JPA annotations at boot. Captured by
running the application with `quarkus.hibernate-orm.database.generation=drop-and-create` and
`quarkus.hibernate-orm.log.sql=true`, into `round-trips/round-trip2-schema-before.md` and `-after`.

**Compared:** that schema against `models/specs-model.jpg`, class by class and
association by association.

Nobody wrote this schema. It is Hibernate's own reading of the annotations, which makes it a
third independent description of the system, alongside the UML and the Java. Where the three
agree, the model survived. Where they differ, something entered the system from outside
every model.

### Findings

| # | Model says | Schema says | Verdict |
|---|---|---|---|
| 1 | six classes | six tables, no join tables | agrees |
| 2 | five associations | five foreign key columns, each on the "many" side | agrees |
| 3 | `Donation` has exactly one `Donor`; `BloodPack` exactly one `Donation`; `Issue` exactly one `Patient` and one `BloodPack` | `donor_id`, `donation_id`, `patient_id`, `bloodPack_id` are all **nullable** | **disagrees** |
| 4 | `BloodPack` has zero or one `StorageLocation` | `storageLocation_id` nullable | agrees |
| 5 | attribute names | column names identical, camelCase preserved (`expiresAt`, `fiscalCode`, `minTempC`) | agrees |
| 6 | enumerated attributes with a fixed set of literals | native `enum('AVAILABLE','DISCARDED','ISSUED','QUARANTINED','RESERVED')` columns | agrees |

### Actions

1. **Record the uniqueness constraints on the model first.** Add a UML constraint `{unique}`
   to `Donor.fiscalCode`, `Patient.healthNum`, `BloodPack.packCode` and `StorageLocation.code`
   on the specification model, and re-export it. 
2. **Then regenerate the mapping** so that the four mandatory associations are not optional
   and the four business keys are unique, see follow-up prompt P03b in `prompts/P03.md`.

### Difference between the two schemas

| Before (P03) | After (P03b) |
|---|---|
| `donor_id bigint` | `donor_id bigint not null` |
| `donation_id bigint` | `donation_id bigint not null` |
| `patient_id bigint` | `patient_id bigint not null` |
| `bloodPack_id bigint` | `bloodPack_id bigint not null` |
| `storageLocation_id bigint` | unchanged — correct, the diagram says `0..1` |
| `fiscalCode varchar(255)` | `fiscalCode varchar(255) unique` |
| `healthNum varchar(255)` | `healthNum varchar(255) unique` |
| `packCode varchar(255)` | `packCode varchar(255) unique` |
| `code varchar(255)` | `code varchar(255) unique` |

The five foreign key constraints are unchanged, including their generated names, which
confirms that only optionality and uniqueness were affected and the structure of the
associations was untouched. Hibernate expressed uniqueness as an inline column constraint
rather than as a separately named table constraint.

**See:** `models/round-trip2-specs-model.jpg`, `round-trips/round-trip2-schema-before`, `round-trips/round-trip2-schema-after`
