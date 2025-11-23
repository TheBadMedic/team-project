package entity;

public enum WeatherType {
    TMP2M,
    PRECIP,
    PRESSURE,
    WIND,
    INVALID;

    @Override
    public String toString() {
        switch (name()) {
            case "TMP_2_M":
                return "Temperature at 2 metres";
            case "PRECIP":
                return "Precipitation";
            case "PRESSURE":
                return "Pressure";
            case "WIND":
                return "Wind Speed";
            default:
                return super.toString();
        }
    }
}
