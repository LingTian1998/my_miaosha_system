package com.imooc.miaosha.validator;

import com.imooc.miaosha.util.ValidateUtil;
import org.apache.commons.lang3.StringUtils;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class isMobileValidator implements ConstraintValidator<isMobile, String> {

  private boolean required = false;

  @Override
  public void initialize(isMobile constraintAnnotation) {
    required = constraintAnnotation.required();
  }

  @Override
  public boolean isValid(String value, ConstraintValidatorContext context) {
    if (required) {
      return ValidateUtil.isMobile(value);
    } else {
      if (StringUtils.isEmpty(value)) {
        return true;
      } else {
        return ValidateUtil.isMobile(value);
      }
    }
  }
}
