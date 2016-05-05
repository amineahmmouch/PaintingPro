package commacreations.apps.paintingpro;

import com.orm.SugarRecord;

public class Product extends SugarRecord {

    String reference;
    String category;
    String application;
    String diluted;
    String cov;
    String emission;

    public Product() {
    }

    public Product(String reference, String category, String application, String diluted, String cov, String emission) {
        this.reference = reference;
        this.category = category;
        this.application = application;
        this.diluted = diluted;
        this.cov = cov;
        this.emission = emission;
    }
}
