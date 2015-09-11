package controllers;

import models.ReportedObject;
import models.User;
import play.data.DynamicForm;
import play.db.jpa.Transactional;
import play.mvc.Controller;
import play.mvc.Result;

public class ReportObjectController extends Controller {
    private static play.api.Logger logger = play.api.Logger.apply(ReportObjectController.class);

    @Transactional
    public static Result createReport() {
        final User localUser = Application.getLocalUser(session());
        DynamicForm form = DynamicForm.form().bindFromRequest();
        ReportedObject reportedObject = new ReportedObject(form, localUser.id);
        logger.underlyingLogger().info(String.format("[u=%d] User reported [%d|%s|%s=%d]", 
                localUser.id, reportedObject.id, reportedObject.reportType.name(), reportedObject.objectType.name(), reportedObject.socialObjectID));
        return ok();
    }
}