package sonic.oidc.code.responder;

import sonic.oidc.code.model.Code;

public class CreateCodeResponder
{
  private Code code;
  
  public CreateCodeResponder(Code code)
  {
    super();
    this.code = code;
  }
  
  public Code getCode()
  {
    return code;
  }

}
