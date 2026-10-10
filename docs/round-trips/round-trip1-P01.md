## Round trip #1 — naming convention, code → model

**Trigger:** P01 output.

**Found:** the generated boolean getters `hasRedCells()`, `hasPlasma()`, `hasPlatelets()` do
not follow the JavaBeans convention that JPA property access and JSON serialisation use to
find properties.

**Decided:** rename the attributes rather than keep names that would break later layers.

**Changed:** `BloodPack` on the conceptual model — `hasRedCells` → `redCells`, and the same
for the other two.

**See:** `models/round-trip1-conceptual-model.jpg`
