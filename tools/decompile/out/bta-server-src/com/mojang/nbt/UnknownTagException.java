package com.mojang.nbt;

import java.io.IOException;

public class UnknownTagException extends IOException {
   public UnknownTagException() {
   }

   public UnknownTagException(String message) {
      super(message);
   }
}
