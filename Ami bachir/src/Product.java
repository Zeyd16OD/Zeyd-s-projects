public class Product {

    private String code;
    private String name;
    private String description;
    private String color;
    private String utility;
    private int quantity;
    private float weight;
    private int seuil;


    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public String getUtility() {
        return utility;
    }

    public void setUtility(String utility) {
        this.utility = utility;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public float getWeight() {
        return weight;
    }

    public void setWeight(float weight) {
        this.weight = weight;
    }

    public int getSeuil() {
        return seuil;
    }

    public void setSeuil(int seuil) {
        this.seuil = seuil;
    }

    public Product(String code, String name, String description, String color, String utility, int quantity, float weight, int seuil) {
        this.code = code;
        this.name = name;
        this.description = description;
        this.color = color;
        this.utility = utility;
        this.quantity = quantity;
        this.weight = weight;
        this.seuil = seuil;



    }
}
