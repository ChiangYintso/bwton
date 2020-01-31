package pers.jiangyinzuo.carbon.validation.validator;

import pers.jiangyinzuo.carbon.validation.annotation.Telephone;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

/**
 * @author Jiang Yinzuo
 */
public class TelephoneValidator implements
        ConstraintValidator<Telephone, String> {

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        return value != null && value.matches("^1\\d{10}$");
    }
}