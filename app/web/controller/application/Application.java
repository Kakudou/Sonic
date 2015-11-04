package web.controller.application;

import play.mvc.Controller;
import play.mvc.Result;

import web.view.application.html.creation;
import web.view.application.html.edition;

public class Application extends Controller
{
  public static Result creation()
  {
    
    
    return ok(creation.render());
  }
  
  public static Result edition(){
    return ok(edition.render());
  }
}
