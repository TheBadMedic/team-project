package entity;

public enum WeatherType {
    TMP_2_M,
    PRECIP,
    PRESSURE,
    WIND;

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
