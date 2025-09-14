public enum TabletModel {
    WIFI("와이파이"),
    CELLULAR("셀룰러");

    private final String model;

    TabletModel(String model) {
        this.model = model;
    }

    public String getModel() {
        return model;
    }
}
