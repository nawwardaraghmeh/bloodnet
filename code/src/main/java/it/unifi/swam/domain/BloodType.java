package it.unifi.swam.domain;

// BloodType.java
public enum BloodType {
    A_POSITIVE, A_NEGATIVE,
    B_POSITIVE, B_NEGATIVE,
    AB_POSITIVE, AB_NEGATIVE,
    O_POSITIVE, O_NEGATIVE;

    public boolean canDonateTo(BloodType recipient) {
        if (recipient == null) {
            return false;
        }
        switch (this) {
            case O_NEGATIVE:
                return true;
            case O_POSITIVE:
                return recipient == O_POSITIVE || recipient == A_POSITIVE
                        || recipient == B_POSITIVE || recipient == AB_POSITIVE;
            case A_NEGATIVE:
                return recipient == A_NEGATIVE || recipient == A_POSITIVE
                        || recipient == AB_NEGATIVE || recipient == AB_POSITIVE;
            case A_POSITIVE:
                return recipient == A_POSITIVE || recipient == AB_POSITIVE;
            case B_NEGATIVE:
                return recipient == B_NEGATIVE || recipient == B_POSITIVE
                        || recipient == AB_NEGATIVE || recipient == AB_POSITIVE;
            case B_POSITIVE:
                return recipient == B_POSITIVE || recipient == AB_POSITIVE;
            case AB_NEGATIVE:
                return recipient == AB_NEGATIVE || recipient == AB_POSITIVE;
            case AB_POSITIVE:
                return recipient == AB_POSITIVE;
            default:
                return false;
        }
    }
}