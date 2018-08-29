package com.github.cloudyrock.mongock;

import net.sf.cglib.proxy.Callback;
import net.sf.cglib.proxy.NoOp;

import java.io.Serializable;

/**
 *
 * @since 04/04/2018
 */
class SerializableNoOp implements NoOp, Serializable {

  static final Callback SERIALIZABLE_INSTANCE = new SerializableNoOp();
  private static final long serialVersionUID = -7528524383141480009L;

  private SerializableNoOp() {
  }
}
