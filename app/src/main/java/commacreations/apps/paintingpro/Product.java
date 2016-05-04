package commacreations.apps.paintingpro;

import com.orm.SugarRecord;

public class Product extends SugarRecord {

    String reference;
    String category;
    String application;
    String diluted;
    float cov;
    String emmission;

    public Product() {
        super();
    }

    public Product(String reference, String category, String application, String diluted, float cov, String emmission) {
        this.reference = reference;
        this.category = category;
        this.application = category;
        this.diluted = category;
        this.cov = cov;
        this.emmission = emmission;
    }
}
