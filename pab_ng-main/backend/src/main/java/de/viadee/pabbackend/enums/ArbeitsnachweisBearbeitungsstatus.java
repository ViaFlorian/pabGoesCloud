package de.viadee.pabbackend.enums;

public enum ArbeitsnachweisBearbeitungsstatus {
    ERFASST(10), FREIGEGEBEN(20), GEAENDERT(30), ABGERECHNET(40), ABGESCHLOSSEN(50);

    private final Integer statusWert;

    ArbeitsnachweisBearbeitungsstatus(final Integer statusWert) {
        this.statusWert = statusWert;
    }

    public static String idToAnzeigeString(final Integer id) {
        switch (id) {
            case 10 -> {
                return "erfasst";
            }
            case 20 -> {
                return "freigegeben";
            }
            case 30 -> {
                return "geändert";
            }
            case 40 -> {
                return "abgerechnet";
            }
            case 50 -> {
                return "abgeschlossen";
            }
        }
        return null;
    }

    public Integer value() {
        return statusWert;
    }

}
