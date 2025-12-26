package org.apache.logging.log4j.core.config.plugins.validation.validators;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.plugins.validation.ConstraintValidator;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.NotBlank;
import org.apache.logging.log4j.status.StatusLogger;
import org.apache.logging.log4j.util.Strings;

public class NotBlankValidator implements ConstraintValidator<NotBlank> {
   private static final Logger LOGGER = StatusLogger.getLogger();
   private NotBlank annotation;

   public void initialize(final NotBlank anAnnotation) {
      this.annotation = anAnnotation;
   }

   @Override
   public boolean isValid(final String name, final Object value) {
      return Strings.isNotBlank(name) || this.err(name);
   }

   private boolean err(final String name) {
      LOGGER.error(this.annotation.message(), name);
      return false;
   }
}
