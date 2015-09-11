package viewmodel;

import java.util.List;

import models.EdmTemplate;
import models.EdmTemplate.EdmType;
import models.EdmUnsubscription;

public class EdmSettingsVM {

    public boolean generalInfoEdm = true;
    public boolean communityInfoEdm = true;
    public boolean promoEdm = true;
    public boolean adsEdm = true;
    
    public EdmSettingsVM(List<EdmUnsubscription> edmUnsubscriptions) {
        for (EdmUnsubscription edmUnsubscription : edmUnsubscriptions) {
            EdmTemplate edmTemplate = EdmTemplate.findById(edmUnsubscription.edmTemplateId);
            if (EdmType.GENERAL_INFO.equals(edmTemplate.edmType)) {
                generalInfoEdm = false;
            } else if (EdmType.COMMUNITY_INFO.equals(edmTemplate.edmType)) {
                communityInfoEdm = false;
            } else if (EdmType.PROMO.equals(edmTemplate.edmType)) {
                promoEdm = false;
            } else if (EdmType.ADS.equals(edmTemplate.edmType)) {
                adsEdm = false;
            }
        }
    }
}