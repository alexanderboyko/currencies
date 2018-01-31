package boyko.alex.waluty;

/**
 * Created by Sasha on 31.01.2018.
 * <p>
 * Model class
 */

class Currency {
    private String code, name;
    private double buy, sell;

    String getCode() {
        return code;
    }

    void setCode(String code) {
        this.code = code;
    }

    String getName() {
        return name;
    }

    void setName(String name) {
        this.name = name;
    }

    double getBuy() {
        return buy;
    }

    void setBuy(double buy) {
        this.buy = buy;
    }

    double getSell() {
        return sell;
    }

    void setSell(double sell) {
        this.sell = sell;
    }
}
