package zone.czh.woi.woim.server.Exception;

import lombok.Data;

/**
*@ClassName: VerifyException
*@Description: None
*@author woi
*/
public class VerifyException extends Exception{
    public VerifyException(){}

    public VerifyException(String message){
        super(message);
    }

}
