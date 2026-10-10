## P02 output — Specifications model → domain classes 

```java
// BloodPackState.java
public enum BloodPackState {
    QUARANTINED, AVAILABLE, RESERVED, ISSUED, DISCARDED
}
```

```java
// StorageLocation.java
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class StorageLocation {
    private String code;
    private double minTempC;
    private double maxTempC;
    private List<BloodPack> bloodPacks;

    public StorageLocation(String code, double minTempC, double maxTempC) {
        this.code = code;
        this.minTempC = minTempC;
        this.maxTempC = maxTempC;
        this.bloodPacks = new ArrayList<>();
    }

    public String getCode() { return code; }
    public double getMinTempC() { return minTempC; }
    public double getMaxTempC() { return maxTempC; }

    public List<BloodPack> getBloodPacks() {
        return Collections.unmodifiableList(bloodPacks);
    }

    // Package-private: called only by BloodPack.setStorageLocation
    void addBloodPack(BloodPack bloodPack) {
        this.bloodPacks.add(bloodPack);
    }

    void removeBloodPack(BloodPack bloodPack) {
        this.bloodPacks.remove(bloodPack);
    }
}
```
