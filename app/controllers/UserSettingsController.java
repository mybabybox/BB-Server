package controllers;

import java.util.List;

import play.data.DynamicForm;
import play.db.jpa.Transactional;
import play.libs.Json;
import play.mvc.Controller;
import play.mvc.Result;
import viewmodel.EdmSettingsVM;
import viewmodel.PrivacySettingsVM;
import models.EdmTemplate;
import models.EdmTemplate.EdmType;
import models.EdmUnsubscription;
import models.PrivacySettings;
import models.User;

public class UserSettingsController extends Controller {
    private static play.api.Logger logger = play.api.Logger.apply(UserSettingsController.class);
    
    @Transactional
    public static Result savePrivacySettings() {
        DynamicForm form = DynamicForm.form().bindFromRequest();
        
        final User localUser = Application.getLocalUser(session());
        try {
            PrivacySettings settings = PrivacySettings.findByUserId(localUser.id);
            settings.showActivitiesTo = Integer.parseInt(form.get("activity"));
            settings.showJoinedcommunitiesTo = Integer.parseInt(form.get("joinedCommunity"));
            settings.showFriendListTo = Integer.parseInt(form.get("friendList"));
            settings.showDetailsTo = Integer.parseInt(form.get("detail"));
            settings.merge();
        } catch(NullPointerException e) {
            PrivacySettings settings = new PrivacySettings();
            settings.user = localUser;
            settings.showActivitiesTo = Integer.parseInt(form.get("activity"));
            settings.showJoinedcommunitiesTo = Integer.parseInt(form.get("joinedCommunity"));
            settings.showFriendListTo = Integer.parseInt(form.get("friendList"));
            settings.showDetailsTo = Integer.parseInt(form.get("detail"));
            settings.save();
        }
        return ok();
    } 
    
    @Transactional
    public static Result getPrivacySettings() {
        final User localUser = Application.getLocalUser(session());
        PrivacySettings settings = PrivacySettings.findByUserId(localUser.id);
        PrivacySettingsVM vm = new PrivacySettingsVM(settings);
        return ok(Json.toJson(vm));
    }
    
    private static EdmUnsubscription getEdmUnsubscription(List<EdmUnsubscription> edmUnsubscriptions, EdmType edmType) {
        for (EdmUnsubscription edmUnsubscription : edmUnsubscriptions) {
            EdmTemplate edmTemplate = EdmTemplate.findById(edmUnsubscription.edmTemplateId);
            if (edmType.equals(edmTemplate.edmType)) {
                return edmUnsubscription;
            }
        }
        return null;
    }
    
    @Transactional
    public static Result saveEdmSettings() {
        DynamicForm form = DynamicForm.form().bindFromRequest();
        
        final User localUser = Application.getLocalUser(session());
        
        List<EdmUnsubscription> edmUnsubscriptions = EdmUnsubscription.getEdmUnsubscriptions(localUser.id);

        EdmUnsubscription edmUnsubscription = getEdmUnsubscription(edmUnsubscriptions, EdmType.GENERAL_INFO);
        boolean generalInfoEdm = Boolean.parseBoolean(form.get("generalInfoEdm"));
        if (generalInfoEdm && edmUnsubscription != null) {
            edmUnsubscription.delete();
        } else if (!generalInfoEdm && edmUnsubscription == null) {
            EdmTemplate edmTemplate = EdmTemplate.getEdmTemplate(EdmType.GENERAL_INFO);
            edmUnsubscription = new EdmUnsubscription(localUser.id, edmTemplate.id);
            edmUnsubscription.save();
        }
        
        edmUnsubscription = getEdmUnsubscription(edmUnsubscriptions, EdmType.COMMUNITY_INFO);
        boolean communityInfoEdm = Boolean.parseBoolean(form.get("communityInfoEdm"));
        if (communityInfoEdm && edmUnsubscription != null) {
            edmUnsubscription.delete();
        } else if (!communityInfoEdm && edmUnsubscription == null) {
            EdmTemplate edmTemplate = EdmTemplate.getEdmTemplate(EdmType.COMMUNITY_INFO);
            edmUnsubscription = new EdmUnsubscription(localUser.id, edmTemplate.id);
            edmUnsubscription.save();
        }
        
        edmUnsubscription = getEdmUnsubscription(edmUnsubscriptions, EdmType.PROMO);
        boolean promoEdm = Boolean.parseBoolean(form.get("promoEdm"));
        if (promoEdm && edmUnsubscription != null) {
            edmUnsubscription.delete();
        } else if (!promoEdm && edmUnsubscription == null) {
            EdmTemplate edmTemplate = EdmTemplate.getEdmTemplate(EdmType.PROMO);
            edmUnsubscription = new EdmUnsubscription(localUser.id, edmTemplate.id);
            edmUnsubscription.save();
        }
        
        edmUnsubscription = getEdmUnsubscription(edmUnsubscriptions, EdmType.ADS);
        boolean adsEdm = Boolean.parseBoolean(form.get("adsEdm"));
        if (adsEdm && edmUnsubscription != null) {
            edmUnsubscription.delete();
        } else if (!adsEdm && edmUnsubscription == null) {
            EdmTemplate edmTemplate = EdmTemplate.getEdmTemplate(EdmType.ADS);
            edmUnsubscription = new EdmUnsubscription(localUser.id, edmTemplate.id);
            edmUnsubscription.save();
        }
        
        return ok();
    } 
    
    @Transactional
    public static Result getEdmSettings() {
        User localUser = Application.getLocalUser(session());
        List<EdmUnsubscription> unsubscriptions = EdmUnsubscription.getEdmUnsubscriptions(localUser.id);
        EdmSettingsVM vm = new EdmSettingsVM(unsubscriptions);
        return ok(Json.toJson(vm));
    }
}